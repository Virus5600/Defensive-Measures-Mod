package com.virus5600.defensive_measures.behvaior.block;

import net.minecraft.core.dispenser.SpawnEggItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.behvaior.block.dispenser.*;
import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.item.ModItems;

/**
 * A class that handles the registration of custom dispenser dispensing behavior, allowing certain
 * items to be dispensed in unique ways when placed in a dispenser block. This class is part of the
 * {@link DefensiveMeasures Defensive Measures mod} and is responsible for initializing and
 * registering the custom behaviors for specific items and blocks.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModDispenserItemBehavior {
	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING DISPENSER BEHAVIORS FOR {}...", DefensiveMeasures.MOD_NAME);

		// v1.2.0-beta
		DispenserBlock.registerBehavior(ModItems.TURRET_REMOVER, new TurretRemoverDispenseItemBehavior());
		for (Item item : ModItems.DM_TURRETS) DispenserBlock.registerBehavior(item, new SpawnEggItemBehavior());
		DispenserBlock.registerBehavior(ModBlocks.ANTI_PERSONNEL_MINE_M14, new APLandmineM14DispenseItemBehavior());
		DispenserBlock.registerBehavior(ModBlocks.ANTI_TANK_MINE_HAWKINS, new ATLandmineHawkinsDispenseItemBehavior());
	}
}
