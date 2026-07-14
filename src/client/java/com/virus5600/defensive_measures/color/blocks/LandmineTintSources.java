package com.virus5600.defensive_measures.color.blocks;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LandmineTintSources implements BlockTintSource {
	@Override
	public int color(BlockState state) {
		return 0x4B5320;
	}

	@Override
	public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
		BlockState stateBelow = level.getBlockState(pos.below());
		int color = 0x4B5320;

		if (stateBelow.is(BlockTags.SAND)) {
			color = 0xE3DBB0;
		}
		else if (stateBelow.is(BlockTags.SNOW)) {
			color = 0xEEFDFF;
		}
		else if (stateBelow.is(BlockTags.BASE_STONE_OVERWORLD) || stateBelow.getBlock() == Blocks.GRAVEL) {
			color = 0x7D7D7D;
		}

		return color;
	}
}
