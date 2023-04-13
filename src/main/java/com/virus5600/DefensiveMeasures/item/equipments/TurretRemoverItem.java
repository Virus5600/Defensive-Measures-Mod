package com.virus5600.DefensiveMeasures.item.equipments;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TurretRemoverItem extends ToolItem {

	public TurretRemoverItem(final ToolMaterial material, final Settings settings) {
		super(
			material,
			// RARITY
			settings.rarity(Rarity.COMMON)
		);
	}

	@Override
	public boolean canMine(final BlockState state, final World world, final BlockPos pos, final PlayerEntity miner) {
		// CAN'T DESTROY IN CREATIVE
		return !miner.isCreative();
	}

	@Override
	public int getEnchantability() {
		return this.getMaterial().getEnchantability();
	}
}
