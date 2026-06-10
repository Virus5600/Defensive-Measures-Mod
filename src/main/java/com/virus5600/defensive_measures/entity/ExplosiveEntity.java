package com.virus5600.defensive_measures.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.world.ModExplosionImpl;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface ExplosiveEntity {
	/**
	 * Defines the maximum effective radius of the explosion. Within this
	 * radius, all entities will receive the full base damage. Entities
	 * outside this radius will receive reduced damage based on the damage
	 * reduction multiplier.
	 *
	 * @return The maximum effective radius of the explosion.
	 */
	double getEffectiveRadius();

	/**
	 * Defines the maximum radius this explosion can reach. Entities within this
	 * radius will receive damage, but the damage will be reduced based on the
	 * distance from the {@link #getEffectiveRadius() effective radius} of the
	 * explosion.
	 *
	 * @return The maximum radius of the explosion.
	 */
	double getMaxDamageRadius();

	/**
	 * Defines the damage reduction multiplier for entities between the
	 * {@link #getEffectiveRadius() effective radius} and {@link #getMaxDamageRadius() max radius}
	 * of the explosion.
	 *
	 * @return The damage reduction multiplier for entities outside the effective radius.
	 */
	double getDamageReduction();

	/**
	 * Defines the base damage this explosive entity will deal. It could be the default damage of an
	 * entity (or owner of the projectile), or a calculated one.
	 *
	 * @return The base damage of this explosive entity.
	 *
	 * @apiNote When implementing and returning a calculated one, do note that the value that this
	 * method will return will further undergo processing under the
	 * {@link ModExplosionImpl ModExplosionImpl#damageEntity(ProjectileEntity, Entity, List, boolean, double)},
	 * adding a damage falloff formula on entities inside the outer radius, and further processing
	 * the final damage based on the entity's size and exposed parts against the explosion.
	 */
	double getBaseDamage();

	/**
	 * Determines the explosion source type of this explosive entity. This will determine the
	 * destruction type of the explosion and whether the explosion can destroy blocks or not based
	 * on the gamerule.
	 * <br><br>
	 * By default, this method returns {@link Level.ExplosionInteraction#MOB}.
	 *
	 * @return The explosion source type of this explosive entity.
	 */
	default Level.ExplosionInteraction getExplosionSourceType() {
		return Level.ExplosionInteraction.MOB;
	}

	/**
	 * Identifies whether the explosion of this entity can destroy blocks or not regardless of the
	 * gamerule {@link GameRules#MOB_GRIEFING mobGriefing}.
	 * <br><br>
	 * By default, this method returns {@code true}.
	 *
	 * @return Whether the explosion can destroy blocks or not.
	 */
	default boolean canDestroyBlocks() {
		return true;
	}

	default void createExplosion(Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, Level.ExplosionInteraction explosionSourceType, ParticleOptions smallParticle, ParticleOptions largeParticle, WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> soundEvent) {
		this.createExplosion(
			entity, damageSource, behavior, x, y, z, power, createFire,
			explosionSourceType, smallParticle, largeParticle,
			blockParticles, soundEvent, true
		);
	}

	default void createExplosion(Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, Level.ExplosionInteraction explosionSourceType, ParticleOptions smallParticle, ParticleOptions largeParticle, WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> soundEvent, boolean destroyBlocks) {
		if (entity.level().isClientSide()) {
			return;
		}

		ServerLevel world = (ServerLevel) entity.level();

		Explosion.BlockInteraction dType;
		switch (explosionSourceType) {
			case NONE -> dType = Explosion.BlockInteraction.KEEP;
			case BLOCK -> dType = world.getDestroyType(GameRules.BLOCK_EXPLOSION_DROP_DECAY);
			case MOB -> dType = world.getGameRules().get(GameRules.MOB_GRIEFING) ? world.getDestroyType(GameRules.MOB_EXPLOSION_DROP_DECAY) : Explosion.BlockInteraction.KEEP;
			case TNT -> dType = world.getDestroyType(GameRules.TNT_EXPLOSION_DROP_DECAY);
			case TRIGGER -> dType = Explosion.BlockInteraction.TRIGGER_BLOCK;
			default -> throw new MatchException(null, null);
		}

		Explosion.BlockInteraction destructionType = destroyBlocks ? dType : Explosion.BlockInteraction.KEEP;
		Vec3 vec3d = new Vec3(x, y, z);
		ModExplosionImpl explosionImpl = new ModExplosionImpl(
			world,
			entity, damageSource,
			behavior, vec3d,
			power, createFire, destructionType
		);

		int blocksDestroyed = explosionImpl.explode(destroyBlocks);
		ParticleOptions particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

		for(ServerPlayer serverPlayerEntity : world.players()) {
			if (serverPlayerEntity.distanceToSqr(vec3d) < (double) 4096.0F) {
				Optional<Vec3> optional = Optional.ofNullable(
					explosionImpl.getHitPlayers()
						.get(serverPlayerEntity)
				);

				serverPlayerEntity.connection
					.send(
						new ClientboundExplodePacket(
							vec3d, power, blocksDestroyed, optional,
							particleEffect, soundEvent, blockParticles
						)
					);
			}
		}

	}
}
