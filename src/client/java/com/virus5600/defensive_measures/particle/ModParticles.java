package com.virus5600.defensive_measures.particle;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.particle.custom.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@Environment(EnvType.CLIENT)
public class ModParticles {
	public static final SimpleParticleType CANNON_FUSE = register("cannon_fuse", true);
	public static final SimpleParticleType CANNON_FLASH = register("cannon_flash", false);

	private static SimpleParticleType register(String identifier, boolean shouldAlwaysSpawn) {
		return Registry.register(
			Registries.PARTICLE_TYPE,
			identifier,
			FabricParticleTypes.simple(shouldAlwaysSpawn)
		);
	}

	public static void registerParticles() {
		DefensiveMeasures.LOGGER.info("REGISTERING PARTICLES FOR {}...", DefensiveMeasures.MOD_NAME);

		ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

		registry.register(ModParticles.CANNON_FUSE, CannonFuse.Factory::new);
		registry.register(ModParticles.CANNON_FLASH, CannonFlash.Factory::new);
	}
}
