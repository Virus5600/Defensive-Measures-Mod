package com.virus5600.defensive_measures.block.misc.tier2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.block.BaseHorizontalFunctionalBlock;
import com.virus5600.defensive_measures.block.TwoBlockWide;
import com.virus5600.defensive_measures.block.misc.tier2.WorkshopBlock.WorkshopPart;
import com.virus5600.defensive_measures.block.state.properties.ModBlockStateProperties;
import com.virus5600.defensive_measures.screen.WorkshopScreenHandler;
import com.virus5600.defensive_measures.stat.ModStats;

import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Workshop is a functional block that allows players to craft higher tiered turrets. Specifically,
 * Tier 3 turrets that occupies a 3x3 area.
 * <br><br>
 * Similar to how crafting tables work, players can interact with the workshop to open a crafting
 * GUI that allows them to craft higher tiered turrets using the materials they have in their
 * inventory. The workshop is a crucial block for players who want to progress in the mod and
 * unlock more powerful turrets, as it is the only way to craft higher tiered turrets.
 * <br><br>
 * Workshop will have an expanded crafting grid compared to the normal crafting table and turret
 * assembly station, allowing for more complex recipes that require more spaces. Furthermore, this
 * also allows lower-tiered, module-based (or parts-based) turret recipes to be crafted in a flat
 * way while consuming less resources than crafting the modules separately and then crafting the
 * turret with the modules, providing a more enticing way of crafting lower-tiered turrets in this
 * workbench compared to the crafting table.
 *
 * @since 1.2.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class WorkshopBlock extends BaseHorizontalFunctionalBlock implements TwoBlockWide<WorkshopPart> {
	private static final Map<Direction, VoxelShape> SHAPES;
	public static final EnumProperty<WorkshopPart> PART;
	public static final MapCodec<WorkshopBlock> CODEC;

	public WorkshopBlock(Properties settings) {
		super(
			settings
				.instrument(NoteBlockInstrument.IRON_XYLOPHONE)
				.requiresCorrectToolForDrops()
				.strength(3.75F, 5.0F)
				.sound(SoundType.IRON)
				.noOcclusion()
		);

		this.registerDefaultState(
			this.stateDefinition
				.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(PART, this.getDefaultPart())
		);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);

		builder.add(PART);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		Direction facing =  state.getValue(FACING);
		WorkshopPart part = state.getValue(PART);

		return switch (part) {
			case WorkshopPart.LEFT -> SHAPES.get(facing);
			case WorkshopPart.RIGHT -> SHAPES.get(facing.getOpposite());
		};
	}

	@Override
	public MapCodec<WorkshopBlock> codec() {
		return CODEC;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	protected MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos) {
		return new SimpleMenuProvider(
			(syncId, inventory, _) ->
				new WorkshopScreenHandler(
					syncId, inventory,
					ContainerLevelAccess.create(world, pos)
				),
			this.getTitle()
		);
	}


	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	// BaseFunctionalBlock
	@Override
	public Component getTitle() {
		return Component.translatable("container.dm.workshop");
	}

	@Override
	protected Identifier getScreenHandlerStat() {
		return ModStats.INTERACT_WITH_WORKSHOP;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	public EnumProperty<Direction> getDirectionProperty() {
		return FACING;
	}

	public EnumProperty<WorkshopPart> getPartProperty() {
		return PART;
	}

	public WorkshopPart getDefaultPart() {
		return WorkshopPart.LEFT;
	}

	public WorkshopPart getOtherPart() {
		return WorkshopPart.RIGHT;
	}

	public Direction getMainPartDirection(Direction dir) {
		return this.getOtherPartDirection(dir).getOpposite();
	}

	public Direction getOtherPartDirection(final Direction dir) {
		return switch (dir) {
			case Direction.NORTH -> Direction.EAST;
			case Direction.EAST -> Direction.SOUTH;
			case Direction.SOUTH -> Direction.WEST;
			case Direction.WEST -> Direction.NORTH;
			default -> throw new IllegalStateException("Unexpected value: " + dir.toString().toUpperCase());
		};
	}

	// ///////////// //
	// ENUM PROPERTY //
	// ///////////// //

	public enum WorkshopPart implements StringRepresentable {
		LEFT("left"),
		RIGHT("right");

		public static final Codec<WorkshopPart> CODEC = StringRepresentable.fromEnum(WorkshopPart::values);
		private final String name;

		WorkshopPart(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		public String getSerializedName(boolean upper) {
			return upper ? this.getSerializedName().toUpperCase() : this.getSerializedName();
		}

		public String getSerializedName() {
			return this.name;
		}
	}

	// ////// //
	// STATIC //
	// ////// //

	static {
		PART = ModBlockStateProperties.WORKSHOP_PART;
		CODEC = simpleCodec(WorkshopBlock::new);

		// Facing default: North
		SHAPES = Shapes.rotateHorizontal(Shapes.or(
			// Table Top //
			Block.box(0, 14, 0, 16, 16, 16),

			// Legs //
			// North-West
			Block.box(1, 0, 13, 3, 14, 15),
			// South-West
			Block.box(1, 0, 1, 3, 14, 3)
		));
	}
}
