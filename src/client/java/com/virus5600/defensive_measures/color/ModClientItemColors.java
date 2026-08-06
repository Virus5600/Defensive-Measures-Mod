package com.virus5600.defensive_measures.color;

import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.color.items.MetalDetectorTintSources;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModClientItemColors {
	public static void init() {
		// v1.2.0-beta
		ItemTintSources.ID_MAPPER.put(
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "metal_detector"),
			MetalDetectorTintSources.CODEC
		);
	}
}
