package com.virus5600.defensive_measures.particle;

import com.virus5600.defensive_measures.DefensiveMeasures;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
	// PARTICLES
	public static final SimpleParticleType SPARKS = register("sparks", false);
	public static final SimpleParticleType CANNON_FUSE = register("cannon_fuse", true);

	// EMITTERS
	public static final SimpleParticleType CANNON_FLASH = register("cannon_flash", false);

	private static SimpleParticleType register(String identifier, boolean shouldAlwaysSpawn) {
		return Registry.register(
			Registries.PARTICLE_TYPE,
			Identifier.of(DefensiveMeasures.MOD_ID, identifier),
			FabricParticleTypes.simple(shouldAlwaysSpawn)
		);
	}

	public static void registerParticles() {
		DefensiveMeasures.LOGGER.info("REGISTERING PARTICLES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
