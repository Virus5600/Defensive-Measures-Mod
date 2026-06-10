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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.entity.ExplosiveEntity;
import com.virus5600.defensive_measures.entity.projectiles.ExplosiveProjectileEntity;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModExplosionImpl extends ServerExplosion {
	public ModExplosionImpl(ServerLevel world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior, Vec3 pos, float power, boolean createFire, BlockInteraction destructionType) {
		super(world, entity, damageSource, behavior, pos, power, createFire, destructionType);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	private void damageEntities() {
		if (!(this.getDirectSourceEntity() instanceof ExplosiveProjectileEntity epe)) {
			throw new IllegalStateException("The damageEntities() method can only be called if the exploding entity is an instance of ExplosiveProjectileEntity.");
		}

		double maxDmgRadius = epe.getMaxDamageRadius();
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
		epe.level()
			.getEntities(epe, fullReceiver)
			.forEach(entity -> this.damageEntity(epe, entity, damagedEntities, explosionPos));
	}

	private void damageEntity(Projectile projectile, Entity entity, List<Entity> list, Vec3 explosionPos) {
		if (list.contains(entity)) return;

		if (!entity.ignoreExplosion(this) && (projectile instanceof ExplosiveEntity epe)) {
			double effectiveRadius = epe.getEffectiveRadius();
			double maxDmgRadius = epe.getMaxDamageRadius();
			double distance = Math.sqrt(entity.distanceToSqr(explosionPos));

			// Spherical radius guard
			if (distance > maxDmgRadius) {
				list.add(entity);
				return;
			}

			boolean shouldDmg = this.damageCalculator.shouldDamageEntity(this, entity);
			double baseDmg = epe.getBaseDamage();
			double dmgReduction = epe.getDamageReduction();
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
				baseDmgFinal = baseDmg;
			}
			else {
				// Outer zone — exponential falloff
				float normalizedRadius = (float) ((distance - effectiveRadius) / (maxDmgRadius - effectiveRadius));
				float numerator = (float) (Math.exp(-dmgReduction * normalizedRadius) - Math.exp(-dmgReduction));
				float denominator = (float) (1 - Math.exp(-dmgReduction));
				baseDmgFinal = baseDmg * (numerator / denominator);
			}

			float dmg = (float) (baseDmgFinal * sizeFactor * exposure);
			dmg = Math.max(1, dmg);

			if (shouldDmg) {
				entity.hurtServer(
					(ServerLevel) projectile.level(),
					this.getDamageSource(),
					dmg
				);
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

			entity.onExplosionHit(projectile.getOwner());
		}

		list.add(entity);
	}

	/**
	 * An overloaded version of the {@link #explode()} method which allows the caller to specify
	 * whether to use the vanilla explosion mechanic, or use the mod's custom explosion mechanic.
	 * However, the custom explosion mechanic only works if the exploding entity is using the
	 * custom {@link ExplosiveProjectileEntity} class. Otherwise, it will revert to the vanilla
	 * explosion mechanic.
	 *
	 * @param useVanilla    Whether to use the vanilla explosion mechanic. If false, will use the mod's custom explosion mechanic.
	 * @param destroyBlocks Whether the explosion should destroy blocks. Only applicable if useVanilla is false and the exploding entity is an instance of {@code ExplosiveProjectileEntity}.
	 *
	 * @return The number of blocks destroyed by the explosion.
	 *
	 * @see #explode()
	 * @see #explode(boolean)
	 */
	public int explode(boolean useVanilla, boolean destroyBlocks) {
		return useVanilla || !(this.getDirectSourceEntity() instanceof ExplosiveProjectileEntity) ?
			super.explode() : this.explode(destroyBlocks);
	}

	/**
	 * An overloaded version of the {@link #explode()} method which utilizes the mod's custom damage
	 * formula. However, this method only works if the exploding entity is using the custom
	 * {@link ExplosiveProjectileEntity}.
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
	 * @param destroyBlocks Whether the explosion should destroy blocks. Only applicable if the exploding entity is an instance of {@code ExplosiveProjectileEntity}.
	 *
	 * @return The number of blocks destroyed by the explosion.
	 *
	 * @see #explode()
	 * @see #explode(boolean, boolean)
	 *
	 * @apiNote This method still reverts to the vanilla formula via {@link #explode()} if the
	 * exploding entity is not an instance of {@link ExplosiveProjectileEntity}.
	 */
	public int explode(boolean destroyBlocks) {
		// If not an instance of the custom explosive entity, then use the vanilla explosion.
		if (!(this.getDirectSourceEntity() instanceof ExplosiveProjectileEntity)) {
			return super.explode();
		}

		List<BlockPos> list = this.calculateExplodedPositions();

		this.level().gameEvent(this.getDirectSourceEntity(), GameEvent.EXPLODE, this.center());
		this.damageEntities();

		if (this.interactsWithBlocks() && destroyBlocks) {
			ProfilerFiller profiler = Profiler.get();

			profiler.push("explosion_blocks");
			this.interactWithBlocks(list);
			profiler.pop();
		}

		if (this.fire) {
			this.createFire(list);
		}

		return list.size();
	}
}
