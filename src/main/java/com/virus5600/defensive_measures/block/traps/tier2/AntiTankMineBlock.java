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
 * Anti Tank Mine is a trap block that only get triggered when a vehicle moves over it. On some
 * occassions though, it could also be triggered by other stuff.
 * <br><br>
 * Modelled after the British No. 75 "Hawkins" Grenade/Mine, this specific AT mine is used by
 * British paratroopers back in WWII. It was used due to its ease of use, deployment, and
 * versitality, allowing the paratroopers to also double the mine as a throwable.
 * <br><br>
 * In that regard, this mine will also be a throwable block that deals blunt damage upon entity hit
 * before falling to the ground and settling down as a landmine.
 * <br><br>
 * It also has two placement model: for when thrown and for when directly placed on a block.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class AntiTankMineBlock extends BaseLandmineBlock implements SimpleWaterloggedBlock {
	public static final MapCodec<AntiTankMineBlock> CODEC = simpleCodec(AntiTankMineBlock::new);
	private static final VoxelShape SHAPE;

	public AntiTankMineBlock(Properties settings) {
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
		return false;
	}

	@Override
	public void detonate(BlockState state, Level level, BlockPos pos, @Nullable Player player) {

	}

	@Override
	protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	public @NonNull MapCodec<AntiTankMineBlock> codec() {
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
