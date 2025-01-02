package com.virus5600.defensive_measures.item.equipments;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TurretRemoverItem extends Item {

	public TurretRemoverItem(Settings settings) {
		super(
			// RARITY
			settings.rarity(Rarity.COMMON)
		);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		// CAN'T DESTROY IN CREATIVE
		return !miner.isCreative();
	}
}
