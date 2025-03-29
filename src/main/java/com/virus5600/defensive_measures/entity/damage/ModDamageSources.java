package com.virus5600.defensive_measures.entity.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * A utility class for creating {@link DamageSource Damage Sources} from
 * {@link RegistryKey<DamageType> DamageTypes}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModDamageSources {
	/**
	 * Creates a {@link DamageSource} from a {@link RegistryKey<DamageType>} and a {@link World}
	 *
	 * @param world The world to create the {@link DamageSource} in
	 * @param type The {@link RegistryKey<DamageType>} to create the {@link DamageSource} from
	 * @return The created {@link DamageSource}
	 * @throws IllegalStateException If the {@link Registry<DamageType>} is not present in the world
	 */
	public static DamageSource create(World world, RegistryKey<DamageType> type) throws IllegalStateException {
		Optional<Registry<DamageType>> optionalRegistry = world.getRegistryManager()
			.getOptional(RegistryKeys.DAMAGE_TYPE);

		if (optionalRegistry.isEmpty()) {
			throw new IllegalStateException("DamageType optionalRegistry is not present");
		}
		Registry<DamageType> registry = optionalRegistry.get();
		RegistryEntry<DamageType> dmgEntry = registry.getEntry(registry.get(type));

		return new DamageSource(dmgEntry);
	}
}
