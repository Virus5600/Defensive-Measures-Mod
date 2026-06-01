package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.turrets.tier2.FlameTurretEntity;
import com.virus5600.defensive_measures.entity.projectiles.interfaces.CloudAction;

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
	private static final TrackedData<Float> RADIUS;
	private static final TrackedData<Boolean> WAITING;
	private static final TrackedData<ParticleEffect> PARTICLE;

	protected static final ParticleEffect DEFAULT_PARTICLE = ParticleTypes.GUST;

	private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();

	private CloudAction cloudAction;
	private @Nullable ParticleEffect customParticle;
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
	public AreaCloudEntity(EntityType<? extends AreaCloudEntity> entityType, World world) {
		super(entityType, world);

		if (this.cloudAction == null) {
			this.cloudAction = (cloud, target) -> {
				target.addVelocity(
					0,
					0.1,
					0
				);

				target.fallDistance = 0f;
			};
		}
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void initDataTracker(@NonNull Builder builder) {
		super.initDataTracker(builder);

		builder.add(RADIUS, 3f)
			.add(WAITING, false)
			.add(PARTICLE, DEFAULT_PARTICLE);
	}

	// //////////// //
	// TRACKED DATA //
	// //////////// //

	public void setRadius(float radius) {
		if (!this.getEntityWorld().isClient()) {
			radius = MathHelper.clamp(radius, 0f, 32f);
			this.getDataTracker().set(RADIUS, radius);
		}
	}

	public void calculateDimensions() {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		super.calculateDimensions();

		this.setPosition(x, y, z);
	}

	public float getRadius() {
		return this.getDataTracker().get(RADIUS);
	}

	protected void setWaiting(boolean waiting) {
		this.getDataTracker().set(WAITING, waiting);
	}

	public boolean isWaiting() {
		return this.getDataTracker().get(WAITING);
	}

	public void setParticleType(@Nullable ParticleEffect customParticle) {
		this.customParticle = customParticle;

		if (this.customParticle != null) {
			this.dataTracker.set(PARTICLE, this.customParticle);
		}
		else {
			this.dataTracker.set(PARTICLE, DEFAULT_PARTICLE);
		}
	}

	public ParticleEffect getParticleType() {
		return this.getDataTracker().get(PARTICLE);
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
	protected ParticleEffect getDefaultParticle() {
		return DEFAULT_PARTICLE;
	}

	@Override
	protected double getGravity() {
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
		targetRadius = MathHelper.clamp(targetRadius, 0f, 32f);

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
		targetAge = MathHelper.clamp(targetAge, -1, Integer.MAX_VALUE);

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
		this.reApplicationDelay = MathHelper.abs(delay);
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
			this.affectedEntities.put(target, this.age + this.getReApplicationDelay());

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

		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, RaycastContext.ShapeType.COLLIDER);

		Vec3d pos = this.getEntityPos();
		if (hitResult.getType() != HitResult.Type.MISS) {
			pos = hitResult.getPos();
		} else {
			pos = pos.add(this.getVelocity());
		}

		ProjectileUtil.setRotationFromVelocity(this, 0.2F);

		this.setPosition(pos);
		this.tickBlockCollision();
		this.applyGravity();
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return EntityDimensions.changing(this.getRadius() * 2.0F, 0.5F);
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (RADIUS.equals(data)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void tick() {
		this.move();
		super.tick();

		if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
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
			ParticleEffect particleEffect = this.getParticleType();
			World world = this.getEntityWorld();

			int cloudArea;
			float currentCloudRadius;

			if (isWaiting) {
				cloudArea = 2;
				currentCloudRadius = 0.2F;
			}
			else {
				cloudArea = MathHelper.ceil((float) Math.PI * cloudRadius * cloudRadius);
				currentCloudRadius = cloudRadius;
			}

			for (int i = 0; i < cloudArea; ++i) {
				float theta = this.random.nextFloat() * ((float) Math.PI * 2F);
				float targetRadPos = MathHelper.sqrt(this.random.nextFloat()) * currentCloudRadius;

				double x = this.getX() + (double) (MathHelper.cos(theta) * targetRadPos);
				double y = this.getY();
				double z = this.getZ() + (double) (MathHelper.sin(theta) * targetRadPos);

				if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
					if (isWaiting && this.random.nextBoolean()) {
						world.addImportantParticleClient(
							DEFAULT_PARTICLE,
							x, y, z,
							0.0F, 0.0F, 0.0F
						);
					}
					else {
						world.addImportantParticleClient(
							particleEffect,
							x, y, z,
							0.0F, 0.0F,  0.0F
						);
					}
				}
				else if (isWaiting) {
					world.addImportantParticleClient(
						particleEffect,
						x, y, z,
						0.0F, 0.0F, 0.0F
					);
				}
				else {
					world.addImportantParticleClient(
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
	protected void serverTick(ServerWorld world) {
		// Discards the entity if it has existed for longer than its duration (if it has a duration)
		// or if it has reached its target radius.
		if ((this.getDuration() != -1 && this.age - this.getWaitTime() >= this.getDuration()) ||
			(MathHelper.abs(this.getRadius()) >= Math.abs(this.getTargetRadius()))
		) {
			this.discard();
		}
		// Otherwise, proceed to handle radius change only.
		// Behavior will be handled by the inheriting class, but the radius change is handled here
		// since it's common for all area clouds.
		else {
			boolean isWaiting = this.isWaiting();
			boolean stillWaiting = this.age < this.getWaitTime();

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
						(this.getTargetAge() > 0 && this.age > this.getTargetAge())
					) {
						this.discard();
						return;
					}

					this.setRadius(radius);
				}
				else {
					if (this.getTargetAge() > 0 && this.age > this.getTargetAge()) {
						this.discard();
					}
				}

				// 1. Clean the global cooldown map
				this.affectedEntities
					.entrySet()
					.removeIf((entry) -> this.age >= entry.getValue());

				// 2. Grab everything in the general area
				List<LivingEntity> targets = this.getEntityWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());

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
	public void readCustomData(@NonNull ReadView view) {
		this.targetAge = view.getInt("TargetAge", this.targetAge);
		this.age = view.getInt("Age", 0);
		this.duration = view.getInt("Duration", this.duration);
		this.waitTime = view.getInt("WaitTime", this.waitTime);
		this.reApplicationDelay = view.getInt("ReApplicationDelay", this.reApplicationDelay);
		this.durationOnUse = view.getInt("DurationOnUse", this.durationOnUse);
		this.targetRadius = view.getFloat("TargetRadius", this.targetRadius);
		this.radiusOnUse = view.getFloat("RadiusOnUse", this.radiusOnUse);
		this.radiusGrowth = view.getFloat("RadiusPerTick", this.radiusGrowth);
		this.setRadius(view.getFloat("Radius", this.getRadius()));
		this.owner = LazyEntityReference.fromData(view, "Owner");

		this.setParticleType(
			view.read("custom_particle", ParticleTypes.TYPE_CODEC)
				.orElse(this.getDefaultParticle())
		);
	}

	@Override
	public void writeCustomData(@NonNull WriteView view) {
		view.putInt("TargetAge", this.targetAge);
		view.putInt("Age", this.age);
		view.putInt("Duration", this.duration);
		view.putInt("WaitTime", this.waitTime);
		view.putInt("ReapplicationDelay", this.reApplicationDelay);
		view.putInt("DurationOnUse", this.durationOnUse);
		view.putFloat("TargetRadius", this.targetRadius);
		view.putFloat("RadiusOnUse", this.radiusOnUse);
		view.putFloat("RadiusPerTick", this.radiusGrowth);
		view.putFloat("Radius", this.getRadius());
		LazyEntityReference.writeData(this.owner, view, "Owner");
		view.putNullable("custom_particle", ParticleTypes.TYPE_CODEC, this.customParticle);
	}

	// ///////////// //
	// FINAL METHODS //
	// ///////////// //

	@Override
	public final boolean damage(ServerWorld world, DamageSource source, float amount) {
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
		RADIUS = DataTracker.registerData(AreaCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
		WAITING = DataTracker.registerData(AreaCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		PARTICLE = DataTracker.registerData(AreaCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
	}
}
