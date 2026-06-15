package com.virus5600.defensive_measures.entity.projectiles;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.registry.tag.ModEntityTypeTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;

import com.virus5600.defensive_measures.entity.ExplosiveEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * {@code TurretProjectile} is an abstract class that acts nearly akin to {@link AbstractArrow}
 * but with more support towards entity based projectiles and more control over some aspects of the
 * projectile such as its damage, piercing, and more.
 * <br><br>
 * For a more basic projectile entity or one that can be shot by a player, use the {@link AbstractArrow}
 * class.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class TurretProjectileEntity extends Projectile {
	protected static final EntityDataAccessor<Byte> PROJECTILE_FLAGS = SynchedEntityData.defineId(TurretProjectileEntity.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(TurretProjectileEntity.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.defineId(TurretProjectileEntity.class, EntityDataSerializers.BOOLEAN);
	protected static final int CRITICAL_FLAG = 1;
	protected static final int NO_CLIP_FLAG = 2;

	public final AnimationState loopAnimationState = new AnimationState();

	/** Defines the current sound this projectile will play when it hit something */
	private SoundEvent sound = this.getHitSound();
	/** Defines the item stack this projectile is. Used when picking up the projectile to turn into items. */
	@Nullable
	private ItemStack stack;
	private int life;

	/** Holds the blockstate information of the block in this projectile's position. */
	@Nullable
	protected BlockState inBlockState;
	/** Holds the information about the list of entities this projectile have pierced through. */
	@Nullable
	protected IntOpenHashSet piercedEntities;
	/** Identifies if this projectile can be picked up or not. */
	protected PickupPermission pickupType = PickupPermission.DISALLOWED;
	/**
	 * Defines the position this projectile is spawned at. Used for calculating the distance
	 * traveled by this projectile.
	 */
	protected Vec3 spawnPos = Vec3.ZERO;
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

	/**
	 * Constructs a new {@code TurretProjectileEntity} with the given {@code EntityType}
	 * and {@code World}.
	 *
	 * @param entityType The type of this entity.
	 * @param world The world this entity is in.
	 *
	 * @see EntityType
	 */
	protected TurretProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, Level world) {
		super(entityType, world);
	}

	/**
	 * Constructs a new {@code TurretProjectileEntity} with the given {@code EntityType},
	 * ({@code x}, {@code y}, {@code z}) coordinates, {@code World}, and the {@code ItemStack} this
	 * projectile comes from if it is throwable/usable.
	 *
	 * @param entityType The type of this entity.
	 * @param x The {@code X} coordinate this projectile is located.
	 * @param y The {@code Y} coordinate this projectile is located.
	 * @param z The {@code Z} coordinate this projectile is located.
	 * @param world The world this entity is in.
	 * @param stack The item stack used to spawn this projectile.
	 *
	 * @see EntityType
	 */
	protected TurretProjectileEntity(
            EntityType<? extends TurretProjectileEntity> entityType,
            double x,
            double y,
            double z,
            Level world,
            @Nullable ItemStack stack
	) {
		this(entityType, world);

		this.stack = stack;
		this.setPos(x, y, z);

		if (stack != null) {
			this.setCustomName(stack.get(DataComponents.CUSTOM_NAME));

			Unit unit = stack.remove(DataComponents.INTANGIBLE_PROJECTILE);
			if (unit != null) {
				this.pickupType = PickupPermission.CREATIVE_ONLY;
			}
		}
	}

	/**
	 * Constructs a new {@code TurretProjectileEntity} with the given {@code EntityType},
	 * {@code LivingEntity} owner, {@code World}, and the {@code ItemStack} this
	 * projectile comes from if it is throwable/usable.
	 *
	 * @param entityType The type of this entity.
	 * @param owner The living entity that owns this projectile.
	 * @param world The world this entity is in.
	 * @param stack The item stack used to spawn this projectile.
	 */
	protected TurretProjectileEntity(
            EntityType<? extends TurretProjectileEntity> entityType,
            @NotNull LivingEntity owner,
            @NotNull Level world,
            @Nullable ItemStack stack
	) {
		this(entityType, owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), world, stack);
		this.setOwner(owner);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //

	@Override
	protected void defineSynchedData(Builder builder) {
		builder.define(PROJECTILE_FLAGS, (byte) 0)
			.define(PIERCE_LEVEL, (byte) 0)
			.define(IN_GROUND, false);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //
	protected HitResult getZeroVelocityCollision() {
		AABB box = this.getBoundingBox()
			.inflate(0.2);

		EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
			this.level(),
			this,
			box.getMinPosition(),
			box.getMaxPosition(),
			box,
			this::canHitEntity
		);

		if (entityHitResult != null) {
			return entityHitResult;
		}

		return BlockHitResult.miss(
			this.position(),
			Direction.DOWN,
			BlockPos.containing(this.position())
		);
	}

	/**
	 * This method is called when the projectile hits an entity, handling the logic
	 * for hitting, damaging, and if applicable, piercing the entity.
	 * <br><br>
	 * In this implementation, the method will handle the damage calculation, the
	 * critical damage multiplier, the pierce behavior, and the fire behavior similar
	 * to how the {@link AbstractArrow} handles it.
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
	protected void onHitEntity(@NonNull EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);

		Entity hitEntity = entityHitResult.getEntity();
		Entity owner = this.getOwner();
		Level world = this.level();

		// Handles the damage calculation
		DamageSource dmgSrc = this.damageSources()
			.source(this.getDamageType(), this, owner != null ? owner : this);
		float velocityMagnitude = (float) this.getDeltaMovement().length();
		double damage = this.getDamage();
		int damageToDeal = Mth.ceil(
			this.speedAffectsDamage() ?
				Mth.clamp(
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
			livingEntity.setLastHurtMob(hitEntity);
		}

		// Handles the behavior of setting its target on fire if hit
		boolean isEnderman = hitEntity.getType().equals(EntityType.ENDERMAN);
		int fireTime = hitEntity.getRemainingFireTicks();
		if (this.isOnFire() && !isEnderman) {
			hitEntity.igniteForSeconds(5F);
		}

		// Handles the application of damage to the target (if it's not an explosive)
		if (!(this instanceof ExplosiveEntity)) {
			if (world instanceof ServerLevel serverWorld) {
				hitEntity.hurtServer(serverWorld, dmgSrc, (float) damageToDeal);

				if (isEnderman) return;

				if (hitEntity instanceof LivingEntity livingEntity) {
					if (!world.isClientSide() && this.getPierceLevel() <= 0) {
						livingEntity.setArrowCount(livingEntity.getArrowCount() + 1);
					}

					this.onHit(livingEntity);
				}

				this.playSound(this.sound , 1f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
				if (this.getPierceLevel() <= 0) {
					this.discard();
				}
			}
			else {
				hitEntity.hurtClient(dmgSrc);

				hitEntity.setRemainingFireTicks(fireTime);
				this.deflect(ProjectileDeflection.REVERSE, hitEntity, EntityReference.of(hitEntity), false);
				this.setDeltaMovement(this.getDeltaMovement().scale(0.2));

				if (world instanceof ServerLevel serverWorld && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
					if (this.pickupType == PickupPermission.ALLOWED && this.stack != null) {
						this.spawnAtLocation(serverWorld, this.asItemStack(), 0.1f);
					}

					this.discard();
				}
			}
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

				this.push(
					this.getDeltaMovement()
						.scale(reduction)
						.reverse()
				);
			}

			// If armor affects damage... (only if it's not an explosives)
			if (this.armorAffectsDamage() && !(this instanceof ExplosiveEntity)) {
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
	protected void onHitBlock(BlockHitResult blockHitResult) {
		Level world = this.level();
		this.inBlockState = world.getBlockState(blockHitResult.getBlockPos());
		super.onHitBlock(blockHitResult);

		Vec3 velocity = this.getDeltaMovement();
		Vec3 dirVector = new Vec3(
			Math.signum(velocity.x),
			Math.signum(velocity.y),
			Math.signum(velocity.z)
		);
		Vec3 embeddingAdjustment = dirVector.scale(0.05);

		this.shake = 7;
		this.setPos(this.position().subtract(embeddingAdjustment));
		this.setDeltaMovement(Vec3.ZERO);
		this.playSound(this.getSound(), 1f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
		this.setInGround(true);
		this.setCritical(false);
		this.setPierceLevel((byte) 0);
		this.setSound(this.getHitSound());
		this.clearPiercingStatus();
	}

	protected void onHit(LivingEntity target) {
	}

	@Override
	public void playerTouch(@NonNull Player player) {
		Level world = this.level();
		if (world.isClientSide() && (this.isInGround() || this.isNoClip()) && this.shake <= 0) {
			if (this.tryPickup(player)) {
				player.take(this, 1);
				this.discard();
			}
		}
	}

	protected void applyCollision(BlockHitResult blockHitResult) {
		while (true) {
			if (this.isAlive()) {
				Vec3 pos = this.position();
				EntityHitResult entityHitResult = this.getEntityCollision(pos, blockHitResult.getLocation());
				Vec3 nextPos = Objects.requireNonNullElse(entityHitResult, blockHitResult).getLocation();

				this.setPos(nextPos);
				this.applyEffectsFromBlocks(pos, nextPos);

				if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
					this.handlePortal();
				}

				if (entityHitResult == null) {
					if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS) {
						this.hitTargetOrDeflectSelf(blockHitResult);
						this.needsSync = true;
					}
				}
				else {
					if (!this.isAlive() || this.noPhysics) {
						continue;
					}

					ProjectileDeflection deflection = this.hitTargetOrDeflectSelf(entityHitResult);
					this.needsSync = true;

					if (this.getPierceLevel() > 0 && deflection == ProjectileDeflection.NONE) {
						continue;
					}
				}
			}

			return;
		}
	}

	protected void applyDrag() {
		Vec3 velocity = this.getDeltaMovement();
		float dragCoefficient = this.getDrag();

		if (this.isInWater()) {
			dragCoefficient = this.getDragInWater();
		}

		this.setDeltaMovement(velocity.scale(dragCoefficient));
	}

	protected void spawnBubbleParticles(Vec3 pos) {
		Vec3 velocity = this.getDeltaMovement();

		for (int i = 0; i < 4; i++) {
			float radius = 0.25F;
			this.level().addParticle(
				ParticleTypes.BUBBLE,
				pos.x - velocity.x * radius,
				pos.y - velocity.y * radius,
				pos.z - velocity.z * radius,
				velocity.x, velocity.y, velocity.z
			);
		}
	}

	protected void fall() {
		Vec3 velocity = this.getDeltaMovement();

		this.life = 0;
		this.setInGround(false);
		this.setDeltaMovement(
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
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d = this.getBoundingBox().getSize() * 10;
		if (Double.isNaN(d)) {
			d = 1;
		}

		d *= 64 * TurretProjectileEntity.getViewScale();
		d *= d;
		return distance < d;
	}

	@Override
	public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> data) {
		super.onSyncedDataUpdated(data);
		if (!this.firstTick && this.shake <= 0 && data.equals(IN_GROUND) && this.isInGround()) {
			this.shake = 7;
		}
	}

	@Nullable
	protected ItemStack asItemStack() {
		return this.stack == null ?
			null : this.stack.copy();
	}

	@Override
	public void addAdditionalSaveData(@NonNull ValueOutput view) {
		super.addAdditionalSaveData(view);

		view.putShort("life", (short) this.life);
		view.putByte("shake", (byte)this.shake);
		view.putBoolean("inGround", this.isInGround());
		view.putByte("pickup", (byte) this.pickupType.ordinal());
		view.putDouble("damage", this.damage);
		view.putBoolean("crit", this.isCritical());
		view.putByte("PierceLevel", this.getPierceLevel());

		Identifier soundId = BuiltInRegistries.SOUND_EVENT.getKey(this.sound);
		if (soundId != null) {
			view.putString("SoundEvent", soundId.toString());
		}

		if (this.stack != null) {
			view.store("item", ItemStack.CODEC, this.stack);
		}

		if (this.inBlockState != null) {
			view.storeNullable("inBlockState", BlockState.CODEC, this.inBlockState);
		}
	}

	@Override
	public void readAdditionalSaveData(@NonNull ValueInput view) {
		super.readAdditionalSaveData(view);

		this.life = view.getShortOr("life", (short) 0);
		this.shake = view.getByteOr("shake", (byte) 0) & 255;
		this.setInGround(view.getBooleanOr("inGround", false));
		this.damage = view.getDoubleOr("damage", 2.0F);
		this.pickupType = PickupPermission.fromOrdinal(view.getByteOr("pickup", (byte) 0));
		this.setCritical(view.getBooleanOr("crit", false));
		this.setPierceLevel(view.getByteOr("PierceLevel", (byte) 0));

		this.inBlockState = view.read("inBlockState", BlockState.CODEC)
			.orElse(null);

		this.setStack(
			view.read(
				"item",
				ItemStack.CODEC
			).orElse(
				this.getDefaultItemStack()
			)
		);
	}

	public void applyDamageModifier(float damageModifier) {
		this.setDamage(
			(double) (damageModifier * 2.0F)
				+ this.random.triangle(
					(double) this.level().getDifficulty().getId() * 0.11,
				0.57425
			)
		);
	}

	protected boolean tryPickup(Player player) {
		boolean didPickedUp = false;

		if (this.pickupType == PickupPermission.ALLOWED) {
			ItemStack stack = this.asItemStack();

			if (stack != null) {
				didPickedUp = player.getInventory()
					.add(stack);
			}
		}
		else if (this.pickupType == PickupPermission.CREATIVE_ONLY) {
			didPickedUp = player.hasInfiniteMaterials();
		}

		return didPickedUp;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			this.updateAnimations();
		}
	}

	// //////////////////////////////// //
	// QUESTION METHODS (True or False) //
	// //////////////////////////////// //

	/**
	 * Determines whether the speed of the projectile affects the damage it deals,
	 * similar to how {@link AbstractArrow} handles it.
	 *
	 * @return {@code true} if the speed affects the damage, {@code false} otherwise
	 *
	 * @see #speedAffectDamage
	 */
	public boolean speedAffectsDamage() {
		return this.speedAffectDamage;
	}

	protected boolean shouldFall() {
		Vec3 pos = this.position();
		return this.isInGround() && this.level().noCollision(new AABB(pos, pos).inflate(0.06));
	}

	@Override
	protected boolean canHitEntity(@NonNull Entity entity) {
		return super.canHitEntity(entity)
			&& (
			this.piercedEntities == null
				|| !this.piercedEntities.contains(entity.getId())
		);
	}

	@Override
	public boolean isPickable() {
		return super.isPickable() && !this.isInGround();
	}

	@Override
	public boolean isAttackable() {
		return RegistryHelper.isOf(this.getType(), EntityTypeTags.REDIRECTABLE_PROJECTILE);
	}

	@Override
	protected boolean shouldBounceOnWorldBorder() {
		return true;
	}

	// //////////// //
	// TRACKED DATA //
	// //////////// //

	protected void setInGround(boolean inGround) {
		this.entityData.set(IN_GROUND, inGround);
	}

	protected boolean isInGround() {
		return this.entityData.get(IN_GROUND);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	/**
	 * Returns this projectile's damage type. Used for when damaging entities for custom message
	 * and exhaustion.
	 *
	 * @return The damage type of this projectile.
	 *
	 * @implNote By default, this returns {@link DamageTypes#ARROW}, but it can be overridden to return a custom damage type if needed.
	 */
	public ResourceKey<DamageType> getDamageType() {
		return DamageTypes.ARROW;
	}

	/** Overridable method that defines the sound played when this projectile hits something. **/
	public SoundEvent getHitSound() {
		return SoundEvents.ARROW_HIT;
	}

	protected final SoundEvent getSound() {
		return this.sound;
	}

	public void setSound(SoundEvent sound) {
		this.sound = sound;
	}

	protected void setSpeedAffectsDamage(boolean speedAffectDamage) {
		this.speedAffectDamage = speedAffectDamage;
	}

	@Override
	protected double getDefaultGravity() {
		return 0.05;
	}

	protected float getDrag() {
		return 0.99F;
	}

	protected float getDragInWater() {
		return 0.6F;
	}

	public float getFinalDrag() {
		return this.wasEyeInWater ?
			this.getDragInWater() : this.getDrag();
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
	public void shoot(double x, double y, double z, float power, float uncertainty) {
		super.shoot(x, y, z, power, uncertainty);
		this.life = 0;

		double sqrdPosMagnitude = Mth.lengthSquared(x, y, z);
		if (this.isInGround() && sqrdPosMagnitude > 0.0) {
			this.setInGround(true);
		}
	}

	@Nullable
	protected EntityHitResult getEntityCollision(Vec3 currentPos, Vec3 nextPos) {
		return ProjectileUtil.getEntityHitResult(
			this.level(),
			this,
			currentPos, nextPos,
			this.getBoundingBox()
				.expandTowards(this.getDeltaMovement())
				.inflate(1.0),
			this::canHitEntity
		);
	}

	@Override
	protected @NonNull MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setCritical(boolean critical) {
		this.setProjectileFlag(CRITICAL_FLAG, critical);
	}

	public byte getPierceLevel() {
		return this.entityData.get(PIERCE_LEVEL);
	}

	protected void setPierceLevel(byte level) {
		this.entityData.set(PIERCE_LEVEL, level);
	}

	private void setProjectileFlag(int index, boolean flag) {
		byte projectileFlags = this.entityData.get(PROJECTILE_FLAGS);
		if (flag) {
			this.entityData.set(PROJECTILE_FLAGS, (byte) (projectileFlags | index));
		} else {
			this.entityData.set(PROJECTILE_FLAGS, (byte) (projectileFlags & ~index));
		}
	}

	public boolean isCritical() {
		byte projectileFlags = this.entityData.get(PROJECTILE_FLAGS);
		return (projectileFlags & 1) != 0;
	}

	public void setNoClip(boolean noClip) {
		this.noPhysics = noClip;
		this.setProjectileFlag(NO_CLIP_FLAG, noClip);
	}

	public boolean isNoClip() {
		return !this.level().isClientSide() ? this.noPhysics : (this.entityData.get(PROJECTILE_FLAGS) & 2) != 0;
	}

	@Override
	public SlotAccess getSlot(int mappedIndex) {
		return mappedIndex == 0 ? SlotAccess.of(this::getItemStack, this::setStack) : super.getSlot(mappedIndex);
	}

	@Override
	public void setOwner(Entity owner) {
		super.setOwner(owner);

		if (owner instanceof TurretEntity turret) {
			this.setDamage(turret.getProjectileDamage());
			this.setPierceLevel(turret.getProjectilePierceLevel());
		}
	}

	public void setSpawnPos(double x, double y, double z) {
		this.setSpawnPos(new Vec3(x, y, z));
	}

	public void setSpawnPos(Vec3 spawnPos) {
		this.spawnPos = spawnPos;
		this.setPos(spawnPos);
		this.moveOrInterpolateTo(spawnPos);
	}

	public Vec3 getSpawnPos() {
		return new Vec3(
			this.spawnPos.x(),
			this.spawnPos.y(),
			this.spawnPos.z()
		);
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	/**
	 * Retrieves the looping animation state of this turret projectile.
	 *
	 * @return {@code AnimationState} The looping animation state of this turret.
	 */
	public AnimationState getLoopAnimationState() {
		return this.loopAnimationState;
	}

	// ////////////////////////////// //
	// ABSTRACT & OVERRIDABLE METHODS //
	// ////////////////////////////// //

	// ABSTRACT METHODS //

	/**
	 * Defines the logic on how to move the projectile. This includes what happens when the
	 * projectile hits a block and/or an entity.
 	 */
	protected abstract void move();

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

	// OVERRIDABLES //

	@Environment(EnvType.CLIENT)
	protected void updateAnimations() {
		this.loopAnimationState.startIfStopped(this.tickCount);
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
			int armor = entity.getArmorValue();

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
