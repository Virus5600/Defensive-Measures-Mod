package com.virus5600.defensive_measures.animations;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.particle.ModParticles;

public class KeyframeScripts {
	public static KeyframeAction EXPLODE_SCRIPT;

	static {
		EXPLODE_SCRIPT = (animState, state, pos) -> {
			ClientWorld world = MinecraftClient.getInstance().world;

			if (world == null) { return; }

			Entity entity = world.getEntity(state.id);
			DamageSource src = ModDamageSources.create(world, ModDamageTypes.SECONDARY_EXPLOSION);

			EntityDimensions dim = EntityDimensions.fixed(1, 1);

			if (entity != null) {
				dim = entity.getDimensions(entity.getPose());
			}

			world.createExplosion(
				entity, src, new ExplosionBehavior(),
				pos.getX(), pos.getY(), pos.getZ(),
				dim.width() + 0.5f, false,
				World.ExplosionSourceType.NONE,
				ModParticles.FLAK_EXPLOSION,
				ModParticles.FLAK_EXPLOSION,
				Pool.<BlockParticleEffect>builder().build(),
				SoundEvents.ENTITY_GENERIC_EXPLODE
			);
		};
	}
}
