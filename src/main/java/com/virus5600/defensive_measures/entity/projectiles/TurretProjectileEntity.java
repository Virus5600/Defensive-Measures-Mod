package com.virus5600.defensive_measures.entity.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.Objects;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

/**
 * {@code TurretProjectile} is an abstract class that acts nearly akin to {@link PersistentProjectileEntity}
 * but with more support towards entity based projectiles and more control over some aspects of the
 * projectile such as its damage, piercing, and more.
 * <br><br>
 * For a more basic projectile entity or one that can be shot by a player, use the {@link PersistentProjectileEntity}
 * class.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class TurretProjectileEntity extends ProjectileEntity {
	protected static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(TurretProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(TurretProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Boolean> IN_GROUND = DataTracker.registerData(TurretProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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
	protected Vec3d spawnPos = Vec3d.ZERO;
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
	protected TurretProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, World world) {
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
	protected HitResult getZeroVelocityCollision() {
		Box box = this.getBoundingBox()
			.expand(0.2);

		EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(
			this.getEntityWorld(),
			this,
			box.getMinPos(),
			box.getMaxPos(),
			box,
			this::canHit
		);

		if (entityHitResult != null) {
			return entityHitResult;
		}

		return BlockHitResult.createMissed(
			this.getEntityPos(),
			Direction.DOWN,
			BlockPos.ofFloored(this.getEntityPos())
		);
	}

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
		World world = this.getEntityWorld();

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
		if (world instanceof ServerWorld serverWorld) {
			hitEntity.damage(serverWorld, dmgSrc, (float) damageToDeal);

			if (isEnderman) return;

			if (hitEntity instanceof LivingEntity livingEntity) {
				if (!world.isClient() && this.getPierceLevel() <= 0) {
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
			hitEntity.clientDamage(dmgSrc);

			hitEntity.setFireTicks(fireTime);
			this.deflect(ProjectileDeflection.SIMPLE, hitEntity, LazyEntityReference.of(hitEntity), false);
			this.setVelocity(this.getVelocity().multiply(0.2));

			if (world instanceof ServerWorld serverWorld && this.getVelocity().lengthSquared() < 1.0E-7) {
				if (this.pickupType == PickupPermission.ALLOWED && this.stack != null) {
					this.dropStack(serverWorld, this.asItemStack(), 0.1f);
				}

				this.discard();
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
		World world = this.getEntityWorld();
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
		this.setPosition(this.getEntityPos().subtract(embeddingAdjustment));
		this.setVelocity(Vec3d.ZERO);
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
	public void onPlayerCollision(PlayerEntity player) {
		World world = this.getEntityWorld();
		if (world.isClient() && (this.isInGround() || this.isNoClip()) && this.shake <= 0) {
			if (this.tryPickup(player)) {
				player.sendPickup(this, 1);
				this.discard();
			}
		}
	}

	protected void applyCollision(BlockHitResult blockHitResult) {
		while (true) {
			if (this.isAlive()) {
				Vec3d pos = this.getEntityPos();
				EntityHitResult entityHitResult = this.getEntityCollision(pos, blockHitResult.getPos());
				Vec3d nextPos = Objects.requireNonNullElse(entityHitResult, blockHitResult).getPos();

				this.setPosition(nextPos);
				this.tickBlockCollision(pos, nextPos);

				if (this.portalManager != null && this.portalManager.isInPortal()) {
					this.tickPortalTeleportation();
				}

				if (entityHitResult == null) {
					if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS) {
						this.hitOrDeflect(blockHitResult);
						this.velocityDirty = true;
					}
				}
				else {
					if (!this.isAlive() || this.noClip) {
						continue;
					}

					ProjectileDeflection deflection = this.hitOrDeflect(entityHitResult);
					this.velocityDirty = true;

					if (this.getPierceLevel() > 0 && deflection == ProjectileDeflection.NONE) {
						continue;
					}
				}
			}

			return;
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
			this.getEntityWorld().addParticleClient(
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
	public void writeCustomData(WriteView view) {
		super.writeCustomData(view);

		view.putShort("life", (short) this.life);
		view.putByte("shake", (byte)this.shake);
		view.putBoolean("inGround", this.isInGround());
		view.putByte("pickup", (byte) this.pickupType.ordinal());
		view.putDouble("damage", this.damage);
		view.putBoolean("crit", this.isCritical());
		view.putByte("PierceLevel", this.getPierceLevel());

		Identifier soundId = Registries.SOUND_EVENT.getId(this.sound);
		if (soundId != null) {
			view.putString("SoundEvent", soundId.toString());
		}

		if (this.stack != null) {
			view.put("item", ItemStack.CODEC, this.stack);
		}

		if (this.inBlockState != null) {
			view.putNullable("inBlockState", BlockState.CODEC, this.inBlockState);
		}
	}

	@Override
	public void readCustomData(ReadView view) {
		super.readCustomData(view);

		this.life = view.getShort("life", (short) 0);
		this.shake = view.getByte("shake", (byte) 0) & 255;
		this.setInGround(view.getBoolean("inGround", false));
		this.damage = view.getDouble("damage", 2.0F);
		this.pickupType = PickupPermission.fromOrdinal(view.getByte("pickup", (byte) 0));
		this.setCritical(view.getBoolean("crit", false));
		this.setPierceLevel(view.getByte("PierceLevel", (byte) 0));

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
				+ this.random.nextTriangular(
					(double) this.getEntityWorld().getDifficulty().getId() * 0.11,
				0.57425
			)
		);
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
	public void tick() {
		super.tick();

		this.updateAnimations();
	}

	// //////////////////////////////// //
	// QUESTION METHODS (True or False) //
	// //////////////////////////////// //

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

	protected boolean shouldFall() {
		Vec3d pos = this.getEntityPos();
		return this.isInGround() && this.getEntityWorld().isSpaceEmpty(new Box(pos, pos).expand(0.06));
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

	@Override
	public boolean isAttackable() {
		return this.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE);
	}

	@Override
	protected boolean deflectsAgainstWorldBorder() {
		return true;
	}

	// //////////// //
	// TRACKED DATA //
	// //////////// //

	protected void setInGround(boolean inGround) {
		this.dataTracker.set(IN_GROUND, inGround);
	}

	protected boolean isInGround() {
		return this.dataTracker.get(IN_GROUND);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

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

		double sqrdPosMagnitude = MathHelper.squaredMagnitude(x, y, z);
		if (this.isInGround() && sqrdPosMagnitude > 0.0) {
			this.setInGround(true);
		}
	}

	@Nullable
	protected EntityHitResult getEntityCollision(Vec3d currentPos, Vec3d nextPos) {
		return ProjectileUtil.getEntityCollision(
			this.getEntityWorld(),
			this,
			currentPos, nextPos,
			this.getBoundingBox()
				.stretch(this.getVelocity())
				.expand(1.0),
			this::canHit
		);
	}

	@Override
	protected MoveEffect getMoveEffect() {
		return MoveEffect.NONE;
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
		return this.dataTracker.get(PIERCE_LEVEL);
	}

	protected void setPierceLevel(byte level) {
		this.dataTracker.set(PIERCE_LEVEL, level);
	}

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
		return !this.getEntityWorld().isClient() ? this.noClip : (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return mappedIndex == 0 ? StackReference.of(this::getItemStack, this::setStack) : super.getStackReference(mappedIndex);
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
		this.setSpawnPos(new Vec3d(x, y, z));
	}

	public void setSpawnPos(Vec3d spawnPos) {
		this.spawnPos = spawnPos;
		this.setPosition(spawnPos);
		this.updateTrackedPosition(spawnPos);
	}

	public Vec3d getSpawnPos() {
		return new Vec3d(
			this.spawnPos.getX(),
			this.spawnPos.getY(),
			this.spawnPos.getZ()
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
		this.loopAnimationState.startIfNotRunning(this.age);
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
