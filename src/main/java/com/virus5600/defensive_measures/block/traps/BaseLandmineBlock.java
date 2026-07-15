package com.virus5600.defensive_measures.block.traps;

import com.virus5600.defensive_measures.sound.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
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

import com.virus5600.defensive_measures.block.ExplosiveBlock;
import com.virus5600.defensive_measures.state.properties.ModProperties;
import com.virus5600.defensive_measures.world.ModExplosionImpl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

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
public abstract class BaseLandmineBlock extends Block implements SimpleWaterloggedBlock, ExplosiveBlock {
	public static final IntegerProperty LANDMINES = ModProperties.LANDMINES;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty ARMED = ModProperties.ARMED;

	protected Level level;
	protected @Nullable EntityReference<Entity> owner;

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
		level.scheduleTick(pos, state.getBlock(), this.getArmingDelay());
	}

	@Override
	protected boolean canBeReplaced(@NonNull BlockState state, BlockPlaceContext context) {
		return !context.isSecondaryUseActive()
			&& context.getItemInHand().getItem() == this.asItem()
			&& state.getValue(LANDMINES) < this.getMaxMines()
			|| super.canBeReplaced(state, context);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(
			LANDMINES,
			WATERLOGGED,
			ARMED
		);
	}

	@Override
	protected @NonNull FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected @NonNull BlockState updateShape(
		BlockState state, LevelReader world,
		ScheduledTickAccess tickView,
		BlockPos pos, Direction direction,
		BlockPos neighborPos, BlockState neighborState,
		RandomSource random
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

	protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
		level.setBlockAndUpdate(pos, state.setValue(ARMED, true));

		if (state.getValue(ARMED)) {
			level.playLocalSound(
				pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS,
				1, 1, false
			);
		}
	}

	protected void setOwner(final @Nullable EntityReference<Entity> owner) {
		this.owner = owner;
	}

	public void setOwner(final @Nullable Entity owner) {
		this.setOwner(EntityReference.of(owner));
	}

	@Nullable
	public Entity getOwner() {
		Level lvl = this.level;

		if (this.owner == null || lvl == null) {
			return null;
		}

		return EntityReference.getEntity(this.owner, lvl);
	}

	@Nullable
	public Level level() {
		return this.level;
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

	/**
	 * Retrieves the maximum number of mines allowed in a single block. This is hard capped to
	 * {@code 3} due to the max limit of the {@link ModProperties#LANDMINES} property.
	 *
 	 * @return The maximum number of mines this block will have.
	 */
	public final int getMaxMines() {
		return Math.max(3, this.maxMines());
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

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	protected void attack(final BlockState state, final Level level, final BlockPos pos, final Player player) {
		if (!level.isClientSide()) {
			if (!player.isCreative()) {
				// TODO: Add disarming tool (probably shears for disposing and a custom one for retrieving?)
				this.detonate(state, level, pos, player);
			}
		}
	}

	protected void entityInside(
		final BlockState state, final Level level, final BlockPos pos,
		final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise
	) {
		if (!level.isClientSide()) {
			if (this.canTrigger(state, level, pos, entity)) {
				this.detonate(state, level, pos, entity instanceof Player player ? player : null);
			}
		}
	}

	/**
	 * Returns the final damage to be dealth when this block is detonated.
	 *
	 * @param state The block state of this landmine block.
	 *
	 * @return The final damage to be dealt when this block is detonated.
	 */
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
	 * @param player The player that triggered the detonation of this landmine.
	 */
	public abstract void detonate(final BlockState state, final Level level, final BlockPos pos, final @Nullable Player player);
}
