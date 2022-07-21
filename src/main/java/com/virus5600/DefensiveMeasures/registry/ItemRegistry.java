package com.virus5600.DefensiveMeasures.registry;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {
	private static Settings SETTING = new Item.Settings().group(ItemGroupRegistry.getItemGroup(ItemGroupRegistry.Groups.dmi));
	
	public final static Item BALLISTA = new Item(SETTING);
	public final static Item BALLISTA_ARROW = new Item(SETTING);
	public final static Item BALLISTA_BASE = new Item(SETTING);
	public final static Item BALLISTA_BASE_WITH_STAND = new Item(SETTING);
	public final static Item BALLISTA_BOW = new Item(SETTING);
	
	/**
	 * Registers all items that this mod provides
	 */
	public static void register() {
		// BALLISTA
		Registry.register(Registry.ITEM, new Identifier("dm", "ballista"), BALLISTA);
		Registry.register(Registry.ITEM, new Identifier("dm", "ballista_arrow"), BALLISTA_ARROW);
		Registry.register(Registry.ITEM, new Identifier("dm", "ballista_base"), BALLISTA_BASE);
		Registry.register(Registry.ITEM, new Identifier("dm", "ballista_base_with_stand"), BALLISTA_BASE_WITH_STAND);
		Registry.register(Registry.ITEM, new Identifier("dm", "ballista_bow"), BALLISTA_BOW);
	}
}