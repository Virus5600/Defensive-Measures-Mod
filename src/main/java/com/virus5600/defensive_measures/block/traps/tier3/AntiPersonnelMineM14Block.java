package com.virus5600.defensive_measures.block.traps.tier3;

import com.mojang.serialization.MapCodec;
import com.virus5600.defensive_measures.registry.tag.ModEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.virus5600.defensive_measures.block.entity.traps.BaseLandmineBlockEntity;
import com.virus5600.defensive_measures.block.traps.BaseLandmineBlock;
import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;

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
public class AntiPersonnelMineM14Block extends BaseLandmineBlock {
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
		boolean isArmed = state.getValue(ARMED);
		boolean isLiving = entity instanceof LivingEntity
			|| entity.is(EntityTypeTags.IMPACT_PROJECTILES)
			|| entity.is(ModEntityTypeTags.PROJECTILES);
		boolean isInCollision = state.getShape(level, pos)
			.move(pos)
			.bounds()
			.intersects(entity.getBoundingBox());

		return isArmed && isLiving && isInCollision;
	}

	@Override
	public void detonate(BlockState state, Level level, BlockPos pos) {
		if (level instanceof ServerLevel lvl) {
			BaseLandmineBlockEntity entity = (BaseLandmineBlockEntity) level.getBlockEntity(pos);
			if (entity != null) {
				entity.setLevel(lvl);
				this.level = lvl;
			}

			DamageSource dmgSrc = ModDamageSources.create(
				level, ModDamageTypes.LANDMINE,
				(Entity) null, null
			);

			this.createExplosion(
				this, dmgSrc, new ExplosionDamageCalculator(),
				pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
				(float) this.getDamageDealt(state, lvl),  false,
				Level.ExplosionInteraction.BLOCK,
				ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
				WeightedList.<ExplosionParticleInfo>builder().build(),
				SoundEvents.GENERIC_EXPLODE, false
			);

			lvl.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
		}
	}

	@Override @NonNull
	protected VoxelShape getShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		return SHAPE;
	}

	@Override @NonNull
	public MapCodec<AntiPersonnelMineM14Block> codec() {
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
		return 4;
	}

	public double getDamageReduction() {
		return 0.5;
	}

	public double getBaseDamage() {
		return 10;
	}

	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.level().getBlockEntity(pos);
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
