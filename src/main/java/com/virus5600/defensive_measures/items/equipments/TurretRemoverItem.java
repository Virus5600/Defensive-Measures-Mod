package com.virus5600.defensive_measures.items.equipments;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TurretRemoverItem extends ToolItem {

	public TurretRemoverItem(ToolMaterial material, Settings settings) {
		super(
			material,
			// RARITY
			settings.rarity(Rarity.COMMON)
		);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		// CAN'T DESTROY IN CREATIVE
		return !miner.isCreative();
	}

	@Override
	public int getEnchantability() {
		return this.getMaterial().getEnchantability();
	}
}
