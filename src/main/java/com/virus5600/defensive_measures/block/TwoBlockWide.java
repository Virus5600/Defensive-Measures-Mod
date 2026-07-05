package com.virus5600.defensive_measures.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * This interface serves as a contract for blocks that are two-block-wide structures, such as doors
 * or double-height plants. It defines methods to get the property representing the part of the
 * structure and to get the opposite part of the default part that was placed. Additionally, it
 * provides static methods to place the opposite half of the structure and handle its destruction
 * when a player destroys one part of it.
 * <br><br>
 * In a sense, it's code were based on how a {@link BedBlock Bed} works.
 *
 * @param <T> The type of the enum that represents the parts of the two-block-wide structure.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface TwoBlockWide<T extends Enum<T> & StringRepresentable> {
	EnumProperty<Direction> getDirectionProperty();

	/**
	 * Returns the property that represents the part of the two-block-wide structure.
	 *
	 * @return The property that represents the part of the two-block-wide structure.
	 */
	EnumProperty<T> getPartProperty();

	/**
	 * Returns the default part of the two-block-wide structure that were placed.
	 *
	 * @return The default part of the two-block-wide structure.
	 */
	T getDefaultPart();

	/**
	 * Returns the opposite part of the default part that were placed.
	 *
	 * @return The block part that is opposite of the default part that were placed.
	 */
	T getOtherPart();

	/**
	 * Returns the direction where the {@link #getDefaultPart() default part} is positoned relative
	 * to the {@link #getOtherPart() other part} that were placed.
	 *
	 * @param dir The direction of the default part that were placed.
	 *
	 * @return The direction where the default part is positioned relative to the other part that
	 * were placed.
	 */
	Direction getMainPartDirection(final Direction dir);

	/**
	 * Returns the direction where the {@link #getOtherPart() other part} is positioned relative to
	 * the {@link #getDefaultPart() default part} that were placed.
	 *
	 * @param dir The direction of the default part that were placed.
	 *
	 * @return The direction where the other part is positioned relative to the default part that
	 * were placed.
	 */
	Direction getOtherPartDirection(final Direction dir);

	// /////////////// //
	// DEFAULT METHODS //
	// /////////////// //

	/**
	 * Returns the direction where the {@link #getDefaultPart() default part} is positioned relative
	 * to the {@link #getOtherPart() other part} that were placed.
	 *
	 * @param state The block state of the other part that were placed.
	 *
	 * @return The direction where the default part is positioned relative to the other part that
	 * were placed.
	 */
	default Direction getMainPartDirection(final BlockState state) {
		Direction dir = state.getValue(getDirectionProperty());
		return this.getMainPartDirection(dir);
	}

	/**
	 * Returns the direction where the {@link #getOtherPart() other part} is positioned relative to
	 * the {@link #getDefaultPart() default part} that were placed.
	 *
	 * @param state The block state of the default part that were placed.
	 *
	 * @return The direction where the other part is positioned relative to the default part that
	 * were placed.
	 */
	default Direction getOtherPartDirection(final BlockState state) {
		Direction dir = state.getValue(this.getDirectionProperty());
		return this.getOtherPartDirection(dir);
	}

	// ////////////// //
	// STATIC METHODS //
	// ////////////// //

	/**
	 * Places the upper half of the two-block-wide structure.
	 *
	 * @param pos The position where the block should be placed.
	 * @param state The block state to place.
	 * @param level The level instance (basically the world currently being played in).
	 * @param block The two-block-wide block instance.
	 *
	 * @param <T> The type of the enum that represents the parts of the two-block-wide structure.
	 */
	static <T extends Enum<T> & StringRepresentable> void placeOtherHalf(
		BlockPos pos, BlockState state, Level level,
		TwoBlockWide<T> block
	) {
		level.setBlockAndUpdate(
			pos,
			state.setValue(
				block.getPartProperty(),
				block.getOtherPart()
			)
		);
	}

	/**
	 * Handles the destruction of a two-block-wide structure when a player destroys one part of it.
	 *
	 * @param level  The level instance (basically the world currently being played in).
	 * @param pos    The position of the block being destroyed.
	 * @param state  The block state of the block being destroyed.
	 * @param player The player who is destroying the block.
	 * @param block  The two-block-wide block instance.
	 *
	 * @return The block state of the block being destroyed.
	 *
	 * @param <T> The type of the enum that represents the parts of the two-block-wide structure.
	 */
	static <T extends Enum<T> & StringRepresentable> BlockState playerWillDestroy(
		Level level, BlockPos pos, BlockState state,
		Player player, TwoBlockWide<T> block
	) {
		if (!level.isClientSide() && player.preventsBlockDrops()) {
			T part = state.getValue(block.getPartProperty());
			T companionPart = part == block.getDefaultPart() ? block.getOtherPart() : block.getDefaultPart();

			Direction dir = getNeighborDirection(
				block.getDefaultPart(), part,
				block.getOtherPartDirection(state)
			);
			BlockPos companionPos = pos.relative(dir);
			BlockState companionState = level.getBlockState(companionPos);

			if (
				companionState.is(state.getBlock())
				&& companionState.getValue(block.getPartProperty()) == companionPart
			) {
				level.setBlock(companionPos, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, 2001, companionPos, Block.getId(companionState));
			}
		}

		return state;
	}

	/**
	 * Returns the direction of the neighbor block based on the default part, the current part,
	 * and the facing direction.
	 *
	 * @param defaultPart The default part of the two-block-wide structure.
	 * @param part        The current part of the two-block-wide structure.
	 * @param facing      The facing direction of the block.
	 *
	 * @return The direction of the neighbor block.
	 *
	 * @param <T> The type of the enum that represents the parts of the two-block-wide structure.
	 */
	private static <T extends Enum<T> & StringRepresentable> Direction getNeighborDirection(
		final T defaultPart, final T part, final Direction facing
	) {
		return part == defaultPart ? facing : facing.getOpposite();
	}
}
