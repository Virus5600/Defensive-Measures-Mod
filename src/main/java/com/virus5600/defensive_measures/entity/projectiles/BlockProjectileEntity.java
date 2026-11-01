package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager;
import com.virus5600.defensive_measures.configs.records.block.AttributeValues;
import com.virus5600.defensive_measures.configs.records.block.ExplosiveValues;
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesBlockProjectile;

import org.jspecify.annotations.NonNull;

/**
 * Represents a generalized block projectile entity that can be used by turrets that shoots blocks.
 * <br><br>
 * This class extends the {@link ExplosiveProjectileEntity} class as the block projectile can be
 * configured to explode, allowing for a wide range of block projectiles to be used by the turret.
 * TNT are such blocks that when used as a projectile that could be configured via the
 * {@link BlockProjectileConfigManager config} file to explode on impact.
 * <br><br>
 * The block projectile can be configured through config files, which then get loaded into the game
 * via {@link BlockProjectileConfigManager} so turrets can use the configured block projectile
 * properties. The configuration allows for the block projectile to have different properties based
 * on the block being used and the turret that fired it. This allows for a wide range of block
 * projectiles to be used by different turrets, each with their own unique properties.
 * <br><br>
 * A block projectile can be configured to:
 * <ul>
 * 	<li><b>{@link AttributeValues#drag Drag}:</b> The multiplier which determines how much of its speed gets retained over time.</li>
 * 	<li><b>{@link AttributeValues#gravity Gravity}:</b> The amount of downward velocity added to the block projectile, adding up over time.</li>
 * 	<li><b>{@link AttributeValues#damage Damage}:</b> The damage dealt when the block projectile directly hits an entity.</li>
 * 	<li><b>{@link AttributeValues#piercing Piercing}:</b> The amount of piercing value this block projectile has. Determines how many entity it could penetrate before being stopped. Does not work when the projectile explodes.</li>
 * 	<li><b>{@link AttributeValues#fire Fire}:</b> Whether the projectile can set its target on fire or not.</li>
 * 	<li><b>{@link AttributeValues#explosive Explosive}:</b> Its explosive attribute. See next list below for details.</li>
 * </ul>
 * <br><br>
 * The explosive attribute can be configured to:
 * <ul>
 * 	<li><b>{@link ExplosiveValues#damage Damage}:</b> The damage dealt by the explosion. This is a separate value from the kinetic damage dealt by this projectile</li>
 * 	<li><b>{@link ExplosiveValues#effectiveRadius Effective Radius}:</b> The radius within which the explosion deals full damage.</li>
 * 	<li><b>{@link ExplosiveValues#maxRadius Max Radius}:</b> The maximum radius of the explosion.</li>
 * </ul>
 *
 * @see ExplosiveProjectileEntity
 * @see BlockProjectileConfigManager
 * @see UsesBlockProjectile
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BlockProjectileEntity extends ExplosiveProjectileEntity {
	protected static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE;
	protected static final EntityDataAccessor<Boolean> DATA_FIRE_ENABLED;
	protected static final EntityDataAccessor<Float> DATA_GRAVITY;
	protected static final EntityDataAccessor<Float> DATA_DRAG;
	public static final BlockState DEFAULT_BLOCK_STATE;

	protected Identifier blockId = BlockItemIds.STONE.block().identifier();
	protected AttributeValues attrValues = AttributeValues.DEFAULT;

	public BlockProjectileEntity(EntityType<? extends BlockProjectileEntity> entityType, Level level) {
		super(entityType, level);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //

	@Override
	protected void defineSynchedData(@NonNull Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);

		builder.define(DATA_BLOCK_STATE, DEFAULT_BLOCK_STATE)
			.define(DATA_FIRE_ENABLED, false)
			.define(DATA_GRAVITY, 0.05f)
			.define(DATA_DRAG, 0.99f);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	protected void move() {
		if (this.attrValues.explosive().enabled()) {
			super.move();
		}
		else {
			boolean physicsEnabled = !this.isNoPhysics();
			Vec3 velocity = this.getDeltaMovement();
			BlockPos blockPos = this.blockPosition();
			BlockState blockState = this.level().getBlockState(blockPos);

			// If this projectile has phjysics and colliding with a block...
			if (!blockState.isAir() && physicsEnabled) {
				VoxelShape voxelShape = blockState.getCollisionShape(this.level(), blockPos);

				// ... and if the block has a collision shape (i.e. it's not a block like tall grass or
				// flowers that have no collision shape)...
				if (!voxelShape.isEmpty()) {
					Vec3 pos = this.position();

					// ... and if the projectile's position is within any of the collision boxes of the
					// block, then the projectile is considered to be in the ground, and its velocity
					// is set to zero.
					for (AABB box : voxelShape.toAabbs()) {
						if (box.move(blockPos).contains(pos)) {
							this.setDeltaMovement(Vec3.ZERO);
							this.setInGround(true);
							break;
						}
					}
				}
			}

			// If this projectile is in ground and has it physics...
			if (this.isInGround() && physicsEnabled) {
				// ... and the the world isn't client...
				if (!this.level().isClientSide()) {
					// ... and if the block state that the projectile is in is different from the block
					// state that it was in when it first collided with the block, and if the
					// projectile should fall, then the projectile falls.
					if (this.lastState != blockState && this.shouldFall()) {
						this.startFalling();
					}
					// ... else, this just ages.
					else {
						this.tickDespawn();
					}

					// ... finally, set this on fire if fire ticks is more than 0.
					this.setSharedFlagOnFire(this.getRemainingFireTicks() > 0);
				}

				++this.inGroundTime;
				// ... and if it is stil alive, then tick the block collision logic.
				if (this.isAlive()) {
					this.applyEffectsFromBlocks();
				}
			}
			// ... else...
			else {
				this.inGroundTime = 0;
				Vec3 pos = this.position();

				// ... if it is touching water, spawn bubble particles at the projectile's position.
				if (this.isInWater()) {
					this.spawnBubbleParticles(pos);
				}

				// ... if it is critical, spawn critical hit particles along the projectile's path. The
				// particles are spawned at the projectile's position and along its velocity vector,
				// with a spacing of 0.25 blocks between each particle, and the particles have a
				// velocity opposite to the projectile's velocity, with a slight upward velocity added
				// to make them more visible.
				if (this.isCritical()) {
					for (int i = 0; i < 4; i++) {
						this.level()
							.addParticle(
								ParticleTypes.CRIT,
								pos.x + velocity.x * (double)i / 4.0,
								pos.y + velocity.y * (double)i / 4.0,
								pos.z + velocity.z * (double)i / 4.0,
								-velocity.x,
								-velocity.y + 0.2,
								-velocity.z
							);
					}
				}

				float pitchDeg = MathUtil.radToDeg((float) (Mth.atan2(velocity.y, velocity.horizontalDistance())));
				float yawDeg = MathUtil.radToDeg((float) (Mth.atan2(velocity.x, velocity.z)));

				this.setXRot(lerpRotation(this.getXRot(), -pitchDeg));
				this.setYRot(lerpRotation(this.getYRot(), -yawDeg));
				this.checkLeftOwner();

				// ... and if it has physics, perform a raycast to check for collisions with blocks
				// along the projectile's path, and if there is a collision, apply the collision logic
				// to the hit result.
				if (physicsEnabled) {
					BlockHitResult hitResult = this.level()
						.clipIncludingBorder(
							new ClipContext(
								pos,
								pos.add(velocity),
								ClipContext.Block.COLLIDER,
								ClipContext.Fluid.NONE,
								this
							)
						);

					this.applyCollision(hitResult);
				}
				// ... otherwise, just move the projectile by its velocity and tick the block collision logic.
				else {
					this.setPos(pos.add(velocity));
					this.applyEffectsFromBlocks();
				}

				// ... and if it is clipping, apply drag to the velocity, and if it is also not in
				// ground, apply gravity to the velocity as well, causing the projectile to slow down
				// and fall over time.
				this.applyDrag();
				if (physicsEnabled && !this.isInGround()) {
					this.applyGravity();
				}
			}
		}
	}

	@Override
	protected boolean isBurning() {
		return this.entityData.get(DATA_FIRE_ENABLED);
	}

	@Override
	protected float getDrag() {
		return this.entityData.get(DATA_DRAG);
	}

	@Override
	protected double getDefaultGravity() {
		return (double) this.entityData.get(DATA_GRAVITY);
	}

	@Override
	public SoundEvent getHitSound() {
		return this.entityData
			.get(DATA_BLOCK_STATE)
			.getSoundType()
			.getBreakSound();
	}

	public boolean isExplosive() {
		return this.attrValues.explosive().enabled();
	}

	/**
	 * Sets the block ID of this block projectile based on the provided {@link Item}. If the
	 * provided item is not an instance of {@link BlockItem}, an {@link IllegalArgumentException}
	 * will be thrown.
	 *
	 * @param block the {@link Item} representing the block to be used as the projectile.
	 *
	 * @return this instance of {@link BlockProjectileEntity} for method chaining.
	 *
	 * @throws IllegalArgumentException if the provided item is not an instance of {@link BlockItem}.
	 */
	public BlockProjectileEntity setBlockId(Item block) throws IllegalArgumentException {
		if (!(block instanceof BlockItem blockItem)) {
			throw new IllegalArgumentException("The provided item is not a BlockItem.");
		}

		this.blockId = RegistryHelper.getIdentifier(block);
		this.entityData.set(DATA_BLOCK_STATE, blockItem.getBlock().defaultBlockState());

		return this;
	}

	/**
	 * Sets the attributes of this block projectile based on the provided {@link AttributeValues AttributeValues}.
	 * In an off case where the provided {@link AttributeValues AttributeValues} is {@code null}, it will default to
	 * {@link AttributeValues#DEFAULT AttributeValues.DEFAULT} to prevent any potential {@link NullPointerException} from
	 * occurring.
	 *
	 * @return this instance of {@link BlockProjectileEntity} for method chaining.
	 *
	 * @see AttributeValues {@link AttributeValues AttributeValues}
	 * @see BlockProjectileConfigManager
	 */
	public BlockProjectileEntity setAttributes(AttributeValues attrVal) {
		this.attrValues = attrVal;

		if (this.attrValues == null) {
			this.attrValues = AttributeValues.DEFAULT;
		}

		this.setDamage(this.attrValues.damage());

		this.entityData.set(DATA_FIRE_ENABLED, this.attrValues.fire());
		this.entityData.set(DATA_GRAVITY, (float) this.attrValues.gravity());
		this.entityData.set(DATA_DRAG, (float) this.attrValues.drag());

		if (!this.attrValues.fire()) {
			this.igniteForTicks(0);
		}

		return this;
	}

	/**
	 * This overridden method checks if the block projectile is configured to be explosive. If it
	 * is, it will call the {@link ExplosiveProjectileEntity#doDamage(boolean) super.doDamage(boolean)}
	 * method to handle the explosion. If it is not, it will act like a {@link KineticProjectileEntity}
	 * and discard the projectile. However, if the block ID is {@link BlockItemIds#IRON_BARS}, it
	 * will not discard the projectile and will instead allow it to pass through the iron bars
	 * without being destroyed.
	 * <hr>
	 * {@inheritDoc}
	 */
	@Override
	public void doDamage(boolean discardAfter) {
		if (this.attrValues.explosive().enabled()) {
			// If it's an Iron Bars...
			if (this.getBlockId() == BlockItemIds.IRON_BARS.block().identifier()) {
				super.doDamage(false);

				// Disables the ability to explode after exploding once.
				if (this.attrValues == AttributeValues.RODS_FROM_GOD) {
					this.attrValues = AttributeValues.RODS_FROM_GOD_INERT;
				}
				else {
					this.attrValues = new AttributeValues(
						this.attrValues.drag(),
						this.attrValues.gravity(),
						this.attrValues.damage(),
						this.attrValues.piercing(),
						this.attrValues.fire(),
						ExplosiveValues.DISABLED
					);
				}

				return;
			}

			super.doDamage(true);
		}
		else {
			if (this.getBlockId() != BlockItemIds.IRON_BARS.block().identifier() || this.getDrag() >= 1.0f) {
				this.discard();
			}
			else {
				this.shake = 0;
			}
		}
	}

	@Override
	public void addAdditionalSaveData(@NonNull ValueOutput view) {
		super.addAdditionalSaveData(view);

		view.store("Block", Identifier.CODEC, this.getBlockId());
		view.store("Attr", AttributeValues.CODEC, this.attrValues);
	}

	@Override
	public void readAdditionalSaveData(@NonNull ValueInput view) {
		super.readAdditionalSaveData(view);

		view.read("Block", Identifier.CODEC)
			.ifPresent(id -> {
				Item item = RegistryHelper.getItem(id);

				if (item instanceof BlockItem) {
					this.setBlockId(item);
				}
				else {
					DefensiveMeasures.LOGGER.warn("Unknown or non-block item '{}' for a block projectile, falling back to stone.", id);
					this.setBlockId(DEFAULT_BLOCK_STATE.getBlock().asItem());
				}
			});

		view.read("Attr", AttributeValues.CODEC)
			.ifPresent(this::setAttributes);
	}

	/**
	 * Fetches the default block state of the block being used as the projectile.
	 *
	 * @return the default block state of the block being used as the projectile.
	 */
	public BlockState getBlockState() {
		return this.entityData.get(DATA_BLOCK_STATE);
	}

	public Identifier getBlockId() {
		return RegistryHelper.getIdentifier(
			this.entityData.get(DATA_BLOCK_STATE).getBlock()
		);
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	// ExplosiveProjectileEntity //

	@Override
	public double getEffectiveRadius() {
		AttributeValues attrVal = this.attrValues;

		if (!attrVal.explosive().enabled()) {
			return 0;
		}

		return attrVal.explosive()
			.effectiveRadius();
	}

	@Override
	public double getMaxDamageRadius() {
		AttributeValues attrVal = this.attrValues;

		if (!attrVal.explosive().enabled()) {
			return 0;
		}

		return attrVal.explosive()
			.maxRadius();
	}

	@Override
	public double getDamageReduction() {
		return 0.25;
	}

	// ExplosiveEntity //

	@Override
	public boolean canDestroyBlocks() {
		return false;
	}

	// TurretProjectileEntity //

	@Override
	public byte getMaxPierceLevel() {
		AttributeValues attrVal = this.attrValues;

		// Prevents piercing when explosive.
		if (this.isExplosive()) {
			return 0;
		}

		double pierce = attrVal.piercing();

		return (byte) Math.clamp(pierce, 0, 127);
	}

	static {
		DATA_BLOCK_STATE = SynchedEntityData.defineId(BlockProjectileEntity.class, EntityDataSerializers.BLOCK_STATE);
		DATA_FIRE_ENABLED = SynchedEntityData.defineId(BlockProjectileEntity.class, EntityDataSerializers.BOOLEAN);
		DATA_GRAVITY = SynchedEntityData.defineId(BlockProjectileEntity.class, EntityDataSerializers.FLOAT);
		DATA_DRAG = SynchedEntityData.defineId(BlockProjectileEntity.class, EntityDataSerializers.FLOAT);

		DEFAULT_BLOCK_STATE = RegistryHelper.getBlockState(BlockItemIds.STONE.block().identifier());
	}
}
