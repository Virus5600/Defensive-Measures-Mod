package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

/**
 * {@code TurretProjectile} is an abstract class that acts nearly akin to {@link PersistentProjectileEntity}
 * but with more support towards entity based projectiles and more control over some aspects of the
 * projectile such as its damage, piercing, and more.
 * <br><br>
 * For a more basic projectile entity or one that can be shot by a player, use the {@link PersistentProjectileEntity}
 * class.
 */
public abstract class TurretProjectileEntity extends ProjectileEntity implements GeoEntity {
	protected static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(TurretProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(TurretProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Boolean> IN_GROUND = DataTracker.registerData(TurretProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final int CRITICAL_FLAG = 1;
	protected static final int NO_CLIP_FLAG = 2;

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	/** Holds the blockstate information of the block in this projectile's position. */
	@Nullable
	private BlockState inBlockState;
	/** Holds the information about the list of entities this projectile have pierced through. */
	@Nullable
	private IntOpenHashSet piercedEntities;
	/** Defines the current sound this projectile will play when it hit something */
	private SoundEvent sound = this.getHitSound();
	/** Defines the item stack this projectile is. Used when picking up the projectile to turn into items. */
	@Nullable
	private ItemStack stack;
	private int life;

	/** Identifies if this projectile can be picked up or not. */
	protected PickupPermission pickupType = PickupPermission.DISALLOWED;
	/** Defines how long the shaking animation will play (in ticks). */
	protected int shake;
	/** Determines how long this projectile have been stuck "**in**" the ground. */
	protected int inGroundTime;
	/** Defines the amount of damage this entity will deal when it hits an entity. */
	protected double damage = 2.0;
	/** Determines whether this projectile scales its damage based on its speed. */
	protected boolean speedAffectDamage = true;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	protected TurretProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected TurretProjectileEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		double x,
		double y,
		double z,
		World world,
		@Nullable ItemStack stack
	) {
		this(entityType, world);

		this.stack = stack;
		this.setPosition(x, y, z);

		if (stack != null) {
			this.setCustomName(stack.get(DataComponentTypes.CUSTOM_NAME));

			Unit unit = stack.remove(DataComponentTypes.INTANGIBLE_PROJECTILE);
			if (unit != null) {
				this.pickupType = PickupPermission.CREATIVE_ONLY;
			}
		}
	}

	protected TurretProjectileEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		@NotNull LivingEntity owner,
		@NotNull World world,
		@Nullable ItemStack stack
	) {
		this(entityType, owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), world, stack);
		this.setOwner(owner);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void initDataTracker(Builder builder) {
		builder.add(PROJECTILE_FLAGS, (byte) 0)
			.add(PIERCE_LEVEL, (byte) 0)
			.add(IN_GROUND, false);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	/**
	 * This method is called when the projectile hits an entity, handling the logic
	 * for hitting, damaging, and if applicable, piercing the entity.
	 * <br><br>
	 * In this implementation, the method will handle the damage calculation, the
	 * critical damage multiplier, the pierce behavior, and the fire behavior similar
	 * to how the {@link PersistentProjectileEntity} handles it.
	 * <br><br>
	 * Furthermore, this implementation, the projectile will have the following behavior
	 * (assuming the projectile is affected by armor points):
	 * <table>
	 * 	<tr>
	 * 		<th>Armor Points</th>
	 * 		<th>Pierce Level Reduction</th>
	 * 		<th>Velocity Reduction</th>
	 * 		<th>Base Damage Reduction</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>Heavy Armor</td>
	 * 		<td>2</td>
	 * 		<td>50%</td>
	 * 		<td>50%</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>Light Armor</td>
	 * 		<td>1</td>
	 * 		<td>25%</td>
	 * 		<td>20%</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>No Armor</td>
	 * 		<td>N/A</td>
	 * 		<td>12.5%</td>
	 * 		<td>5%</td>
	 * 	</tr>
	 * </table>
	 *
	 * @param entityHitResult {@inheritDoc EntityHitResult}
	 */
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);

		Entity hitEntity = entityHitResult.getEntity();
		Entity owner = this.getOwner();

		// Handles the damage calculation
		DamageSource dmgSrc = this.getDamageSources().create(DamageTypes.ARROW, this, owner != null ? owner : this);
		float velocityMagnitude = (float) this.getVelocity().length();
		double damage = this.getDamage();
		int damageToDeal = MathHelper.ceil(
			this.speedAffectsDamage() ?
				MathHelper.clamp(
					(double) velocityMagnitude * damage,
					0.0,
					2.147483647E9
				) : damage
		);

		// Handles the pierce behavior
		if (this.getPierceLevel() > 0) {
			if (this.piercedEntities == null) {
				this.piercedEntities = new IntOpenHashSet(this.getMaxPierceLevel());
			}

			if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
				this.discard();
				return;
			}

			this.piercedEntities.add(hitEntity.getId());
		}

		// Handles the critical damage multiplier
		if (this.isCritical()) {
			long critDamage = this.random.nextInt(damageToDeal / 2 + 2);
			damageToDeal = (int) Math.min(critDamage + damageToDeal, 2147483647L);
		}

		// Makes sure that the owner of this projectile gets to know that it is attacking
		if (owner instanceof LivingEntity livingEntity) {
			livingEntity.onAttacking(hitEntity);
		}

		// Handles the behavior of setting its target on fire if hit
		boolean isEnderman = hitEntity.getType().equals(EntityType.ENDERMAN);
		int fireTime = hitEntity.getFireTicks();
		if (this.isOnFire() && !isEnderman) {
			hitEntity.setOnFireFor(5F);
		}

		// Handles the application of damage to the target
		if (this.getWorld() instanceof ServerWorld) {
			if (hitEntity.damage((ServerWorld) this.getWorld(), dmgSrc, (float) damageToDeal)) {
				if (isEnderman) return;

				if (hitEntity instanceof LivingEntity livingEntity) {
					if (!this.getWorld().isClient() && this.getPierceLevel() <= 0) {
						livingEntity.setStuckArrowCount(livingEntity.getStuckArrowCount() + 1);
					}

					this.onHit(livingEntity);
				}

				this.playSound(this.sound , 1f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
				if (this.getPierceLevel() <= 0) {
					this.discard();
				}
			}
			else {
				hitEntity.setFireTicks(fireTime);
				this.deflect(ProjectileDeflection.SIMPLE, hitEntity, owner, false);
				this.setVelocity(this.getVelocity().multiply(0.2));

				if (this.getWorld() instanceof ServerWorld serverWorld && this.getVelocity().lengthSquared() < 1.0E-7) {
					if (this.pickupType == PickupPermission.ALLOWED && this.stack != null) {
						this.dropStack(serverWorld, this.asItemStack(), 0.1f);
					}

					this.discard();
				}
			}
		}
		else {
			hitEntity.clientDamage(dmgSrc);
		}

		// Proceeds to modify some data if, and only if the pierce level is greater than 0
		// Handles the piercing, velocity, and damage reduction behaviors
		if (this.getPierceLevel() > 0) {
			Entity ent = entityHitResult.getEntity();
			LivingEntity entity = ent instanceof LivingEntity livingEntity ? livingEntity : null;
			ArmorType armorType = ArmorType.getArmorType(entity);

			// If armor affects piercing...
			if (this.armorAffectsPiercing()) {
				byte reduction = 0;

				switch (armorType) {
					case HEAVY -> reduction = 2;
					case LIGHT -> reduction = 1;
				}

				this.setPierceLevel((byte) (this.getPierceLevel() - reduction));
			}

			// If armor affects velocity...
			if (this.armorAffectsVelocity()) {
				double reduction = 0.125;

				switch (armorType) {
					case HEAVY -> reduction = 0.5;
					case LIGHT -> reduction = 0.25;
				}

				this.addVelocity(
					this.getVelocity()
						.multiply(reduction)
						.negate()
				);
			}

			// If armor affects damage...
			if (this.armorAffectsDamage()) {
				double reduction = 0.05;

				switch (armorType) {
					case HEAVY -> reduction = 0.5;
					case LIGHT -> reduction = 0.25;
				}

				this.setDamage(this.getDamage() * (1 - reduction));
			}
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		World world = this.getWorld();
		this.inBlockState = world.getBlockState(blockHitResult.getBlockPos());
		super.onBlockHit(blockHitResult);

		Vec3d velocity = this.getVelocity();
		Vec3d dirVector = new Vec3d(
			Math.signum(velocity.x),
			Math.signum(velocity.y),
			Math.signum(velocity.z)
		);
		Vec3d embeddingAdjustment = dirVector.multiply(0.05);

		this.shake = 7;
		this.setPosition(this.getPos().subtract(embeddingAdjustment));
		this.setVelocity(Vec3d.ZERO);
		this.playSound(this.getSound(), 1f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
		this.setInGround(true);
		this.setCritical(false);
		this.setPierceLevel((byte) 0);
		this.setSound(SoundEvents.ENTITY_ARROW_HIT);
		this.clearPiercingStatus();
	}

	protected void onHit(LivingEntity target) {
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		World world = this.getWorld();
		if (world.isClient() && (this.isInGround() || this.isNoClip()) && this.shake <= 0) {
			if (this.tryPickup(player)) {
				player.sendPickup(this, 1);
				this.discard();
			}
		}
	}

	protected void applyCollision(BlockHitResult blockHitResult) {
		while (this.isAlive()) {
			Vec3d pos = this.getPos();
			EntityHitResult entityHitResult = this.getEntityCollision(pos, blockHitResult.getPos());
			Vec3d hitPos = Objects.requireNonNullElse(entityHitResult, blockHitResult).getPos();

			this.setPosition(hitPos);
			this.tickBlockCollision(pos, hitPos);

			if (this.portalManager != null && this.portalManager.isInPortal()) {
				this.tickPortalTeleportation();
			}

			if (entityHitResult == null) {
				if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS) {
					this.hitOrDeflect(blockHitResult);
					this.velocityDirty = true;
				}

				break;
			}
			else if (this.isAlive() && !this.noClip) {
				ProjectileDeflection deflection = this.hitOrDeflect(entityHitResult);
				this.velocityDirty = true;

				if (this.getPierceLevel() > 0 && deflection == ProjectileDeflection.NONE) {
					continue;
				}
				break;
			}
		}
	}

	protected void applyDrag() {
		Vec3d velocity = this.getVelocity();
		float dragCoefficient = this.getDrag();

		if (this.isTouchingWater()) {
			dragCoefficient = this.getDragInWater();
		}

		this.setVelocity(velocity.multiply(dragCoefficient));
	}

	protected void spawnBubbleParticles(Vec3d pos) {
		Vec3d velocity = this.getVelocity();

		for (int i = 0; i < 4; i++) {
			float radius = 0.25F;
			this.getWorld().addParticle(
				ParticleTypes.BUBBLE,
				pos.x - velocity.x * radius,
				pos.y - velocity.y * radius,
				pos.z - velocity.z * radius,
				velocity.x, velocity.y, velocity.z
			);
		}
	}

	protected void fall() {
		Vec3d velocity = this.getVelocity();

		this.life = 0;
		this.setInGround(false);
		this.setVelocity(
			velocity.multiply(
				this.random.nextFloat() * 0.2F,
				this.random.nextFloat() * 0.2F,
				this.random.nextFloat() * 0.2F
			)
		);
	}

	protected void age() {
		this.life++;

		if (this.life >= 1200) {
			this.discard();
		}
	}

	protected void clearPiercingStatus() {
		if (this.piercedEntities != null) {
			this.piercedEntities.clear();
		}
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 10;
		if (Double.isNaN(d)) {
			d = 1;
		}

		d *= 64 * TurretProjectileEntity.getRenderDistanceMultiplier();
		d *= d;
		return distance < d;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (!this.firstUpdate && this.shake <= 0 && data.equals(IN_GROUND) && this.isInGround()) {
			this.shake = 7;
		}
	}

	@Nullable
	protected ItemStack asItemStack() {
		return this.stack == null ?
			null : this.stack.copy();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);

		nbt.putShort("life", (short) this.life);
		nbt.putByte("shake", (byte)this.shake);
		nbt.putBoolean("inGround", this.isInGround());
		nbt.putByte("pickup", (byte)this.pickupType.ordinal());
		nbt.putDouble("damage", this.damage);
		nbt.putBoolean("crit", this.isCritical());
		nbt.putByte("PierceLevel", this.getPierceLevel());

		Identifier soundId = Registries.SOUND_EVENT.getId(this.sound);
		if (soundId != null) {
			nbt.putString("SoundEvent", soundId.toString());
		}

		if (this.stack != null) {
			nbt.put("item", this.stack.toNbt(this.getRegistryManager()));
		}

		if (this.inBlockState != null) {
			nbt.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);

		this.life = nbt.getShort("life");
		this.shake = nbt.getByte("shake");
		this.setInGround(nbt.getBoolean("inGround"));
		this.damage = nbt.getDouble("damage");
		this.pickupType = PickupPermission.fromOrdinal(nbt.getByte("pickup"));
		this.setCritical(nbt.getBoolean("crit"));
		this.setPierceLevel(nbt.getByte("PierceLevel"));

		if (nbt.contains("inBlockState", NbtElement.COMPOUND_TYPE)) {
			this.inBlockState = NbtHelper.toBlockState(
				this.getWorld()
					.createCommandRegistryWrapper(RegistryKeys.BLOCK),
				nbt.getCompound("inBlockState")
			);
		}

		if (nbt.contains("item", NbtElement.COMPOUND_TYPE)) {
			this.setStack(
				ItemStack.fromNbt(
					this.getRegistryManager(),
					nbt.getCompound("item")
				).orElse(this.getDefaultItemStack())
			);
		}
		else {
			this.setStack(this.getDefaultItemStack());
		}
	}

	public void applyDamageModifier(float damageModifier) {
		this.setDamage(
			(double) (damageModifier * 2.0F)
				+ this.random.nextTriangular(
					(double) this.getWorld().getDifficulty().getId() * 0.11,
				0.57425
			)
		);
	}

	@Override
	public void tick() {
		boolean isClipping = !this.isNoClip();
		Vec3d velocity = this.getVelocity();
		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.getWorld().getBlockState(blockPos);

		if (!blockState.isAir() && isClipping) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.getWorld(), blockPos);

			if (!voxelShape.isEmpty()) {
				Vec3d pos = this.getPos();

				for (Box box : voxelShape.getBoundingBoxes()) {
					if (box.offset(blockPos).contains(pos)) {
						this.setInGround(true);
						break;
					}
				}
			}
		}

		if (this.shake > 0) {
			this.shake--;
		}

		if (this.isTouchingWaterOrRain() || blockState.isOf(Blocks.POWDER_SNOW)) {
			this.extinguish();
		}

		if (this.isInGround() && isClipping) {
			if (!this.getWorld().isClient()) {
				if (this.inBlockState != blockState && this.shouldFall()) {
					this.fall();
				} else {
					this.age();
				}
			}

			this.inGroundTime++;
			if (this.isAlive()) {
				this.tickBlockCollision();
			}
		} else {
			this.inGroundTime = 0;
			Vec3d pos = this.getPos();

			if (this.isTouchingWater()) {
				this.spawnBubbleParticles(pos);
			}

			if (this.isCritical()) {
				for (int i = 0; i < 4; i++) {
					this.getWorld()
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

			float yawDeg;
			if (!isClipping) {
				yawDeg = (float)(MathHelper.atan2(-velocity.x, -velocity.z) * 180.0F / (float)Math.PI);
			} else {
				yawDeg = (float) (MathHelper.atan2(velocity.x, velocity.z) * 180.0F / (float) Math.PI);
			}

			float pitchDeg = (float)(MathHelper.atan2(velocity.y, velocity.horizontalLength()) * 180.0F / (float)Math.PI);

			this.setPitch(updateRotation(this.getPitch(), pitchDeg));
			this.setYaw(updateRotation(this.getYaw(), yawDeg));

			if (isClipping) {
				BlockHitResult hitResult = this.getWorld()
					.getCollisionsIncludingWorldBorder(
						new RaycastContext(
							pos,
							pos.add(velocity),
							RaycastContext.ShapeType.COLLIDER,
							RaycastContext.FluidHandling.NONE,
							this
						)
					);

				this.applyCollision(hitResult);
			} else {
				this.setPosition(pos.add(velocity));
				this.tickBlockCollision();
			}

			this.applyDrag();
			if (isClipping && !this.isInGround()) {
				this.applyGravity();
			}

			super.tick();
		}
	}

	// //////////////////////////// //
	// GETTERS, SETTERS, AND ASKERS //
	// //////////////////////////// //
	/** Overridable method that defines the sound played when this projectile hits something. **/
	public SoundEvent getHitSound() {
		return SoundEvents.ENTITY_ARROW_HIT;
	}

	protected final SoundEvent getSound() {
		return this.sound;
	}

	public void setSound(SoundEvent sound) {
		this.sound = sound;
	}

	/**
	 * Determines whether the speed of the projectile affects the damage it deals,
	 * similar to how {@link PersistentProjectileEntity} handles it.
	 *
	 * @return {@code true} if the speed affects the damage, {@code false} otherwise
	 *
	 * @see #speedAffectDamage
	 */
	public boolean speedAffectsDamage() {
		return this.speedAffectDamage;
	}

	protected void setSpeedAffectsDamage(boolean speedAffectDamage) {
		this.speedAffectDamage = speedAffectDamage;
	}

	@Override
	protected double getGravity() {
		return 0.05;
	}

	protected float getDrag() {
		return 0.99F;
	}

	protected float getDragInWater() {
		return 0.6F;
	}

	/**
	 * {@return the read-only item stack representing the projectile}
	 *
	 * <p>This is the original stack used to spawn the projectile. {@link #asItemStack}
	 * returns a copy of that stack which can be safely changed. Additionally,
	 * {@link #asItemStack} reflects changes to the entity data, such as custom potion ID.
	 */
	public ItemStack getItemStack() {
		return this.stack;
	}

	@Nullable
	protected ItemStack getDefaultItemStack() {
		return null;
	}

	protected void setStack(@Nullable ItemStack stack) {
		if (stack != null && !stack.isEmpty()) {
			this.stack = stack;
		} else {
			this.stack = this.getDefaultItemStack();
		}
	}

	@Override
	public void setVelocity(double x, double y, double z, float power, float uncertainty) {
		super.setVelocity(x, y, z, power, uncertainty);
		this.life = 0;
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.setVelocity(x, y, z);
		this.life = 0;

		double sqrdPosMagnitude = MathHelper.squaredMagnitude(x, y, z);
		if (this.isInGround() && sqrdPosMagnitude > 0.0) {
			this.setInGround(true);
		}
	}

	@Nullable
	protected EntityHitResult getEntityCollision(Vec3d currentPos, Vec3d nextPos) {
		return ProjectileUtil.getEntityCollision(
			this.getWorld(),
			this,
			currentPos, nextPos,
			this.getBoundingBox()
				.stretch(this.getVelocity())
				.expand(1.0),
			this::canHit
		);
	}

	protected boolean shouldFall() {
		return this.isInGround() && this.getWorld().isSpaceEmpty(new Box(this.getPos(), this.getPos()).expand(0.06));
	}

	protected boolean isInGround() {
		return this.dataTracker.get(IN_GROUND);
	}

	protected void setInGround(boolean inGround) {
		this.dataTracker.set(IN_GROUND, inGround);
	}

	@Override
	protected boolean canHit(Entity entity) {
		return super.canHit(entity)
			&& (
				this.piercedEntities == null
				|| !this.piercedEntities.contains(entity.getId())
			);
	}

	@Override
	public boolean canHit() {
		return super.canHit() && !this.isInGround();
	}

	protected boolean tryPickup(PlayerEntity player) {
		boolean didPickedUp = false;

		if (this.pickupType == PickupPermission.ALLOWED) {
			if (this.stack != null) {
				didPickedUp = player.getInventory().insertStack(this.asItemStack());
			}
		}
		else if (this.pickupType == PickupPermission.CREATIVE_ONLY) {
			didPickedUp = player.isInCreativeMode();
		}

		return didPickedUp;
	}

	@Override
	protected  MoveEffect getMoveEffect() {
		return MoveEffect.NONE;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	@Override
	public boolean isAttackable() {
		return this.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE);
	}

	public void setCritical(boolean critical) {
		this.setProjectileFlag(CRITICAL_FLAG, critical);
	}

	public byte getPierceLevel() {
		return this.dataTracker.get(PIERCE_LEVEL);
	}

	/**
	 * Defines this projectile's maximum allowed piercing level.
	 * <br>
	 * The piercing level defines the number of entities this projectile can pierce through before
	 * it is destroyed. If the piercing level is set to 0, the projectile will not pierce through
	 * any entity. This is the usual behavior for any {@link KineticProjectileEntity Kinetic Projectiles}.
	 * <br>
	 * However, when an {@link ExplosiveProjectileEntity Explosive Projectile} defines a piercing level
	 * more than 0, it will pierce through entities and explode upon reaching the maximum piercing
	 * level. This allows the projectile to deal damage to multiple entities before exploding, or
	 * even allowing possibilities for chain reactions like when a projectile hits an entity, it
	 * explodes and damages nearby entities before finally exploding itself.
	 *
	 * @return the maximum pierce level
	 */
	public abstract byte getMaxPierceLevel();

	protected void setPierceLevel(byte level) {
		this.dataTracker.set(PIERCE_LEVEL, level);
	}

	/**
	 * An overridable method that determines whether an armor point affects the
	 * piercing of the projectile.
	 *
	 * @return {@code true} if the armor point affects the piercing of the projectile,
	 * {@code false} otherwise.
	 */
	public abstract boolean armorAffectsPiercing();

	/**
	 * An overridable method that determines whether an armor point affects the
	 * speed of the projectile.
	 *
	 * @return {@code true} if the armor point affects the piercing of the projectile,
	 * {@code false} otherwise.
	 */
	public abstract boolean armorAffectsVelocity();

	/**
	 * An overridable method that determines whether an armor point affects the
	 * damage dealt by the projectile.
	 *
	 * @return {@code true} if the armor point affects the piercing of the projectile,
	 * {@code false} otherwise.
	 */
	public abstract boolean armorAffectsDamage();

	private void setProjectileFlag(int index, boolean flag) {
		byte projectileFlags = this.dataTracker.get(PROJECTILE_FLAGS);
		if (flag) {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte) (projectileFlags | index));
		} else {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte) (projectileFlags & ~index));
		}
	}

	public boolean isCritical() {
		byte projectileFlags = this.dataTracker.get(PROJECTILE_FLAGS);
		return (projectileFlags & 1) != 0;
	}

	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
		this.setProjectileFlag(NO_CLIP_FLAG, noClip);
	}

	public boolean isNoClip() {
		return !this.getWorld().isClient ? this.noClip : (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return mappedIndex == 0 ? StackReference.of(this::getItemStack, this::setStack) : super.getStackReference(mappedIndex);
	}

	@Override
	protected boolean deflectsAgainstWorldBorder() {
		return true;
	}

	@Override
	public void setOwner(Entity owner) {
		super.setOwner(owner);

		if (owner instanceof TurretEntity turret) {
			this.setDamage(turret.getProjectileDamage());
			this.setPierceLevel(turret.getProjectilePierceLevel());
		}
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	// GeoEntity //
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}

	// ///////////////////// //
	// LOCAL CLASS AND ENUMS //
	// ///////////////////// //
	public enum PickupPermission {
		DISALLOWED,
		ALLOWED,
		CREATIVE_ONLY;

		public static PickupPermission fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal > values().length) {
				ordinal = 0;
			}

			return values()[ordinal];
		}
	}

	/**
	 * An enumeration that defines the type of armor the entity has based on
	 * the amount of armor points they have.
	 */
	public enum ArmorType {
		/** Entity is categorized as {@code HEAVY} if the entity has more than 15 armor points. */
		HEAVY,
		/**
		 * Entity is categorized as {@code LIGHT} if the entity has less than or equal to 15 armor
		 * points, but more than 0 armor points.
		 */
		LIGHT,
		/** Entity is categorized as {@code NONE} if the entity has 0 armor points. */
		NONE;

		/**
		 * Identifies what kind of armor type the entity has based on the amount of armor points
		 * the entity has.
		 *
		 * @param entity the entity to check
		 * @return the armor type of the entity
		 */
		public static ArmorType getArmorType(LivingEntity entity) {
			ArmorType toRet = NONE;
			if (entity == null) {
				return toRet;
			}

			// For reference, the max armor points is 30.
			int armor = entity.getArmor();

			if (armor > 15) {
				toRet = HEAVY;
			}
			else if (armor <= 15 && armor > 0) {
				toRet = LIGHT;
			}

			return toRet;
		}
	}
}
