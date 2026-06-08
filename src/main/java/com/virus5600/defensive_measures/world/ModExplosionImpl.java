package com.virus5600.defensive_measures.world;

import com.virus5600.defensive_measures.entity.ExplosiveEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;

import com.virus5600.defensive_measures.entity.projectiles.ExplosiveProjectileEntity;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

public class ModExplosionImpl extends ExplosionImpl {
	public ModExplosionImpl(ServerWorld world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, Vec3d pos, float power, boolean createFire, DestructionType destructionType) {
		super(world, entity, damageSource, behavior, pos, power, createFire, destructionType);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	private void damageEntities() {
		if (!(this.getEntity() instanceof ExplosiveProjectileEntity epe)) {
			throw new IllegalStateException("The damageEntities() method can only be called if the exploding entity is an instance of ExplosiveProjectileEntity.");
		}

		// Damaging entities within a radius.
		double effectiveRadius = epe.getEffectiveRadius();
		double maxDmgRadius = epe.getMaxDamageRadius();

		// Damaging entities within Effective Radius
		Vec3d explosionPos = this.getPosition();
		Box fullDmgReceiver = new Box(
			explosionPos.getX() - effectiveRadius,
			explosionPos.getY() - effectiveRadius,
			explosionPos.getZ() - effectiveRadius,
			explosionPos.getX() + effectiveRadius,
			explosionPos.getY() + effectiveRadius,
			explosionPos.getZ() + effectiveRadius
		);

		// Entities in the inner radius
		List<Entity> damagedEntities = new ArrayList<>();
		epe.getEntityWorld()
			.getOtherEntities(epe, fullDmgReceiver)
			.forEach(entity -> this.damageEntity(epe, entity, damagedEntities, false, effectiveRadius));

		// Creates a dynamic increment for the radius to reduce the number of entities to check
		double steps = (maxDmgRadius - effectiveRadius) > 10 ? 0.5 : 0.25;

		// Damaging entities outside Effective Radius but within Max Damage Radius
		for (double currentRadius = effectiveRadius; currentRadius <= maxDmgRadius; currentRadius += steps) {
			final double capturedRadius = currentRadius;
			double ext = capturedRadius + 1;
			Box partialDmgReceiver = new Box(
				explosionPos.getX() - ext,
				explosionPos.getY() - ext,
				explosionPos.getZ() - ext,
				explosionPos.getX() + ext,
				explosionPos.getY() + ext,
				explosionPos.getZ() + ext
			);

			epe.getEntityWorld()
				.getOtherEntities(epe, partialDmgReceiver)
				.forEach(entity -> this.damageEntity(epe, entity, damagedEntities, true, capturedRadius));
		}
	}

	private void damageEntity(ProjectileEntity projectile, Entity entity, List<Entity> list, boolean outerRad, double currentRadius) {
		// Skip if already damaged
		if (list.contains(entity) || entity.isImmuneToExplosion(this)) return;

		if (projectile instanceof ExplosiveEntity epe) {
			boolean shouldDmg = this.behavior.shouldDamage(this, entity);
			double baseDmg = epe.getBaseDamage();
			double effectiveRadius = epe.getEffectiveRadius();
			double maxDmgRadius = epe.getMaxDamageRadius();
			double dmgReduction = epe.getDamageReduction();
			float knockbackMod = this.behavior.getKnockbackModifier(entity);
			float exposure = !shouldDmg && knockbackMod == 0 ?
				0f : calculateReceivedDamage(this.getPosition(), entity);

			// Size-aware factor
			Box box = entity.getBoundingBox();
			double sizeFactor = MathHelper.clamp(
				(box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ)
					/ (0.6 * 1.9 * 0.6),
				0.5,
				2.0
			);

			// If this is the outer radius, implement the damage falloff.
			if (outerRad) {
				float normalizedRadius = (float) ((currentRadius - effectiveRadius) / (maxDmgRadius - effectiveRadius));
				float numerator = (float) (Math.exp(-dmgReduction * normalizedRadius) - Math.exp(-dmgReduction));
				float denominator = (float) (1 - Math.exp(-dmgReduction));
				float formula = numerator / denominator;

				baseDmg = baseDmg * formula;
			}

			// sizeFactor and exposure applied on top of baseDmg
			float dmg = (float) (baseDmg * sizeFactor * exposure);
			dmg = Math.max(1, dmg);

			// Apply damage only if shouldDmg is true
			if (shouldDmg) {
				entity.damage(
					(ServerWorld) projectile.getEntityWorld(),
					this.getDamageSource(),
					dmg
				);
			}

			// Apply knockback
			if (knockbackMod != 0) {
				Vec3d entityPos = entity.getEyePos();
				double distance = Math.sqrt(entity.squaredDistanceTo(this.getPosition()));
				double normalizedDist = distance / maxDmgRadius;
				double knockbackResistance = entity instanceof LivingEntity living
					? living.getAttributeValue(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE)
					: 0.0;

				double knockback = (1 - normalizedDist) * exposure * knockbackMod * (1 - knockbackResistance);
				Vec3d knockbackVec = entityPos.subtract(this.getPosition())
					.normalize()
					.multiply(knockback);

				entity.addVelocity(knockbackVec);
			}

			// Notify entity of explosion
			entity.onExplodedBy(projectile.getOwner());
		}
		else {
			throw new IllegalStateException("The damageEntity() method can only be called if the exploding entity is an instance of ExplosiveEntity.");
		}

		// Don't forget to call `onExplodedBy` for the damaged entity
		entity.onExplodedBy(projectile);

		// Always add to list to prevent double-processing
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
		return useVanilla || !(this.getEntity() instanceof ExplosiveProjectileEntity) ?
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
		if (!(this.getEntity() instanceof ExplosiveProjectileEntity)) {
			return super.explode();
		}

		List<BlockPos> list = this.getBlocksToDestroy();

		this.getWorld().emitGameEvent(this.getEntity(), GameEvent.EXPLODE, this.getPosition());
		this.damageEntities();

		if (this.shouldDestroyBlocks() && destroyBlocks) {
			Profiler profiler = Profilers.get();

			profiler.push("explosion_blocks");
			this.destroyBlocks(list);
			profiler.pop();
		}

		if (this.createFire) {
			this.createFire(list);
		}

		return list.size();
	}
}
