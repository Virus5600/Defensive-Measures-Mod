package com.virus5600.DefensiveMeasures.item;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.entity.ModEntities;
import com.virus5600.DefensiveMeasures.item.equipments.TurretRemoverItem;
import com.virus5600.DefensiveMeasures.item.turrets.ballista.*;
import com.virus5600.DefensiveMeasures.item.turrets.cannon.*;
import com.virus5600.DefensiveMeasures.item.turrets.machine_gun.*;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
	private final static Settings SETTING_DMI = new FabricItemSettings().group(ModItemGroup.DEFENSIVE_MEASURES_ITEMS);
	private final static Settings SETTING_DME = new FabricItemSettings().group(ModItemGroup.DEFENSIVE_MEASURES_EQUIPMENTS);
	private final static Settings SETTING_DMT = new FabricItemSettings().group(ModItemGroup.DEFENSIVE_MEASURES_TURRETS);

	/////////////
	// TURRETS //
	/////////////

	// CANNON
	public final static Item CANNON_TURRET = new CannonTurretItem(ModEntities.CANNON_TURRET, SETTING_DMT);
	public final static Item CANNON_BASE = new CannonBaseItem(SETTING_DMI);
	public final static Item CANNON_HEAD = new CannonHeadItem(SETTING_DMI);
	public final static Item CANNON_STAND = new CannonStandItem(SETTING_DMI);
	public final static Item UNFINISHED_CANNON_HEAD = new UnfinishedCannonHeadItem(SETTING_DMI);

	// BALLISTA
	public final static Item BALLISTA = new BallistaTurretItem(ModEntities.BALLISTA, SETTING_DMT);
	public final static Item BALLISTA_ARROW = new BallistaArrowItem(SETTING_DMI);
	public final static Item BALLISTA_BASE = new BallistaBaseItem(SETTING_DMI);
	public final static Item BALLISTA_BASE_WITH_STAND = new BallistaBaseWithStandItem(SETTING_DMI);
	public final static Item BALLISTA_BOW = new BallistaBowItem(SETTING_DMI);

	// MACHINE GUN
	public final static Item MG_TURRET = new MachineGunTurretItem(ModEntities.MG_TURRET, SETTING_DMT);
	public final static Item AMMO_CASE = new AmmoCaseItem(SETTING_DMI);
	public final static Item AMMO_ROUNDS = new AmmoRoundsItem(SETTING_DMI);
	public final static Item MACHINE_GUN_BASE = new MachineGunBaseItem(SETTING_DMI);
	public final static Item MACHINE_GUN_HEAD = new MachineGunHeadItem(SETTING_DMI);
	public final static Item MACHINE_GUN_STAND = new MachineGunStandItem(SETTING_DMI);

	////////////////
	// EQUIPMENTS //
	////////////////

	// TURRET REMOVER
	public final static ToolItem TURRET_REMOVER = new TurretRemoverItem(ModToolMaterials.TURRET_REMOVER, SETTING_DME);


	private static Item registerItem(String name, Item item) {
		return Registry.register(Registry.ITEM, new Identifier(DefensiveMeasures.MOD_ID, name), item);
	}

	public static void registerModItems() {
		DefensiveMeasures.LOGGER.debug("REGISTERING ITEMS FOR " + DefensiveMeasures.MOD_NAME);

		// CANNON TURRET
		registerItem("cannon_turret", CANNON_TURRET);
		registerItem("cannon_base", CANNON_BASE);
		registerItem("cannon_head", CANNON_HEAD);
		registerItem("cannon_stand", CANNON_STAND);
		registerItem("unfinished_cannon_head", UNFINISHED_CANNON_HEAD);

		// BALLISTA
		registerItem("ballista", BALLISTA);
		registerItem("ballista_arrow", BALLISTA_ARROW);
		registerItem("ballista_base", BALLISTA_BASE);
		registerItem("ballista_base_with_stand", BALLISTA_BASE_WITH_STAND);
		registerItem("ballista_bow", BALLISTA_BOW);

		// MACHINE GUN
		registerItem("mg_turret", MG_TURRET);
		registerItem("ammo_case", AMMO_CASE);
		registerItem("ammo_rounds", AMMO_ROUNDS);
		registerItem("machine_gun_base", MACHINE_GUN_BASE);
		registerItem("machine_gun_head", MACHINE_GUN_HEAD);
		registerItem("machine_gun_stand", MACHINE_GUN_STAND);

		// EQUIPMENT
		registerItem("turret_remover", TURRET_REMOVER);
	}
}
