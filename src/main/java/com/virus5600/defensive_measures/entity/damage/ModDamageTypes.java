package com.virus5600.defensive_measures.entity.damage;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.util.RegistryUtil;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;

public class ModDamageTypes {
	public static final RegistryKey<DamageType> ARROWHEAD = RegistryUtil.getDamageTypeKey("arrowhead");

	public static void registerDamageTypes() {
		DefensiveMeasures.LOGGER.info("REGISTERING DAMAGE TYPES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
