package com.virus5600.defensive_measures.entity.damage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

/**
 * A class that registers custom damage types for the mod.
 * <br><br>
 * All custom damage types are registered and stored as
 * constants here, allowing other classes to reference or
 * use them.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModDamageTypes {
	// v1.0.0-beta
	public static final ResourceKey<DamageType> BOLT_HEAD = RegistryHelper.getDamageTypeKey("bolt_head");

	// v1.1.0-beta
	public static final ResourceKey<DamageType> ELECTRICITY = RegistryHelper.getDamageTypeKey("electricity");
	public static final ResourceKey<DamageType> THROWN_FLAME = RegistryHelper.getDamageTypeKey("thrown_flame");
	public static final ResourceKey<DamageType> SECONDARY_EXPLOSION = RegistryHelper.getDamageTypeKey("secondary_explosion");

	public static void registerDamageTypes() {
		DefensiveMeasures.LOGGER.info("REGISTERING DAMAGE TYPES FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
