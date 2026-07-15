package com.virus5600.defensive_measures.color;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.color.blocks.LandmineTintSources;
import net.minecraft.client.color.block.BlockColors;

import java.util.List;

public class ModBlockColors extends BlockColors {
	public static void init() {
		BlockColors colors = new BlockColors();

		// v1.2.0-beta
		colors.register(List.of(new LandmineTintSources()),
			ModBlocks.ANTI_PERSONNEL_MINE_M14,
			ModBlocks.ANTI_TANK_MINE
		);
	}
}
