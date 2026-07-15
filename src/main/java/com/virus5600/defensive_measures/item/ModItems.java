package com.virus5600.defensive_measures.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.item.equipments.TurretRemoverItem;
import com.virus5600.defensive_measures.item.interfaces.FuelItem;
import com.virus5600.defensive_measures.item.turrets.TurretItem;
import com.virus5600.defensive_measures.item.turrets.tier_0.pellet_turret.*;
import com.virus5600.defensive_measures.item.turrets.tier_1.ballista.*;
import com.virus5600.defensive_measures.item.turrets.tier_1.cannon.*;
import com.virus5600.defensive_measures.item.turrets.tier_1.mg_turret.*;
import com.virus5600.defensive_measures.item.turrets.tier_2.aa_turret.*;
import com.virus5600.defensive_measures.item.turrets.tier_2.flame_turret.*;
import com.virus5600.defensive_measures.item.turrets.tier_2.missile_turret.*;
import com.virus5600.defensive_measures.item.turrets.tier_3.*;

import java.util.Arrays;
import java.util.function.Function;

/**
 * A class that contains all the items in the mod.
 * <br><br>
 * This class is used to register all the items in the mod, from turrets, to equipments, to traps, block items,
 * ingredients, etc. This class is also used to register the items to their respective item groups to categorize
 * them in the creative inventory.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModItems {
	// VANILLA GROUPS
	public final static Item[] FUNCTIONAL_ITEMS;

	// MODDED GROUPS
	public final static Item[] DM_ITEMS;
	public final static Item[] DM_EQUIPMENTS;
	public final static Item[] DM_TRAPS;
	public final static Item[] DM_TURRETS;
	public final static Item[] FUEL_ITEMS;

	// /////// //
	// TURRETS //
	// /////// //

	// CANNON
	public final static Item CANNON_TURRET = registerItem("cannon_turret", ModEntities.CANNON_TURRET, CannonTurretItem::new);
	public final static Item CANNON_BASE = registerItem("cannon_base", CannonBaseItem::new);
	public final static Item CANNON_HEAD = registerItem("cannon_head", CannonHeadItem::new);
	public final static Item CANNON_STAND = registerItem("cannon_stand", CannonStandItem::new);
	public final static Item UNFINISHED_CANNON_HEAD = registerItem("unfinished_cannon_head", UnfinishedCannonHeadItem::new);

	// BALLISTA
	public final static Item BALLISTA_TURRET = registerItem("ballista", ModEntities.BALLISTA_TURRET, BallistaTurretItem::new);
	public final static Item BOLT_HEAD = registerItem(ModBlocks.BOLT_HEAD);
	public final static Item BALLISTA_BOLT = registerItem("ballista_bolt", BallistaBoltItem::new);
	public final static Item BALLISTA_BASE = registerItem("ballista_base", BallistaBaseItem::new);
	public final static Item BALLISTA_BASE_WITH_STAND = registerItem("ballista_base_with_stand", BallistaBaseWithStandItem::new);
	public final static Item BALLISTA_BOW = registerItem("ballista_bow", BallistaBowItem::new);

	// MACHINE GUN
	public final static Item MG_TURRET = registerItem("mg_turret", ModEntities.MG_TURRET, MGTurretItem::new);
	public final static Item MG_AMMO_CASE = registerItem("mg_ammo_case", MGAmmoCaseItem::new);
	public final static Item MG_AMMO_ROUNDS = registerItem("mg_ammo_rounds", MGAmmoRoundsItem::new);
	public final static Item MG_BASE = registerItem("mg_base", MGBaseItem::new);
	public final static Item MG_HEAD = registerItem("mg_head", MGHeadItem::new);
	public final static Item MG_STAND = registerItem("mg_stand", MGStandItem::new);

	// AA TURRET
	public final static Item AA_TURRET = registerItem("aa_turret", ModEntities.AA_TURRET, AATurretItem::new);
	public final static Item AA_TURRET_BASE = registerItem("aa_turret_base", AABaseItem::new);
	public final static Item AA_TURRET_TRAVERSE_PLATFORM = registerItem("aa_turret_traverse_platform", AATraversePlatformItem::new);
	public final static Item AA_TURRET_GUN_BARREL = registerItem("aa_turret_gun_barrel", AAGunBarrelItem::new);

	// FLAME TURRET
	public final static Item FLAME_TURRET = registerItem("flame_turret", ModEntities.FLAME_TURRET, FlameTurretItem::new);
	public final static Item FLAME_TURRET_BASE = registerItem("flame_turret_base", FlameBaseItem::new);
	public final static Item FLAME_TURRET_HEAD = registerItem("flame_turret_head", FlameHeadItem::new);
	public final static Item FLAME_TURRET_NOZZLE = registerItem("flame_turret_nozzle", FlameNozzleItem::new);

	// MISSILE TURRET
	public final static Item MISSILE_TURRET = registerItem("missile_turret", ModEntities.MISSILE_TURRET, MissileTurretItem::new);
	public final static Item MISSILE_TURRET_BASE = registerItem("missile_turret_base", MissileBaseItem::new);
	public final static Item MISSILE_TURRET_COLUMN = registerItem("missile_turret_column", MissileColumnItem::new);
	public final static Item MISSILE_TURRET_BATTERY = registerItem("missile_turret_battery", MissileBatteryItem::new);
	public final static Item MISSILE_TURRET_RADAR = registerItem("missile_turret_radar", MissileRadarItem::new);

	// PELLET TURRET
	public final static Item PELLET_TURRET = registerItem("pellet_turret", ModEntities.PELLET_TURRET, PelletTurretItem::new);

	// ////////// //
	// EQUIPMENTS //
	// ////////// //

	// TURRET REMOVER
	public final static Item TURRET_REMOVER = registerToolItem("turret_remover", (settings) -> new TurretRemoverItem(ModToolMaterials.TURRET_REMOVER, 0.0f, 0.0f, settings));

	// ////// //
	// BLOCKS //
	// ////// //

	// TURRET ASSEMBLY STATION
	public final static Item TURRET_ASSEMBLY_STATION = registerItem(ModBlocks.TURRET_ASSEMBLY_STATION, new Properties().rarity(Rarity.UNCOMMON));
	public final static Item ELECTRIC_FENCE = registerItem(ModBlocks.ELECTRIC_FENCE, new Properties().rarity(Rarity.UNCOMMON));

	// WORKSHOP
	public final static Item WORKSHOP = registerItem(ModBlocks.WORKSHOP, new Properties().rarity(Rarity.RARE));

	// FABRICATION MATRIX
	public final static Item FABRICATION_MATRIX = registerItem(ModBlocks.FABRICATION_MATRIX, new Properties().rarity(Rarity.EPIC));

	// LANDMINES
	public final static Item M14_ANTI_PERSONNEL_MINE = registerItem(ModBlocks.ANTI_PERSONNEL_MINE_M14, new Properties().rarity(Rarity.UNCOMMON));
	public final static Item ANTI_TANK_MINE = registerItem(ModBlocks.ANTI_TANK_MINE, new Properties().rarity(Rarity.UNCOMMON));

	// //// //
	// MISC //
	// //// //

	public final static Item GEAR = registerItem("gear", GearItem::new);

	// //////////////////////// //
	// REGISTRY RELATED METHODS //
	// //////////////////////// //

	/**
	 * Registers a turret item which spawns a turret entity.
	 * @param name The name of the item. (e.g. {@code "cannon_turret"})
	 * @param entityType The entity type of the turret. (e.g. {@code ModEntities.CANNON_TURRET})
	 * @param factory The factory method to create the turret item. Usually a lambda expression like {@code CannonTurretItem::new}.
	 * @return The registered item.
	 */
	private static Item registerItem(String name, EntityType<? extends Mob> entityType, TurretItemFactory<? extends TurretItem> factory) {
		return RegistryHelper.registerItem(
			name,
			(settings) -> factory.create(entityType, settings),
			new Properties()
		);
	}

	/**
	 * Registers a tool item such as shovels and weapons.
	 *
	 * @param name The name of the item. (e.g. "turret_remover")
	 * @param factory The factory method to create the item. Usually a lambda expression like {@code (Function) ((settings) -> new TurretRemoverItem(settings, ...))}.
	 *
	 * @return The registered item.
	 */
	private static Item registerToolItem(String name, Function<Item.Properties, Item> factory) {
		return RegistryHelper.registerItem(
			name,
			factory
		);
	}

	/**
	 * Registers a normal item such as ingredients, etc.
	 * @param name The name of the item. (e.g. {@code "cannon_base"})
	 * @param factory The factory method to create the item. Usually a lambda expression like {@code CannonBaseItem::new}.
	 * @return The registered item.
	 */
	private static Item registerItem(String name, ItemFactory<? extends Item> factory) {
		return RegistryHelper.registerItem(
			name,
			factory::create,
			new Properties()
		);
	}

	/**
	 * Registers a block item.
	 * @param block The block to register as an item.
	 * @return The registered item.
	 */
	private static Item registerItem(Block block) {
		return RegistryHelper.registerItem(block);
	}

	/**
	 * Registers a block item.
	 * @param block The block to register as an item.
	 * @param props The properties of the item.
	 * @return The registered item.
	 */
	private static Item registerItem(Block block, Properties props) {
		return RegistryHelper.registerItem(block, props);
	}

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING ITEMS TO ITEM GROUPS...");

		Arrays.stream(FUNCTIONAL_ITEMS).iterator().forEachRemaining(
			(item) -> CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(
				(output) -> output.accept(item)
			)
		);

		Arrays.stream(DM_ITEMS).iterator().forEachRemaining(
			(item) -> CreativeModeTabEvents.modifyOutputEvent(ModItemGroups.DMI_KEY).register(
				(output) -> output.accept(item)
			)
		);

		Arrays.stream(DM_EQUIPMENTS).iterator().forEachRemaining(
			(item) -> CreativeModeTabEvents.modifyOutputEvent(ModItemGroups.DME_KEY).register(
				(output) -> output.accept(item)
			)
		);

		Arrays.stream(DM_TRAPS).iterator().forEachRemaining(
			(item) -> CreativeModeTabEvents.modifyOutputEvent(ModItemGroups.DMTR_KEY).register(
				(output) -> output.accept(item)
			)
		);

		Arrays.stream(DM_TURRETS).iterator().forEachRemaining(
			(item) -> CreativeModeTabEvents.modifyOutputEvent(ModItemGroups.DMTT_KEY).register(
				(output) -> output.accept(item)
			)
		);

		Arrays.stream(FUEL_ITEMS).iterator().forEachRemaining((item) -> FuelValueEvents.BUILD.register((builder, _) -> {
			// Verifies whether the item is a valid fuel item. Ignores the item if it is not.
			if (item instanceof FuelItem) {
				builder.add(item, ((FuelItem) item).getFuelTime());
			}
			else {
				DefensiveMeasures.LOGGER.warn("Item {} is not a valid fuel item.", item.getName(item.getDefaultInstance()).getString());
			}
		}));
	}

	private interface TurretItemFactory<T extends TurretItem> {
		T create(EntityType<? extends Mob> entityType, Properties settings);
	}

	private interface ItemFactory<T extends Item> {
		T create(Properties settings);
	}

	static {
		FUNCTIONAL_ITEMS = new Item[] {
			TURRET_ASSEMBLY_STATION,
			WORKSHOP,
			FABRICATION_MATRIX
		};

		DM_ITEMS = new Item[] {
			// CANNON
			CANNON_BASE,
			CANNON_HEAD,
			CANNON_STAND,
			UNFINISHED_CANNON_HEAD,

			// BALLISTA
			BALLISTA_BOLT,
			BALLISTA_BASE,
			BALLISTA_BASE_WITH_STAND,
			BALLISTA_BOW,

			// MACHINE GUN
			MG_AMMO_CASE,
			MG_AMMO_ROUNDS,
			MG_BASE,
			MG_HEAD,
			MG_STAND,

			// AA TURRET
			AA_TURRET_BASE,
			AA_TURRET_TRAVERSE_PLATFORM,
			AA_TURRET_GUN_BARREL,

			// FLAME TURRET
			FLAME_TURRET_BASE,
			FLAME_TURRET_HEAD,
			FLAME_TURRET_NOZZLE,

			// MISSILE TURRET
			MISSILE_TURRET_BASE,
			MISSILE_TURRET_COLUMN,
			MISSILE_TURRET_BATTERY,
			MISSILE_TURRET_RADAR,

			// MISC
			GEAR,
		};

		DM_EQUIPMENTS = new Item[] {
			TURRET_REMOVER
		};

		DM_TRAPS = new Item[] {
			BOLT_HEAD,
			ELECTRIC_FENCE,

			M14_ANTI_PERSONNEL_MINE,
			ANTI_TANK_MINE
		};

		DM_TURRETS = new Item[] {
			// TIER 0
			PELLET_TURRET,

			// TIER 1
			CANNON_TURRET,
			BALLISTA_TURRET,
			MG_TURRET,

			// TIER 2
			AA_TURRET,
			FLAME_TURRET,
			MISSILE_TURRET
		};

		FUEL_ITEMS = new Item[] {
			// PELLET TURRET
			PELLET_TURRET,

			// CANNON TURRET
			CANNON_STAND,
			CANNON_BASE,

			// BALLISTA
			BALLISTA_TURRET,
			BALLISTA_BOLT,
			BALLISTA_BASE,
			BALLISTA_BASE_WITH_STAND,
			BALLISTA_BOW,
		};
	}
}
