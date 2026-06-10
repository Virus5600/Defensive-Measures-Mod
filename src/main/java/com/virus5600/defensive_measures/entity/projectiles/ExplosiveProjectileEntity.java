package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures.entity.ExplosiveEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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
 * vanilla minecraft explosion deals. The main differences was that the vanilla {@link net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile ExplosiveProjectileEntity}
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
public abstract class ExplosiveProjectileEntity extends TurretProjectileEntity implements ExplosiveEntity {
	protected static final WeightedList<ExplosionParticleInfo> EXPLOSION_BLOCK_PARTICLES;
	protected static final WeightedList<ExplosionParticleInfo> EMPTY_EXPLOSION_BLOCK_PARTICLES;

	public double accelerationPower = 0.1;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	protected ExplosiveProjectileEntity(EntityType<? extends TurretProjectileEntity> entityType, Level world) {
		super(entityType, world);

		this.setDamage(5);
		this.setPierceLevel(this.getMaxPierceLevel());
		this.setSpeedAffectsDamage(false);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void defineSynchedData(Builder builder) {
		super.defineSynchedData(builder);
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	protected void onDeflection(boolean fromAttack) {
		super.onDeflection(fromAttack);

		if (fromAttack) {
			this.accelerationPower = 0.1;
		} else {
			this.accelerationPower *= 0.5;
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult hitResult) {
		super.onHitBlock(hitResult);

		if (!this.level().isClientSide()) {
			this.doDamage();
		}
	}

	@Override
	protected void onHitEntity(@NonNull EntityHitResult hitResult) {
		super.onHitEntity(hitResult);

		if (!this.level().isClientSide()) {
			this.doDamage();
		}
	}

	/**
	 * Applies damage to the surrounding entity.
	 * <br><br>
	 * The damage is applied in two stages:
	 * <ol>
	 *     <li>Entities within the {@link #getEffectiveRadius() effective radius} will receive the full {@link #getBaseDamage() base damage}.</li>
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
		if (this.level().isClientSide()) {
			return;
		}

		// Create the damage source
		DamageSource dmgSrc = this.damageSources().source(
			this.getDamageType(),
			this,
			this.getOwner() == null ? this : this.getOwner()
		);

		// Create the explosion
		this.createExplosion(
			this,
			dmgSrc,
			new ExplosionDamageCalculator(),
			this.getX(),
			this.getY(0.0625),
			this.getZ(),
			1.25F,
			false,
			this.getExplosionSourceType(),
			this.getSmallExplosionParticleType(),
			this.getLargeExplosionParticleType(),
			EMPTY_EXPLOSION_BLOCK_PARTICLES,
			this.getExplosionSoundEvent(),
			this.canDestroyBlocks()
		);

		this.discard();
	}

	@Override
	public boolean hurtServer(@NonNull ServerLevel world, @NonNull DamageSource source, float amount) {
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
	protected void addParticles(Vec3 pos, ParticleOptions particleEffect) {
		if (particleEffect != null) {
			this.level().addParticle(
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
	protected void addParticles(Vec3 pos) {
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
		this.addParticles(this.getPositionCodec().getBase());
	}

	protected void move() {
		this.applyDrag();

		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, this.getRaycastShapeType());

		Vec3 pos = this.position();
		if (hitResult.getType() != HitResult.Type.MISS) {
			pos = hitResult.getLocation();
		} else {
			pos = pos.add(this.getDeltaMovement());
		}

		ProjectileUtil.rotateTowardsMovement(this, 0.2F);
		this.setPos(pos);
		this.applyEffectsFromBlocks();
		this.applyGravity();
	}

	@Override
	public void tick() {
		Entity owner = this.getOwner();
		Vec3 pos = this.position();

		int[] xz = new int[] {
			this.chunkPosition().x,
			this.chunkPosition().z
		};

		if ((owner == null || !owner.isRemoved())
			&& this.level().getChunkSource().hasChunk(xz[0], xz[1])
		) {
			this.move();
			super.tick();

			if (this.level().isClientSide()) {
				// Trail Particle
				this.addParticles(pos.add(0, 0.25, 0));
			}
			else {
				HitResult hitResult;

				if (this.getDeltaMovement().lengthSqr() > 1.0E-7) {
					hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, this.getRaycastShapeType());
				}
				else {
					hitResult = this.getZeroVelocityCollision();
				}

				if (this.isBurning()) {
					this.igniteForSeconds(1.0F);
				}

				if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
					this.hitTargetOrDeflectSelf(hitResult);
				}
			}
		}
		else {
			this.discard();
		}
	}

	@Override
	public void addAdditionalSaveData(@NonNull ValueOutput view) {
		super.addAdditionalSaveData(view);

		view.putDouble("acceleration_power", this.accelerationPower);
	}

	@Override
	public void readAdditionalSaveData(@NonNull ValueInput view) {
		super.readAdditionalSaveData(view);

		this.accelerationPower = view.getDoubleOr("acceleration_power", 0d);
	}

	// //////////////////////////// //
	// GETTERS, SETTERS, AND ASKERS //
	// //////////////////////////// //
	protected ClipContext.Block getRaycastShapeType() {
		return ClipContext.Block.COLLIDER;
	}

	/**
	 * Defines the trailing particle this projectile will use. By default, this method returns {@code null},
	 * which means this projectile won't leave a trailing particle in its wake.
	 *
	 * @return The particle effect to use for the projectile's trail, or {@code null} if no trail should be rendered.
	 */
	@Nullable
	protected ParticleOptions getTrailParticleType() {
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
	protected ParticleOptions getSmallExplosionParticleType() {
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
	protected ParticleOptions getLargeExplosionParticleType() {
		return ParticleTypes.EXPLOSION_EMITTER;
	}

	/**
	 * Defines the sound event to play when this projectile explodes.
	 *
	 * @return {@link SoundEvents#GENERIC_EXPLODE}
	 */
	@NotNull
	protected Holder<SoundEvent> getExplosionSoundEvent() {
		return SoundEvents.GENERIC_EXPLODE;
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
	protected boolean canHitEntity(@NonNull Entity entity) {
		return super.canHitEntity(entity) && !entity.noPhysics;
	}

	@Override
	public ResourceKey<DamageType> getDamageType() {
		return DamageTypes.PLAYER_EXPLOSION;
	}

	/**
	 * Defines the maximum effective radius of the explosion. Within this
	 * radius, all entities will receive the full {@link #getDamage() base damage}.
	 * Entities outside this radius will receive reduced damage based on the
	 * {@link ExplosiveEntity#getDamageReduction() damage reduction} multiplier.
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

	@Override
	public double getBaseDamage() {
		return this.getDamage();
	}

	protected void setVelocityWithAcceleration(Vec3 velocity, double accelerationPower) {
		this.setDeltaMovement(velocity.normalize().scale(accelerationPower));
		this.needsSync = true;
	}

	// ////////// //
	// NETWORKING //
	// ////////// //

	@Override
	public @NonNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entityTrackerEntry) {
		Entity entity = this.getOwner();
		int i = entity == null ? 0 : entity.getId();
		Vec3 vec3d = entityTrackerEntry.getPositionBase();

		return new ClientboundAddEntityPacket(
			this.getId(),
			this.getUUID(),
			vec3d.x(),
			vec3d.y(),
			vec3d.z(),
			entityTrackerEntry.getLastSentXRot(),
			entityTrackerEntry.getLastSentYRot(),
			this.getType(),
			i,
			entityTrackerEntry.getLastSentMovement(),
			0.0
		);
	}

	@Override
	public void recreateFromPacket(@NonNull ClientboundAddEntityPacket packet) {
		super.recreateFromPacket(packet);
		Vec3 vel = packet.getMovement();

		Vec3 vec3d = new Vec3(vel.x(), vel.y(), vel.z());
		this.setDeltaMovement(vec3d);
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
		EXPLOSION_BLOCK_PARTICLES = WeightedList.<ExplosionParticleInfo>builder()
			.add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
			.add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
			.build();

		EMPTY_EXPLOSION_BLOCK_PARTICLES = WeightedList.<ExplosionParticleInfo>builder()
			.build();
	}
}
