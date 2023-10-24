package com.virus5600.DefensiveMeasures.item;

import com.virus5600.DefensiveMeasures.block.ModBlocks;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class ModItemGroup {
	public static final ItemGroup DEFENSIVE_MEASURES_ITEMS = FabricItemGroupBuilder.build(
		new Identifier("dm", "defensive_measures.items"),
		() -> ModItems.CANNON_HEAD.getDefaultStack()
	);

	public static final ItemGroup DEFENSIVE_MEASURES_EQUIPMENTS = FabricItemGroupBuilder.build(
		new Identifier("dm", "defensive_measures.equipments"),
		() -> ModItems.TURRET_REMOVER.getDefaultStack()
	);

	public static final ItemGroup DEFENSIVE_MEASURES_TRAPS = FabricItemGroupBuilder.build(
		new Identifier("dm", "defensive_measures.traps"),
		() -> ModBlocks.ARROWHEAD.asItem().getDefaultStack()
	);

	public static final ItemGroup DEFENSIVE_MEASURES_TURRETS = FabricItemGroupBuilder.build(
		new Identifier("dm", "defensive_measures.turrets"),
		() -> ModItems.CANNON_TURRET.getDefaultStack()
	);
}
