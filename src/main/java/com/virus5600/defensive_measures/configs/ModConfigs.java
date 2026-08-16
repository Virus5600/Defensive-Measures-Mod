package com.virus5600.defensive_measures.configs;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

/**
 * Loads all configuration files for the mod. This class is responsible for initializing and
 * managing the configuration settings used by this mod, caching the global and configurations.
 * This allows the configuration classes to be callable down the line without having to re-read the
 * configuration files, which can be resource intensive.
 * <br><br>
 * Additionally, some configurations has a per-world configuration, which is also cached and
 * managed by the classes statically initialized here once a world is loaded.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModConfigs {
	public static void init() {
		BlockProjectileConfigManager.init();

		ServerLifecycleEvents.SERVER_STARTED.register(ModConfigs::initOnWorldLoad);
		ServerLifecycleEvents.SERVER_STOPPED.register(ModConfigs::clearCachedConfig);
	}

	public static void initOnWorldLoad(MinecraftServer server) {
		BlockProjectileConfigManager.loadPerWorld(server);
	}

	public static void clearCachedConfig(MinecraftServer server) {
		BlockProjectileConfigManager.clearPerWorld();
	}
}
