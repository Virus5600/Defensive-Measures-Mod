package com.virus5600.defensive_measures.item;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.item.equipments.TurretRemoverItem;
import com.virus5600.defensive_measures.item.interfaces.FuelItem;
import com.virus5600.defensive_measures.item.turrets.TurretItem;
import com.virus5600.defensive_measures.item.turrets.cannon.*;

import com.virus5600.defensive_measures.util.RegistryUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;

import java.util.Arrays;

public class ModItems {
	public final static Item[] DM_ITEMS;
	public final static Item[] DM_EQUIPMENTS;
	public final static Item[] DM_TRAPS;
	public final static Item[] DM_TURRETS;
	public final static Item[] FUEL_ITEMS;

	/////////////
	// TURRETS //
	/////////////

	// CANNON
	public final static Item CANNON_TURRET = registerItem("cannon_turret", ModEntities.CANNON_TURRET, CannonTurretItem::new);
	public final static Item CANNON_BASE = registerItem("cannon_base");
	public final static Item CANNON_HEAD = registerItem("cannon_head");
	public final static Item CANNON_STAND = registerItem("cannon_stand");
	public final static Item UNFINISHED_CANNON_HEAD = registerItem("unfinished_cannon_head");

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
	public final static Item TURRET_REMOVER = registerItem("turret_remover");

	//////////////////////////////
	// REGISTRY RELATED METHODS //
	//////////////////////////////

	private static Item registerItem(String name, EntityType<? extends MobEntity> entityType, ItemFactory<? extends TurretItem> factory) {
		return RegistryUtil.registerItem(
			name,
			(settings) -> factory.create(entityType, settings),
			new Settings()
		);
	}

	private static Item registerItem(String name) {
		return RegistryUtil.registerItem(name);
	}

	public static void registerModItems() {
		DefensiveMeasures.LOGGER.info("REGISTERING ITEMS TO ITEM GROUPS...");

		Arrays.stream(DM_ITEMS).iterator().forEachRemaining(
			(item) -> ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMI_KEY).register(
				(content) -> content.add(item)
			)
		);

		Arrays.stream(DM_EQUIPMENTS).iterator().forEachRemaining(
			(item) -> ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DME_KEY).register(
				(content) -> content.add(item)
			)
		);

//		Arrays.stream(DM_TRAPS).iterator().forEachRemaining(
//			(item) -> ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMTR_KEY).register(
//				(content) -> {content.add(item)
//			)
//		);

		Arrays.stream(DM_TURRETS).iterator().forEachRemaining(
			(item) -> ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMTT_KEY).register(
				(content) -> content.add(item)
			)
		);

		Arrays.stream(FUEL_ITEMS).iterator().forEachRemaining((item) -> FuelRegistryEvents.BUILD.register((builder, _) -> {
			// Verifies whether the item is a valid fuel item. Ignores the item if it is not.
			if (item instanceof FuelItem)
				builder.add(item, ((FuelItem) item).getFuelTime());
		}));
	}

	private interface ItemFactory<T extends Item> {
		T create(EntityType<? extends MobEntity> entityType, Settings settings);
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

		FUEL_ITEMS = new Item[]{
			CANNON_STAND
		};
	}
}
