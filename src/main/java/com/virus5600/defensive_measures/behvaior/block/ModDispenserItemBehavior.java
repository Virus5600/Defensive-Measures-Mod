package com.virus5600.defensive_measures.behvaior.block;

import net.minecraft.core.dispenser.SpawnEggItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.behvaior.block.dispenser.TurretRemoverDispenseItemBehavior;
import com.virus5600.defensive_measures.item.ModItems;

public class ModDispenserItemBehavior {
	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING DISPENSER BEHAVIORS FOR {}...", DefensiveMeasures.MOD_NAME);

		// v1.2.0-beta
		DispenserBlock.registerBehavior(ModItems.TURRET_REMOVER, new TurretRemoverDispenseItemBehavior());
		for (Item item : ModItems.DM_TURRETS) {
			DispenserBlock.registerBehavior(item, new SpawnEggItemBehavior());
		}

	}
}
