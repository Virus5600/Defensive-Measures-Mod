package com.virus5600.defensive_measures.block.traps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import com.virus5600.defensive_measures.block.ExplosiveBlock;
import com.virus5600.defensive_measures.block.entity.traps.BaseLandmineBlockEntity;
import com.virus5600.defensive_measures.sound.ModSoundEvents;
import com.virus5600.defensive_measures.state.properties.ModProperties;
import com.virus5600.defensive_measures.world.ModExplosionImpl;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Am abstract class that will serve as the base class for all landmine implementations of this mod.
 * <br><br>
 * By default, all landmines extending this abstract class will use this damage calculation before
 * using the custom {@link ModExplosionImpl#explode(boolean)} to utilize the custom explosion
 * damage fall-off:
 * <pre><code>
 * (baseDamage + (2 * difficultyId)) * penalty
 * </code></pre>
 * Wherein:
 * <ul>
 *     <li>{@code baseDamage}: The base damage value.</li>
 *     <li>{@code difficultyId}: A multiplier based on the game difficulty.</li>
 *     <li>{@code penalty}: A penalty modifier fixed at 25% when the mine is waterlogged.</li>
 * </ul>
 * <br><br>
 * Furthermore, any block extending this base class will also inherit the dynamic waterlogging
 * wherein the landmine will be waterlogged if the block is placed in water or in the path of a
 * flowing water, taking in the water level of the flowing water to seamlessly camouflage the
 * landmine in water. This is done by implementing the {@link SimpleWaterloggedBlock} interface.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseLandmineBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, ExplosiveBlock, EntityBlock {
	public static final IntegerProperty LANDMINES = ModProperties.LANDMINES;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty ARMED = ModProperties.ARMED;

	@Nullable protected Entity owner;
	protected Level level;

	/**
	 * Creates an instance of a landmine.
	 *
	 * @param properties Block properties for the landmine block.
	 */
	public BaseLandmineBlock(Properties properties) {
		super(properties);

		this.registerDefaultState(
			this.stateDefinition
				.any()
				.setValue(LANDMINES, 1)
				.setValue(WATERLOGGED, false)
				.setValue(ARMED, false)
		);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);

		this.level = level;
		BaseLandmineBlockEntity entity = (BaseLandmineBlockEntity) level.getBlockEntity(pos);

		if (entity != null) {
			entity.setLevel(level);
		}

		level.scheduleTick(pos, state.getBlock(), this.getArmingDelay());
	}

	@Override
	protected boolean canBeReplaced(@NonNull BlockState state, BlockPlaceContext context) {
		boolean isNotSneaking = !context.isSecondaryUseActive();
		boolean isSameItem = context.getItemInHand().getItem() == this.asItem();
		boolean mineInRange = state.getValue(LANDMINES) < this.getMaxMines();
		boolean superMethod = super.canBeReplaced(state, context);

		return (isNotSneaking && isSameItem && mineInRange) || superMethod;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(
			LANDMINES,
			WATERLOGGED,
			ARMED
		);
	}

	@Override @NonNull
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override @NonNull
	protected BlockState updateShape(
		BlockState state, LevelReader level,
		ScheduledTickAccess tickView,
		BlockPos pos, Direction direction,
		BlockPos neighborPos, BlockState neighborState,
		RandomSource random
	) {
		if (!isWithinFluidDepthThreshold((Level) level, pos, this.getFluidLevelThreshold())) {
			this.detonate(state, (Level) level, pos);
		}

		if (state.getValue(WATERLOGGED)) {
			tickView.scheduleTick(
				pos,
				Fluids.WATER,
				Fluids.WATER.getTickDelay(level)
			);
		}

		if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
			((Level) level).scheduleTick(pos, this, 2);
		}

		return super.updateShape(state, level, tickView, pos, direction, neighborPos, neighborState, random);
	}

	protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
		if (!state.getValue(ARMED)) {
			level.setBlockAndUpdate(pos, state.setValue(ARMED, true));
			level.playSound(
				null, pos,
				ModSoundEvents.BLOCK_LANDMINE_ARMED, SoundSource.BLOCKS,
				1, 1
			);
		}

		if (!this.canSurvive(state, level, pos) && pos.getY() >= level.getMinSectionY()) {
			if (level.getBlockState(pos.below()).isAir()) {
				FallingBlockEntity fbe = FallingBlockEntity.fall(level, pos, state);
				BlockEntity be = level.getBlockEntity(pos);

				if (be != null) {
					fbe.blockData = be.saveWithFullMetadata(level.registryAccess());
				}
			}
		}
	}

	public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
		super.setPlacedBy(level, pos, state, by, itemStack);

		if (by != null) {
			if (level.getBlockEntity(pos) instanceof BaseLandmineBlockEntity mine) {
				this.owner = by;
				mine.setOwner(by);
			}
		}
	}

	@Override
	public boolean placeLiquid(@NonNull LevelAccessor world, @NonNull BlockPos pos, BlockState state, @NonNull FluidState fluidState) {
		if (!(Boolean)state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
			BlockState blockState = state.setValue(WATERLOGGED, true);

			world.setBlock(pos, blockState, Block.UPDATE_ALL);
			world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));

			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Retrieves the maximum number of mines allowed in a single block. This is hard capped to
	 * {@code 3} due to the max limit of the {@link ModProperties#LANDMINES} property.
	 *
 	 * @return The maximum number of mines this block will have.
	 */
	public final int getMaxMines() {
		return Math.max(1, Math.min(3, this.maxMines()));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState blockState = ctx.getLevel()
			.getBlockState(ctx.getClickedPos());

		if (blockState.is(this)) {
			return blockState.cycle(LANDMINES);
		}
		else {
			FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());

			boolean isFluidWater = fluidState.getType() == Fluids.WATER;
			return Objects.requireNonNull(super.getStateForPlacement(ctx))
				.setValue(WATERLOGGED, isFluidWater);
		}
	}

	@Override
	public boolean isPossibleToRespawnInThis(@NonNull BlockState state) {
		return false;
	}

	@Override
	protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
		return level.getBlockState(pos.below())
			.isFaceSturdy(level, pos.below(), Direction.UP);
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	protected void onExplosionHit(
		final BlockState state, final ServerLevel level, final BlockPos pos,
		final Explosion explosion, final BiConsumer<ItemStack, BlockPos> onHit
	) {
		this.wasExploded(level, pos, explosion);
	}

	protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
		return Shapes.empty();
	}

	protected void attack(final BlockState state, final Level level, final BlockPos pos, final Player player) {
		if (!level.isClientSide()) {
			boolean isArmed = state.getValue(ARMED);

			if (!player.isCrouching() && isArmed) {
				// TODO: Add disarming tool (probably shears for disposing and a custom one for retrieving?)
				this.detonate(state, level, pos);
			}
		}
	}

	public void playerDestroy(final Level level, final Player player, final BlockPos pos, final BlockState state, final @Nullable BlockEntity blockEntity, final ItemStack destroyedWith) {
		if (!level.isClientSide()) {
			boolean isArmed = state.getValue(ARMED);

			if (!player.isCrouching() && isArmed) {
				// TODO: Add disarming tool (probably shears for disposing and a custom one for retrieving?)
				this.detonate(state, level, pos);
			}
		}
	}

	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide()) {
			boolean isArmed = state.getValue(ARMED);

			if (!player.isCrouching() && isArmed) {
				// TODO: Add disarming tool (probably shears for disposing and a custom one for retrieving?)
				this.detonate(state, level, pos);

				BlockState replacedBy = state.getValue(WATERLOGGED) ?
					Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();

				return level.setBlock(pos, replacedBy, Block.UPDATE_ALL) ?
					replacedBy : state;
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	public void wasExploded(final ServerLevel level, final BlockPos pos, final Explosion explosion) {
		this.detonate(level.getBlockState(pos), level, pos);
	}

	public void entityInside(
		BlockState state, Level level, BlockPos pos, Entity entity,
		InsideBlockEffectApplier effectApplier, boolean isPrecise
	) {
		if (!level.isClientSide()) {
			boolean canTrigger = this.canTrigger(state, level, pos, entity);

			if (canTrigger) {
				if (entity.is(EntityTypeTags.IMPACT_PROJECTILES)) {
					entity.discard();

					if (level instanceof ServerLevel sl) {
						RandomSource rand = level.getRandom();
						BlockState particleState = level.getBlockState(pos.below());
						Vec3 boundPos = state.getShape(level, pos)
							.bounds()
							.move(pos)
							.getCenter();

						sl.sendParticles(
							new BlockParticleOption(ParticleTypes.BLOCK, particleState),
							boundPos.x, boundPos.y, boundPos.z,
							rand.nextIntBetweenInclusive(10, 15),
							0.0, 0.125, 0.0,
							rand.nextIntBetweenInclusive(10, 25)
						);
					}
				}

				this.detonate(state, level, pos);
			}
		}
	}

	public boolean dropFromExplosion(final Explosion explosion) {
		return false;
	}

	public double getDamageDealt(final BlockState state, final Level level) {
		int mines = state.getValue(LANDMINES);
		int difficultyId = level.getDifficulty().getId();
		float penalty = state.getValue(WATERLOGGED) ? 0.75F : 1.0F;

		return (this.getBaseDamage() * mines + (2 * difficultyId)) * penalty;
	}

	/**
	 * Defines how many ticks before the landmine can be considered "armed".
	 *
	 * @return The number of ticks before the landmine can be considered "armed".
	 */
	public int getArmingDelay() {
		return (int) (20 * 2.5);
	}

	public Level level() {
		return this.level;
	}

	@Nullable
	public Entity getOwner() {
		return this.owner;
	}

	/**
	 * Defines the maximum fluid depth threshold for the landmine to be triggered. If the fluid
	 * column above the landmine exceeds this threshold, the landmine will be triggered. This is
	 * useful for triggering landmines when they are submerged in water or other fluids below this
	 * threshold.
	 *
	 * @return The maximum fluid depth threshold for the landmine to be triggered.
	 *
	 * @apiNote Default value is {@code 3} blocks.
	 */
	public int getFluidLevelThreshold() {
		return 3;
	}

	// ////////////// //
	// STATIC METHODS //
	// ////////////// //

	/**
	 * Checks if the given position is within the specified fluid depth threshold. This allows for
	 * checks where a mine should trigger if under a certain depth. The {@code maxDepth} parameter
	 * is an inclusive value, meaning that if the water column is equal to or less than
	 * {@code maxDepth}, the method will return {@code true}. If the water column exceeds
	 * {@code maxDepth}, it will return {@code false}.
	 *
	 * @param level    The level in which the check is performed.
	 * @param pos      The position to check.
	 * @param maxDepth The maximum depth to check.
	 *
	 * @return         {@code true} if the position is within the fluid depth threshold; {@code false} otherwise.
	 */
	public static boolean isWithinFluidDepthThreshold(Level level, BlockPos pos, int maxDepth) {
		for (int i = 1; i <= maxDepth; i++) {
			BlockPos check = pos.above(i);

			if (!level.getBlockState(check).getFluidState().is(FluidTags.WATER)) {
				// Water column ended before exceeding maxDepth
				return true;
			}
		}

		// Water continues beyond maxDepth
		return false;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	/**
	 * Defines the maximum number of mines this block will have.
	 *
	 * @return The maximum number of mines this block will have.
	 */
	protected abstract int maxMines();

	/**
	 * Defines the base damage this landmine will deal.
	 *
	 * @return The base damage value.
	 */
	public abstract double getBaseDamage();

	/**
	 * Determines whether the landmine can be triggered by the given entity.
	 *
	 * @param state  The current state of this landmine.
	 * @param level  The level in which this landmine is placed.
	 * @param pos    The position of this landmine in the level.
	 * @param entity The entity that is attempting to trigger the landmine.
	 *
	 * @return {@code true} if the landmine can be triggered by the given entity; {@code false} otherwise.
	 */
	public abstract boolean canTrigger(BlockState state, Level level, BlockPos pos, Entity entity);

	/**
	 * Detonates and makes the landmine explode.
	 *
	 * @param state  The current state of this landmine.
	 * @param level  The level in which this landmine is placed.
	 * @param pos    The position of this landmine in the level.
	 */
	public abstract void detonate(final BlockState state, final Level level, final BlockPos pos);
}
