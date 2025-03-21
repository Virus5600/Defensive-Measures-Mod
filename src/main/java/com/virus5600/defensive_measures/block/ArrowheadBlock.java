package com.virus5600.defensive_measures.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;

/**
 * Arrowhead blocks are blocks that deal damage to entities that step on them.
 * <br><br>
 * Similar to how magma blocks deal damage to entities that step on them,
 * arrowhead blocks deal damage to entities that step on them. This, however,
 * does not discriminate against sneaking entities, meaning that sneaking
 * entities will still take damage from arrowhead blocks unlike magma blocks.
 * <br><br>
 * Arrowhead blocks deal 2 damage to entities that step on them. This could
 * be changed by any subclass of this block by simply changing the value of
 * the {@link #damageDealt} field.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ArrowheadBlock extends Block {
	private static final VoxelShape BOTTOM;
	private static final VoxelShape MIDDLE;
	private static final VoxelShape TOP;
	private static final VoxelShape SHAPE;

	/**
	 * Defines the amount of damage that the arrowhead block will deal to entities.
	 */
	protected int damageDealt = 2;

	public ArrowheadBlock(Settings settings) {
		super(settings);

		settings.hardness(1.5f)
			.resistance(1.0f)
			.nonOpaque();
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			if (world instanceof ServerWorld serverWorld) {
				DamageSource dmgSrc = ModDamageSources.create(world, ModDamageTypes.ARROWHEAD);
				livingEntity.damage(serverWorld, dmgSrc, this.damageDealt);
			}
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
