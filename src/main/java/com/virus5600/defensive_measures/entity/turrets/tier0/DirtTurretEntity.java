package com.virus5600.defensive_measures.entity.turrets.tier0;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.AttributeValues;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.EntityBlockAttributeIndex;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.ExplosiveValues;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.ResolvedBlockGroup;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager;
import com.virus5600.defensive_measures.entity.ModEntities;
import com.virus5600.defensive_measures.entity.TurretMaterial;
import com.virus5600.defensive_measures.entity.ai.goal.ProjectileAttackGoal;
import com.virus5600.defensive_measures.entity.projectiles.ExplosiveProjectileEntity;
import com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesBlockProjectile;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.sound.ModSoundEvents;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Represents the Dirt Turret entity
 * <br><br>
 * A joke turret suggested by one of the players who played the mod. While it might be a joke
 * turret, it is still a turret that can be used to defend against enemies. It has a very low
 * health and deals very low damage, but it can be used to distract enemies and buy time for other
 * turrets to deal damage. It can also be used to block enemy projectiles and protect other turrets.
 * <br><br>
 * Additionally, while it has low damage, the dirt it "shoots" gives a Blindness effect for a short
 * while to anyone hit by the dirt. This can be used to blind enemies and make them easier to kill.
 * However, the Blindness effect is not very long, so it is not very effective against enemies with
 * high health or armor.
 * <hr/>
 * <b>Attributes:</b>
 * <ul>
 *     <li><b>Health:</b> 10</li>
 *     <li><b>Base Damage:</b> 1.0</li>
 *     <li><b>Base Pierce Level:</b> 0</li>
 *     <li><b>Attack Cooldown:</b> 2.5 seconds</li>
 *     <li><b>Attack Range:</b> 8 blocks</li>
 *     <li><b>X Firing Arc:</b> ±360°</li>
 *     <li><b>Y Firing Arc:</b> ±7°</li>
 *     <li><b>Armor:</b> 2</li>
 *     <li><b>Armor Toughness:</b> 2</li>
 * </ul>
 * <hr/>
 * This entity implements the {@link UsesBlockProjectile} interface, which means it can use blocks
 * as projectiles. In this case, it uses dirt blocks as projectiles by default through the
 * config file under {@code data/dm/configs/block-projectiles.json@entities/dm:dirt_turret}. While
 * that is the default, it can be changed to use any other blocks by following the same format
 * dictated by the documentation under {@link BlockProjectileConfigManager}.
 *
 * @see TurretEntity
 * @see UsesBlockProjectile
 * @see BlockProjectileConfigManager
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class DirtTurretEntity extends TurretEntity implements UsesBlockProjectile {
	private static final EntityDataAccessor<ItemStack> CURRENT_AMMO;

	/**
	 * Defines how many seconds the ballista should wait before shooting again.
	 * The time is calculated in ticks and by default, it's 2.5 seconds <b>(20 ticks times 2.5 seconds)</b>.
	 */
	private static final int TOTAL_ATT_COOLDOWN = (int) (20 * 2.5);
	private static final Map<DirtTurretEntity.Offsets, List<Vec3>> OFFSETS;
	private static final Map<Item, SoundEvent> HEAL_SOUNDS;
	private static final double[] DAMAGE;
	private static final byte[] PIERCE_LEVELS;
	/**
	 * Contains all the items that can heal this entity.
	 */
	protected static final Map<Item, Float> healables;
	/**
	 * Contains all the items that can give effect to this entity
	 */
	protected static final Map<Item, List<Object[]>> effectSource;

	private Item currentAmmo;
	private AttributeValues currentAmmoAttr;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //
	public DirtTurretEntity(EntityType<? extends Mob> entityType, Level world) {
		super(entityType, world, TurretMaterial.DIRT, ModEntities.FLINT_PELLET, ModItems.DIRT_TURRET);

		this.addHealables(healables)
			.addEffectSource(effectSource)
			.setShootSound(ModSoundEvents.TURRET_DIRT_SHOOT)
		;

		if (this.currentAmmo == null) {
			this.currentAmmo = Items.DIRT;
		}

		this.updateCurrentAmmo(this.currentAmmo);
	}

	// //////////// //
	// INITIALIZERS //
	// //////////// //
	@Override
	protected void defineSynchedData(@NonNull Builder builder) {
		super.defineSynchedData(builder);

		if (this.currentAmmo == null) {
			this.currentAmmo = Items.DIRT;
		}

		builder.define(CURRENT_AMMO, this.currentAmmo.getDefaultInstance());
	}

	@Override
	protected void registerGoals() {
		// Goal instances
		this.attackGoal = new ProjectileAttackGoal(this, 0, TOTAL_ATT_COOLDOWN, this.getMaxAttackRange(), this.getMinAttackRange());

		// Set the standard goals
		super.registerGoals();
	}

	public static AttributeSupplier.Builder setAttributes() {
		TurretEntity.setTurretMaxHealth(10);
		TurretEntity.setTurretMaxRange(8 + ModEntities.BALLISTA_TURRET.getDimensions().eyeHeight());

		return TurretEntity.setAttributes();
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	protected <P extends Projectile> void onProjectileCreateCallback(P projectile) {
		if (projectile instanceof TurretProjectileEntity tpe) {
			// Set Drag
			// Set Gravity

			if (this.currentAmmoAttr.fire()) {
				tpe.setRemainingFireTicks(Integer.MAX_VALUE);
			}

			ExplosiveValues ev = this.currentAmmoAttr.explosive();
			if (ev.enabled() && tpe instanceof ExplosiveProjectileEntity epe) {

				epe.setDamage(ev.damage());
				// Set effectiveRadius
			}
		}
	}

	@Override
	public void performRangedAttack(@NonNull LivingEntity target, float pullProgress) {
		TurretProjectileVelocity velocityData = this.getProjectileVelocityData(target);

		super.shootAt(velocityData);
	}

	@Override
	public @NonNull InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
		ItemStack usedItemStack = player.getItemInHand(hand);
		Item usedItem = usedItemStack.getItem();

		if (this.isHealableItem(usedItem)) {
			this.setHealSound(HEAL_SOUNDS.get(usedItem));
		}

		if (this.isValidAmmo(usedItem)) {
			if (this.updateCurrentAmmo(usedItem)) {
				usedItemStack.setCount(usedItemStack.getCount() - 1);

				return InteractionResult.SUCCESS;
			}
		}

		return super.mobInteract(player, hand);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			this.updateAnimations();
		}
	}

	// /////////////////// //
	// GETTERS AND SETTERS //
	// /////////////////// //

	@Override
	public int getMaxHeadXRot() {
		return 7;
	}

	@Override @Nullable
	protected SoundEvent getHurtSound(@NonNull DamageSource source) {
		return ModSoundEvents.TURRET_DIRT_HURT;
	}

	@Override @Nullable
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.TURRET_DIRT_DESTROYED;
	}

	@Override
	public ItemStack getEntityItem() {
		return new ItemStack(ModItems.DIRT_TURRET);
	}

	@Override
	public SoundEvent getEntityRemoveSound() {
		return ModSoundEvents.TURRET_REMOVED_DIRT;
	}

	// //////////////////////// //
	// ABSTRACT IMPLEMENTATIONS //
	// //////////////////////// //

	// TurretEntity //

	/**
	 * {@inheritDoc}
	 * @see {@code DirtTurretAnimation#ANIM_DIRT_TURRET_DEATH}
	 */
	protected int getDeathAnimDuration() {
		return (int) (1.0F * 20);
	}

	protected List<Vec3> getTurretProjectileSpawn() {
		return OFFSETS.get(Offsets.BARREL);
	}

	public TurretProjectileVelocity getProjectileVelocityData(LivingEntity target) {
		float dist = (float) this.position()
			.distanceTo(target.position());

		return TurretProjectileVelocity
			.init(this)
			.setLaunchAngle(dist * 0.125f)
			.setVelocity(target);
	}

	public double getProjectileDamage() {
		return DAMAGE[this.getTrackedLevel() - 1] + this.currentAmmoAttr.damage();
	}

	public byte getProjectilePierceLevel() {
		return (byte) (PIERCE_LEVELS[this.getTrackedLevel() - 1] + this.currentAmmoAttr.piercing());
	}

	public int getTotalAttCooldown() {
		return TOTAL_ATT_COOLDOWN;
	}

	// //////////// //
	// OVERRIDABLES //
	// //////////// //

	@Override
	protected int getSetupAnimDuration() {
		return (int) (1.75F * 20);
	}

	@Override
	protected int getTeardownAnimDuration() {
		return (int) (1.75F * 20);
	}

	// ///////////////////////// //
	// INTERFACE IMPLEMENTATIONS //
	// ///////////////////////// //

	// UsesBlockProjectile //

	@Override
	public Item getCurrentAmmo() {
		return this.currentAmmo;
	}

	@Override
	public boolean updateCurrentAmmo(Item item) {
		if (!this.isValidAmmo(item)) {
			return false;
		}

		EntityBlockAttributeIndex attrIndex = BlockProjectileConfigManager.getConfigForEntity(this.getType());
		this.currentAmmo = item;
		this.currentAmmoAttr = attrIndex.entityDefault();

		for (ResolvedBlockGroup blockGroup : attrIndex.blockGroups()) {
			if (blockGroup.blockRef().equals(RegistryHelper.getIdentifier(item).toString())) {
				this.currentAmmoAttr = blockGroup.attributes();
				break;
			}
		}

		return true;
	}

	@Override
	public TurretEntity getTurretEntity() {
		return this;
	}

	// /////////////////////////////// //
	// CUSTOM DATA READING AND WRITING //
	// /////////////////////////////// //

	@Override
	public void readAdditionalSaveData(@NonNull ValueInput view) {
		view.read("current_ammo", ItemStack.CODEC)
			.ifPresent(stack -> this.updateCurrentAmmo(stack.getItem()));
	}

	@Override
	public void addAdditionalSaveData(@NonNull ValueOutput view) {
		ItemStack currentAmmoStack = new ItemStack(this.currentAmmo);
		currentAmmoStack.setCount(1);

		view.store("current_ammo", ItemStack.CODEC, currentAmmoStack);
	}

	// /////////////////// //
	// LOCAL CLASSES/ENUMS //
	// /////////////////// //
	public enum Offsets {
		BARREL
	}

	// ///////////////// //
	// STATIC INITIALIZE //
	// ///////////////// //

	static {
		CURRENT_AMMO = SynchedEntityData.defineId(DirtTurretEntity.class, EntityDataSerializers.ITEM_STACK);

		DAMAGE = new double[] {
			1,
			2.5,
			5
		};

		PIERCE_LEVELS = new byte[] {
			0,
			0,
			1
		};

		OFFSETS = Map.of(
			Offsets.BARREL, List.of(
				new Vec3(0, 0, 0.5)
			)
		);

		// HEAL SOUNDS
		HEAL_SOUNDS = Map.of(
			Items.GRASS_BLOCK, ModSoundEvents.TURRET_REPAIR_DIRT,
			Items.DIRT, ModSoundEvents.TURRET_REPAIR_DIRT
		);

		healables = Map.of(
			Items.GRASS_BLOCK, 2.5f,
			Items.DIRT, 5.0f
		);

		effectSource = Map.of(
			Items.DIRT, List.<Object[]>of(
				new Object[] {MobEffects.ABSORPTION, 30, 2}
			)
		);
	}
}
