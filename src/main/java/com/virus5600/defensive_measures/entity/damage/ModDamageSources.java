package com.virus5600.defensive_measures.entity.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A utility class for creating {@link DamageSource Damage Sources} from
 * {@link ResourceKey < DamageType > DamageTypes}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModDamageSources {
	/**
	 * Creates a {@link DamageSource} from a {@link ResourceKey < DamageType >} and a {@link Level}
	 *
	 * @param world The world to create the {@link DamageSource} in
	 * @param type The {@link ResourceKey < DamageType >} to create the {@link DamageSource} from
	 * @param source The source where the damage is coming from.
	 * @param attacker The entity from which the source of the damage came from.
	 *
	 * @return The created {@link DamageSource}
	 *
	 * @throws IllegalStateException If the {@link Registry < DamageType >} is not present in the world
	 */
	public static DamageSource create(
            Level world, ResourceKey<DamageType> type,
            @Nullable Entity source, @Nullable Entity attacker
		) throws IllegalStateException {
		Optional<Registry<DamageType>> optionalRegistry = world.registryAccess()
			.lookup(Registries.DAMAGE_TYPE);

		if (optionalRegistry.isEmpty()) {
			throw new IllegalStateException("DamageType optionalRegistry is not present");
		}
		Registry<DamageType> registry = optionalRegistry.get();
		Holder<DamageType> dmgEntry = registry.wrapAsHolder(registry.getValue(type));

		return new DamageSource(dmgEntry, source, attacker);
	}
}
