package com.virus5600.defensive_measures.items;

import com.virus5600.defensive_measures.DefensiveMeasures;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
	public static final RegistryKey<ItemGroup> DMI_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(DefensiveMeasures.MOD_ID, "defensive_measures_items"));
	public static final ItemGroup DEFENSIVE_MEASURES_ITEMS = FabricItemGroup.builder()
		.displayName(Text.translatable("itemGroup.dm.defensive_measures.items"))
		.icon(ModItems.CANNON_HEAD::getDefaultStack)
		.build();

	public static final RegistryKey<ItemGroup> DME_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(DefensiveMeasures.MOD_ID, "defensive_measures_equipments"));
	public static final ItemGroup DEFENSIVE_MEASURES_EQUIPMENTS = FabricItemGroup.builder()
		.displayName(Text.translatable("itemGroup.dm.defensive_measures.equipments"))
		.icon(ModItems.TURRET_REMOVER::getDefaultStack)
		.build();

//	public static final RegistryKey<ItemGroup> DMTR_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(DefensiveMeasures.MOD_ID, "defensive_measures_traps"));
//	public static final ItemGroup DEFENSIVE_MEASURES_TRAPS = FabricItemGroup.builder()
//		.displayName(Text.translatable("itemGroup.dm.defensive_measures.traps"))
//		.icon(() -> ModBlocks.ARROWHEAD.asItem().getDefaultStack())
//		.build();

	public static final RegistryKey<ItemGroup> DMTT_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(DefensiveMeasures.MOD_ID, "defensive_measures_turrets"));
	public static final ItemGroup DEFENSIVE_MEASURES_TURRETS = FabricItemGroup.builder()
		.displayName(Text.translatable("itemGroup.dm.defensive_measures.turrets"))
		.icon(ModItems.CANNON_TURRET::getDefaultStack)
		.build();

	////////////////////
	// REGISTER ITEMS //
	////////////////////

	public static void registerModItemGroups() {
		DefensiveMeasures.LOGGER.info("Registering item groups...");
		Registry.register(Registries.ITEM_GROUP, DMI_KEY, DEFENSIVE_MEASURES_ITEMS);
		Registry.register(Registries.ITEM_GROUP, DME_KEY, DEFENSIVE_MEASURES_EQUIPMENTS);
//		Registry.register(Registries.ITEM_GROUP, DMTR_KEY, DEFENSIVE_MEASURES_TRAPS);
		Registry.register(Registries.ITEM_GROUP, DMTT_KEY, DEFENSIVE_MEASURES_TURRETS);
	}
}
