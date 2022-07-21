package com.virus5600.DefensiveMeasures.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {
	private static ItemGroup DEFENSIVE_MEASURES_ITEMS;
	private static ItemGroup DEFENSIVE_MEASURES_TOOLS;
	private static ItemGroup DEFENSIVE_MEASURES_TURRETS;
	
	/**
	 * All current groups available for the item groups, along with its corresponding keys.
	 * Refer to the list below to identify the keys and their respective groups.
	 * <ul>
	 * 	<li>dmi - DEFENSIVE_MEASURES_ITEMS</li>
	 * 	<li>dmto - DEFENSIVE_MEASURES_TOOLS</li>
	 * 	<li>dmtu - DEFENSIVE_MEASURES_TURRETS</li>
	 * </ul>
	 * @author Virus5600
	 */
	public static enum Groups {dmi, dmto, dmtu};
	
	/**
	 * Registers all item groups that this mod provides
	 */
	public static void register() {
		DEFENSIVE_MEASURES_ITEMS = FabricItemGroupBuilder.build(
			new Identifier("dm", "defensive_measures.items"),
			() -> ItemRegistry.BALLISTA_ARROW.getDefaultStack()
		);
		
		DEFENSIVE_MEASURES_TOOLS = FabricItemGroupBuilder.build(
			new Identifier("dm", "defensive_measures.tools"),
			() -> ItemRegistry.BALLISTA_ARROW.getDefaultStack()
		);
		
		DEFENSIVE_MEASURES_TURRETS = FabricItemGroupBuilder.build(
			new Identifier("dm", "defensive_measures.turrets"),
			() -> ItemRegistry.BALLISTA.getDefaultStack()
		);
	}
	
	/**
	 * Returns an Item Group based on a specified key from {@link Groups}
	 * 
	 * @param key An object from the {@link Groups Groups enum}
	 * @return ItemGroup
	 * @see net.minecraft.item.ItemGroup ItemGroup
	 */
	public static ItemGroup getItemGroup(Groups key) {
		switch (key) {
			case dmi:
				return DEFENSIVE_MEASURES_ITEMS;
			case dmto:
				return DEFENSIVE_MEASURES_TOOLS;
			case dmtu:
				return DEFENSIVE_MEASURES_TURRETS;
			default:
				return null;
		}
	}
}