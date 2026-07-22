package com.virus5600.defensive_measures.color.blocks;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import com.virus5600.defensive_measures.block.traps.BaseLandmineBlock;

public class LandmineTintSources implements BlockTintSource {
	private static final int GREEN = 0x7CB342;
	private static final int LIGHT_YELLOW = 0xE7E4BB;
	private static final int YELLOW = 0xFFEE33;
	private static final int WHITE = 0xFDFDFD;
	private static final int GRAY = 0x8F8F8F;
	private static final int BLUE = 0x4D6B8F;

	@Override
	public int color(BlockState state) {
		return GREEN;
	}

	@Override
	public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
		BlockState stateBelow = level.getBlockState(pos.below());
		int color = GREEN;

		if (
			stateBelow.is(ConventionalBlockTags.BLUE_DYED)
			|| stateBelow.is(ConventionalBlockTags.LIGHT_BLUE_DYED)
			|| stateBelow.is(ConventionalBlockTags.CYAN_DYED)
			|| stateBelow.is(BlockTags.ICE)
			|| stateBelow.getBlock() == Blocks.LAPIS_BLOCK
			|| stateBelow.getBlock() == Blocks.DIAMOND_BLOCK
			|| state.getValue(BaseLandmineBlock.WATERLOGGED)
		) {
			color = BLUE;
		}
		else if (
			stateBelow.is(BlockTags.SAND)
		) {
			color = LIGHT_YELLOW;
		}
		else if (
			stateBelow.is(ConventionalBlockTags.YELLOW_DYED)
			|| stateBelow.getBlock() == Blocks.GOLD_BLOCK
		) {
			color = YELLOW;
		}
		else if (
			stateBelow.is(BlockTags.SNOW)
			|| stateBelow.is(ConventionalBlockTags.WHITE_DYED)
			|| stateBelow.getBlock() == Blocks.IRON_BLOCK
		) {
			color = WHITE;
		}
		else if (
			stateBelow.is(BlockTags.BASE_STONE_OVERWORLD)
			|| stateBelow.is(ConventionalBlockTags.COBBLESTONES)
			|| stateBelow.is(ConventionalBlockTags.ORES)
			|| stateBelow.is(ConventionalBlockTags.GRAY_DYED)
			|| stateBelow.is(ConventionalBlockTags.LIGHT_GRAY_DYED)
			|| stateBelow.is(Blocks.GRAVEL)
			|| stateBelow.is(Blocks.BEDROCK)
		) {
			color = GRAY;
		}

		return color;
	}
}
