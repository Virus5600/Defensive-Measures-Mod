package com.virus5600.defensive_measures.screen.slots;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.block.misc.tier3.FabricationMatrixBlock;
import com.virus5600.defensive_measures.block.misc.tier3.FabricationMatrixBlock.FabricationMatrixPart;

public class FabMatResultSlot extends SpriteResultSlot {
	private final Level level;
	private final BlockPos pos;

	public FabMatResultSlot(
		final Player player, final CraftingContainer craftSlots,
		final Container container, final int id,
		final int x, final int y,
		final Level level, final BlockPos pos
	) {
		super(player, craftSlots, container, id, x, y);

		this.level = level;
		this.pos = pos;
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //


	@Override
	public void onTake(Player player, ItemStack carried) {
		super.onTake(player, carried);

		Level level = this.level;

		if (level instanceof ServerLevel lvl) {
			BlockState state = lvl.getBlockState(this.pos);

			if (state.is(ModBlocks.FABRICATION_MATRIX)) {
				lvl.setBlock(this.pos, state.setValue(FabricationMatrixBlock.ACTIVE, true), 3);
				lvl.scheduleTick(this.pos, state.getBlock(), FabricationMatrixBlock.getActiveDurationTicks());

				BlockPos targetPos = this.pos;

				if (state.getBlock() instanceof FabricationMatrixBlock fabMat
					&& state.getValue(FabricationMatrixBlock.PART) == FabricationMatrixPart.LEFT
				) {
					targetPos = this.pos.relative(fabMat.getOtherPartDirection(state.getValue(fabMat.getDirectionProperty())));
				}

				BlockState targetState = lvl.getBlockState(targetPos);
				if (targetState.is(ModBlocks.FABRICATION_MATRIX)) {
					lvl.setBlock(targetPos, targetState.setValue(FabricationMatrixBlock.ACTIVE, true), 3);
					lvl.scheduleTick(targetPos, targetState.getBlock(), FabricationMatrixBlock.getActiveDurationTicks());
				}
			}
		}
	}
}
