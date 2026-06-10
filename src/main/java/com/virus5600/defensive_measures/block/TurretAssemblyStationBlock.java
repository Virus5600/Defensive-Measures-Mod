package com.virus5600.defensive_measures.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.screen.TurretAssemblyStationScreenHandler;
import com.virus5600.defensive_measures.stat.ModStats;

import org.jspecify.annotations.NonNull;

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
	public static final MapCodec<TurretAssemblyStationBlock> CODEC = simpleCodec(TurretAssemblyStationBlock::new);

	public static final EnumProperty<Direction> FACING;

	private static final VoxelShape SHAPE;

	public TurretAssemblyStationBlock(Properties settings) {
		super(settings);

		settings.instrument(NoteBlockInstrument.IRON_XYLOPHONE)
			.requiresCorrectToolForDrops()
			.strength(5.0F, 6.0F)
			.sound(SoundType.IRON)
			.noOcclusion();

		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected @NonNull BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected @NonNull BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public @NonNull MapCodec<TurretAssemblyStationBlock> codec() {
		return CODEC;
	}

	@Override
	protected boolean isPathfindable(@NonNull BlockState state, @NonNull PathComputationType type) {
		return false;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	protected MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos) {
		return new SimpleMenuProvider(
			(syncId, inventory, _) ->
				new TurretAssemblyStationScreenHandler(
					syncId, inventory,
					ContainerLevelAccess.create(world, pos)
				),
			this.getTitle()
		);
	}


	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	public Component getTitle() {
		return Component.translatable("container.dm.turret_assembly_station");
	}

	@Override
	protected Identifier getScreenHandlerStat() {
		return ModStats.INTERACT_WITH_TURRET_ASSEMBLY_STATION;
	}

	static {
		FACING = HorizontalDirectionalBlock.FACING;

		SHAPE = Shapes.or(
			// Table Top
			Block.box(0, 14, 0, 16, 16, 16),
			// Legs
			Block.box(1, 0, 13, 3, 14, 15),
			Block.box(1, 0, 1, 3, 14, 3),
			Block.box(13, 0, 13, 15, 14, 15),
			Block.box(13, 0, 1, 15, 14, 3)
		);
	}
}
