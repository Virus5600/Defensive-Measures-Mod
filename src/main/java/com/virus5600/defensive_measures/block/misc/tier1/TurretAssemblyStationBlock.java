package com.virus5600.defensive_measures.block.misc.tier1;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.block.BaseHorizontalFunctionalBlock;
import com.virus5600.defensive_measures.screen.TurretAssemblyStationScreenHandler;
import com.virus5600.defensive_measures.stat.ModStats;

import org.jspecify.annotations.NonNull;

/**
 * Turret Assembly Station (or TAS) is a functional block that allows players to craft higher
 * tiered turrets. Specifically, Tier 2 turrets that usually occupies a 2x2 area.
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
public class TurretAssemblyStationBlock extends BaseHorizontalFunctionalBlock {
	private static final VoxelShape SHAPE;
	public static final MapCodec<TurretAssemblyStationBlock> CODEC;

	public TurretAssemblyStationBlock(Properties settings) {
		super(
			settings
				.instrument(NoteBlockInstrument.IRON_XYLOPHONE)
				.requiresCorrectToolForDrops()
				.strength(2.5F, 4.75F)
				.sound(SoundType.IRON)
				.noOcclusion()
		);


		this.registerDefaultState(
			this.stateDefinition
				.any()
				.setValue(FACING, Direction.NORTH)
		);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public MapCodec<TurretAssemblyStationBlock> codec() {
		return CODEC;
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
		SHAPE = Shapes.or(
			// Table Top
			Block.box(0, 14, 0, 16, 16, 16),
			// Legs
			Block.box(1, 0, 13, 3, 14, 15),
			Block.box(1, 0, 1, 3, 14, 3),
			Block.box(13, 0, 13, 15, 14, 15),
			Block.box(13, 0, 1, 15, 14, 3)
		);

		CODEC = simpleCodec(TurretAssemblyStationBlock::new);
	}
}
