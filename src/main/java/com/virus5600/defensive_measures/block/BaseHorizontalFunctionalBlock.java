package com.virus5600.defensive_measures.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import org.jspecify.annotations.Nullable;

/**
 * An extension of the {@link BaseFunctionalBlock} class that is used for blocks that have a
 * horizontal orientation. This class is abstracted, requiring its children to mandatorily
 * implement methods that will serve to support the functionality of this directional block
 * implmenetation.
 * <br><br>
 * With how this class already extends {@link BaseFunctionalBlock}, which only extends
 * {@link Block}, this abstract class must re-implement the already defined methods and stuff from
 * the {@link HorizontalDirectionalBlock}, which is an abstract class (preventing this class from
 * extending or implementing said class).
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.2.0
 *
 * @see net.minecraft.world.level.block.HorizontalDirectionalBlock
 */
public abstract class BaseHorizontalFunctionalBlock extends BaseFunctionalBlock {
	public static final EnumProperty<Direction> FACING;

	protected BaseHorizontalFunctionalBlock(Properties settings) {
		super(settings);
	}

	// /////// //
	// METHODS //
	// /////// //

	protected BlockState rotate(final BlockState state, final Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	protected BlockState mirror(final BlockState state, final Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	protected BlockState updateShape(
		final BlockState state, final LevelReader level,
		final ScheduledTickAccess ticks, final BlockPos pos,
		final Direction directionToNeighbor, final BlockPos neighborPos,
		final BlockState neighborState, final RandomSource random
	) {
		if (this instanceof TwoBlockWide<?> block) {
			boolean isThisMainPart = state.getValue(block.getPartProperty()) == block.getDefaultPart();
			BlockPos companionPos = pos.relative(isThisMainPart ?
				block.getOtherPartDirection(state) :
				block.getMainPartDirection(state)
			);

			System.out.println("[updateShape] pos=" + pos
				+ " part=" + state.getValue(block.getPartProperty())
				+ " isThisMainPart=" + isThisMainPart
				+ " companionPos=" + companionPos
				+ " neighborPos=" + neighborPos
				+ " neighborState.is(this)=" + neighborState.is(this)
				+ " neighborPart=" + (neighborState.is(this) ? neighborState.getValue(block.getPartProperty()) : "N/A"));

			if (
				!neighborPos.equals(companionPos)
				|| neighborState.is(this)
			) {
				return state;
			}

			return Blocks.AIR.defaultBlockState();
		}

		return super.updateShape(
			state, level, ticks,
			pos, directionToNeighbor,
			neighborPos, neighborState,
			random
		);
	}

	/**
	 * {@inheritDoc}
	 * <hr/>
	 * Returns the current blockstate of this block when placed in the world.
	 * <br><br>
	 * This method handles both cases for the default 1x1x1 block size and for the 2-block wide
	 * blocks by simply checking if the block is a 2-block wide block by simply checking if the
	 * block class implements {@link TwoBlockWide} interface.
	 * method.
	 *
	 * @param ctx The context of the block placement.
	 *
	 * @return The blockstate of this block when placed in the world, or {@code null} if the block cannot be placed.
	 *
	 * @see TwoBlockWide
	 */
	public BlockState getStateForPlacement(final BlockPlaceContext ctx) {
		Direction facing = ctx.getHorizontalDirection();

		if (this instanceof TwoBlockWide<?> block) {
			Direction dir = block.getOtherPartDirection(facing);
			Level level = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();
			BlockPos relative = pos.relative(dir);

			boolean canBeReplaced = level.getBlockState(relative).canBeReplaced(ctx);
			boolean isWithinBounds = level.getWorldBorder().isWithinBounds(relative);

			return canBeReplaced && isWithinBounds ?
				this.defaultBlockState()
					.setValue(FACING, facing) : null;
		}
		else {
			return this.defaultBlockState()
				.setValue(FACING, facing.getOpposite());
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (this instanceof TwoBlockWide<?> block) {
			return super.playerWillDestroy(
				level, pos,
				TwoBlockWide.playerWillDestroy(
					level, pos,
					state, player, block
				), player
			);
		}

		return super.playerWillDestroy(
			level, pos,
			state, player
		);
	}

	public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
		super.setPlacedBy(level, pos, state, by, itemStack);

		if (this instanceof TwoBlockWide<?> block && !level.isClientSide()) {
			if (state.getValue(block.getPartProperty()) == block.getDefaultPart()) {
				BlockPos otherPos = pos.relative(block.getOtherPartDirection(state));

				TwoBlockWide.placeOtherHalf(
					otherPos, state, level, block
				);
			}
		}
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		FACING = BlockStateProperties.HORIZONTAL_FACING;
	}
}
