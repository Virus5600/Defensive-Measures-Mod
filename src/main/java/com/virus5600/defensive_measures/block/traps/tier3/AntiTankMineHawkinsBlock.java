package com.virus5600.defensive_measures.block.traps.tier3;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.block.entity.traps.BaseLandmineBlockEntity;
import com.virus5600.defensive_measures.block.traps.BaseLandmineBlock;
import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
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
public class AntiTankMineHawkinsBlock extends BaseLandmineBlock {
	public static final MapCodec<AntiTankMineHawkinsBlock> CODEC = simpleCodec(AntiTankMineHawkinsBlock::new);
	private static final VoxelShape SHAPE;

	public AntiTankMineHawkinsBlock(Properties settings) {
		super(
			settings.instabreak()
				.requiresCorrectToolForDrops()
				.explosionResistance(2.0f)
				.noTerrainParticles()
				.noOcclusion()
		);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	public boolean canTrigger(BlockState state, Level level, BlockPos pos, Entity entity) {
		boolean isArmed = state.getValue(ARMED);
		boolean isInShallowWaters = isWithinFluidDepthThreshold(level, pos, this.getFluidLevelThreshold());
		boolean doingTriggerActions = entity.isSprinting() || entity.fallDistance > 3.0F;
		boolean isLarge = entity.getBoundingBox().getSize() > 3.0 || entity.is(ModEntityTypeTags.HEAVY_ENTITIES);
		boolean isLandmine = entity instanceof FallingBlockEntity fbe && fbe.getBlockState().getBlock() instanceof BaseLandmineBlock;
		boolean isVehicle = entity instanceof VehicleEntity;
		boolean isInCollision = state.getShape(level, pos)
			.move(pos)
			.bounds()
			.intersects(entity.getBoundingBox());

		return isArmed && isInShallowWaters && isInCollision
			&& (doingTriggerActions || isLarge || isVehicle || isLandmine);
	}

	@Override
	public void detonate(BlockState state, Level level, BlockPos pos) {
		if (level instanceof ServerLevel lvl && state.getBlock() == ModBlocks.ANTI_TANK_MINE_HAWKINS) {
			BaseLandmineBlockEntity entity = (BaseLandmineBlockEntity) level.getBlockEntity(pos);
			if (entity != null) {
				entity.setLevel(lvl);
				this.level = lvl;
			}

			DamageSource dmgSrc = ModDamageSources.create(
				level, ModDamageTypes.LANDMINE,
				(Entity) null, null
			);

			lvl.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

			this.createExplosion(
				this, dmgSrc, new ExplosionDamageCalculator(),
				pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
				(float) this.getDamageDealt(state, level), (float) this.getMaxDamageRadius(),
				false, Level.ExplosionInteraction.BLOCK,
				ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
				WeightedList.<ExplosionParticleInfo>builder().build(),
				SoundEvents.GENERIC_EXPLODE, false
			);
		}
	}

	@Override @NonNull
	protected VoxelShape getShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return SHAPE;
	}

	@Override @NonNull
	public MapCodec<AntiTankMineHawkinsBlock> codec() {
		return CODEC;
	}

	@Override
	public int getFluidLevelThreshold() {
		return 5;
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
		return 3;
	}

	public double getMaxDamageRadius() {
		return 5;
	}

	public double getDamageReduction() {
		return 0.5;
	}

	public double getBaseDamage() {
		return 25;
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
		SHAPE = Shapes.or(
			Block.box(
				6.5, -1.0, 5.5,
				9.5, 1.0, 10.5
			),
			Block.box(
				7.375, -0.625, 5.0,
				8.625, 0.625, 5.5
			),
			Block.box(
				7.25, 1.38, 6.0,
				8.75, 1.38, 9.75
			)
		);
	}
}
