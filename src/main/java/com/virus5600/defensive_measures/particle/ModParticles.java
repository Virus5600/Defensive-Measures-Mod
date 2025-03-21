package com.virus5600.defensive_measures.particle;

import com.virus5600.defensive_measures.DefensiveMeasures;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Class containing all the particle types used by the mod. This is
 * usually used register the particles defined in the client-side
 * of the mod.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
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
