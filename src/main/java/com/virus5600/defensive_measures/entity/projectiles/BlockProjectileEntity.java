package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.AttributeValues;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.EntityBlockAttributeIndex;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.ExplosiveValues;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager;
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
	public static final BlockState DEFAULT_BLOCK_STATE;

	protected Identifier blockId = BlockItemIds.STONE.block().identifier();
	protected EntityBlockAttributeIndex attrIndex = BlockProjectileConfigManager.createFrom(AttributeValues.DEFAULT);
	protected AttributeValues attrValues;
	protected boolean attributesSet = false;

	public BlockProjectileEntity(EntityType<? extends BlockProjectileEntity> entityType, Level level) {
		super(entityType, level);
	}

	public BlockProjectileEntity(EntityType<? extends BlockProjectileEntity> entityType, Level level, UsesBlockProjectile owner) {
		this(entityType, level);

		this.setOwner(owner.getTurretEntity());
	}

	public BlockProjectileEntity(EntityType<? extends BlockProjectileEntity> entityType, Level level, UsesBlockProjectile owner, Item block) {
		this(entityType, level, owner);

		this.setBlockId(block);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //

	@Override
	protected void defineSynchedData(@NonNull Builder builder) {
		// Initialize standard data trackers
		super.defineSynchedData(builder);

		builder.define(DATA_BLOCK_STATE, DEFAULT_BLOCK_STATE)
			.define(DATA_FIRE_ENABLED, false);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	protected boolean isBurning() {
		return this.entityData.get(DATA_FIRE_ENABLED);
	}

	@Override
	protected float getDrag() {
		return (float) this.attrValues.drag();
	}

	@Override
	protected double getDefaultGravity() {
		return this.attrValues.gravity();
	}

	public boolean isExplosive() throws IllegalStateException {
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
		this.attrValues = null;

		return this;
	}

	/**
	 * Sets the attributes of this block projectile based on what block is being used and from which
	 * turret it was fired from. If the {@link #blockId} is {@code null}, it will default to
	 * {@link Items#STONE}. If the owner of the projectile is {@code null}, an
	 * {@link IllegalStateException} will be thrown. Furthermore, if the owner of the projectile is
	 * {@code null} or does not implement the {@link UsesBlockProjectile} interface, it will
	 * default to the default attribute values defined in {@link AttributeValues#DEFAULT AttributeValues.DEFAULT}.
	 *
	 * @return this instance of {@link BlockProjectileEntity} for method chaining.
	 */
	public BlockProjectileEntity setAttributes() {
		if (this.blockId == null) {
			this.setBlockId(DEFAULT_BLOCK_STATE.getBlock().asItem());
		}

		if (!this.attributesSet) {
			// Handles the case where the owner is null or does not implement UsesBlockProjectile. In such cases, we default to the default attribute values.
			if ((this.getOwner() == null || !(this.getOwner() instanceof UsesBlockProjectile))) {
				this.attrIndex = BlockProjectileConfigManager.createFrom(AttributeValues.DEFAULT);
			}
			else {
				this.attrIndex = BlockProjectileConfigManager.getConfigForEntity(this.getOwner().getType());
			}

			this.attrValues = this.attrIndex.getBlockAttributes(this.blockId.toString());
		}

		if (this.attrValues != null) {
			this.entityData.set(DATA_FIRE_ENABLED, this.attrValues.fire());

			if (!this.attrValues.fire()) {
				this.igniteForTicks(0);
			}
		}

		return this;
	}

	/**
	 * This overridden method checks if the block projectile is configured to be explosive. If it
	 * is, it will call the {@link ExplosiveProjectileEntity#doDamage() super.doDamage()} method to
	 * handle the explosion. If it is not, it will act like a {@link KineticProjectileEntity} and
	 * discard the projectile.
	 * <br><br>
	 * {@inheritDoc}
	 */
	@Override
	public void doDamage() {
		if (this.attrValues.explosive().enabled()) {
			super.doDamage();
		}
		else {
			this.discard();
		}
	}

	@Override
	public void addAdditionalSaveData(@NonNull ValueOutput view) {
		super.addAdditionalSaveData(view);

		if (this.attrIndex == null) {
			this.setAttributes();
		}

		view.store("Block", Identifier.CODEC, this.blockId);
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
					this.setBlockId(DEFAULT_BLOCK_STATE.getBlock().asItem());
				}
			});

		view.read("Attr", AttributeValues.CODEC)
			.ifPresent(attr -> {
				this.attrIndex = BlockProjectileConfigManager.createFrom(attr);
				this.setAttributes();
			});
	}

	/**
	 * Fetches the default block state of the block being used as the projectile. If the
	 * {@link #blockId} is {@code null}, it will default to {@link Items#STONE}.
	 *
	 * @return the default block state of the block being used as the projectile.
	 */
	public BlockState getBlockState() {
		if (this.blockId == null) {
			this.setBlockId(Items.STONE);
		}

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

		double radius = attrVal.explosive()
			.effectiveRadius();

		return Math.max(radius, 0);
	}

	@Override
	public double getMaxDamageRadius() {
		AttributeValues attrVal = this.attrValues;

		if (!attrVal.explosive().enabled()) {
			return 0;
		}

		double radius = attrVal.explosive()
			.maxRadius();

		return Math.max(radius, 0);
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
		double pierce = attrVal.piercing();

		return (byte) Math.max(pierce, 0);
	}

	static {
		DATA_BLOCK_STATE = SynchedEntityData.defineId(BlockProjectileEntity.class, EntityDataSerializers.BLOCK_STATE);
		DATA_FIRE_ENABLED = SynchedEntityData.defineId(BlockProjectileEntity.class, EntityDataSerializers.BOOLEAN);

		DEFAULT_BLOCK_STATE = RegistryHelper.getBlockState(BlockItemIds.STONE.block().identifier());
	}
}
