package com.virus5600.defensive_measures.item;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.item.equipments.TurretRemoverItem;
import com.virus5600.defensive_measures.item.interfaces.FuelItem;
import com.virus5600.defensive_measures.item.turrets.TurretItem;
import com.virus5600.defensive_measures.item.turrets.ballista.*;
import com.virus5600.defensive_measures.item.turrets.cannon.*;
import com.virus5600.defensive_measures.util.RegistryUtil;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;

import java.util.Arrays;

/**
 * A class that contains all the items in the mod.
 * <br><br>
 * This class is used to register all the items in the mod, from turrets, to equipments, to traps, block items,
 * ingredients, etc. This class is also used to register the items to their respective item groups to categorize
 * them in the creative inventory.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
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
	public final static Item CANNON_BASE = registerItem("cannon_base", CannonBaseItem::new);
	public final static Item CANNON_HEAD = registerItem("cannon_head", CannonHeadItem::new);
	public final static Item CANNON_STAND = registerItem("cannon_stand", CannonStandItem::new);
	public final static Item UNFINISHED_CANNON_HEAD = registerItem("unfinished_cannon_head", UnfinishedCannonHeadItem::new);

	// BALLISTA
	public final static Item BALLISTA_TURRET = registerItem("ballista", ModEntities.BALLISTA_TURRET, BallistaTurretItem::new);
	public final static Item ARROWHEAD = registerItem(ModBlocks.ARROWHEAD);
	public final static Item BALLISTA_ARROW = registerItem("ballista_arrow", BallistaArrowItem::new);
	public final static Item BALLISTA_BASE = registerItem("ballista_base", BallistaBaseItem::new);
	public final static Item BALLISTA_BASE_WITH_STAND = registerItem("ballista_base_with_stand", BallistaBaseWithStandItem::new);
	public final static Item BALLISTA_BOW = registerItem("ballista_bow", BallistaBowItem::new);

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
	public final static Item TURRET_REMOVER = registerItem("turret_remover", TurretRemoverItem::new);

	//////////////////////////////
	// REGISTRY RELATED METHODS //
	//////////////////////////////

	/**
	 * Registers a turret item which spawns a turret entity.
	 * @param name The name of the item. (e.g. "cannon_turret")
	 * @param entityType The entity type of the turret. (e.g. {@code ModEntities.CANNON_TURRET})
	 * @param factory The factory method to create the turret item. Usually a lambda expression like {@code CannonTurretItem::new}.
	 * @return The registered item.
	 */
	private static Item registerItem(String name, EntityType<? extends MobEntity> entityType, TurretItemFactory<? extends TurretItem> factory) {
		return RegistryUtil.registerItem(
			name,
			(settings) -> factory.create(entityType, settings),
			new Settings()
		);
	}

	/**
	 * Registers a normal item such as ingredients, tools, etc.
	 * @param name The name of the item. (e.g. "cannon_base")
	 * @param factory The factory method to create the item. Usually a lambda expression like {@code CannonBaseItem::new}.
	 * @return The registered item.
	 */
	private static Item registerItem(String name, ItemFactory<? extends Item> factory) {
		return RegistryUtil.registerItem(
			name,
			factory::create,
			new Settings()
		);
	}

	/**
	 * Registers a block item.
	 * @param block The block to register as an item.
	 * @return The registered item.
	 */
	private static Item registerItem(Block block) {
		return RegistryUtil.registerItem(block);
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

		Arrays.stream(DM_TRAPS).iterator().forEachRemaining(
			(item) -> ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMTR_KEY).register(
				(content) -> content.add(item)
			)
		);

		Arrays.stream(DM_TURRETS).iterator().forEachRemaining(
			(item) -> ItemGroupEvents.modifyEntriesEvent(ModItemGroups.DMTT_KEY).register(
				(content) -> content.add(item)
			)
		);

		Arrays.stream(FUEL_ITEMS).iterator().forEachRemaining((item) -> FuelRegistryEvents.BUILD.register((builder, _) -> {
			// Verifies whether the item is a valid fuel item. Ignores the item if it is not.
			if (item instanceof FuelItem)
				builder.add(item, ((FuelItem) item).getFuelTime());
			else
				DefensiveMeasures.LOGGER.warn("Item {} is not a valid fuel item.", item.getName().getString());
		}));
	}

	private interface TurretItemFactory<T extends TurretItem> {
		T create(EntityType<? extends MobEntity> entityType, Settings settings);
	}

	private interface ItemFactory<T extends Item> {
		T create(Settings settings);
	}

	static {
		DM_ITEMS = new Item[] {
			// CANNON
			CANNON_BASE,
			CANNON_HEAD,
			CANNON_STAND,
			UNFINISHED_CANNON_HEAD,

			// BALLISTA
			BALLISTA_ARROW,
			BALLISTA_BASE,
			BALLISTA_BASE_WITH_STAND,
			BALLISTA_BOW,

			// MACHINE GUN
//			AMMO_CASE,
//			AMMO_ROUNDS,
//			MACHINE_GUN_BASE,
//			MACHINE_GUN_HEAD,
//			MACHINE_GUN_STAND
		};

		DM_EQUIPMENTS = new Item[] {
			TURRET_REMOVER
		};

		DM_TRAPS = new Item[] {
			ARROWHEAD
		};

		DM_TURRETS = new Item[] {
			CANNON_TURRET,
			BALLISTA_TURRET,
//			MG_TURRET
		};

		FUEL_ITEMS = new Item[] {
			// CANNON
			CANNON_STAND,
			CANNON_BASE,

			// BALLISTA
			BALLISTA_ARROW,
			BALLISTA_BASE,
			BALLISTA_BASE_WITH_STAND,
			BALLISTA_BOW
		};
	}
}
