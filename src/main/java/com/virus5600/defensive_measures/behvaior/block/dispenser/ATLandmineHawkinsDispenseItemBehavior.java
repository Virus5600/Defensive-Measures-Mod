package com.virus5600.defensive_measures.behvaior.block.dispenser;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.block.traps.tier3.AntiTankMineHawkinsBlock;

/**
 * A custom dispenser item behavior extending {@link LandmineDispenseItemBehavior<AntiTankMineHawkinsBlock>},
 * specifically designed for the {@link AntiTankMineHawkinsBlock Hawkins Anti-Tank Mine (ATM)}.
 * This behavior allows the dispenser to place the Anti-Tank Mine block in the world when
 * activated, ensuring that it is placed correctly and functions as intended. The behavior is
 * tailored to the unique properties of the ATM and ensures that it is placed in a valid
 * location, taking into account the surrounding environment and the specific requirements of the
 * mine.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ATLandmineHawkinsDispenseItemBehavior extends LandmineDispenseItemBehavior<
	AntiTankMineHawkinsBlock
> {
	public ATLandmineHawkinsDispenseItemBehavior() {}

	@Override
	protected AntiTankMineHawkinsBlock getLandmineBlock() {
		return (AntiTankMineHawkinsBlock) ModBlocks.ANTI_TANK_MINE_HAWKINS;
	}
}
