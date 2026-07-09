package com.virus5600.defensive_measures.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.virus5600.defensive_measures.block.misc.tier1.TurretAssemblyStationBlock;
import com.virus5600.defensive_measures.block.traps.tier1.BoltHeadBlock;

import java.util.function.Function;

import org.jspecify.annotations.NonNull;

/**
 * A base class for functional blocks. Functional blocks are blocks that have a GUI that can be
 * opened by the player, allowing players to interact with the block in a more complex way than
 * just placing and breaking it. This class provides the basic functionality for opening a GUI when
 * the block is used, as well as some abstract methods that must be implemented by any subclass of
 * this block to provide the necessary information for the GUI and its functionality.
 * <br><br>
 * {@code BaseFunctionalBlock} extends the {@link Block} class, meaning that it inherits all of the
 * properties and methods of a normal block, but ith its methods are derived from the {@link net.minecraft.world.level.block.CraftingTableBlock CraftingTableBlock}
 * to simplify and centralize the common logic code of future functional blocks such as {@link TurretAssemblyStationBlock}.
 * <br><br>
 * The use of this abstract class will also allow the block to inherit this to be 2-block wide,
 * similar to the {@link BedBlock Bed}, which takes up 2 blocks.
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.1.0
 *
 * @see Block
 * @see CraftingTableBlock
 */
public abstract class BaseFunctionalBlock extends Block {
	public BaseFunctionalBlock(BlockBehaviour.Properties settings) {
		super(settings);
	}

	protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
		if (!world.isClientSide()) {
			player.openMenu(state.getMenuProvider(world, pos));
			player.awardStat(getScreenHandlerStat());
		}

		return InteractionResult.SUCCESS;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	/**
	 * Creates a screen handler factory for this block. This is used to create the GUI for this
	 * block when it is used by the player. By default, this method returns null, meaning that this
	 * block will not have a GUI. However, subclasses of this block can override this method to
	 * provide a screen handler factory that will create the GUI for this block when it is used by
	 * the player.
	 *
	 * @param state {@link BlockState} This block's state.
	 * @param world {@link Level} The world this block is in.
	 * @param pos {@link BlockPos} The integer block position of this block.
	 *
	 * @return {@link MenuProvider} A screen handler factory that will create the GUI for this block when it is used by the player. By default, this method returns {@code null}, meaning that this block will not have a GUI.
	 */
	@Override
	protected MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos) {
		return null;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	/**
	 * Returns the text that will be displayed on the crafting grid GUI.
	 *
	 * @return {@link Component} A translatable Mutable Text object that will be displayed on the
	 * crafting grid GUI.
	 *
	 * @since 1.1.0
	 */
	protected abstract Component getTitle();

	/**
	 * Returns this block's codec. To create one, just create a static final {@link MapCodec} property
	 * and set its value with the result of {{@link #simpleCodec(Function)}}.<br><br>
	 *
	 * Example:<br>
	 * - {@code createCodec(CraftingTableBlock::new)}<br>
	 * - {@link BoltHeadBlock#CODEC createCodec(BoltHeadBlock::new);}<br>
	 *
	 * @return {@link MapCodec} A MapCodec that will be used to serialize and deserialize this block.
	 */
	protected abstract @NonNull MapCodec<? extends BaseFunctionalBlock> codec();

	/**
	 * A dedicated stat to increment when interacting with this block.<br><br>
	 *
	 * Example:<br>
	 * - {@link com.virus5600.defensive_measures.stat.ModStats#INTERACT_WITH_TURRET_ASSEMBLY_STATION ModStats.INTERACT_WITH_TURRET_ASSEMBLY_STATION}<br>
	 * - {@link net.minecraft.stats.Stats#INTERACT_WITH_CRAFTING_TABLE Stats.INTERACT_WITH_CRAFTING_TABLE}
	 *
	 * @return {@link Identifier} The unique identifier of the stat.
	 */
	protected abstract Identifier getScreenHandlerStat();
}
