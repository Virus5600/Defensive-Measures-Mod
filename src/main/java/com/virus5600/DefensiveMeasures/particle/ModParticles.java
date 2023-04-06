package com.virus5600.DefensiveMeasures.particle;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.particle.custom.CannonFlash;
import com.virus5600.DefensiveMeasures.particle.custom.CannonFuse;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModParticles {
	public static final DefaultParticleType CANNON_FLASH = FabricParticleTypes.simple();
	public static final DefaultParticleType CANNON_FUSE = FabricParticleTypes.simple();

	public static void registerParticles() {
		DefensiveMeasures.LOGGER.debug("REGISTERING PARTICLES FOR " + DefensiveMeasures.MOD_NAME);

		Registry.register(Registry.PARTICLE_TYPE, new Identifier(DefensiveMeasures.MOD_ID, "cannon_flash"), CANNON_FLASH);
		Registry.register(Registry.PARTICLE_TYPE, new Identifier(DefensiveMeasures.MOD_ID, "cannon_fuse"), CANNON_FUSE);
	}

	public static void registerClientParticles() {
		ParticleFactoryRegistry.getInstance().register(CANNON_FLASH, CannonFlash.Factory::new);
		ParticleFactoryRegistry.getInstance().register(CANNON_FUSE, CannonFuse.Factory::new);
	}
}
