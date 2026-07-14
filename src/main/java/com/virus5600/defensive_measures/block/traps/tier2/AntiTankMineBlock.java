package com.virus5600.defensive_measures.block.traps.tier2;

import com.mojang.serialization.MapCodec;
import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.state.properties.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Anti Personnel Mine is a trap block that only get triggered .
 * <br><br>
 * Similar to how magma blocks deal damage to entities that step on them,
 * bolt head blocks deal damage to entities that step on them. This, however,
 * does not discriminate against sneaking entities, meaning that sneaking
 * entities will still take damage from bolt head blocks unlike magma blocks.
 * <br><br>
 * Bolt head blocks deal 2 damage to entities that step on them. This could
 * be changed by any subclass of this block by simply changing the value of
 * the {@link #damageDealt} field.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class AntiTankMineBlock extends Block implements SimpleWaterloggedBlock {
	public static final MapCodec<AntiTankMineBlock> CODEC = simpleCodec(AntiTankMineBlock::new);

	public static int MAX_HEADS = 4;

	public static final IntegerProperty BOLT_HEADS = ModProperties.BOLT_HEADS;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final VoxelShape ONE_HEAD;
	private static final VoxelShape TWO_HEADS;
	private static final VoxelShape THREE_HEADS;
	private static final VoxelShape FOUR_HEADS;
	/**
	 * Defines the block hitbox of bolt head based on the amount of bolt heads
	 * block placed in the same space.
	 */
	private static final VoxelShape[] SHAPES_BY_HEAD;

	/**
	 * Defines the amount of damage that the bolt head block will deal to entities.
	 */
	protected int damageDealt = 2;

	public AntiTankMineBlock(Properties settings) {
		super(
			settings.destroyTime(1.5f)
				.explosionResistance(1.0f)
				.noOcclusion()
		);


		this.registerDefaultState(
			this.stateDefinition
				.any()
				.setValue(BOLT_HEADS, 1)
				.setValue(WATERLOGGED, false)
		);
	}

	@Override
	protected boolean canBeReplaced(@NonNull BlockState state, BlockPlaceContext context) {
		return !context.isSecondaryUseActive()
			&& context.getItemInHand().getItem() == this.asItem()
			&& state.getValue(BOLT_HEADS) < MAX_HEADS
			|| super.canBeReplaced(state, context);
	}

	@Override
	protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return SHAPES_BY_HEAD[state.getValue(BOLT_HEADS) - 1];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BOLT_HEADS, WATERLOGGED);
	}

	@Override
	protected boolean canSurvive(@NonNull BlockState state, @NonNull LevelReader world, @NonNull BlockPos pos) {
		return canSupportRigidBlock(world, pos) || Block.canSupportCenter(world, pos.below(), Direction.UP);
	}

	@Override
	protected @NonNull FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected @NonNull BlockState updateShape(
		BlockState state,
		@NonNull LevelReader world,
		@NonNull ScheduledTickAccess tickView,
		@NonNull BlockPos pos,
		@NonNull Direction direction,
		@NonNull BlockPos neighborPos,
		@NonNull BlockState neighborState,
		@NonNull RandomSource random
	) {
		if (state.getValue(WATERLOGGED)) {
			tickView.scheduleTick(
				pos,
				Fluids.WATER,
				Fluids.WATER.getTickDelay(world)
			);
		}

		return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public boolean placeLiquid(@NonNull LevelAccessor world, @NonNull BlockPos pos, BlockState state, @NonNull FluidState fluidState) {
		if (!(Boolean)state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
			BlockState blockState = state.setValue(WATERLOGGED, true);

			world.setBlock(pos, blockState, Block.UPDATE_ALL);
			world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));

			return true;
		} else {
			return false;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState blockState = ctx.getLevel()
			.getBlockState(ctx.getClickedPos());

		if (blockState.is(this)) {
			return blockState.cycle(BOLT_HEADS);
		} else {
			FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());

			boolean isFluidWater = fluidState.getType() == Fluids.WATER;
			return Objects.requireNonNull(super.getStateForPlacement(ctx))
				.setValue(WATERLOGGED, isFluidWater);
		}
	}

	@Override
	public void stepOn(@NonNull Level world, @NonNull BlockPos pos, @NonNull BlockState state, Entity entity) {
		if (!entity.isSteppingCarefully() && entity instanceof LivingEntity) {
			if (world instanceof ServerLevel serverWorld) {
				DamageSource dmgSrc = ModDamageSources.create(world, ModDamageTypes.BOLT_HEAD, null, null);
				entity.hurtServer(serverWorld, dmgSrc, this.getDamageDealt(state));
			}
		}

		super.stepOn(world, pos, state, entity);
	}

	@Override
	public boolean isPossibleToRespawnInThis(@NonNull BlockState state) {
		return false;
	}

	@Override
	public @NonNull MapCodec<AntiTankMineBlock> codec() {
		return CODEC;
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	/**
	 * Returns the final damage to be dealt when this block is stepped on.
	 *
	 * @param state The block state of this bolt head block.
	 *
	 * @return The final damage to be dealt when this block is stepped on.
	 */
	public float getDamageDealt(BlockState state) {
		return this.damageDealt * state.getValue(BOLT_HEADS);
	}

	static {
		ONE_HEAD = Block.box(6, 0, 6, 10, 4, 10);
		TWO_HEADS = Block.box(3, 0, 6, 13, 4, 10);
		THREE_HEADS = Block.box(3, 0, 3, 13, 4, 13);
		FOUR_HEADS = Block.box(3, 0, 3, 13, 4, 13);

		SHAPES_BY_HEAD = new VoxelShape[] {
			ONE_HEAD,
			TWO_HEADS,
			THREE_HEADS,
			FOUR_HEADS
		};
	}
}
