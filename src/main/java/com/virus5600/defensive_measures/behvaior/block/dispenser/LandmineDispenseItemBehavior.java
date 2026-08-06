package com.virus5600.defensive_measures.behvaior.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import com.virus5600.defensive_measures.block.traps.BaseLandmineBlock;

/**
 * A custom (and abstracted) dispenser item behavior for the {@link BaseLandmineBlock Landmines},
 * allowing it to summon or place a landmine whether on air on on ground. When placed on air, it
 * will immediately fall to the ground and be placed. When placed on ground, it will be placed on
 * the ground.
 *
 * @param <T> The type of landmine block that this behavior is for. Must extend {@link BaseLandmineBlock}.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class LandmineDispenseItemBehavior<T extends BaseLandmineBlock> extends OptionalDispenseItemBehavior {
	public LandmineDispenseItemBehavior() {}

	public ItemStack execute(final BlockSource source, final ItemStack dispensed) {
		ServerLevel level = source.level();

		if (!level.isClientSide()) {
			BlockPos pos = source.pos()
				.relative(source.state()
					.getValue(DispenserBlock.FACING));

			T landmine = this.getLandmineBlock();

			this.setSuccess(tryPlaceLandmine(level, pos));

			if (this.isSuccess()) {
				BlockState defaultState = landmine.defaultBlockState();

				level.setBlockAndUpdate(pos, defaultState);
				level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
				dispensed.shrink(1);

				// Make it explode immediately if spawned deeper than it should.
				if (!BaseLandmineBlock.isWithinFluidDepthThreshold(level, pos, landmine.getFluidLevelThreshold())) {
					landmine.detonate(defaultState, level, pos);
				}
			}
			else {
				return super.execute(source, dispensed);
			}
		}

		return dispensed;
	}

	protected boolean tryPlaceLandmine(
		final ServerLevel level,
		final BlockPos pos
	) {
		return level.isEmptyBlock(pos);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected abstract T getLandmineBlock();
}
