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

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	// PROTECTED
	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public void tick() {
		boolean isClipping = !this.isNoClip();
		Vec3d velocity = this.getVelocity();
		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.getEntityWorld().getBlockState(blockPos);

		if (!blockState.isAir() && isClipping) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.getEntityWorld(), blockPos);

			if (!voxelShape.isEmpty()) {
				Vec3d pos = this.getEntityPos();

				for (Box box : voxelShape.getBoundingBoxes()) {
					if (box.offset(blockPos).contains(pos)) {
						this.setVelocity(Vec3d.ZERO);
						this.setInGround(true);
						break;
					}
				}
			}
		}

		if (this.shake > 0) {
			--this.shake;
		}

		if (this.isTouchingWaterOrRain() || blockState.isOf(Blocks.POWDER_SNOW)) {
			this.extinguish();
		}

		if (this.isInGround() && isClipping) {
			if (!this.getEntityWorld().isClient()) {
				if (this.inBlockState != blockState && this.shouldFall()) {
					this.fall();
				} else {
					this.age();
				}
			}

			++this.inGroundTime;
			if (this.isAlive()) {
				this.tickBlockCollision();
			}

			if (!this.getEntityWorld().isClient()) {
				this.setOnFire(this.getFireTicks() > 0);
			}
		}
		else {
			this.inGroundTime = 0;
			Vec3d pos = this.getEntityPos();

			if (this.isTouchingWater()) {
				this.spawnBubbleParticles(pos);
			}

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

			float yawDeg;
			if (!isClipping) {
				yawDeg = (float)(MathHelper.atan2(-velocity.x, -velocity.z) * 180.0F / (float)Math.PI);
			}
			else {
				yawDeg = (float) (MathHelper.atan2(velocity.x, velocity.z) * 180.0F / (float) Math.PI);
			}

			float pitchDeg = (float)(MathHelper.atan2(velocity.y, velocity.horizontalLength()) * 180.0F / (float)Math.PI);

			this.setPitch(updateRotation(this.getPitch(), pitchDeg));
			this.setYaw(updateRotation(this.getYaw(), yawDeg));
			this.tickLeftOwner();

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
			else {
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
