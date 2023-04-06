package com.virus5600.DefensiveMeasures.block;

import com.virus5600.DefensiveMeasures.entity.damage.ModDamageSource;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ArrowheadBlock extends Block {

	private static final VoxelShape BOTTOM;
	private static final VoxelShape MIDDLE;
	private static final VoxelShape TOP;
	private static final VoxelShape SHAPE;

	public ArrowheadBlock(Settings settings) {
		super(settings);

		settings.hardness(1.5f)
			.resistance(1.0f)
			.nonOpaque();
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			livingEntity.damage(ModDamageSource.ARROWHEAD, 2);
		}

		super.onSteppedOn(world, pos, state, entity);
	}

	static {
		BOTTOM = Block.createCuboidShape(6f, 0f, 6f, 10f, 2f, 10f);
		MIDDLE = Block.createCuboidShape(6.5f, 1.75f, 6.5f, 9.5f, 3.25f, 9.5f);
		TOP = Block.createCuboidShape(7f, 2.25f, 7f, 9f, 4.25f, 9f);
		SHAPE = VoxelShapes.union(BOTTOM, MIDDLE, TOP);
	}
}
