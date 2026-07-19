package com.virus5600.defensive_measures.color;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.color.blocks.LandmineTintSources;

import java.util.List;

public class ModClientBlockColors {
	public static void init() {
		// v1.2.0-beta
		BlockColorRegistry.register(
			List.of(new LandmineTintSources()),
			ModBlocks.ANTI_PERSONNEL_MINE_M14,
			ModBlocks.ANTI_TANK_MINE
		);
	}
}
