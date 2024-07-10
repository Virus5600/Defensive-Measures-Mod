package com.virus5600.defensive_measures.entities.turrets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.virus5600.defensive_measures.DefensiveMeasures;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.virus5600.defensive_measures.entities.ai.goal.TargetOtherTeamGoal;
import com.virus5600.defensive_measures.entities.TurretMaterial;

public class CannonTurretEntity extends TurretEntity implements RangedAttackMob, Itemable {
	private static final int totalAttCooldown = 20 * 5;
	private static final TrackedData<Boolean> FUSE_LIT;
	/**
	 * Contains all the items that can heal this entity.
	 */
	private static Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity
	 */
	private static Map<Item, List<Object[]>> effectSource;
	/**
	 * Defines the current target of this Cannon.
	 */
	@Nullable
	private LivingEntity currentTarget = null;
	private double attCooldown = totalAttCooldown;

	//////////////////
	// CONSTRUCTORS //
	//////////////////
	public CannonTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world, TurretMaterial.METAL, ArrowEntity.class);
//		this.setShootSound(ModSoundEvents.TURRET_CANNON_SHOOT);
		this.setShootSound(SoundEvents.ENTITY_SHULKER_SHOOT);
		this.addHealables(healables);
		this.addEffectSource(effectSource);
	}

	//////////////////
	// INITIALIZERS //
	//////////////////
	@Override
	protected void initGoals() {
		// Goals
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 0, totalAttCooldown, 16.625F));
		this.goalSelector.add(2, new LookAtEntityGoal(this, MobEntity.class, 8.0F, 0.02F, true));
		this.goalSelector.add(8, new LookAroundGoal(this));

		// Targets
		this.targetSelector.add(1, new ActiveTargetGoal<MobEntity>(this, MobEntity.class, 10, true, false, (entity) -> {
			return entity instanceof Monster;
		}));
		this.targetSelector.add(3, new TargetOtherTeamGoal(this));
	}

	@Override
	protected void initDataTracker(Builder builder) {
		super.initDataTracker(builder);

		builder.add(FUSE_LIT, false);
	}

	public static DefaultAttributeContainer.Builder setAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
	}

	/////////////////////
	// PROCESS METHODS //
	/////////////////////

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		if (target == null) {
			if (this.isShooting())
				this.setShooting(false);
			return;
		}

		this.setShooting(true);

		try {
			double vx = (target.getX() - this.getX()) * 1.0625;
			double vy = target.getBodyY((double) 2 / 3) - this.getY() + 0.25;
			double vz = (target.getZ() - this.getZ()) * 1.0625;
			double variance = Math.sqrt(vx * vx + vz * vz);
			float divergence = this.getWorld().getDifficulty().getId() * 2;
//			ProjectileEntity projectile = (ProjectileEntity) new CannonballEntity(ModEntities.CANNONBALL, this, vx, vy, vz, this.getWorld());
			ProjectileEntity projectile = (ProjectileEntity) new ArrowEntity(EntityType.ARROW, this.getWorld());

			projectile.setVelocity(vx, vy + variance * 0.1f, vz, 1.5f, divergence);
			projectile.setPos(this.getX(), this.getY() + 0.5, this.getZ());

			this.playSound(this.getShootSound(), 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
			this.getWorld().spawnEntity(projectile);
		} catch (IllegalArgumentException | SecurityException e) {
			e.printStackTrace();

			DefensiveMeasures.LOGGER.error("");
			DefensiveMeasures.LOGGER.error("	 " + DefensiveMeasures.MOD_ID.toUpperCase() + " ERROR OCCURRED	 ");
			DefensiveMeasures.LOGGER.error("===== ERROR MSG START =====");
			DefensiveMeasures.LOGGER.error("LOCALIZED ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.error(e.getLocalizedMessage());
			DefensiveMeasures.LOGGER.error("");
			DefensiveMeasures.LOGGER.error("ERROR MESSAGE:");
			DefensiveMeasures.LOGGER.error(e.getMessage());
			DefensiveMeasures.LOGGER.error("===== ERROR MSG END =====");
			DefensiveMeasures.LOGGER.error("");
		}
	}

	//////////////////////////////////////
	// QUESTION METHODS (True or False) //
	//////////////////////////////////////

	/////////////////////////
	// GETTERS AND SETTERS //
	/////////////////////////

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_IRON_GOLEM_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
	}

	protected boolean isFuseLit() {
		return this.dataTracker.get(FUSE_LIT);
	}
	protected void setFuseLit(boolean lit) {
		this.dataTracker.set(FUSE_LIT, lit);
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(Items.IRON_BLOCK);
	}

	///////////////////////////////
	// INTERFACE IMPLEMENTATIONS //
	///////////////////////////////

	///////////////////
	// LOCAL CLASSES //
	///////////////////

	///////////////////////
	// STATIC INITIALIZE //
	///////////////////////

	static {
		FUSE_LIT = DataTracker.registerData(CannonTurretEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

		healables = new HashMap<Item, Float>() {
			{
				for (Item item : TurretEntity.PLANKS)
					put(item, 1.0f);
				put(Items.IRON_NUGGET, 1f);
				put(Items.IRON_INGOT, 10f);
				put(Items.IRON_BLOCK, 100f);
			}
		};

		effectSource = new HashMap<Item, List<Object[]>>() {
			{
				put(Items.IRON_BLOCK, new ArrayList<Object[]>() {
					{
						add(new Object[] {StatusEffects.ABSORPTION, 60, 2});
					}
				});
			}
		};
	}
}
