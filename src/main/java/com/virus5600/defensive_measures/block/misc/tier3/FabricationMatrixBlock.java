package com.virus5600.defensive_measures.block.misc.tier3;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures._util.MathUtil;
import com.virus5600.defensive_measures.block.BaseHorizontalFunctionalBlock;
import com.virus5600.defensive_measures.block.TwoBlockWide;
import com.virus5600.defensive_measures.block.misc.tier3.FabricationMatrixBlock.FabricationMatrixPart;
import com.virus5600.defensive_measures.block.state.properties.ModBlockStateProperties;
import com.virus5600.defensive_measures.inventory.ModContainerLevelAccess;
import com.virus5600.defensive_measures.screen.FabricationMatrixScreenHandler;
import com.virus5600.defensive_measures.stat.ModStats;

import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Fabrication Matrix is a functional block that allows players to craft higher tiered turrets.
 * Specifically, Tier 3 turrets that occupies a 3x3 area.
 * <br><br>
 * Similar to how crafting tables work, players can interact with the Fabrication Matrix to open a
 * crafting GUI that allows them to craft higher tiered turrets using the materials they have in
 * their inventory. The Fabrication Matrix is an optional block for players who want to save up
 * resources as this bench upgrade allows all turrets to be crafted using its flat recipe variant
 * instead of the more expensive modular recipe.
 * <br><br>
 * Fabrication Matrix will have an expanded crafting grid compared to the normal crafting table and
 * turret assembly station, allowing for more complex recipes that require more spaces.
 * Furthermore, this also allows lower-tiered, module-based (or parts-based) turret recipes to be
 * crafted in a flat way while consuming less resources than crafting the modules separately and
 * then crafting the turret with the modules, providing a more enticing way of crafting
 * lower-tiered turrets in this fabrication matrix compared to the crafting table.
 *
 * @since 1.2.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FabricationMatrixBlock extends BaseHorizontalFunctionalBlock implements TwoBlockWide<FabricationMatrixPart> {
	private static final Map<FabricationMatrixPart, Map<Direction, VoxelShape>> SHAPES;
	public static final EnumProperty<FabricationMatrixPart> PART;
	public static final BooleanProperty ACTIVE;
	public static final MapCodec<FabricationMatrixBlock> CODEC;

	public FabricationMatrixBlock(Properties settings) {
		super(
			settings
				.requiresCorrectToolForDrops()
				.strength(5.0F, 6.25F)
				.sound(SoundType.METAL)
				.noOcclusion()
		);

		this.registerDefaultState(
			this.stateDefinition
				.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(PART, this.getDefaultPart())
				.setValue(ACTIVE, Boolean.FALSE)
		);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);

		builder
			.add(PART)
			.add(ACTIVE);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		Direction facing =  state.getValue(FACING);
		FabricationMatrixPart part = state.getValue(PART);

		return SHAPES.get(part).get(facing);
	}

	@Override
	public MapCodec<FabricationMatrixBlock> codec() {
		return CODEC;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);

		if (state.getValue(ACTIVE) && state.getValue(PART) == FabricationMatrixPart.RIGHT) {
			Direction dir = state.getValue(FACING);
			float yRot = dir.toYRot();
			int inverse = dir == Direction.NORTH || dir ==  Direction.SOUTH ? 1 : -1;

			Vec3 relPos = MathUtil.getRelativePos(
				Vec3.atCenterOf(pos),
				-0.25 * inverse, 0.125, 0.35 * inverse,
				yRot
			);

			level.addParticle(
				ParticleTypes.CAMPFIRE_COSY_SMOKE,
				relPos.x(), relPos.y() + 0.5, relPos.z(),
				0, 0, 0
			);
		}
	}

	@Override
	protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
		if (state.getValue(ACTIVE)) {
			level.setBlock(pos, state.setValue(ACTIVE, false), 3);
		}
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	protected MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos) {
		return new SimpleMenuProvider(
			(syncId, inventory, _) ->
				new FabricationMatrixScreenHandler(
					syncId, inventory,
					ModContainerLevelAccess.create(level, pos)
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
		return Component.translatable("container.dm.fabrication_matrix");
	}

	@Override
	protected Identifier getScreenHandlerStat() {
		return ModStats.INTERACT_WITH_FABRICATION_MATRIX;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	public EnumProperty<Direction> getDirectionProperty() {
		return FACING;
	}

	public EnumProperty<FabricationMatrixPart> getPartProperty() {
		return PART;
	}

	public FabricationMatrixPart getDefaultPart() {
		return FabricationMatrixPart.LEFT;
	}

	public FabricationMatrixPart getOtherPart() {
		return FabricationMatrixPart.RIGHT;
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

	public enum FabricationMatrixPart implements StringRepresentable {
		LEFT("left"),
		RIGHT("right");

		public static final Codec<FabricationMatrixPart> CODEC = StringRepresentable.fromEnum(FabricationMatrixPart::values);
		private final String name;

		FabricationMatrixPart(String name) {
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

	// ////////////// //
	// STATIC METHODS //
	// ////////////// //

	public static int getActiveDurationTicks() {
		return (int) (20 * 0.5);
	}

	// ////////////////////// //
	// STATIC INITIALIZATIONS //
	// ////////////////////// //

	static {
		PART = ModBlockStateProperties.FABRICATION_MATRIX_PART;
		ACTIVE = BooleanProperty.create("active");
		CODEC = simpleCodec(FabricationMatrixBlock::new);

		// Facing default: North
		SHAPES = Map.of(
			FabricationMatrixPart.LEFT, Shapes.rotateHorizontal(Shapes.or(
				Block.box(0, 0, 0, 16, 14, 16),
				Block.box(0, 14, 0, 16, 16, 1),
				Block.box(15, 14, 1, 16, 16, 15),
				Block.box(0, 14, 1, 1, 16, 15),
				Block.box(0, 14, 15, 16, 16, 16)
			)),
			FabricationMatrixPart.RIGHT, Shapes.rotateHorizontal(Shapes.or(
				Block.box(0, 0, 0, 12, 1,16),
				Block.box(0, 1, 0, 12, 12, 12),
				Block.box(12.5, 12, 2.5, 13.5, 16, 3.5),
				Block.box(12, 5, 2, 14, 12, 4)
			))
		);
	}
}
