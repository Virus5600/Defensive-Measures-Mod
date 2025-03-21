package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import net.minecraft.world.explosion.ExplosionBehavior;
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
 *
 * @see #doDamage()
 *
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @since 1.0.0
 */
public abstract class ExplosiveProjectileEntity extends TurretProjectileEntity {
	protected ParticleEffect particleTrail;
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

	protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, World world) {
		this(type, world);
		this.setPosition(x, y, z);
	}

	public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, Vec3d velocity, World world) {
		this(type, world);
		this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
		this.refreshPosition();
		this.setVelocityWithAcceleration(velocity, this.accelerationPower);
	}

	public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, Vec3d velocity, World world) {
		this(type, owner.getX(), owner.getY(), owner.getZ(), velocity, world);
		this.setOwner(owner);
		this.setRotation(owner.getYaw(), owner.getPitch());
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
	protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
		super.onDeflected(deflector, fromAttack);

		if (fromAttack) {
			this.accelerationPower = 0.1;
		} else {
			this.accelerationPower *= 0.5;
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult hitResult) {
		super.onBlockHit(hitResult);

		if (!this.getWorld().isClient()) {
			this.doDamage();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult hitResult) {
		super.onEntityHit(hitResult);

		if (!this.getWorld().isClient()) {
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
		this.getWorld()
			.createExplosion(
				this,
				dmgSrc,
				new ExplosionBehavior(),
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				1.25F,
				false,
				World.ExplosionSourceType.NONE
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

		List<LivingEntity> damagedEntities = new ArrayList<>();
		this.getWorld()
			.getOtherEntities(this, fullDmgReceiver)
			.forEach(entity -> {
				if (entity instanceof LivingEntity) {
					entity.damage(
						(ServerWorld) this.getWorld(),
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

			this.getWorld()
				.getOtherEntities(this, partialDmgReceiver)
				.forEach(entity -> {
					if (entity instanceof LivingEntity && !damagedEntities.contains(entity)) {
						float normalizedRadius = (float) ((radius - effectiveRadius) / (maxDmgRadius - effectiveRadius));
						float formula = (float) (Math.exp(-dmgReduction * normalizedRadius) - Math.exp(-dmgReduction) / (1 - Math.exp(-dmgReduction)));
						float dmg = (float) (baseDmg * formula);

						entity.damage(
							(ServerWorld) this.getWorld(),
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
			this.getWorld().addParticle(
				particleEffect,
				pos.x, pos.y, pos.z,
				0.0, 0.0, 0.0
			);
		}
	}

	/**
	 * Adds particles to the projectile. It uses the provided position
	 * as the position of where the particle will appear. If the
	 * {@link #getParticleType() particle type} is {@code null},
	 * this method will not add any particles.
	 *
	 * @param pos The position of the particle.
	 */
	protected void addParticles(Vec3d pos) {
		this.addParticles(pos, this.getParticleType());
	}

	/**
	 * Adds particles to the projectile. It uses the current position
	 * of the projectile as the position of the particle. If the
	 * {@link #getParticleType() particle type} is {@code null},
	 * this method will not add any particles.
	 */
	protected void addParticles() {
		this.addParticles(this.getPos());
	}

	@Override
	public void tick() {
		Entity owner = this.getOwner();

		this.applyDrag();

		int[] xz = new int[] {
			this.getChunkPos().x,
			this.getChunkPos().z
		};

		if (this.getWorld().isClient
			|| (owner == null || !owner.isRemoved())
			&& this.getWorld().getChunkManager().isChunkLoaded(xz[0], xz[1])
		) {
			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
			Vec3d vec3d;
			if (hitResult.getType() != HitResult.Type.MISS) {
				vec3d = hitResult.getPos();
			} else {
				vec3d = this.getPos().add(this.getVelocity());
			}

			ProjectileUtil.setRotationFromVelocity(this, 0.2F);
			this.setPosition(vec3d);
			this.tickBlockCollision();
			super.tick();
			if (this.isBurning()) {
				this.setOnFireFor(1.0F);
			}

			if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
				this.hitOrDeflect(hitResult);
			}

			this.addParticles(this.getPos().add(0, 0.5, 0));
		} else {
			System.out.println("Projectile is removed.");
			this.discard();
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putDouble("acceleration_power", this.accelerationPower);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("acceleration_power", NbtElement.DOUBLE_TYPE)) {
			this.accelerationPower = nbt.getDouble("acceleration_power");
		}
	}

	// //////////////////////////// //
	// GETTERS, SETTERS, AND ASKERS //
	// //////////////////////////// //
	protected RaycastContext.ShapeType getRaycastShapeType() {
		return RaycastContext.ShapeType.COLLIDER;
	}

	@Nullable
	protected ParticleEffect getParticleType() {
		return this.particleTrail;
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
		Vec3d vec3d = new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
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
}
