package com.virus5600.defensive_measures.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.screen.TurretAssemblyStationScreenHandler;
import com.virus5600.defensive_measures.stat.ModStats;

/**
 * Turret Assembly Station (or TAS) is a functional block that allows players to craft higher tiered turrets.
 * <br><br>
 * Similar to how crafting tables work, players can interact with the turret assembly station to
 * open a crafting GUI that allows them to craft higher tiered turrets using the materials they
 * have in their inventory. The turret assembly station is a crucial block for players who want to
 * progress in the mod and unlock more powerful turrets, as it is the only way to craft higher
 * tiered turrets.
 * <br><br>
 * TAS will have an expanded crafting grid compared to the normal crafting table, allowing for more
 * complex recipes that require more spaces. Furthermore, this also allows lower-tiered, module-based
 * turret recipes to be crafted in a flat way while consuming less resources than crafting the
 * modules separately and then crafting the turret with the modules, providing a more enticing way
 * of crafting lower-tiered turrets in this workbench compared to the crafting table.
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class TurretAssemblyStationBlock extends BaseFunctionalBlock {
	public static final MapCodec<TurretAssemblyStationBlock> CODEC = createCodec(TurretAssemblyStationBlock::new);

	public static final EnumProperty<Direction> FACING;

	private static final VoxelShape SHAPE;

	public TurretAssemblyStationBlock(Settings settings) {
		super(settings);

		settings.instrument(NoteBlockInstrument.IRON_XYLOPHONE)
			.requiresTool()
			.strength(5.0F, 6.0F)
			.sounds(BlockSoundGroup.IRON)
			.nonOpaque();

		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public MapCodec<TurretAssemblyStationBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(syncId, inventory, player) ->
				new TurretAssemblyStationScreenHandler(
					syncId, inventory,
					ScreenHandlerContext.create(world, pos)
				),
			this.getTitle()
		);
	}


	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	public Text getTitle() {
		return Text.translatable("container.dm.turret_assembly_station");
	}

	@Override
	protected Identifier getScreenHandlerStat() {
		return ModStats.INTERACT_WITH_TURRET_ASSEMBLY_STATION;
	}

	static {
		FACING = HorizontalFacingBlock.FACING;

		SHAPE = VoxelShapes.union(
			// Table Top
			Block.createCuboidShape(0, 14, 0, 16, 16, 16),
			// Legs
			Block.createCuboidShape(1, 0, 13, 3, 14, 15),
			Block.createCuboidShape(1, 0, 1, 3, 14, 3),
			Block.createCuboidShape(13, 0, 13, 15, 14, 15),
			Block.createCuboidShape(13, 0, 1, 15, 14, 3)
		);
	}
}
