package com.virus5600.DefensiveMeasures.block;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.item.ModItemGroup;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
	// 1.0.0
	public final static Block ARROWHEAD = new ArrowheadBlock(FabricBlockSettings.of(Material.AGGREGATE));

	private static Block registerBlock(String name, Block block, ItemGroup tab) {
		registerBlockItem(name, block, tab);

		return Registry.register(
			Registry.BLOCK,
			new Identifier(DefensiveMeasures.MOD_ID, name),
			block
		);
	}

	private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
		return Registry.register(
			Registry.ITEM,
			new Identifier(DefensiveMeasures.MOD_ID, name),
			new BlockItem(block, new FabricItemSettings().group(tab))
		);
	};

	public static void registerModBlocks() {
		DefensiveMeasures.LOGGER.debug("REGISTERING BLOCKS FOR " + DefensiveMeasures.MOD_NAME);

		// 1.0.0
		registerBlock("arrowhead", ARROWHEAD, ModItemGroup.DEFENSIVE_MEASURES_TRAPS);
	}
}
