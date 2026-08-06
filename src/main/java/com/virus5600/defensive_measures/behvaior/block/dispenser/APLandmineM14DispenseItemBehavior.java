package com.virus5600.defensive_measures.behvaior.block.dispenser;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.block.traps.tier3.AntiPersonnelMineM14Block;

/**
 * A custom dispenser item behavior extending {@link LandmineDispenseItemBehavior<AntiPersonnelMineM14Block>},
 * specifically designed for the {@link AntiPersonnelMineM14Block M14 Anti-Personnel Mine (APM)}.
 * This behavior allows the dispenser to place the Anti-Personnel Mine M14 block in the world when
 * activated, ensuring that it is placed correctly and functions as intended. The behavior is
 * tailored to the unique properties of the M14 APM and ensures that it is placed in a valid
 * location, taking into account the surrounding environment and the specific requirements of the
 * mine.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class APLandmineM14DispenseItemBehavior extends LandmineDispenseItemBehavior<
	AntiPersonnelMineM14Block
> {
	public APLandmineM14DispenseItemBehavior() {}

	@Override
	protected AntiPersonnelMineM14Block getLandmineBlock() {
		return (AntiPersonnelMineM14Block) ModBlocks.ANTI_PERSONNEL_MINE_M14;
	}
}
