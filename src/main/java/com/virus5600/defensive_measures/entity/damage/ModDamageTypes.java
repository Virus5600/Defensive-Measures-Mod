package com.virus5600.defensive_measures.entity.damage;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.util.RegistryUtil;

/**
 * A class that registers custom damage types for the mod.
 * <br><br>
 * All custom damage types are registered and stored as
 * constants here, allowing other classes to reference or
 * use them.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModDamageTypes {
	public static final RegistryKey<DamageType> ARROWHEAD = RegistryUtil.getDamageTypeKey("arrowhead");

	public static void registerDamageTypes() {
		DefensiveMeasures.LOGGER.info("REGISTERING DAMAGE TYPES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
