package com.virus5600.defensive_measures.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

/**
 * A base class for functional blocks. Functional blocks are blocks that have a GUI that can be
 * opened by the player, allowing players to interact with the block in a more complex way than
 * just placing and breaking it. This class provides the basic functionality for opening a GUI when
 * the block is used, as well as some abstract methods that must be implemented by any subclass of
 * this block to provide the necessary information for the GUI and its functionality.<br>
 * <br>
 * {@code BaseFunctionalBlock} extends the {@link Block} class, meaning that it inherits all of the
 * properties and methods of a normal block, but ith its methods are derived from the {@link net.minecraft.block.CraftingTableBlock CraftingTableBlock}
 * to simplify and centralize the common logic code of future functional blocks such as {@link TurretAssemblyStation}.
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.1.0
 *
 * @see net.minecraft.block.Block
 * @see net.minecraft.block.CraftingTableBlock
 * @see TurretAssemblyStation
 */
public abstract class BaseFunctionalBlock extends Block {
	public BaseFunctionalBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient()) {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			player.incrementStat(getScreenHandlerStat());
		}

		return ActionResult.SUCCESS;
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
	 * @param world {@link World} The world this block is in.
	 * @param pos {@link BlockPos} The integer block position of this block.
	 *
	 * @return {@link NamedScreenHandlerFactory} A screen handler factory that will create the GUI for this block when it is used by the player. By default, this method returns {@code null}, meaning that this block will not have a GUI.
	 */
	@Override
	protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return null;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	/**
	 * Returns the text that will be displayed on the crafting grid GUI.
	 *
	 * @return {@link Text} A translatable Mutable Text object that will be displayed on the
	 * crafting grid GUI.
	 *
	 * @since 1.1.0
	 */
	protected abstract Text getTitle();

	/**
	 * Returns this block's codec. To create one, just create a static final {@link MapCodec} property
	 * and set its value with the result of {{@link #createCodec(Function)}}.<br><br>
	 *
	 * Example:<br>
	 * - {@code createCodec(CraftingTableBlock::new)}<br>
	 * - {@link ArrowheadBlock#CODEC createCodec(ArrowheadBlock::new);}<br>
	 *
	 * @return {@link MapCodec} A MapCodec that will be used to serialize and deserialize this block.
	 */
	public abstract MapCodec<? extends BaseFunctionalBlock> getCodec();

	/**
	 * A dedicated stat to increment when interacting with this block.<br><br>
	 *
	 * Example:<br>
	 * - {@link com.virus5600.defensive_measures.stat.ModStats#INTERACT_WITH_TURRET_ASSEMBLY_STATION ModStats.INTERACT_WITH_TURRET_ASSEMBLY_STATION}<br>
	 * - {@link net.minecraft.stat.Stats#INTERACT_WITH_CRAFTING_TABLE Stats.INTERACT_WITH_CRAFTING_TABLE}
	 *
	 * @return {@link Identifier} The unique identifier of the stat.
	 */
	protected abstract Identifier getScreenHandlerStat();
}
