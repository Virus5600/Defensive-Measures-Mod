package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * {@code KineticProjectileEntity} is an abstract class that extends {@link TurretProjectileEntity}.
 * This class is used to represent a projectile entity that uses kinetic energy to deal damage
 * to entities, such as arrows, bullets, and other similar projectiles.
 * <br><br>
 * By default, the kinetic projectile entity has the following behavior:
 * <ul>
 *     <li>Armor points affect the piercing of the projectile.</li>
 *     <li>Armor points affect the speed of the projectile.</li>
 *     <li>Armor points affect the base damage dealt by the projectile.</li>
 *     <li>Base damage is set to 4.</li>
 * </ul>
 *
 * @see TurretProjectileEntity
 * @see ExplosiveProjectileEntity
 * @see AreaCloudEntity
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class KineticProjectileEntity extends TurretProjectileEntity {

	// ///////////// //
	// CONSTRUCTORS //
	// ///////////// //
	public KineticProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, World world) {
		super(entityType, world);
		this.setDamage(4);
		this.setPierceLevel(this.getMaxPierceLevel());
		this.setSpeedAffectsDamage(false);
	}

	// ////////////// //
	// INITIALIZATION //
	// ////////////// //

	// PROTECTED
	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	protected void move() {
		boolean isClipping = !this.isNoClip();
		Vec3d velocity = this.getVelocity();
		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.getEntityWorld().getBlockState(blockPos);

		// If this projectile is clipping and colliding with a block...
		if (!blockState.isAir() && isClipping) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.getEntityWorld(), blockPos);

			// ... and if the block has a collision shape (i.e. it's not a block like tall grass or
			// flowers that have no collision shape)...
			if (!voxelShape.isEmpty()) {
				Vec3d pos = this.getEntityPos();

				// ... and if the projectile's position is within any of the collision boxes of the
				// block, then the projectile is considered to be in the ground, and its velocity
				// is set to zero.
				for (Box box : voxelShape.getBoundingBoxes()) {
					if (box.offset(blockPos).contains(pos)) {
						this.setVelocity(Vec3d.ZERO);
						this.setInGround(true);
						break;
					}
				}
			}
		}

		// If this projectile is in ground and clipping it...
		if (this.isInGround() && isClipping) {
			// ... and the the world isn't client...
			if (!this.getEntityWorld().isClient()) {
				// ... and if the block state that the projectile is in is different from the block
				// state that it was in when it first collided with the block, and if the
				// projectile should fall, then the projectile falls.
				if (this.inBlockState != blockState && this.shouldFall()) {
					this.fall();
				}
				// ... else, this just ages.
				else {
					this.age();
				}

				// ... finally, set this on fire if fire ticks is more than 0.
				this.setOnFire(this.getFireTicks() > 0);
			}

			++this.inGroundTime;
			// ... and if it is stil alive, then tick the block collision logic.
			if (this.isAlive()) {
				this.tickBlockCollision();
			}
		}
		// ... else...
		else {
			this.inGroundTime = 0;
			Vec3d pos = this.getEntityPos();

			// ... if it is touching water, spawn bubble particles at the projectile's position.
			if (this.isTouchingWater()) {
				this.spawnBubbleParticles(pos);
			}

			// ... if it is critical, spawn critical hit particles along the projectile's path. The
			// particles are spawned at the projectile's position and along its velocity vector,
			// with a spacing of 0.25 blocks between each particle, and the particles have a
			// velocity opposite to the projectile's velocity, with a slight upward velocity added
			// to make them more visible.
			if (this.isCritical()) {
				for (int i = 0; i < 4; i++) {
					this.getEntityWorld()
						.addParticleClient(
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

			// ... and if this is not clipping, set the yaw to the angle of the velocity vector,
			// and the pitch to the angle between the velocity vector and the horizontal plane.
			float yawDeg;
			if (!isClipping) {
				yawDeg = (float) (MathHelper.atan2(-velocity.x, -velocity.z) * 180.0F / (float) Math.PI);
			}
			// ... otherwise, set the yaw to the angle of the velocity vector, but without negating
			// the x and z components, since the projectile is clipping and thus, the yaw should be
			// based on the direction of the velocity vector rather than the opposite direction.
			else {
				yawDeg = (float) (MathHelper.atan2(velocity.x, velocity.z) * 180.0F / (float) Math.PI);
			}

			float pitchDeg = (float)(MathHelper.atan2(velocity.y, velocity.horizontalLength()) * 180.0F / (float)Math.PI);

			this.setPitch(updateRotation(this.getPitch(), pitchDeg));
			this.setYaw(updateRotation(this.getYaw(), yawDeg));
			this.tickLeftOwner();

			// ... and if it is clipping, perform a raycast to check for collisions with blocks
			// along the projectile's path, and if there is a collision, apply the collision logic
			// to the hit result.
			if (isClipping) {
				BlockHitResult hitResult = this.getEntityWorld()
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
			}
			// ... otherwise, just move the projectile by its velocity and tick the block collision logic.
			else {
				this.setPosition(pos.add(velocity));
				this.tickBlockCollision();
			}

			// ... and if it is clipping, apply drag to the velocity, and if it is also not in
			// ground, apply gravity to the velocity as well, causing the projectile to slow down
			// and fall over time.
			this.applyDrag();
			if (isClipping && !this.isInGround()) {
				this.applyGravity();
			}
		}
	}

	@Override
	public void tick() {
		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.getEntityWorld().getBlockState(blockPos);

		if (this.shake > 0) {
			--this.shake;
		}

		if (this.isTouchingWaterOrRain() || blockState.isOf(Blocks.POWDER_SNOW)) {
			this.extinguish();
		}

		this.move();
		super.tick();
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	// PUBLIC
	@Override
	public boolean armorAffectsPiercing() {
		return true;
	}

	@Override
	public boolean armorAffectsVelocity() {
		return true;
	}

	@Override
	public boolean armorAffectsDamage() {
		return true;
	}
}
