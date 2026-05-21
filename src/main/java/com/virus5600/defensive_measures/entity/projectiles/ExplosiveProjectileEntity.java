package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code ExplosiveProjectileEntity} is an abstract class that extends {@link TurretProjectileEntity}.
 * This class is used to represent a projectile entity that explodes on impact, dealing damage to
 * entities within a certain radius. This class is used to represent explosive projectiles, such as
 * cannonballs, missiles, and other similar projectiles.
 * <br><br>
 * By default, the explosive projectile entity has the following behavior:
 * <ul>
 *     <li>Armor points affect the piercing of the projectile.</li>
 *     <li>Armor points do not affect the speed of the projectile.</li>
 *     <li>Armor points do not affect the base damage dealt by the projectile.</li>
 *     <li>Base damage is set to 5.</li>
 * </ul>
 * <hr/>
 * <h2>Damage Mechanics</h2>
 * The mechanics of applying damage to an entity is different from the way how
 * vanilla minecraft explosion deals. The main differences was that the vanilla {@link net.minecraft.entity.projectile.ExplosiveProjectileEntity ExplosiveProjectileEntity}
 * deals the full damage outright to all entities within the effective radius, while this
 * custom implementation deals the full damage to entities within the effective radius while
 * those within the max radius and outside the effective radius will receive exponential reduced
 * damage based on the distance from the effective radius.
 * <br><br>
 * The formula used to calculate the exponential reduced damage could be seen in
 * {@link #doDamage() doDamage} method. And to help the developers or anyone exploring this
 * mod's source code, a Desmos graphing calculator was created to visualize the reduced damage
 * per iterations. The graphing calculator could be found at <a href="https://www.desmos.com/calculator/pdm27kw9oe">this link</a>.
 *
 * @see TurretProjectileEntity
 * @see KineticProjectileEntity
 * @see AreaCloudEntity
 *
 * @see #doDamage()
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class ExplosiveProjectileEntity extends TurretProjectileEntity {
	protected static final Pool<BlockParticleEffect> EXPLOSION_BLOCK_PARTICLES;
	protected static final Pool<BlockParticleEffect> EMPTY_EXPLOSION_BLOCK_PARTICLES;

	public double accelerationPower = 0.1;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	protected ExplosiveProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, World world) {
		super(entityType, world);

		this.setDamage(5);
		this.setPierceLevel(this.getMaxPierceLevel());
		this.setSpeedAffectsDamage(false);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	protected void onDeflected(boolean fromAttack) {
		super.onDeflected(fromAttack);

		if (fromAttack) {
			this.accelerationPower = 0.1;
		} else {
			this.accelerationPower *= 0.5;
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult hitResult) {
		super.onBlockHit(hitResult);

		if (!this.getEntityWorld().isClient()) {
			this.doDamage();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult hitResult) {
		super.onEntityHit(hitResult);

		if (!this.getEntityWorld().isClient()) {
			this.doDamage();
		}
	}

	private double radius = 0;
	/**
	 * Applies damage to the surrounding entity.
	 * <br><br>
	 * The damage is applied in two stages:
	 * <ol>
	 *     <li>Entities within the {@link #getEffectiveRadius() effective radius} will receive the full {@link #getDamage() base damage}.</li>
	 *     <li>Entities outside the effective radius but within the {@link #getMaxDamageRadius() max radius} will receive reduced damage based on the {@link #getDamageReduction() damage reduction} multiplier.</li>
	 * </ol>
	 * <hr>
	 * <h2>Damage Calculation</h2>
	 * The full damage is not calculated as it is applied directly to all the entities
	 * within the {@link #getEffectiveRadius() effective radius}. However, the reduced
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
	 */
	public void doDamage() {
		// Create the damage source
		DamageSource dmgSrc = this.getDamageSources().explosion(
			this,
			this.getOwner() == null ? this : this.getOwner()
		);

		// Create the explosion
		this.getEntityWorld()
			.createExplosion(
				this,
				dmgSrc,
				new ExplosionBehavior(),
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				1.25F,
				false,
				World.ExplosionSourceType.NONE,
				this.getSmallExplosionParticleType(),
				this.getLargeExplosionParticleType(),
				EMPTY_EXPLOSION_BLOCK_PARTICLES,
				SoundEvents.ENTITY_GENERIC_EXPLODE
			);

		// Damaging entities within a radius.
		double effectiveRadius = this.getEffectiveRadius();
		double maxDmgRadius = this.getMaxDamageRadius();
		double dmgReduction = this.getDamageReduction();
		double baseDmg = this.getDamage();

		// Damaging entities within Effective Radius
		Box fullDmgReceiver = new Box(
			this.getX() - effectiveRadius,
			this.getY() - effectiveRadius,
			this.getZ() - effectiveRadius,
			this.getX() + effectiveRadius,
			this.getY() + effectiveRadius,
			this.getZ() + effectiveRadius
		);

		// Entities receives full damage
		List<LivingEntity> damagedEntities = new ArrayList<>();
		this.getEntityWorld()
			.getOtherEntities(this, fullDmgReceiver)
			.forEach(entity -> {
				if (entity instanceof LivingEntity) {
					entity.damage(
						(ServerWorld) this.getEntityWorld(),
						dmgSrc,
						(float) baseDmg
					);
					damagedEntities.add((LivingEntity) entity);
				}
			});

		// Creates a dynamic increment for the radius to reduce the number of entities to check
		double steps = (maxDmgRadius - effectiveRadius) > 10 ? 0.5 : 0.25;

		// Damaging entities outside Effective Radius but within Max Damage Radius
		for (radius = effectiveRadius; radius <= maxDmgRadius; radius += steps) {
			double ext = radius + 1;
			Box partialDmgReceiver = new Box(
				this.getX() - ext,
				this.getY() - ext,
				this.getZ() - ext,
				this.getX() + ext,
				this.getY() + ext,
				this.getZ() + ext
			);

			this.getEntityWorld()
				.getOtherEntities(this, partialDmgReceiver)
				.forEach(entity -> {
					if (entity instanceof LivingEntity && !damagedEntities.contains(entity)) {
						float normalizedRadius = (float) ((radius - effectiveRadius) / (maxDmgRadius - effectiveRadius));
						float numerator = (float) (Math.exp(-dmgReduction * normalizedRadius) - Math.exp(-dmgReduction));
						float denominator = (float) (1 - Math.exp(-dmgReduction));
						float formula = numerator / denominator;
						float dmg = (float) (baseDmg * formula);

						entity.damage(
							(ServerWorld) this.getEntityWorld(),
							dmgSrc,
							dmg
						);

						damagedEntities.add((LivingEntity) entity);
					}
				});
		}

		this.discard();
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return false;
	}

	/**
	 * Adds particles to the projectile. It uses the provided position
	 * as the position of where the provided particle will appear. If the
	 * {@code particleEffect} is {@code null}, this method will not add
	 * any particles.
	 *
	 * @param pos The position of the particle.
	 * @param particleEffect The particle effect to add.
	 */
	protected void addParticles(Vec3d pos, ParticleEffect particleEffect) {
		if (particleEffect != null) {
			this.getEntityWorld().addParticleClient(
				particleEffect,
				pos.x, pos.y, pos.z,
				0.0, 0.0, 0.0
			);
		}
	}

	/**
	 * Adds particles to the projectile. It uses the provided position
	 * as the position of where the particle will appear. If the
	 * {@link #getTrailParticleType() particle type} is {@code null},
	 * this method will not add any particles.
	 *
	 * @param pos The position of the particle.
	 */
	protected void addParticles(Vec3d pos) {
		if (this.getTrailParticleType() != null) {
			this.addParticles(pos, this.getTrailParticleType());
		}
	}

	/**
	 * Adds particles to the projectile. It uses the current position
	 * of the projectile as the position of the particle. If the
	 * {@link #getTrailParticleType() particle type} is {@code null},
	 * this method will not add any particles.
	 */
	protected void addParticles() {
		this.addParticles(this.getTrackedPosition().getPos());
	}

	@Override
	public void tick() {
		Entity owner = this.getOwner();
		Vec3d pos = this.getEntityPos();

		this.applyDrag();

		int[] xz = new int[] {
			this.getChunkPos().x,
			this.getChunkPos().z
		};

		if (this.getEntityWorld().isClient()
			|| (owner == null || !owner.isRemoved())
			&& this.getEntityWorld().getChunkManager().isChunkLoaded(xz[0], xz[1])
		) {
			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
			Vec3d vec3d = pos;
			if (hitResult.getType() != HitResult.Type.MISS) {
				vec3d = hitResult.getPos();
			} else {
				vec3d = vec3d.add(this.getVelocity());
			}

			ProjectileUtil.setRotationFromVelocity(this, 0.2F);
			this.setPosition(vec3d);
			this.tickBlockCollision();
			this.applyGravity();
			super.tick();
			if (this.isBurning()) {
				this.setOnFireFor(1.0F);
			}

			if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
				this.hitOrDeflect(hitResult);
			}

			// Trail Particle
			this.addParticles(pos.add(0, 0.25, 0));
		}
		else {
//			System.out.println("Projectile is removed.");
			this.discard();
		}
	}

	@Override
	public void writeCustomData(WriteView view) {
		super.writeCustomData(view);

		view.putDouble("acceleration_power", this.accelerationPower);
	}

	@Override
	public void readCustomData(ReadView view) {
		super.readCustomData(view);

		this.accelerationPower = view.getDouble("acceleration_power", 0d);
	}

	// //////////////////////////// //
	// GETTERS, SETTERS, AND ASKERS //
	// //////////////////////////// //
	protected RaycastContext.ShapeType getRaycastShapeType() {
		return RaycastContext.ShapeType.COLLIDER;
	}

	/**
	 * Defines the trailing particle this projectile will use. By default, this method returns {@code null},
	 * which means this projectile won't leave a trailing particle in its wake.
	 *
	 * @return The particle effect to use for the projectile's trail, or {@code null} if no trail should be rendered.
	 */
	@Nullable
	protected ParticleEffect getTrailParticleType() {
		return null;
	}

	/**
	 * Defines the particle effect to use for the small explosion when the projectile hits a blockor
	 * an entity. By default, this method returns {@link ParticleTypes#EXPLOSION}, which is also the
	 * default explosion particle used by vanilla explosions.
	 *
	 * @return The particle effect to use for the small explosion. This method should not return {@code null} as the small explosion particle is essential for the explosion effect.
	 */
	@NotNull
	protected ParticleEffect getSmallExplosionParticleType() {
		return ParticleTypes.EXPLOSION;
	}

	/**
	 * Defines the particle effect to use for the large explosion when the projectile hits a block or
	 * an entity. By default, this method returns {@link ParticleTypes#EXPLOSION_EMITTER}, which is
	 * also the default large explosion particle used by vanilla explosions.
	 *
	 * @return The particle effect to use for the large explosion. This method should not return {@code null} as the large explosion particle is essential for the explosion effect.
	 */
	@NotNull
	protected ParticleEffect getLargeExplosionParticleType() {
		return ParticleTypes.EXPLOSION_EMITTER;
	}

	@Override
	protected float getDrag() {
		return 0.95F;
	}

	@Override
	protected float getDragInWater() {
		return 0.8F;
	}

	protected boolean isBurning() {
		return true;
	}

	@Override
	protected boolean canHit(Entity entity) {
		return super.canHit(entity) && !entity.noClip;
	}

	/**
	 * Defines the maximum effective radius of the explosion. Within this
	 * radius, all entities will receive the full {@link #getDamage() base damage}.
	 * Entities outside this radius will receive reduced damage based on the
	 * {@link #getDamageReduction() damage reduction} multiplier.
	 *
	 * @return The maximum effective radius of the explosion.
	 */
	public abstract double getEffectiveRadius();

	/**
	 * Defines the maximum radius this explosion can reach. Entities within this
	 * radius will receive damage, but the damage will be reduced based on the
	 * distance from the {@link #getEffectiveRadius() effective radius} of the
	 * explosion.
	 *
	 * @return The maximum radius of the explosion.
	 */
	public abstract double getMaxDamageRadius();

	/**
	 * Defines the damage reduction multiplier for entities within the
	 * {@link #getEffectiveRadius() effective radius} and {@link #getMaxDamageRadius() max radius}
	 * of the explosion.
	 * <hr>
	 *
	 * <h2>Damage Reduction Formula</h2>
	 * <p>The reduced damage is calculated as:</p>
	 * <pre>{@code baseDmg * ((e^(-dmgReduction * ((radius - effectiveRadius) / (maxRadius - effectiveRadius))) - e^(-dmgReduction)) / (1 - e^(-dmgReduction)))}</pre>
	 *
	 * <p>OR</p>
	 * <pre><code>
	 * normalizedRadius = (radius - effectiveRadius) / (maxDmgRadius - effectiveRadius)
	 * formula = (e^(-dmgReduction * normalizedRadius) - e^(-dmgReduction)) / (1 - e^(-dmgReduction))
	 * dmg = baseDmg * formula
	 * </code></pre>
	 *
	 * <p>Wherein:</p>
	 * <pre><code>
	 * effectiveRadius <= radius <= maxRadius
	 * 0.0 <= dmgReduction <= 1.0
	 * </code></pre>
	 *
	 * <p>
	 * 	This means that if the <b>base damage is 10</b>, the <b>reduction multiplier is 0.25</b>,
	 * 	and the <b>max radius is 5</b>, and a <b>min (effective) radius of 2.5</b>, and with a <b>radius
	 * 	of 3</b>, the <b>reduced damage will be 7.79517413414</b> since the <b>reduction is 2.5</b>.
	 * 	Plugging the values will result to this:
	 * </p>
	 * <pre><code>
	 * float normalizedRadius = (3 - 2.5) / (5 - 2.5);	// 0.2
	 * float formula = (Math.exp(-0.25 * normalizedRadius) - Math.exp(-0.25)) / (1 - Math.exp(-0.25));	// 0.779517413414
	 * float dmg = 10 * formula;	// 7.79517413414
	 * </code></pre>
	 * <p>Assuming the code looks like this:</p>
	 * <pre><code>
	 * float normalizedRadius = (radius - effectiveRadius) / (maxDmgRadius - effectiveRadius);
	 * float formula = (Math.exp(-dmgReduction * normalizedRadius) - Math.exp(-dmgReduction)) / (1 - Math.exp(-dmgReduction));
	 * float dmg = baseDmg * formula;
	 * </code></pre>
	 * <p>
	 * 	This {@code 7.79517413414} is the damage that will be dealt for entities at the {@code 3}
	 * 	block distance from the point origin of the explosion.
	 * </p>
	 * <hr>
	 * <p>
	 *     See reduced damage per iterations on my Desmos graphing calculator at
	 *     <a href="https://www.desmos.com/calculator/pdm27kw9oe">this link</a>. This
	 *     could also serve as a calculator for the damage reduction multiplier, allowing
	 *     anyone who's working on this mod to adjust the values as needed for balancing.
	 * </p>
	 *
	 * @return The damage reduction multiplier for entities outside the effective radius.
	 *
	 * @see <a href="https://www.desmos.com/calculator/pdm27kw9oe">Formulas</a>
	 */
	public abstract double getDamageReduction();

	protected void setVelocityWithAcceleration(Vec3d velocity, double accelerationPower) {
		this.setVelocity(velocity.normalize().multiply(accelerationPower));
		this.velocityDirty = true;
	}

	// ////////// //
	// NETWORKING //
	// ////////// //

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
		Entity entity = this.getOwner();
		int i = entity == null ? 0 : entity.getId();
		Vec3d vec3d = entityTrackerEntry.getPos();

		return new EntitySpawnS2CPacket(
			this.getId(),
			this.getUuid(),
			vec3d.getX(),
			vec3d.getY(),
			vec3d.getZ(),
			entityTrackerEntry.getPitch(),
			entityTrackerEntry.getYaw(),
			this.getType(),
			i,
			entityTrackerEntry.getVelocity(),
			0.0
		);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		Vec3d vel = packet.getVelocity();

		Vec3d vec3d = new Vec3d(vel.getX(), vel.getY(), vel.getZ());
		this.setVelocity(vec3d);
	}

	// //////////////////////// //
	// INTERFACE IMPLEMENTATION //
	// //////////////////////// //

	// PUBLIC
	@Override
	public boolean armorAffectsPiercing() {
		return true;
	}

	@Override
	public boolean armorAffectsVelocity() {
		return false;
	}

	@Override
	public boolean armorAffectsDamage() {
		return false;
	}

	// ///////////////////// //
	// STATIC INITIALIZATION //
	// ///////////////////// //
	static {
		EXPLOSION_BLOCK_PARTICLES = Pool.<BlockParticleEffect>builder()
			.add(new BlockParticleEffect(ParticleTypes.POOF, 0.5F, 1.0F))
			.add(new BlockParticleEffect(ParticleTypes.SMOKE, 1.0F, 1.0F))
			.build();

		EMPTY_EXPLOSION_BLOCK_PARTICLES = Pool.<BlockParticleEffect>builder()
			.build();
	}
}
