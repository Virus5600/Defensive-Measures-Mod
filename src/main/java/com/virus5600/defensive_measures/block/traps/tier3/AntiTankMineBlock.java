package com.virus5600.defensive_measures.block.traps.tier3;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.block.entity.traps.BaseLandmineBlockEntity;
import com.virus5600.defensive_measures.block.traps.BaseLandmineBlock;
import com.virus5600.defensive_measures.registry.tag.ModEntityTypeTags;

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
public class AntiTankMineBlock extends BaseLandmineBlock {
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

	// ////// //
	// METHOD //
	// ////// //

	@Override
	public boolean canTrigger(BlockState state, Level level, BlockPos pos, Entity entity) {

		boolean doingTriggerActions = entity.isSprinting() || entity.fallDistance > 3.0F;
		boolean isLarge = entity.getBoundingBox().getSize() > 3.0 || entity.is(ModEntityTypeTags.HEAVY_ENTITIES);
		boolean isVehicle = entity instanceof VehicleEntity;
		boolean isArmed = state.getValue(ARMED);

		return isArmed && (doingTriggerActions || isLarge || isVehicle);
	}

	@Override
	public void detonate(BlockState state, Level level, BlockPos pos) {
		if (level instanceof ServerLevel lvl) {
			lvl.sendParticles(
				ParticleTypes.EXPLOSION,
				pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
				1, 0, 0, 0, 0
			);
		}
	}

	@Override @NonNull
	protected VoxelShape getShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return SHAPE;
	}

	@Override @NonNull
	public MapCodec<AntiTankMineBlock> codec() {
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

	// EntityBlock

	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BaseLandmineBlockEntity(pos, state);
	}

	// ////////////////// //
	// STATIC INITIALIZER //
	// ////////////////// //

	static {
		SHAPE = Block.box(
			7.0, 0.0, 7.0,
			9.0, 0.4, 9.0
		);
	}
}
