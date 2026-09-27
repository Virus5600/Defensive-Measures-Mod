package com.virus5600.defensive_measures.entity.projectiles;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.AttributeValues;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.EntityBlockAttributeIndex;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager.ExplosiveValues;
import com.virus5600.defensive_measures.configs.BlockProjectileConfigManager;
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesBlockProjectile;

/**
 * Represents a generalized block projectile entity that can be used by turrets that shoots blocks.
 * <br><br>
 * This class extends the {@link ExplosiveProjectileEntity} class as the block projectile can be
 * configured to explode, allowing for a wide range of block projectiles to be used by the turret.
 * TNT are such blocks that when used as a projectile that could be configured via the
 * {@link BlockProjectileConfigManager config} file to explode on impact.
 * <br><br>
 * The block projectile can be configured through config files, which then get loaded into the game
 * via {@link BlockProjectileConfigManager} so turrets can use the configured block projectile
 * properties. The configuration allows for the block projectile to have different properties based
 * on the block being used and the turret that fired it. This allows for a wide range of block
 * projectiles to be used by different turrets, each with their own unique properties.
 * <br><br>
 * A block projectile can be configured to:
 * <ul>
 * 	<li><b>{@link AttributeValues#drag Drag}:</b> The multiplier which determines how much of its speed gets retained over time.</li>
 * 	<li><b>{@link AttributeValues#gravity Gravity}:</b> The amount of downward velocity added to the block projectile, adding up over time.</li>
 * 	<li><b>{@link AttributeValues#damage Damage}:</b> The damage dealt when the block projectile directly hits an entity.</li>
 * 	<li><b>{@link AttributeValues#piercing Piercing}:</b> The amount of piercing value this block projectile has. Determines how many entity it could penetrate before being stopped. Does not work when the projectile explodes.</li>
 * 	<li><b>{@link AttributeValues#fire Fire}:</b> Whether the projectile can set its target on fire or not.</li>
 * 	<li><b>{@link AttributeValues#explosive Explosive}:</b> Its explosive attribute. See next list below for details.</li>
 * </ul>
 * <br><br>
 * The explosive attribute can be configured to:
 * <ul>
 * 	<li><b>{@link ExplosiveValues#damage Damage}:</b> The damage dealt by the explosion. This is a separate value from the kinetic damage dealt by this projectile</li>
 * 	<li><b>{@link ExplosiveValues#effectiveRadius Effective Radius}:</b> The radius within which the explosion deals full damage.</li>
 * 	<li><b>{@link ExplosiveValues#maxRadius Max Radius}:</b> The maximum radius of the explosion.</li>
 * </ul>
 *
 * @see ExplosiveProjectileEntity
 * @see BlockProjectileConfigManager
 * @see UsesBlockProjectile
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BlockProjectile extends ExplosiveProjectileEntity {
	protected Identifier blockId;
	protected EntityBlockAttributeIndex attrIndex;
	protected AttributeValues attrValues;

	public BlockProjectile(EntityType<? extends BlockProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public BlockProjectile(EntityType<? extends BlockProjectile> entityType, Level level, UsesBlockProjectile owner) {
		this(entityType, level);

		this.setOwner(owner.getTurretEntity());
	}

	public BlockProjectile(EntityType<? extends BlockProjectile> entityType, Level level, UsesBlockProjectile owner, Item block) {
		this(entityType, level, owner);

		this.setBlockId(block);
	}

	// /////// //
	// METHODS //
	// /////// //

	public boolean isExplosive() throws IllegalStateException {
		if (this.attrValues == null) {
			this.setAttributes();
		}

		ExplosiveValues ev = this.attrValues.explosive();

		return ev != null && ev.enabled();
	}

	/**
	 * Sets the block ID of this block projectile based on the provided {@link Item}. If the
	 * provided item is not an instance of {@link BlockItem}, an {@link IllegalArgumentException}
	 * will be thrown.
	 *
	 * @param block the {@link Item} representing the block to be used as the projectile.
	 *
	 * @return this instance of {@link BlockProjectile} for method chaining.
	 *
	 * @throws IllegalArgumentException if the provided item is not an instance of {@link BlockItem}.
	 */
	public BlockProjectile setBlockId(Item block) throws IllegalArgumentException {
		if (!(block instanceof BlockItem)) {
			throw new IllegalArgumentException("The provided item is not a BlockItem.");
		}

		this.blockId = RegistryHelper.getIdentifier(block);

		return this;
	}

	/**
	 * Sets the attributes of this block projectile based on what block is being used and from which
	 * turret it was fired from. If the {@link #blockId} is {@code null}, it will default to
	 * {@link Items#STONE}. If the owner of the projectile is {@code null}, an
	 * {@link IllegalStateException} will be thrown. Furthermore, if the owner of the projectile is
	 * does not implement the {@link UsesBlockProjectile} interface, it will once more throw
	 * an {@link IllegalStateException}.
	 *
	 * @return this instance of {@link BlockProjectile} for method chaining.
	 *
	 * @throws IllegalStateException if the owner of the projectile is {@code null} or does not implement the {@link UsesBlockProjectile} interface.
	 */
	public BlockProjectile setAttributes() throws IllegalStateException {
		if (this.blockId == null) {
			this.setBlockId(Items.STONE);
		}

		if (this.getOwner() == null) {
			throw new IllegalStateException("Owner of the projectile is null. Cannot set attributes.");
		}
		else if (!(this.getOwner() instanceof UsesBlockProjectile)) {
			throw new IllegalStateException("Owner of the projectile does not implement UsesBlockProjectile. Cannot set attributes.");
		}

		this.attrIndex = BlockProjectileConfigManager.getConfigForEntity(this.getOwner().getType());
		this.attrValues = this.attrIndex.getBlockAttributes(this.blockId.toString());

		return this;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	// ExplosiveProjectileEntity //

	@Override
	public double getEffectiveRadius() {
		double radius = this.attrIndex
			.getBlockAttributes(this.blockId.toString())
			.explosive()
			.effectiveRadius();

		return Math.max(radius, 0);
	}

	@Override
	public double getMaxDamageRadius() {
		double radius = this.attrIndex
			.getBlockAttributes(this.blockId.toString())
			.explosive()
			.maxRadius();

		return Math.max(radius, 0);
	}

	@Override
	public double getDamageReduction() {
		return 0.25;
	}

	// ExplosiveEntity //

	@Override
	public boolean canDestroyBlocks() {
		return false;
	}

	// TurretProjectileEntity //

	@Override
	public byte getMaxPierceLevel() {
		double pierce = this.attrIndex
			.getBlockAttributes(this.blockId.toString())
			.piercing();

		return (byte) Math.max(pierce, 0);
	}
}
