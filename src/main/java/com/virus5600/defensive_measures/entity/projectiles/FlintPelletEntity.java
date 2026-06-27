package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._helper.BlockHelper;
import com.virus5600.defensive_measures.entity.turrets.tier0.PelletTurretEntity;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

/**
 * The projectile used by {@link PelletTurretEntity}.
 * <br>
 * Represents a flint pellet projectile entity that is the basic of the basic projectiles.
 * <br>
 * The flint pellet is a {@link KineticProjectileEntity kinetic projectile} that is fired from a
 * {@link PelletTurretEntity pellet turret}.
 * <br>
 * While it cannot pierce any entities nor do an AoE damage... It can deal 1 heart (2 HP) of damage.
 *
 * @see KineticProjectileEntity
 * @see TurretProjectileEntity
 * @see TurretProjectileEntity#onHitEntity(EntityHitResult)
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FlintPelletEntity extends KineticProjectileEntity {
	protected int idleAge = 0;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public FlintPelletEntity(
		EntityType<? extends TurretProjectileEntity> entityType,
		Level world
	) {
		super(entityType, world);

		this.setPierceLevel((byte) 0);
		this.setDamage(2);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	protected void onHitBlock(BlockHitResult hitResult) {
		BlockState state = this.level().getBlockState(hitResult.getBlockPos());
		BlockHelper.BlockCategory blockCat = BlockHelper.getBlockCategory(state);
		Vec3 velocity = this.getDeltaMovement();

		velocity = switch (hitResult.getDirection().getAxis()) {
			case X -> new Vec3(velocity.x() * -1, velocity.y(), velocity.z());
			case Y -> new Vec3(velocity.x(), velocity.y() * -1, velocity.z());
			case Z -> new Vec3(velocity.x(), velocity.y(), velocity.z() * -1);
		};

		double retainedVelocity = switch (blockCat) {
			case BlockHelper.BlockCategory.GLASS -> 0.40;
			case BlockHelper.BlockCategory.GRAINY -> 0.10;
			case BlockHelper.BlockCategory.METAL -> 0.85;
			case BlockHelper.BlockCategory.STONE -> 0.70;
			case BlockHelper.BlockCategory.WOOD -> 0.35;
			default -> 0.25;
		};

		this.setDeltaMovement(velocity.scale(retainedVelocity));
	}

	@Override
	public SoundEvent getHitSound() {
		return ModSoundEvents.FLINT_PELLET_DEFAULT;
	}

	@Override
	public byte getMaxPierceLevel() {
		return 1;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.getDeltaMovement().length() < 0.01 && this.idleAge == 0) {
			this.idleAge = this.tickCount;
		}

		if (this.idleAge + 100 <= this.tickCount) {
			this.discard();
		}
	}
}
