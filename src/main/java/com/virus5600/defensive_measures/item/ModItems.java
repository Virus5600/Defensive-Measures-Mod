package com.virus5600.defensive_measures.item;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.item.equipments.TurretRemoverItem;
import com.virus5600.defensive_measures.item.turrets.cannon.*;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class ModItems {
	public final static Item[] DM_ITEMS;
	public final static Item[] DM_EQUIPMENTS;
	public final static Item[] DM_TRAPS;
	public final static Item[] DM_TURRETS;

	/////////////
	// TURRETS //
	/////////////

	// CANNON
	public final static Item CANNON_TURRET = registerItem("cannon_turret", new CannonTurretItem(ModEntities.CANNON_TURRET, itemSettings()));
	public final static Item CANNON_BASE = registerItem("cannon_base", new CannonBaseItem(itemSettings()));
	public final static Item CANNON_HEAD = registerItem("cannon_head", new CannonHeadItem(itemSettings()));
	public final static Item CANNON_STAND = registerItem("cannon_stand", new CannonStandItem(itemSettings()));
	public final static Item UNFINISHED_CANNON_HEAD = registerItem("unfinished_cannon_head", new UnfinishedCannonHeadItem(itemSettings()));

	// BALLISTA
//	public final static Item BALLISTA = new BallistaTurretItem(ModEntities.BALLISTA, SETTING_DMT);
//	public final static Item BALLISTA_ARROW = new BallistaArrowItem(SETTING_DMI);
//	public final static Item BALLISTA_BASE = new BallistaBaseItem(SETTING_DMI);
//	public final static Item BALLISTA_BASE_WITH_STAND = new BallistaBaseWithStandItem(SETTING_DMI);
//	public final static Item BALLISTA_BOW = new BallistaBowItem(SETTING_DMI);

	// MACHINE GUN
//	public final static Item MG_TURRET = new MachineGunTurretItem(ModEntities.MG_TURRET, SETTING_DMT);
//	public final static Item AMMO_CASE = new AmmoCaseItem(SETTING_DMI);
//	public final static Item AMMO_ROUNDS = new AmmoRoundsItem(SETTING_DMI);
//	public final static Item MACHINE_GUN_BASE = new MachineGunBaseItem(SETTING_DMI);
//	public final static Item MACHINE_GUN_HEAD = new MachineGunHeadItem(SETTING_DMI);
//	public final static Item MACHINE_GUN_STAND = new MachineGunStandItem(SETTING_DMI);

	////////////////
	// EQUIPMENTS //
	////////////////

	// TURRET REMOVER
	public final static Item TURRET_REMOVER = registerItem("turret_remover", new TurretRemoverItem(ModToolMaterials.TURRET_REMOVER.getToolMaterialInstance(), itemSettings()));

	//////////////////////////////
	// REGISTRY RELATED METHODS //
	//////////////////////////////

	/**
	 * Creates an instance of {@link Settings} for items.
	 * @return an instance of {@link Settings}
	 */
	private static Settings itemSettings()  {
		return new Settings();
	};

	/**
	 * Registers an item to the game.
	 * @param name the name of the item.
	 * @param item the item to register.
	 */
	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(DefensiveMeasures.MOD_ID, name), item);
	}

	public static void registerModItems() {
		DefensiveMeasures.LOGGER.info("REGISTERING ITEMS TO ITEM GROUPS...");

		Arrays.stream(DM_ITEMS).iterator().forEachRemaining((item) -> {
			ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMI_KEY).register((content) -> {
				content.add(item);
			});
		});

		Arrays.stream(DM_EQUIPMENTS).iterator().forEachRemaining((item) -> {
			ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DME_KEY).register((content) -> {
				content.add(item);
			});
		});

//		Arrays.stream(DM_TRAPS).iterator().forEachRemaining((item) -> {
//			ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMTR_KEY).register((content) -> {
//				content.add(item);
//			});
//		});

		Arrays.stream(DM_TURRETS).iterator().forEachRemaining((item) -> {
			ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMTT_KEY).register((content) -> {
				content.add(item);
			});
		});
	}

	static {
		DM_ITEMS = new Item[]{
			CANNON_BASE,
			CANNON_HEAD,
			CANNON_STAND,
			UNFINISHED_CANNON_HEAD,
//			BALLISTA_ARROW,
//			BALLISTA_BASE,
//			BALLISTA_BASE_WITH_STAND,
//			BALLISTA_BOW,
//			AMMO_CASE,
//			AMMO_ROUNDS,
//			MACHINE_GUN_BASE,
//			MACHINE_GUN_HEAD,
//			MACHINE_GUN_STAND
		};

		DM_EQUIPMENTS = new Item[]{
			TURRET_REMOVER
		};

		DM_TRAPS = new Item[]{
		};

		DM_TURRETS = new Item[]{
			CANNON_TURRET,
//			BALLISTA,
//			MG_TURRET
		};
	}
}
