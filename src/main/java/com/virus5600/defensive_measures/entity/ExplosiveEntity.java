package com.virus5600.defensive_measures.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;

import com.virus5600.defensive_measures.world.ModExplosionImpl;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

import java.util.List;
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
	 * By default, this method returns {@link World.ExplosionSourceType#MOB}.
	 *
	 * @return The explosion source type of this explosive entity.
	 */
	default World.ExplosionSourceType getExplosionSourceType() {
		return World.ExplosionSourceType.MOB;
	}

	/**
	 * Identifies whether the explosion of this entity can destroy blocks or not regardless of the
	 * gamerule {@link GameRules#DO_MOB_GRIEFING mobGriefing}.
	 * <br><br>
	 * By default, this method returns {@code true}.
	 *
	 * @return Whether the explosion can destroy blocks or not.
	 */
	default boolean canDestroyBlocks() {
		return true;
	}

	default void createExplosion(Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, Pool<BlockParticleEffect> blockParticles, RegistryEntry<SoundEvent> soundEvent) {
		this.createExplosion(
			entity, damageSource, behavior, x, y, z, power, createFire,
			explosionSourceType, smallParticle, largeParticle,
			blockParticles, soundEvent, true
		);
	}

	default void createExplosion(Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, Pool<BlockParticleEffect> blockParticles, RegistryEntry<SoundEvent> soundEvent, boolean destroyBlocks) {
		if (entity.getEntityWorld().isClient()) {
			return;
		}

		ServerWorld world = (ServerWorld) entity.getEntityWorld();

		Explosion.DestructionType dType;
		switch (explosionSourceType) {
			case NONE -> dType = Explosion.DestructionType.KEEP;
			case BLOCK -> dType = world.getDestructionType(GameRules.BLOCK_EXPLOSION_DROP_DECAY);
			case MOB -> dType = world.getGameRules().getValue(GameRules.DO_MOB_GRIEFING) ? world.getDestructionType(GameRules.MOB_EXPLOSION_DROP_DECAY) : Explosion.DestructionType.KEEP;
			case TNT -> dType = world.getDestructionType(GameRules.TNT_EXPLOSION_DROP_DECAY);
			case TRIGGER -> dType = Explosion.DestructionType.TRIGGER_BLOCK;
			default -> throw new MatchException(null, null);
		}

		Explosion.DestructionType destructionType = destroyBlocks ? dType : Explosion.DestructionType.KEEP;
		Vec3d vec3d = new Vec3d(x, y, z);
		ModExplosionImpl explosionImpl = new ModExplosionImpl(
			world,
			entity, damageSource,
			behavior, vec3d,
			power, createFire, destructionType
		);

		int blocksDestroyed = explosionImpl.explode(destroyBlocks);
		ParticleEffect particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

		for(ServerPlayerEntity serverPlayerEntity : world.getPlayers()) {
			if (serverPlayerEntity.squaredDistanceTo(vec3d) < (double) 4096.0F) {
				Optional<Vec3d> optional = Optional.ofNullable(
					explosionImpl.getKnockbackByPlayer()
						.get(serverPlayerEntity)
				);

				serverPlayerEntity.networkHandler
					.sendPacket(
						new ExplosionS2CPacket(
							vec3d, power, blocksDestroyed, optional,
							particleEffect, soundEvent, blockParticles
						)
					);
			}
		}

	}
}
