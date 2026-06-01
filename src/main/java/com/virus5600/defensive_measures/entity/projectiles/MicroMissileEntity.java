package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.entity.LoopingSoundEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesMissile;
import com.virus5600.defensive_measures.entity.turrets.tier2.MissileTurretEntity;
import com.virus5600.defensive_measures.particle.ModParticles;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * The projectile used by {@link MissileTurretEntity}.
 * <br><br>
 * Represents a small explosive that can fly in a straight line for a given distance before falling
 * to the ground. It can also home into targets with variable turn rate, allowing for a more
 * lethal missile that can be harder to shake off.
 * <br><br>
 * The Micro Missile's homing logic is quite simple: it always takes the shortest path towards its
 * target. This meant that even when it is hard to shake off, it can still be easily outmaneuvered
 * by fast and erratic movements, or by simply hiding behind blocks since it doesn't have any
 * pathfinding logic to navigate around blocks.
 *
 * @see ExplosiveProjectileEntity
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MicroMissileEntity extends ExplosiveProjectileEntity implements LoopingSoundEntity {
	protected static final TrackedData<Integer> REMAINING_FUEL;
	protected static final TrackedData<Boolean> IS_HOMING;
	protected static final TrackedData<Optional<LazyEntityReference<LivingEntity>>> TARGET;

	protected double maxRange = -1;
	protected double turnRate = 0;
	protected int maxMissileFuel = 0;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public MicroMissileEntity(
		EntityType<? extends ExplosiveProjectileEntity> entityType,
		net.minecraft.world.World world
	) {
		super(entityType, world);

		this.setFireTicks(0);
		this.setOnFire(false);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);

		builder.add(IS_HOMING, false)
			.add(REMAINING_FUEL, this.getMissileFuel())
			.add(TARGET, Optional.empty());
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	/**
	 * Identifies how far this missile's engine will fire, allowing it to correct its path
	 * towards its target. By default, its fuel range is 32 blocks.
	 * <br><br>
	 * When an {@link #getOwner() owner} is present, this will check if the owner is a
	 * {@link TurretEntity turret} so it could check if it could fetch its fuel value from the
	 * turret, allowing this value to be variable from turret to turret.
	 *
	 * @return The distance in blocks that this missile can travel while its engine is firing.
	 *
	 * @implNote For any entities that will use a custom missile fuel value, override the {@link UsesMissile}'s
	 * {@link UsesMissile#getMissileFuel() getMissileFuel()} method after implementing said interface
	 * to your custom entity.
	 *
	 * @see UsesMissile#getMissileFuel()
	 */
	protected int getMissileFuel() {
		int defaultVal = UsesMissile.MISSILE_FUEL;

		if (this.getOwner() == null) {
			return this.maxMissileFuel == 0 ? defaultVal : this.maxMissileFuel;
		}

		if (this.getOwner() instanceof UsesMissile turret) {
			return this.maxMissileFuel = turret.getMissileFuel();
		}

		return this.maxMissileFuel == 0 ? defaultVal : this.maxMissileFuel;
	}

	/**
	 * Identifies the turn rate of this missile in degrees per second. This will be used to identify
	 * how fast the missile will turn towards its target. By default, the turn rate is 30 degrees
	 * per second, allowing it to have a decent tracking ability while still being able to miss its
	 * target if the target is moving fast enough or if the missile is too far from the target.
	 * <br><br>
	 * When an {@link #getOwner() owner} is present, this will check if the owner is a
	 * {@link TurretEntity turret} so it could check if it could fetch its turn rate value from the
	 * turret, allowing this value to be variable from turret to turret.
	 *
	 * @return The turn rate in degrees per second that the missile will turn towards its target.
	 *
	 * @implNote For any entities that will use a custom missile turn rate value, override the
	 * {@link UsesMissile}'s {@link UsesMissile#getMissileTurnRate() getMissileTurnRate()} method
	 * after implementing said interface to your custom entity.
	 *
	 * @see UsesMissile#getMissileTurnRate()
	 */
	protected double getMissileTurnRate() {
		double defaultVal = UsesMissile.MISSILE_TURN_RATE;

		if (this.getOwner() == null) {
			return this.turnRate == 0 ? defaultVal : this.turnRate;
		}

		if (this.getOwner() instanceof UsesMissile turret) {
			return turret.getMissileTurnRate();
		}

		return this.turnRate == 0 ? defaultVal : this.turnRate;
	}

	/**
	 * {@inheritDoc}
	 * @return {@link ModParticles#ROCKET_THRUSTER} as the micro missile's trail particle effect,
	 * simulating an exhaust of a rocket engine.
	 */
	@Override @Nullable
	protected ParticleEffect getTrailParticleType() {
		return this.getRemainingFuel() > 0 ?
			ModParticles.ROCKET_THRUSTER : null;
	}

	@Override
	protected double getGravity() {
		return this.isTouchingWater() || this.getRemainingFuel() <= 0 ?
			0.01 : 0;
	}

	@Override
	protected boolean isBurning() {
		return false;
	}

	@Override
	protected float getDrag() {
		return 1F;
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (source.isOf(DamageTypes.EXPLOSION)) {
			this.doDamage();
			return true;
		}
		return false;
	}

	@Override
	public void tick() {
		if (this.getEntityWorld() instanceof ServerWorld) {
			if (this.age == 1) {
				this.setSpawnPos(this.getEntityPos());
			}

			// Update the distance traveled by this projectile
			this.distanceTraveled += (float) this.getVelocity().length();

			// While the remaining fuel is more than 0...
			if (this.getRemainingFuel() > 0) {
				// Use the default velocity for now...
				Vec3d vel = this.getVelocity();

				if ((this.getTargetEntity() == null ||
					(this.getTargetEntity() != null && this.getTargetEntity().isDead())) &&
					this.getOwner() != null &&
					this.getOwner() instanceof MobEntity entityOwner &&
					entityOwner.getTarget() != null
				) {
					this.setTarget(entityOwner.getTarget());
				}

				// If this is homing, has a "MobEntity" owner, and the owner has a target, then
				// adjust the velocity via lerp towards the target's direction.
				if (this.isHoming() &&
					this.getTargetEntity() != null
				) {
					double turnDegPerSec = this.getMissileTurnRate();
					double turnDegPerTick = turnDegPerSec / 20;

					Vec3d currentDir = vel.normalize();
					Vec3d targetDir = MathUtil.getTargetDirection(
						this,
						this.getTargetEntity()
					);

					double dot = currentDir.dotProduct(targetDir);
					dot = MathHelper.clamp(dot, -1, 1);

					double theta = Math.toDegrees(Math.acos(dot));

					// Slerp instead of lerp for accurate angular turn
					Vec3d cross = currentDir.crossProduct(targetDir).normalize();
					double actualTurnRad = Math.toRadians(Math.min(turnDegPerTick, theta));

					// Rodrigues' rotation formula
					Vec3d newDir = currentDir.multiply(Math.cos(actualTurnRad))
						.add(cross.crossProduct(currentDir).multiply(Math.sin(actualTurnRad)))
						.normalize();

					double speed = this.getVelocity().length();
					this.setVelocity(newDir.multiply(speed));
				}
			}
			else {
				// If there's an owner, set the max distance to the owner's follow range.
				if (this.getOwner() instanceof MobEntity entity) {
					this.maxRange = entity.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
				}
				else {
					if (this.maxRange == -1) {
						this.maxRange = UsesMissile.MISSILE_FUEL;
					}
				}

				// Sets the hard limit range of the missile at 125% of the follow range.
				double despawnRange = this.maxRange * 1.25;

				// If the missile is past its max range...
				if (this.distanceTraveled >= this.maxRange && this.getRemainingFuel() <= 0) {
					// ... starts randomizing whether to make it explode or not.
					if (this.distanceTraveled < despawnRange) {
						boolean shouldExplode = this.getRandom().nextBoolean();

						if (shouldExplode) {
							this.doDamage();
						}
					}
					// Otherwise, if it reaches the despawn range, just discard the projectile safely.
					else {
						this.discard();
					}
				}
			}
		}

		// Call super tick to apply other stuff.
		super.tick();

		this.setRemainingFuel(this.getMissileFuel() - MathHelper.floor(this.distanceTraveled));
	}

	@Override
	public void doDamage() {
		super.doDamage();

		if (this.getEntityWorld() instanceof ServerWorld world) {
			world.spawnParticles(
				ParticleTypes.EXPLOSION_EMITTER,
				true, true,
				this.getX(), this.getEyeY(), this.getZ(),
				1,
				0, 0, 0,
				0
			);
		}
	}

	@Override
	public void writeCustomData(WriteView view) {
		super.writeCustomData(view);

		view.putDouble("TurnRate",  this.getMissileTurnRate());
		view.putBoolean("Homing", this.isHoming());
		view.putInt("MaxFuel", this.getMissileFuel());
		view.putInt("Fuel", this.getRemainingFuel());

		LazyEntityReference.writeData(this.getTarget(), view, "Target");
	}

	@Override
	public void readCustomData(ReadView view) {
		super.readCustomData(view);

		this.turnRate = view.getDouble("TurnRate", this.getMissileTurnRate());
		this.maxMissileFuel = view.getInt("MaxFuel", this.getMissileFuel());

		this.setHoming(view.getBoolean("Homing", false));
		this.setRemainingFuel(view.getInt("Fuel", this.getMissileFuel()));

		LazyEntityReference<LivingEntity> ref = LazyEntityReference.fromData(view, "Target");
		LivingEntity entity = ref == null ?
			null : ref.getEntityByClass(
				this.getEntityWorld(),
				LivingEntity.class
			);

		this.setTarget(entity);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	@Override @NonNull
	protected RegistryEntry<SoundEvent> getExplosionSoundEvent() {
		return ModSoundEvents.asRegistryEntry(ModSoundEvents.ROCKET_EXPLOSION);
	}

	@Override
	public SoundEvent getHitSound() {
		return SoundEvents.ENTITY_GENERIC_EXPLODE.value();
	}

	public void setHoming(boolean homing) {
		this.dataTracker.set(IS_HOMING, homing);
	}

	public boolean isHoming() {
		return this.dataTracker.get(IS_HOMING);
	}

	public void setRemainingFuel(int remainingFuel) {
		this.dataTracker.set(REMAINING_FUEL, remainingFuel);
	}

	public int getRemainingFuel() {
		return this.dataTracker.get(REMAINING_FUEL);
	}

	public void setTarget(LivingEntity target) {
		Optional<LazyEntityReference<LivingEntity>> ref;

		if (target == null) {
			ref = Optional.empty();
		}
		else {
			ref = Optional.of(LazyEntityReference.of(target));
		}

		this.dataTracker.set(TARGET, ref);
	}

	@Nullable
	public LazyEntityReference<LivingEntity> getTarget() {
		Optional<LazyEntityReference<LivingEntity>> opt = this.dataTracker.get(TARGET);

		return opt.orElse(null);
	}

	@Nullable
	public LivingEntity getTargetEntity() {
		LazyEntityReference<LivingEntity> ref = this.getTarget();

		if (ref == null) {
			return null;
		}

		return ref.getEntityByClass(this.getEntityWorld(), LivingEntity.class);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	// EplosiveProjectileEntity //
	@Override
	public double getEffectiveRadius() {
		return 2;
	}

	@Override
	public double getMaxDamageRadius() {
		return 3;
	}

	@Override
	public double getDamageReduction() {
		return 0.125;
	}

	// TurretProjectileEntity //
	@Override
	public byte getMaxPierceLevel() {
		return 1;
	}

	// LoopingSoundEntity //
	@Override
	public boolean isPlaying() {
		return this.getRemainingFuel() > 0;
	}

	// STATIC //

	static {
		REMAINING_FUEL = DataTracker.registerData(MicroMissileEntity.class, TrackedDataHandlerRegistry.INTEGER);
		IS_HOMING = DataTracker.registerData(MicroMissileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		TARGET = DataTracker.registerData(MicroMissileEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE);
	}
}
