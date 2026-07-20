package com.virus5600.defensive_measures.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._util.interfaces.ModExplosives;
import com.virus5600.defensive_measures.block.ExplosiveBlock;
import com.virus5600.defensive_measures.entity.ExplosiveEntity;
import com.virus5600.defensive_measures.entity.projectiles.ExplosiveProjectileEntity;
import com.virus5600.defensive_measures.registry.tag.ModBlockTags;

import org.apache.commons.compress.utils.Lists;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension of the {@link ServerExplosion} class, allowing the mod to apply its own custom
 * explosion damage formula as mentioned in {@link #explode(boolean)}. While it allows the usage of
 * the new damage formula, it still allows the use of the vanilla one, keeping the class vanilla
 * friendly. The new damage formula is only applied, and it is still optional, if the entity uses
 * the {@link ExplosiveEntity} interface.
 *
 * @see ExplosiveProjectileEntity
 * @see ExplosiveBlock
 * @see ExplosiveEntity
 * @see ModExplosives
 *
 * @since 1.1.2-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModExplosionImpl extends ServerExplosion {
	protected ModExplosives explosiveSource;
	protected float power;

	public ModExplosionImpl(
		ServerLevel world, @Nullable Entity source, @Nullable ModExplosives explosiveSource,
		@Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior,
		Vec3 pos, float power, float radius, boolean createFire, BlockInteraction destructionType
	) {
		super(world, source, damageSource, behavior, pos, radius, createFire, destructionType);

		this.explosiveSource = explosiveSource;
		this.power = power;
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	private void damageEntities() {
		ModExplosives explosive = this.explosiveSource;

		if (explosive != null && this.getDirectSourceEntity() instanceof ModExplosives explosiveEntity) {
			explosive = explosiveEntity;
		}

		if (explosive == null) {
			throw new IllegalStateException("The damageEntities() method can only be called if the exploding entity is an instance of ExplosiveProjectileEntity.");
		}

		double maxDmgRadius = explosive.getMaxDamageRadius();
		Vec3 explosionPos = this.center();

		// Single query with max radius — damageEntity handles zone detection internally
		AABB fullReceiver = new AABB(
			explosionPos.x() - maxDmgRadius,
			explosionPos.y() - maxDmgRadius,
			explosionPos.z() - maxDmgRadius,
			explosionPos.x() + maxDmgRadius,
			explosionPos.y() + maxDmgRadius,
			explosionPos.z() + maxDmgRadius
		);

		List<Entity> damagedEntities = new ArrayList<>();
		Level lvl = explosive.level();

		if (lvl != null) {
			Entity except = this.explosiveSource instanceof Entity ? (Entity) this.explosiveSource : null;
			ModExplosives finalExplosive = explosive;
			lvl.getEntities(except, fullReceiver)
				.forEach(entity -> this.damageEntity(finalExplosive, entity, damagedEntities, explosionPos));
		}
	}

	private void damageEntity(ModExplosives explosive, Entity entity, List<Entity> list, Vec3 explosionPos) {
		if (list.contains(entity)) return;

		if (!entity.ignoreExplosion(this)) {
			double effectiveRadius = explosive.getEffectiveRadius();
			double maxDmgRadius = explosive.getMaxDamageRadius();
			double distance = Math.sqrt(entity.distanceToSqr(explosionPos));

			// Spherical radius guard
			if (distance > maxDmgRadius) {
				list.add(entity);
				return;
			}

			boolean shouldDmg = this.damageCalculator.shouldDamageEntity(this, entity);
			double damageDealt = this.getPower();
			double dmgReduction = explosive.getDamageReduction();
			float knockbackMod = this.damageCalculator.getKnockbackMultiplier(entity);
			float exposure = !shouldDmg && knockbackMod == 0 ?
				0f : getSeenPercent(this.center(), entity);

			// Size-aware factor
			AABB box = entity.getBoundingBox();
			double sizeFactor = Mth.clamp(
				(box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ)
					/ (0.6 * 1.9 * 0.6),
				0.5,
				2.0
			);

			// Determine inner vs outer zone from distance
			double baseDmgFinal;
			if (distance <= effectiveRadius) {
				// Inner zone — full damage
				baseDmgFinal = damageDealt;
			}
			else {
				// Outer zone — exponential falloff
				float normalizedRadius = (float) ((distance - effectiveRadius) / (maxDmgRadius - effectiveRadius));
				float numerator = (float) (Math.exp(-dmgReduction * normalizedRadius) - Math.exp(-dmgReduction));
				float denominator = (float) (1 - Math.exp(-dmgReduction));
				baseDmgFinal = damageDealt * (numerator / denominator);
			}

			float dmg = (float) (baseDmgFinal * sizeFactor * exposure);
			dmg = Math.max(1, dmg);

			if (shouldDmg) {
				boolean hurt = entity.hurtServer(
					(ServerLevel) explosive.level(),
					this.getDamageSource(),
					dmg
				);

				if (hurt && entity instanceof LivingEntity le && explosive instanceof ExplosiveBlock) {
					LivingEntity by = null;

					if (explosive.getOwner() instanceof LivingEntity ownerLe) {
						by = ownerLe;
					}

					le.setLastHurtByMob(by);
				}
			}

			// Knockback
			if (knockbackMod != 0) {
				double normalizedDist = distance / maxDmgRadius;
				double knockbackResistance = entity instanceof LivingEntity living
					? living.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)
					: 0.0;

				double knockback = (1 - normalizedDist) * exposure * knockbackMod * (1 - knockbackResistance);
				Vec3 knockbackVec = entity.getEyePosition()
					.subtract(explosionPos)
					.normalize()
					.scale(knockback);

				entity.push(knockbackVec);

				if (entity instanceof ServerPlayer serverPlayer) {
					this.getHitPlayers().put(serverPlayer, knockbackVec);
				}
			}

			entity.onExplosionHit(explosive.getOwner());
		}

		list.add(entity);
	}

	/**
	 * The vanilla explosion mechanic, which is the same as calling {@link ServerExplosion#explode()}.
	 *
	 * @return The number of blocks destroyed by the explosion, using the vanilla explosion mechanic.
	 *
	 * @see #vanillaExplode()
	 * @see #explode()
	 * @see #explode(boolean)
	 * @see #explode(boolean, boolean)
	 */
	public final int vanillaExplode() {
		return super.explode();
	}

	/**
	 * An overloaded version of the {@link #explode()} method which allows the caller to specify
	 * whether to use the vanilla explosion mechanic, or use the mod's custom explosion mechanic.
	 * However, the custom explosion mechanic only works if the exploding entity is using the
	 * custom {@link ModExplosives} interface. Otherwise, it will revert to the vanilla
	 * explosion mechanic.
	 * <br><br>
	 * Using this method meant that the explosive can trigger other explosive blocks defined under
	 * the {@link ModBlockTags#EXPLOSIVES} tag.
	 *
	 * @param destroyBlocks Whether the explosion should destroy blocks.
	 *
	 * @return The number of blocks destroyed by the explosion.
	 *
	 * @see #vanillaExplode()
	 * @see #explode()
	 * @see #explode(boolean)
	 * @see #explode(boolean, boolean)
	 */
	public int explode(boolean destroyBlocks) {
		return (this.getDirectSourceEntity() instanceof ModExplosives) ?
			this.vanillaExplode() : this.explode(destroyBlocks, true);
	}

	/**
	 * An overloaded version of the {@link #explode()} method which utilizes the mod's custom damage
	 * formula. However, this method only works if the exploding entity is using the custom
	 * {@link ModExplosives}.
	 * <br><br>
	 * The way the damage works is similar to vanilla but now, two more stages are added to identify
	 * the damage falloff:
	 * <ol>
	 *     <li>Entities within the {@link ExplosiveProjectileEntity#getEffectiveRadius() effective radius} will receive the full {@link ExplosiveProjectileEntity#getDamage() base damage}.</li>
	 *     <li>Entities outside the effective radius but within the {@link ExplosiveProjectileEntity#getMaxDamageRadius() max radius} will receive reduced damage based on the {@link ExplosiveProjectileEntity#getDamageReduction() damage reduction} multiplier.</li>
	 * </ol>
	 * <hr>
	 * <h2>Damage Calculation</h2>
	 * The full damage is not calculated as it is applied directly to all the entities
	 * within the {@link ExplosiveProjectileEntity#getEffectiveRadius() effective radius}. However, the reduced
	 * damage is calculated as follows:
	 * <pre><code>
	 * normalizedRadius = (radius - effectiveRadius) / (maxDmgRadius - effectiveRadius)
	 * formula = (e^(-dmgReduction * normalizedRadius) - e^(-dmgReduction)) / (1 - e^(-dmgReduction))
	 * dmg = baseDmg * formula
	 * </code></pre>
	 * Wherein:
	 * <br>
	 * <ul>
	 *     <li>{@code effectiveRadius <= radius <= maxRadius}</li>
	 *     <li>{@code 0.0 <= dmgReduction <= 1.0}</li>
	 *     <li>{@code baseDmg} is the base damage dealt by the explosion.</li>
	 *     <li>{@code normalizedRadius} is the normalized radius of the entity from the effective radius.</li>
	 *     <li>{@code formula} is the formula used to calculate the reduced damage.</li>
	 *     <li>{@code dmg} is the reduced damage dealt to the entity.</li>
	 * </ul>
	 *
	 * @param destroyBlocks Whether the explosion should destroy blocks.
	 * @param triggerExplosives Whether the explosion should trigger other explosives.
	 *
	 * @return The number of blocks destroyed by the explosion.
	 *
	 * @see #vanillaExplode()
	 * @see #explode()
	 * @see #explode(boolean)
	 * @see #explode(boolean, boolean)
	 *
	 * @apiNote This method still reverts to the vanilla formula via {@link #explode()} if the
	 * exploding entity is not an instance of {@link ExplosiveProjectileEntity}.
	 *
	 * @implNote The graphing calculator could be found at <a href="https://www.desmos.com/calculator/pdm27kw9oe">this link</a>.
	 */
	public int explode(boolean destroyBlocks, boolean triggerExplosives) {
		ModExplosives explosive = this.explosiveSource;

		if (explosive != null && this.getDirectSourceEntity() instanceof ModExplosives explosiveEntity) {
			explosive = explosiveEntity;
		}

		if (explosive == null) {
			return super.explode();
		}

		List<BlockPos> list = this.calculateExplodedPositions();

		this.level().gameEvent(this.getDirectSourceEntity(), GameEvent.EXPLODE, this.center());
		this.damageEntities();

		if (this.interactsWithBlocks() || destroyBlocks || triggerExplosives) {
			ProfilerFiller profiler = Profiler.get();

			if (destroyBlocks) {
				profiler.push("explosion_blocks");
				this.interactWithBlocks(list);
				profiler.pop();
			}
			else if (triggerExplosives) {
				profiler.push("explosion_trigger");
				List<BlockPos> forTrigger = Lists.newArrayList();
				for (BlockPos pos : list) {
					BlockState block = this.level().getBlockState(pos);
					if (block.is(Blocks.TNT) || block.is(ModBlockTags.EXPLOSIVES)) {
						forTrigger.add(pos);
					}
				}
				this.interactWithBlocks(forTrigger);
				profiler.pop();
			}
		}

		if (this.fire) {
			this.createFire(list);
		}

		return list.size();
	}

	// /////////////////// //
	// OVERRIDANLE METHODS //
	// /////////////////// //

	public float getPower() {
		return this.power;
	}
}
