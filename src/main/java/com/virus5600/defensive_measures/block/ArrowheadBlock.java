package com.virus5600.defensive_measures.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.state.properties.ModProperties;

import java.util.Objects;

/**
 * Arrowhead blocks are blocks that deal damage to entities that step on them.
 * <br><br>
 * Similar to how magma blocks deal damage to entities that step on them,
 * arrowhead blocks deal damage to entities that step on them. This, however,
 * does not discriminate against sneaking entities, meaning that sneaking
 * entities will still take damage from arrowhead blocks unlike magma blocks.
 * <br><br>
 * Arrowhead blocks deal 2 damage to entities that step on them. This could
 * be changed by any subclass of this block by simply changing the value of
 * the {@link #damageDealt} field.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.1.0
 */
public class ArrowheadBlock extends Block implements Waterloggable {
	public static final MapCodec<ArrowheadBlock> CODEC = createCodec(ArrowheadBlock::new);

	public static int MAX_HEADS = 4;

	public static final IntProperty ARROWHEADS = ModProperties.ARROWHEADS;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

	private static final VoxelShape ONE_HEAD;
	private static final VoxelShape TWO_HEADS;
	private static final VoxelShape THREE_HEADS;
	private static final VoxelShape FOUR_HEADS;
	/**
	 * Defines the block hitbox of Arrowhead based on the amount of arrowhead
	 * block placed in the same space.
	 */
	private static final VoxelShape[] SHAPES_BY_HEAD;

	/**
	 * Defines the amount of damage that the arrowhead block will deal to entities.
	 */
	protected int damageDealt = 2;

	public ArrowheadBlock(Settings settings) {
		super(settings);

		settings.hardness(1.5f)
			.resistance(1.0f)
			.nonOpaque();

		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(ARROWHEADS, 1)
				.with(WATERLOGGED, false)
		);
	}

	@Override
	protected boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.shouldCancelInteraction()
			&& context.getStack().getItem() == this.asItem()
			&& state.get(ARROWHEADS) < MAX_HEADS
			|| super.canReplace(state, context);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES_BY_HEAD[state.get(ARROWHEADS) - 1];
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ARROWHEADS, WATERLOGGED, FACING);
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return hasTopRim(world, pos) || Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (state.get(WATERLOGGED)) {
			tickView.scheduleFluidTick(
				pos,
				Fluids.WATER,
				Fluids.WATER.getTickRate(world)
			);
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			BlockState blockState = state.with(WATERLOGGED, true);

			world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
			world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));

			return true;
		} else {
			return false;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld()
			.getBlockState(ctx.getBlockPos());

		if (blockState.isOf(this)) {
			Direction dir = ctx.getHorizontalPlayerFacing().getOpposite();

			return blockState.cycle(ARROWHEADS)
				.with(FACING, dir);
		} else {
			FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

			boolean isFluidWater = fluidState.getFluid() == Fluids.WATER;
			return Objects.requireNonNull(super.getPlacementState(ctx))
				.with(WATERLOGGED, isFluidWater);
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			if (world instanceof ServerWorld serverWorld) {
				DamageSource dmgSrc = ModDamageSources.create(world, ModDamageTypes.ARROWHEAD);
				livingEntity.damage(serverWorld, dmgSrc, this.getDamageDealt(state));
			}
		}

		super.onSteppedOn(world, pos, state, entity);
	}

	@Override
	public MapCodec<ArrowheadBlock> getCodec() {
		return CODEC;
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	/**
	 * Returns the final damage to be dealt when this block is stepped on.
	 *
	 * @param state The block state of this arrowhead block.
	 *
	 * @return The final damage to be dealt when this block is stepped on.
	 */
	public float getDamageDealt(BlockState state) {
		return this.damageDealt * state.get(ARROWHEADS);
	}

	static {
		ONE_HEAD = Block.createCuboidShape(6, 0, 6, 10, 3, 10);
		TWO_HEADS = Block.createCuboidShape(6, 0, 3, 10, 3, 13);
		THREE_HEADS = Block.createCuboidShape(3, 0, 3, 13, 3, 13);
		FOUR_HEADS = Block.createCuboidShape(3, 0, 3, 13, 3, 13);

		SHAPES_BY_HEAD = new VoxelShape[] {
			ONE_HEAD,
			TWO_HEADS,
			THREE_HEADS,
			FOUR_HEADS
		};
	}
}
