package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.entity.projectiles.interfaces.CloudAction;
import com.virus5600.defensive_measures.entity.turrets.tier2.FlameTurretEntity;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 *{@code AreaCloudEntity} is an abstract class that extends {@link TurretProjectileEntity}. This
 * class is used to represent "projectiles" that are "spewed" out by turrets that lingers or acts like
 * some gas that damages or applies some stuff to an entity within a certain radius. This class
 * is used to represent gas attacks such as {@link FlammableAerosolEntity flammable aerosol} (from
 * {@link FlameTurretEntity} and similar gasses.
 * <br><br>
 * By default, the gas "projectile" entity has the following behavior:
 * <ul>
 *     <li>Armor points gets ignored.</li>
 *     <li>The gas does not affect the entity (Requires {@link CloudAction} for behavior).</li>
 *     <li>Cloud gets large overtime.</li>
 *     <li>Hardcoded to have a minimum size of 0.</li>
 *     <li>Hardcoded to have a maximum size of 32.</li>
 *     <li>Does nothing.</li>
 * </ul>
 * <hr/>
 * <h2>Damage Mechanics</h2>
 * The mechanics of applying damage to an entity is not handled by this class and is instead
 * handled by the {@link CloudAction} assigned to the cloud. This is to allow for more flexible and
 * customizable behavior of the cloud, allowing easier implementation of inherited classes with
 * different behaviors and mechanics. In addition to those, the cloud also has a cooldown system
 * that prevents the cloud from applying its effect on an entity every tick, allowing for more
 * balanced and fair mechanics when it comes to the cloud applying its effect on an entity. The
 * cooldown system is a simple map that maps an entity to the tick it can be affected again,
 * allowing for easy implementation of cooldown mechanics for the cloud's effect on entities.
 * <br><br>
 * The cloud also has the ability to apply penalties to itself whenever it applies its effect on an
 * entity, allowing for more dynamic and interactive mechanics when it comes to the cloud applying
 * its effect on an entity. The penalties can be in the form of radius reduction, duration
 * reduction, or both, allowing for more strategic and tactical gameplay when it comes to the cloud
 * applying its effect on an entity. The penalties are applied after the cloud applies its effect
 * on an entity, allowing for the cloud to have a chance to apply its effect on an entity before it
 * gets penalized, allowing for more fair and balanced mechanics when it comes to the cloud
 * applying its effect on an entity.
 *
 * @see TurretProjectileEntity
 * @see KineticProjectileEntity
 * @see ExplosiveProjectileEntity
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class AreaCloudEntity extends TurretProjectileEntity {
	private static final EntityDataAccessor<Float> RADIUS;
	private static final EntityDataAccessor<Boolean> WAITING;
	private static final EntityDataAccessor<ParticleOptions> PARTICLE;

	protected static final ParticleOptions DEFAULT_PARTICLE = ParticleTypes.GUST;

	private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();

	private CloudAction cloudAction;
	private @Nullable ParticleOptions customParticle;
	/**
	 * The target radius of the cloud. This is used to define the radius that the cloud will try to
	 * reach when it grows or shrinks.<br>
	 * <br>
	 * Using this alongside with {@link #targetAge} will result in whichever value is reached first.
	 */
	private float targetRadius = 3f;
	/**
	 * The radius amount the cloud will grow (positive value) or shrink (negative value) every
	 * tick.
	 */
	private float radiusGrowth = 0f;
	/**
	 * The radius that will be used up when the behavior is applied to a target. Using a positive
	 * value will reduce the radius while using a negative value will increase the radius.
	 */
	private float radiusOnUse = 0f;
	/**
	 * The target age of the cloud. This is used to define the age that the cloud will try to reach
	 * when it grows or shrink.<br>
	 * <br>
	 * Using this alongside with {@link #targetRadius} will result in whichever value is reached
	 * first.
	 */
	private int targetAge = 20;
	/**
	 * The duration that will be deducted when the behavior is applied to a target. Using a positive
	 * value will reduce the duration while using a negative value will increase the duration.
	 */
	private int durationOnUse = 0;
	/** The duration in ticks. */
	private int duration = -1;
	/** Time (in ticks) before the cloud activates. */
	private int waitTime = 0;
	/** Duration (in ticks) before the behavior ran on a target. */
	private int reApplicationDelay = 20;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public AreaCloudEntity(EntityType<? extends AreaCloudEntity> entityType, Level world) {
		super(entityType, world);

		if (this.cloudAction == null) {
			this.cloudAction = (_, target) -> target.push(
				0,
				0.1,
				0
			);
		}
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void defineSynchedData(@NonNull Builder builder) {
		super.defineSynchedData(builder);

		builder.define(RADIUS, 3f)
			.define(WAITING, false)
			.define(PARTICLE, DEFAULT_PARTICLE);
	}

	// //////////// //
	// TRACKED DATA //
	// //////////// //

	public void setRadius(float radius) {
		if (!this.level().isClientSide()) {
			radius = Mth.clamp(radius, 0f, 32f);
			this.getEntityData().set(RADIUS, radius);
		}
	}

	public void refreshDimensions() {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		super.refreshDimensions();

		this.setPos(x, y, z);
	}

	public float getRadius() {
		return this.getEntityData().get(RADIUS);
	}

	protected void setWaiting(boolean waiting) {
		this.getEntityData().set(WAITING, waiting);
	}

	public boolean isWaiting() {
		return this.getEntityData().get(WAITING);
	}

	public void setParticleType(@Nullable ParticleOptions customParticle) {
		this.customParticle = customParticle;

		if (this.customParticle != null) {
			this.entityData.set(PARTICLE, this.customParticle);
		}
		else {
			this.entityData.set(PARTICLE, DEFAULT_PARTICLE);
		}
	}

	public ParticleOptions getParticleType() {
		return this.getEntityData().get(PARTICLE);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	/**
	 * Identifies the default particle for a given instance. Override to provide a different particle
	 * for different types of clouds.
	 *
	 * @return the default particle for this cloud instance.
	 */
	protected ParticleOptions getDefaultParticle() {
		return DEFAULT_PARTICLE;
	}

	@Override
	protected double getDefaultGravity() {
		return 0.025;
	}

	@Override
	protected float getDrag() {
		return 0.875F;
	}

	protected float getDragInWater() {
		return 0.06125F;
	}


	/**
	 * Sets the action or behavior the cloud will use when it affects an entity. This is used to
	 * define the behavior of the cloud when it applies its effect on an entity, allowing for more
	 * flexible and customizable behavior of the cloud.
	 *
	 * @param cloudAction the action or behavior the cloud will use.
	 */
	protected void setCloudAction(CloudAction cloudAction) {
		this.cloudAction = cloudAction;
	}

	public CloudAction getCloudAction() {
		return this.cloudAction;
	}

	/**
	 * Sets the duration of the cloud in ticks. Once the cloud has existed for longer than its
	 * duration, it will be discarded. Setting the duration to -1 will make the cloud last
	 * indefinitely until it's discarded by other means (e.g. radius growth, radius on use, etc.).
	 * <br><br>
	 * By default, the duration is set to -1, making the cloud last indefinitely until it's
	 * discarded by other means.
	 *
	 * @param duration the duration of the cloud in ticks. Set to -1 for infinite duration.
	 * @throws IllegalArgumentException if the duration is less than -1.
	 */
	public void setDuration(int duration) {
		if (duration < -1) {
			throw new IllegalArgumentException("Duration must be >= -1");
		}

		this.duration = duration;
	}

	public int getDuration() {
		return this.duration;
	}

	/**
	 * Sets the target radius of the cloud. This is used to define the radius that the cloud will
	 * try to reach when it grows or shrinks. Setting the target radius to the current radius
	 * will make the cloud disappear immediately.
	 * <br><br>
	 * By default, the target radius is set to 3, making the cloud try to reach a radius of 3 when
	 * it grows or shrinks.
	 * <br><br>
	 * Setting the target radius to a value less than 0.5 or more than 32 will make the cloud be
	 * discarded once it reaches that radius.
	 *
	 * @param targetRadius the target radius of the cloud. Setting to current radius will make the cloud static in terms of radius change.
	 */
	public void setTargetRadius(float targetRadius) {
		targetRadius = Mth.clamp(targetRadius, 0f, 32f);

		this.targetRadius = targetRadius;
	}

	public float getTargetRadius() {
		return this.targetRadius;
	}

	/**
	 * Sets the target age of the cloud (in ticks). This is used to define the age that the cloud
	 * will try to reach when it grows or shrinks. Setting the target age to the current age
	 * will make the cloud disappear immediately.
	 * <br><br>
	 * By default, the target age is set to 20, making the cloud try to reach an age of 20 ticks
	 * when it grows or shrinks.
	 * <br><br>
	 * Setting the target age to a value less than 0 will make the cloud not check the age while
	 * setting it to a positive value will make the cloud be discarded once it reaches that age.
	 *
	 * @param targetAge the target age of the cloud in ticks.
	 */
	public void setTargetAge(int targetAge) {
		targetAge = Mth.clamp(targetAge, -1, Integer.MAX_VALUE);

		this.targetAge = targetAge;
	}

	public int getTargetAge() {
		return this.targetAge;
	}

	/**
	 * Sets the time (in ticks) before the cloud activates and starts applying its effect on
	 * entities.
	 * <br><br>
	 * Setting the wait time to 0 will make the cloud activate immediately, while setting it to a
	 * positive value will make the cloud wait for that many ticks before activating.
	 *
	 * @param waitTime the time (in ticks) before the cloud activates. Setting to 0 will make the cloud activate immediately.
	 * @throws IllegalArgumentException if the wait time is negative.
	 */
	public void setWaitTime(int waitTime) {
		if (waitTime < -1) {
			throw new IllegalArgumentException("WaitTime must be >= -1");
		}

		this.waitTime = waitTime;
	}

	public int getWaitTime() {
		return this.waitTime;
	}

	/**
	 * Sets the radius amount the cloud will grow (positive value) or shrink (negative value) every
	 * tick.
	 * <br><br>
	 * Setting the radius growth to 0 will make the cloud not change its radius over time and thus,
	 * will left to linger indefinitely, requiring an external way to manually remove this entity.
	 *
	 * @param radiusGrowth the radius amount the cloud will grow (positive value) or shrink (negative value) every tick.
	 */
	public void setRadiusGrowth(float radiusGrowth) {
		this.radiusGrowth = radiusGrowth;
	}

	public float getRadiusGrowth() {
		return this.radiusGrowth;
	}

	/**
	 * Sets the amount of radius that will be used up when the cloud applies its effect on an
	 * entity. This is used to define the radius penalty of the cloud when it applies its effect on
	 * an entity.
	 * <br><br>
	 * Setting the radius on use to 0 will prevent the cloud from having a penalty when it applies
	 * its effect on its target. A positive value will reduce the radius while using a negative
	 * value will increase the radius.
	 *
	 * @param radiusOnUse the amount radius that will be used up when the cloud applies its effect on an entity.
	 */
	public void setRadiusOnUse(float radiusOnUse) {
		this.radiusOnUse = radiusOnUse;
	}

	public float getRadiusOnUse() {
		return this.radiusOnUse;
	}

	/**
	 * Sets the amount of duration that will be used up when the cloud applies its effect on an
	 * entity. This is used to define the duration penalty of the cloud when it applies its effect
	 * on an entity.
	 * <br><br>
	 * Setting the duration on use to 0 will prevent the cloud from having a penalty when it
	 * applies its effect on its target. A positive value will reduce the duration while using a
	 * negative value will increase the duration.
	 *
	 * @param durationOnUse the amount of duration that will be used up when the cloud applies its effect on an entity.
	 */
	public void setDurationOnUse(int durationOnUse) {
		this.durationOnUse = durationOnUse;
	}

	public int getDurationOnUse() {
		return this.durationOnUse;
	}

	/**
	 * Sets the amount of the cooldown (in ticks) that will be applied to an entity before another
	 * instance of effect/behavior can be applied to a target. Values lower than 0 will be converted
	 * into positive numbers as having a negative cooldown does not make sense.
	 *
	 * @param delay the amount of the cooldown (in ticks) that will be applied to an entity before another instance of effect/behavior can be applied to a target.
	 */
	public void setReApplicationDelay(int delay) {
		this.reApplicationDelay = Mth.abs(delay);
	}

	public int getReApplicationDelay() {
		return this.reApplicationDelay;
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	protected boolean isEntityInside(@NonNull LivingEntity target) {
		double dX = target.getX() - this.getX();
		double dZ = target.getZ() - this.getZ();
		float currentRadius = this.getRadius(); // Assuming you have a getter for the radius

		return (dX * dX + dZ * dZ) <= (double) (currentRadius * currentRadius);
	}

	protected void applyUsePenalty() {
		// 1. Radius penalty
		if (this.getRadiusOnUse() != 0.0F) {
			float newRadius = this.getRadius() + this.getRadiusOnUse();

			if (newRadius < 0.5F) {
				this.discard();
				return;
			}

			this.setRadius(newRadius); // Assuming you have a setter for the radius
		}

		// 2. Duration penalty
		if (this.getDurationOnUse() != 0 && this.getDuration() != -1) {
			this.duration += this.getDurationOnUse();

			if (this.getDuration() <= 0) {
				this.discard();
			}
		}
	}

	protected void processEntity(LivingEntity target) {
		// Check if the entity is currently off cooldown
		if (!this.affectedEntities.containsKey(target)) {

			// Put the entity on cooldown
			this.affectedEntities.put(target, this.tickCount + this.getReApplicationDelay());

			// EXECUTE CUSTOM SCRIPT: Apply fire, cryo, etc.
			if (this.getCloudAction() != null) {
				this.getCloudAction().execute(this, target);
			}

			// Apply structural penalties (optional for inheritors)
			this.applyUsePenalty();
		}
	}

	@Override
	protected void move() {
		this.applyDrag();

		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, ClipContext.Block.COLLIDER);

		Vec3 pos = this.position();
		if (hitResult.getType() != HitResult.Type.MISS) {
			pos = hitResult.getLocation();
		} else {
			pos = pos.add(this.getDeltaMovement());
		}

		ProjectileUtil.rotateTowardsMovement(this, 0.2F);

		this.setPos(pos);
		this.applyEffectsFromBlocks();
		this.applyGravity();
	}

	@Override
	public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
		return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
	}

	@Override
	public @NonNull PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> data) {
		if (RADIUS.equals(data)) {
			this.refreshDimensions();
		}

		super.onSyncedDataUpdated(data);
	}

	@Override
	public void tick() {
		this.move();
		super.tick();

		if (this.level() instanceof ServerLevel serverWorld) {
			this.serverTick(serverWorld);
		}
		else {
			this.clientTick();
		}
	}

	/**
	 * Gets called every tick on the client side. Responsible for spawning particles around the
	 * cloud.
	 */
	protected void clientTick() {
		boolean isWaiting = this.isWaiting();
		float cloudRadius = this.getRadius();

		if (!isWaiting || !this.random.nextBoolean()) {
			ParticleOptions particleEffect = this.getParticleType();
			Level world = this.level();

			int cloudArea;
			float currentCloudRadius;

			if (isWaiting) {
				cloudArea = 2;
				currentCloudRadius = 0.2F;
			}
			else {
				cloudArea = Mth.ceil((float) Math.PI * cloudRadius * cloudRadius);
				currentCloudRadius = cloudRadius;
			}

			for (int i = 0; i < cloudArea; ++i) {
				float theta = this.random.nextFloat() * ((float) Math.PI * 2F);
				float targetRadPos = Mth.sqrt(this.random.nextFloat()) * currentCloudRadius;

				double x = this.getX() + (double) (Mth.cos(theta) * targetRadPos);
				double y = this.getY();
				double z = this.getZ() + (double) (Mth.sin(theta) * targetRadPos);

				if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
					if (isWaiting && this.random.nextBoolean()) {
						world.addAlwaysVisibleParticle(
							DEFAULT_PARTICLE,
							x, y, z,
							0.0F, 0.0F, 0.0F
						);
					}
					else {
						world.addAlwaysVisibleParticle(
							particleEffect,
							x, y, z,
							0.0F, 0.0F,  0.0F
						);
					}
				}
				else if (isWaiting) {
					world.addAlwaysVisibleParticle(
						particleEffect,
						x, y, z,
						0.0F, 0.0F, 0.0F
					);
				}
				else {
					world.addAlwaysVisibleParticle(
						particleEffect,
						x, y, z,
						((double) 0.5F - this.random.nextDouble()) * 0.15,
						0.01,
						((double) 0.5F - this.random.nextDouble()) * 0.15
					);
				}
			}
		}
	}

	/**
	 * Gets called every tick on the server side. Responsible for ...
	 *
	 * @param world the world this entity is in
	 */
	protected void serverTick(ServerLevel world) {
		// Discards the entity if it has existed for longer than its duration (if it has a duration)
		// or if it has reached its target radius.
		if ((this.getDuration() != -1 && this.tickCount - this.getWaitTime() >= this.getDuration()) ||
			(Mth.abs(this.getRadius()) >= Math.abs(this.getTargetRadius()))
		) {
			this.discard();
		}
		// Otherwise, proceed to handle radius change only.
		// Behavior will be handled by the inheriting class, but the radius change is handled here
		// since it's common for all area clouds.
		else {
			boolean isWaiting = this.isWaiting();
			boolean stillWaiting = this.tickCount < this.getWaitTime();

			// Update waiting status
			if (isWaiting != stillWaiting) {
				this.setWaiting(stillWaiting);
			}

			// If the cloud is not waiting anymore, proceed to update radius and affect entities.
			if (!stillWaiting) {
				float radius = this.getRadius();

				if (this.getRadiusGrowth() != 0.0F) {
					radius += this.getRadiusGrowth();

					// Makes sure that the cloud is discarded once it reaches its minimum or
					// maximum size, or when the age reaches its target age (if it has a target age).
					if ((radius < 0.5F || radius > 32F) ||
						(this.getTargetAge() > 0 && this.tickCount > this.getTargetAge())
					) {
						this.discard();
						return;
					}

					this.setRadius(radius);
				}
				else {
					if (this.getTargetAge() > 0 && this.tickCount > this.getTargetAge()) {
						this.discard();
					}
				}

				// 1. Clean the global cooldown map
				this.affectedEntities
					.entrySet()
					.removeIf((entry) -> this.tickCount >= entry.getValue());

				// 2. Grab everything in the general area
				List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());

				// 3. Process them cleanly
				for (LivingEntity target : targets) {
					if (this.isEntityInside(target)) {
						this.processEntity(target);
					}
				}
			}
		}
	}

	// /////////////////////////////// //
	// CUSTOM DATA READING AND WRITING //
	// /////////////////////////////// //
	@Override
	public void readAdditionalSaveData(@NonNull ValueInput view) {
		this.targetAge = view.getIntOr("TargetAge", this.targetAge);
		this.tickCount = view.getIntOr("Age", 0);
		this.duration = view.getIntOr("Duration", this.duration);
		this.waitTime = view.getIntOr("WaitTime", this.waitTime);
		this.reApplicationDelay = view.getIntOr("ReApplicationDelay", this.reApplicationDelay);
		this.durationOnUse = view.getIntOr("DurationOnUse", this.durationOnUse);
		this.targetRadius = view.getFloatOr("TargetRadius", this.targetRadius);
		this.radiusOnUse = view.getFloatOr("RadiusOnUse", this.radiusOnUse);
		this.radiusGrowth = view.getFloatOr("RadiusPerTick", this.radiusGrowth);
		this.setRadius(view.getFloatOr("Radius", this.getRadius()));
		this.owner = EntityReference.read(view, "Owner");

		this.setParticleType(
			view.read("custom_particle", ParticleTypes.CODEC)
				.orElse(this.getDefaultParticle())
		);
	}

	@Override
	public void addAdditionalSaveData(@NonNull ValueOutput view) {
		view.putInt("TargetAge", this.targetAge);
		view.putInt("Age", this.tickCount);
		view.putInt("Duration", this.duration);
		view.putInt("WaitTime", this.waitTime);
		view.putInt("ReapplicationDelay", this.reApplicationDelay);
		view.putInt("DurationOnUse", this.durationOnUse);
		view.putFloat("TargetRadius", this.targetRadius);
		view.putFloat("RadiusOnUse", this.radiusOnUse);
		view.putFloat("RadiusPerTick", this.radiusGrowth);
		view.putFloat("Radius", this.getRadius());
		EntityReference.store(this.owner, view, "Owner");
		view.storeNullable("custom_particle", ParticleTypes.CODEC, this.customParticle);
	}

	// ///////////// //
	// FINAL METHODS //
	// ///////////// //

	@Override
	public final boolean hurtServer(@NonNull ServerLevel world, @NonNull DamageSource source, float amount) {
		return false;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public byte getMaxPierceLevel() {
		return 0;
	}

	@Override
	public boolean armorAffectsPiercing() {
		return false;
	}

	@Override
	public boolean armorAffectsVelocity() {
		return false;
	}

	@Override
	public boolean armorAffectsDamage() {
		return false;
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //
	static {
		RADIUS = SynchedEntityData.defineId(AreaCloudEntity.class, EntityDataSerializers.FLOAT);
		WAITING = SynchedEntityData.defineId(AreaCloudEntity.class, EntityDataSerializers.BOOLEAN);
		PARTICLE = SynchedEntityData.defineId(AreaCloudEntity.class, EntityDataSerializers.PARTICLE);
	}
}
