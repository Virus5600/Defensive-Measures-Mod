package com.virus5600.defensive_measures.entity.projectiles;

import com.virus5600.defensive_measures.registry.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.block.ModBlocks;
import com.virus5600.defensive_measures.block.traps.tier3.AntiTankMineHawkinsBlock;
import com.virus5600.defensive_measures.entity.ExplosiveEntity;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.item.ModItems;

import org.jetbrains.annotations.NotNull;

/**
 * The thrown entity for the {@link AntiTankMineHawkinsBlock Anti-Tank Mine Hawkins (ATMH)}.
 * <br><br>
 * This represents the "grenade" version of the ATMH, which can be thrown by the player like how
 * the British paratroopers uses this when the need arises and thus, showing the versatility of the
 * ATMH.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class AntiTankHawkinsEntity extends ThrowableItemProjectile implements ExplosiveEntity {
	@NotNull
	private BlockState blockState = ModBlocks.ANTI_TANK_MINE_HAWKINS.defaultBlockState();

	public AntiTankHawkinsEntity(final EntityType<? extends ThrowableItemProjectile> type, final Level level) {
		super(type, level);
	}

	public AntiTankHawkinsEntity(final Level level, final LivingEntity owner, final ItemStack stack) {
		super(ModEntities.ANTI_TANK_HAWKINS, owner, level, stack);
	}

	public AntiTankHawkinsEntity(final Level level, final double x, final double y, final double z, final ItemStack stack) {
		super(ModEntities.ANTI_TANK_HAWKINS, x, y, z, level, stack);
	}

	// /////// //
	// METHODS //
	// /////// //

	protected void onHitEntity(final EntityHitResult hitResult) {
		super.onHitEntity(hitResult);

		Level level = this.level();
		((AntiTankMineHawkinsBlock) this.blockState.getBlock())
			.detonate(this.blockState, this, level, this.position());
	}

	protected void onHitBlock(final BlockHitResult hitResult) {
		super.onHitBlock(hitResult);

		Level level = this.level();
		if (level instanceof ServerLevel lvl) {
			Direction hitFace = hitResult.getDirection();

			if (hitFace == Direction.UP) {
				BlockPos pos = hitResult.getBlockPos().relative(hitFace);
				FluidState fluidState = lvl.getFluidState(pos);
				BlockState state = lvl.getBlockState(pos);
				boolean isWaterlogged = fluidState.isSource() && fluidState.is(FluidTags.WATER);
				boolean isAir = state.isAir();
				boolean isWater = state.is(Blocks.WATER);
				boolean isShrub = state.is(ModBlockTags.GREENERY);
				boolean canTrigger = state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.FIRE);

				if ((isAir || isWater || isShrub) && !canTrigger) {
					if (isShrub) {
						lvl.destroyBlock(pos, true);
					}

					boolean placed = lvl.setBlockAndUpdate(
						pos,
						this.blockState
							.setValue(AntiTankMineHawkinsBlock.ARMED, true)
							.setValue(AntiTankMineHawkinsBlock.THROWN, true)
							.setValue(AntiTankMineHawkinsBlock.WATERLOGGED, isWaterlogged)
					);

					BlockPos posBelow = pos.relative(Direction.DOWN);
					if (placed) {
						if (lvl.getBlockState(pos).getBlock() instanceof AntiTankMineHawkinsBlock landmine) {
							landmine.setPlacedBy(level, pos, this.blockState, (LivingEntity) this.getOwner(), this.getItem());

							if (lvl.getBlockState(posBelow).isAir()) {
								lvl.scheduleTick(pos, landmine, 1);
							}
						}
					}
				}
				else {
					((AntiTankMineHawkinsBlock) this.blockState.getBlock())
						.detonate(this.blockState, this, level, this.position());
				}
			}
		}
	}

	protected void onHit(final HitResult hitResult) {
		super.onHit(hitResult);

		Level level = this.level();

		if (level instanceof ServerLevel lvl) {
			if (hitResult instanceof BlockHitResult bhr && bhr.getDirection() != Direction.UP) {
				this.setDeltaMovement(this.getDeltaMovement().multiply(-0.1, -0.125, -0.1));
			}
			else {
				byte event = hitResult.getType() == HitResult.Type.ENTITY ? EntityEvent.DEATH : EntityEvent.POOF;
				lvl.broadcastEntityEvent(this, event);
				this.discard();
			}
		}
	}

	protected void onDeflection(boolean byAttack) {
		super.onDeflection(byAttack);

		if (byAttack) {
			((AntiTankMineHawkinsBlock) this.blockState.getBlock())
				.detonate(this.blockState, this, this.level(), this.position());
		}
	}

	protected ParticleOptions getParticle() {
		return ParticleTypes.EXPLOSION;
	}

	public void handleEntityEvent(final byte id) {
		if (id == EntityEvent.DEATH) {
			ParticleOptions particle = this.getParticle();

			for(int i = 0; i < 8; ++i) {
				this.level()
					.addParticle(
						particle,
						this.getX(), this.getY(), this.getZ(),
						0.0, 0.0, 0.0
					);
			}
		}
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	public void setBlockState(BlockState state) {
		if (state.getBlock() != ModBlocks.ANTI_TANK_MINE_HAWKINS) {
			DefensiveMeasures.LOGGER.warn("'{}' is not an AT Hawkins Landmine.", state.getBlock().getName().getString());
			return;
		}

		this.blockState = state;
	}

	public BlockState getBlockState() {
		return this.blockState;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	// ThrowableItemProjectile

	@Override
	protected Item getDefaultItem() {
		return ModItems.HAWKINS_ANTI_TANK_MINE;
	}

	// ExplosiveEntity

	@Override
	public double getEffectiveRadius() {
		return ((AntiTankMineHawkinsBlock) this.blockState.getBlock()).getEffectiveRadius();
	}

	@Override
	public double getMaxDamageRadius() {
		return ((AntiTankMineHawkinsBlock) this.blockState.getBlock()).getMaxDamageRadius();
	}

	@Override
	public double getDamageReduction() {
		return ((AntiTankMineHawkinsBlock) this.blockState.getBlock()).getDamageReduction();
	}

	@Override
	public double getBaseDamage() {
		return ((AntiTankMineHawkinsBlock) this.blockState.getBlock()).getBaseDamage();
	}
}
