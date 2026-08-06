package com.virus5600.defensive_measures.block.defenses.tier2;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.registry.tag.ModBlockTags;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A defensive block that blocks off entities from passing through through it. An additional
 * defensive feature of the block is it deals damage against entities that makes contact with it,
 * "electrecuting" them to death.
 * <br><br>
 * This block's implementation follows the implementation of the vanilla fence and wall blocks,
 * allowing it to connect to other Electric Fences.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ElectricFenceBlock extends CrossCollisionBlock {
	public static final MapCodec<ElectricFenceBlock> CODEC = simpleCodec(ElectricFenceBlock::new);

	private final Function<BlockState, VoxelShape> cullingShapeFunction;

	public ElectricFenceBlock(Properties settings) {
		super(4.0F, 16.0F, 4.0F, 16.0F, 24.0F, settings);

		this.registerDefaultState(
			this.getStateDefinition()
				.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(WEST, false)
				.setValue(SOUTH, false)
				.setValue(WATERLOGGED, false)
		);

		this.cullingShapeFunction = this.makeShapes(
			4.0F, 16.0F,
			2.0F, 6.0F, 15.0F
		);
	}

	@Override
	protected @NonNull VoxelShape getOcclusionShape(@NonNull BlockState state) {
		return this.cullingShapeFunction.apply(state);
	}

	@Override
	protected @NonNull VoxelShape getVisualShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return this.getShape(state, world, pos, context);
	}

	@Override
	protected boolean isPathfindable(@NonNull BlockState state, @NonNull PathComputationType type) {
		return true;
	}

	@Override
	protected void entityInside(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull Entity entity, @NonNull InsideBlockEffectApplier handler, boolean bl) {
		if (entity instanceof LivingEntity) {
			if (world instanceof ServerLevel serverWorld) {
				// Damage the entity
				DamageSource dmgSrc = ModDamageSources.create(world, ModDamageTypes.ELECTRICITY, (Entity) null, null);
				entity.hurtServer(serverWorld, dmgSrc, this.getDamageDealt(state));

				// Spawn Electric Particles
				serverWorld.sendParticles(
					ParticleTypes.ELECTRIC_SPARK,
					entity.getX(), entity.getY() + 1, entity.getZ(),
					3, 0.25, 0.25, 0.25,
					(serverWorld.getRandom().nextIntBetweenInclusive(-2, 2) / 10f)
				);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	protected @NonNull BlockState updateShape(BlockState state, @NonNull LevelReader world, @NonNull ScheduledTickAccess tickView, @NonNull BlockPos pos, @NonNull Direction direction, @NonNull BlockPos neighborPos, @NonNull BlockState neighborState, @NonNull RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return direction.getAxis().isHorizontal() ?
			state.setValue(
				PROPERTY_BY_DIRECTION.get(direction),
				this.canConnect(
					neighborState,
					neighborState.isFaceSturdy(world, neighborPos, direction.getOpposite()),
					direction.getOpposite())) :
			super.updateShape(
				state, world, tickView,
				pos, direction,
				neighborPos, neighborState,
				random);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockGetter blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());

		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();

		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);

		return Objects.requireNonNull(super.getStateForPlacement(ctx))
			.setValue(
				NORTH,
				this.canConnect(
					blockState,
					blockState.isFaceSturdy(blockView, blockPos2, Direction.SOUTH),
					Direction.SOUTH))
			.setValue(
				EAST,
				this.canConnect(
					blockState2,
					blockState2.isFaceSturdy(blockView, blockPos3, Direction.WEST),
					Direction.WEST))
			.setValue(
				SOUTH,
				this.canConnect(
					blockState3,
					blockState3.isFaceSturdy(blockView, blockPos4, Direction.NORTH),
					Direction.NORTH))
			.setValue(
				WEST,
				this.canConnect(
					blockState4,
					blockState4.isFaceSturdy(blockView, blockPos5, Direction.EAST),
					Direction.EAST))
			.setValue(
				WATERLOGGED,
				fluidState.getType() == Fluids.WATER);
	}

	private boolean canConnectToFence(BlockState state) {
		return (state.is(BlockTags.FENCES) &&
			state.is(BlockTags.WOODEN_FENCES) == this.defaultBlockState().is(BlockTags.WOODEN_FENCES)) ||
			state.is(ModBlockTags.ELECTRIC_FENCE)
			;
	}

	public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
		Block block = state.getBlock();
		boolean canConnectToFence = this.canConnectToFence(state);
		boolean isFenceGate = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, dir);

		return !isExceptionForConnection(state) && neighborIsFullSquare || canConnectToFence || isFenceGate;
	}

	@Override
	public @NonNull MapCodec<ElectricFenceBlock> codec() {
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
	public boolean canPlaceLiquid(@Nullable LivingEntity filler, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull Fluid fluid) {
		return false;
	}

	@Override
	public boolean placeLiquid(@NonNull LevelAccessor world, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull FluidState fluidState) {
		return false;
	}

	@Override
	public @NonNull ItemStack pickupBlock(@Nullable LivingEntity drainer, @NonNull LevelAccessor world, @NonNull BlockPos pos, @NonNull BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NonNull Optional<SoundEvent> getPickupSound() {
		return Optional.empty();
	}
}
