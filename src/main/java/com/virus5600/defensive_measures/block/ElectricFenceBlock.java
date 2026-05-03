package com.virus5600.defensive_measures.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.registry.tag.ModBlockTags;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class ElectricFenceBlock extends HorizontalConnectingBlock {
	public static final MapCodec<ElectricFenceBlock> CODEC = createCodec(ElectricFenceBlock::new);

	private final Function<BlockState, VoxelShape> cullingShapeFunction;

	public ElectricFenceBlock(Settings settings) {
		super(4.0F, 16.0F, 4.0F, 16.0F, 24.0F, settings);

		this.setDefaultState(
			this.getStateManager()
				.getDefaultState()
				.with(NORTH, false)
				.with(EAST, false)
				.with(WEST, false)
				.with(SOUTH, false)
				.with(WATERLOGGED, false)
		);

		this.cullingShapeFunction = this.createShapeFunction(
			4.0F, 16.0F,
			2.0F, 6.0F, 15.0F
		);
	}

	@Override
	protected VoxelShape getCullingShape(BlockState state) {
		return this.cullingShapeFunction.apply(state);
	}

	@Override
	protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getOutlineShape(state, world, pos, context);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return true;
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
		if (entity instanceof LivingEntity) {
			if (world instanceof ServerWorld serverWorld) {
				// Damage the entity
				DamageSource dmgSrc = ModDamageSources.create(world, ModDamageTypes.ELECTRICITY);
				entity.damage(serverWorld, dmgSrc, this.getDamageDealt(state));

				// Spawn Electric Particles
				serverWorld.spawnParticles(
					ParticleTypes.ELECTRIC_SPARK,
					entity.getX(), entity.getY() + 1, entity.getZ(),
					3, 0.25, 0.25, 0.25,
					(serverWorld.getRandom().nextBetween(-2, 2) / 10f)
				);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
		if (state.get(WATERLOGGED)) {
			tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return direction.getAxis().isHorizontal() ?
			state.with(
				FACING_PROPERTIES.get(direction),
				this.canConnect(
					neighborState,
					neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite()),
					direction.getOpposite())) :
			super.getStateForNeighborUpdate(
				state, world, tickView,
				pos, direction,
				neighborPos, neighborState,
				random);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();

		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);

		return super.getPlacementState(ctx)
			.with(
				NORTH,
				this.canConnect(
					blockState,
					blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH),
					Direction.SOUTH))
			.with(
				EAST,
				this.canConnect(
					blockState2,
					blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.WEST),
					Direction.WEST))
			.with(
				SOUTH,
				this.canConnect(
					blockState3,
					blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.NORTH),
					Direction.NORTH))
			.with(
				WEST,
				this.canConnect(
					blockState4,
					blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.EAST),
					Direction.EAST))
			.with(
				WATERLOGGED,
				fluidState.getFluid() == Fluids.WATER);
	}

	private boolean canConnectToFence(BlockState state) {
		return (state.isIn(BlockTags.FENCES) &&
			state.isIn(BlockTags.WOODEN_FENCES) == this.getDefaultState().isIn(BlockTags.WOODEN_FENCES)) ||
			state.isIn(ModBlockTags.ELECTRIC_FENCE)
			;
	}

	public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
		Block block = state.getBlock();
		boolean canConnectToFence = this.canConnectToFence(state);
		boolean isFenceGate = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, dir);

		return !cannotConnect(state) && neighborIsFullSquare || canConnectToFence || isFenceGate;
	}

	@Override
	public MapCodec<ElectricFenceBlock> getCodec() {
		return CODEC;
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	/**
	 * Returns the final damage to be dealt when this block gets in contact with an entitiy.
	 *
	 * @param state The block state of this electric fence block.
	 * @return The final damage to be dealt when this block gets in contact with an entity.
	 */
	public float getDamageDealt(BlockState state) {
		return 3;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}

	@Override
	public ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.empty();
	}
}
