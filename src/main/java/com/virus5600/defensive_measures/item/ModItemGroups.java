package com.virus5600.defensive_measures.item;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.block.ModBlocks;

/**
 * A class that contains all the item groups in the mod.
 * <br><br>
 * This class is used to register all the item groups in the mod, from items, to equipments, to
 * traps, turrets, etc. This class is also used to register the item groups to the registry so
 * that they can be used in the creative inventory.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModItemGroups {
	public static final ResourceKey<CreativeModeTab> DMI_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "defensive_measures_items"));
	public static final CreativeModeTab DEFENSIVE_MEASURES_ITEMS = FabricCreativeModeTab.builder()
		.title(Component.translatable("itemGroup.dm.defensive_measures.items"))
		.icon(ModItems.CANNON_HEAD::getDefaultInstance)
		.build();

	public static final ResourceKey<CreativeModeTab> DME_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "defensive_measures_equipments"));
	public static final CreativeModeTab DEFENSIVE_MEASURES_EQUIPMENTS = FabricCreativeModeTab.builder()
		.title(Component.translatable("itemGroup.dm.defensive_measures.equipments"))
		.icon(ModItems.TURRET_REMOVER::getDefaultInstance)
		.build();

	public static final ResourceKey<CreativeModeTab> DMTR_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "defensive_measures_traps"));
	public static final CreativeModeTab DEFENSIVE_MEASURES_TRAPS = FabricCreativeModeTab.builder()
		.title(Component.translatable("itemGroup.dm.defensive_measures.traps"))
		.icon(() -> ModBlocks.ELECTRIC_FENCE.asItem().getDefaultInstance())
		.build();

	public static final ResourceKey<CreativeModeTab> DMTT_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "defensive_measures_turrets"));
	public static final CreativeModeTab DEFENSIVE_MEASURES_TURRETS = FabricCreativeModeTab.builder()
		.title(Component.translatable("itemGroup.dm.defensive_measures.turrets"))
		.icon(ModItems.CANNON_TURRET::getDefaultInstance)
		.build();

	// ////////////// //
	// REGISTER ITEMS //
	// ////////////// //

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING ITEM GROUPS FOR {}...", DefensiveMeasures.MOD_NAME);

		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DMI_KEY, DEFENSIVE_MEASURES_ITEMS);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DME_KEY, DEFENSIVE_MEASURES_EQUIPMENTS);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DMTR_KEY, DEFENSIVE_MEASURES_TRAPS);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DMTT_KEY, DEFENSIVE_MEASURES_TURRETS);
	}
}
