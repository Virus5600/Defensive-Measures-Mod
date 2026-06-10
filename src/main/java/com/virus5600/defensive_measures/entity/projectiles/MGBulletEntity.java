package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._helper.BlockHelper.BlockCategory;
import com.virus5600.defensive_measures._helper.BlockHelper;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.turrets.tier1.MGTurretEntity;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import org.jspecify.annotations.NonNull;

/**
 * The projectile used by {@link MGTurretEntity MG Turret}.
 *
 * <p>Represents a machine gun bullet projectile entity that pierces through multiple unarmored entities.</p>
 * <p>
 *     The machine gun bullet is a {@link KineticProjectileEntity kinetic projectile}
 *     that is fired from a {@link MGTurretEntity MG turret}.
 * </p>
 * <p>
 *     This projectile can pierce through multiple unarmored entities, dealing 5 hearts of damage to each
 *     entity it hits. The bullet can pierce through a maximum of 5 entities before it is destroyed.
 *     And everytime it pierces through an entity, it will reduce its pierce level its speed
 *     depending on the armor of the entity it hit.
 * </p>
 * <p>
 *     The bullet will be destroyed upon hitting a block like a true fast moving kinetic
 *     projectile would do.
 * </p>
 *
 * @see KineticProjectileEntity
 * @see TurretProjectileEntity
 * @see TurretProjectileEntity#onHitEntity(EntityHitResult)
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MGBulletEntity extends KineticProjectileEntity {
	// ////////////// //
	//  CONSTRUCTORS  //
	// ////////////// //
	public MGBulletEntity(
            EntityType<? extends TurretProjectileEntity> entityType,
            Level world
	) {
		super(entityType, world);
	}

	public MGBulletEntity(Level world, LivingEntity owner) {
		this(ModEntities.MG_BULLET, world);
		this.setOwner(owner);

		this.setPierceLevel((byte) 5);
		this.setDamage(5);
	}

	public MGBulletEntity(
            LivingEntity owner,
            double velocityX,
            double velocityY,
            double velocityZ,
            Level world
	) {
		this(world, owner);
		this.setDeltaMovement(velocityX, velocityY, velocityZ);
	}

	// ///////// //
	//  METHODS  //
	// ///////// //

	// PROTECTED
	@Override
	protected void onHitEntity(@NonNull EntityHitResult entityHitResult) {
		SoundEvent sound = ModSoundEvents.BULLET_IMPACT_FLESH;
		if (entityHitResult.getEntity() instanceof IronGolem) {
			sound = ModSoundEvents.BULLET_IMPACT_METAL;
		}

		this.setSound(sound);

		super.onHitEntity(entityHitResult);
	}

	/**
	 * The MG bullet's behavior when it hits a block is similar to other
	 * {@link KineticProjectileEntity kinetic projectiles}. However, the sound
	 * that is played when the bullet hits a block is different, thus the need
	 * to override this method.
	 * <br><br>
	 * The sound that is played when the bullet hits a block is determined by
	 * the {@link BlockCategory category} of the block that the bullet hit.
	 * <br><br>
	 * So far, the currently defined block categories are:
	 * <ul>
	 *     <li>
	 *         <b>Version 1.1.x-beta-1.21.4:</b>
	 *         <ul>
	 *             <li>{@link BlockCategory#GLASS Glass} blocks</li>
	 *             <li>{@link BlockCategory#GRAINY Grainy} blocks</li>
	 *             <li>{@link BlockCategory#METAL Metal} blocks</li>
	 *             <li>{@link BlockCategory#STONE Stone} blocks</li>
	 *             <li>{@link BlockCategory#WOOD Wood} blocks</li>
	 *             <li>{@link BlockCategory#DIRT Dirt} blocks</li>
	 *             <li>{@link BlockCategory#GREENERY Greenery} blocks</li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 * Each category has a different sound that is played when the bullet hits,
	 * aside from the following categories:
	 * <ul>
	 *     <li>{@link BlockCategory#DIRT Dirt} blocks</li>
	 *     <li>{@link BlockCategory#GREENERY Greenery} blocks</li>
	 *     <li>{@link BlockCategory#OTHERS Other} blocks</li>
	 * </ul>
	 *
	 * @param blockHitResult The block hit result
	 *
	 * @see BlockHelper#getBlockCategory(BlockState)
	 * @see BlockCategory
	 */
	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		BlockState state = this.level().getBlockState(blockHitResult.getBlockPos());
		BlockCategory blockCat = BlockHelper.getBlockCategory(state);

		switch (blockCat) {
			case BlockCategory.GLASS -> this.setSound(ModSoundEvents.BULLET_IMPACT_GLASS);
			case BlockCategory.GRAINY -> this.setSound(ModSoundEvents.BULLET_IMPACT_GRAINY);
			case BlockCategory.METAL -> this.setSound(ModSoundEvents.BULLET_IMPACT_METAL);
			case BlockCategory.STONE -> this.setSound(ModSoundEvents.BULLET_IMPACT_STONE);
			case BlockCategory.WOOD -> this.setSound(ModSoundEvents.BULLET_IMPACT_WOOD);
			// Default sound == Dirt, Greenery, Others
			default -> this.setSound(ModSoundEvents.BULLET_IMPACT_DEFAULT);
		}

		if (blockCat != BlockCategory.GLASS) {
			BlockState hitState = this.level().getBlockState(blockHitResult.getBlockPos());
			ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, hitState);

			Vec3 vel = this.getDeltaMovement().reverse().scale(2.5).normalize();
			Vec3 pos = this.position();

			if (this.level() instanceof ServerLevel sw) {
				int pNum = this.random.nextIntBetweenInclusive(5, 7);
				for (int i = 0; i < pNum; i++) {
					Vec3 rPos = pos.offsetRandom(this.random, 0.5F);
					sw.sendParticles(
						particle,
						rPos.x, rPos.y, rPos.z,
						0,
						vel.x,  vel.y, vel.z,
						this.random.nextIntBetweenInclusive(3, 7)
					);
				}
			}
		}

		super.onHitBlock(blockHitResult);

		if (this.level() instanceof ServerLevel serverWorld) {
			if (serverWorld.getGameRules().get(GameRules.MOB_GRIEFING)
				&& blockCat == BlockCategory.GLASS
			) {
				this.level().destroyBlock(
					blockHitResult.getBlockPos(),
					false,
					this.getOwner(),
					2
				);
			}
		}

		if (this.isAlive() || this.isRemoved() || this.isInGround() || this.onGround()) {
			this.discard();
		}
	}

	@Override
	protected final void setPierceLevel(byte pierceLevel) {
		this.entityData.set(PIERCE_LEVEL, pierceLevel);
	}

	// PUBLIC
	public SoundEvent getHitSound() {
		return this.getSound();
	}

	public byte getMaxPierceLevel() {
		return 5;
	}
}
