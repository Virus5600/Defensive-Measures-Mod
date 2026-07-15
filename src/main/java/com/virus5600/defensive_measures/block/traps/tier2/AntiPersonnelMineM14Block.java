package com.virus5600.defensive_measures.block.traps.tier2;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.block.traps.BaseLandmineBlock;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Anti Personnel Mine is a trap block that only get triggered when an entity steps over it.
 * <br><br>
 * Modelled after the M14 AP mine, it is designed to be effective in maiming an entity instead of
 * dealing a lethal amount of damage. This is to ensure that the entity will be incapacitated and
 * unable to continue moving, allowing for a follow-up attack or capture.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class AntiPersonnelMineM14Block extends BaseLandmineBlock implements SimpleWaterloggedBlock {
	public static final MapCodec<AntiPersonnelMineM14Block> CODEC = simpleCodec(AntiPersonnelMineM14Block::new);
	private static final VoxelShape SHAPE;

	public AntiPersonnelMineM14Block(Properties settings) {
		super(
			settings.instabreak()
				.requiresCorrectToolForDrops()
				.explosionResistance(1.0f)
				.noTerrainParticles()
				.noOcclusion()
		);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	public boolean canTrigger(BlockState state, Level level, BlockPos pos, Entity entity) {
		return state.getValue(ARMED) && entity instanceof LivingEntity;
	}

	@Override
	public void detonate(BlockState state, Level level, BlockPos pos, @Nullable Player player) {
	}

	@Override
	protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	public @NonNull MapCodec<AntiPersonnelMineM14Block> codec() {
		return CODEC;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	@Override
	protected int maxMines() {
		return 1;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	// ModExplosives

	public double getEffectiveRadius() {
		return 2;
	}

	public double getMaxDamageRadius() {
		return 5;
	}

	public double getDamageReduction() {
		return 0.5;
	}

	public double getBaseDamage() {
		return 10;
	}

	@Nullable
	public Level level() {
		return this.level;
	}

	// OwnableBlock

	@Nullable
	public EntityReference<LivingEntity> getOwnerReference() {
		return null;
	}

	// ////////////////// //
	// STATIC INITIALIZER //
	// ////////////////// //

	static {
		SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	}
}
