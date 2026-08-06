package com.virus5600.defensive_measures.block.entity.traps;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.virus5600.defensive_measures.block.ExplosiveBlock;
import com.virus5600.defensive_measures.block.entity.ModBlockEntities;
import com.virus5600.defensive_measures.block.traps.BaseLandmineBlock;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * The base block entity that all {@link BaseLandmineBlockEntity Landmine} blocks will use, allowing
 * them to track who placed them. This only serves as the base class to allow extensions for a more
 * customized implementation when the base class is insufficient.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BaseLandmineBlockEntity extends BlockEntity implements ExplosiveBlock {
	private final BaseLandmineBlock landmine;

	@Nullable private UUID ownerId;

	public BaseLandmineBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.LAND_MINE, pos, state);

		this.landmine = (BaseLandmineBlock) state.getBlock();
	}

	// ////// //
	// METHOD //
	// ////// //

	public void setOwner(Entity owner) {
		this.ownerId = owner.getUUID();
		this.setChanged();
	}

	@Nullable
	public Entity getOwner() {
		Level level = this.getLevel();

		if (this.ownerId == null || level == null) {
			return null;
		}

		return level.getEntity(this.ownerId);
	}

	@Nullable
	public Level getLevel() {
		return this.level;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);

		if (this.ownerId != null) {
			EntityReference<Entity> owner = EntityReference.of(this.ownerId);
			EntityReference.store(owner, output, "Owner");
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);

		EntityReference<Entity> owner = EntityReference.read(input, "Owner");

		if (owner != null) {
			this.ownerId = owner.getUUID();
		}
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	// ExplosiveBlock
	@Override
	public double getDamageDealt(BlockState state, Level level) {
		return this.landmine.getDamageDealt(state, level);
	}

	// ModExplosives
	@Override
	public double getEffectiveRadius() {
		return this.landmine.getEffectiveRadius();
	}

	@Override
	public double getMaxDamageRadius() {
		return this.landmine.getMaxDamageRadius();
	}

	@Override
	public double getDamageReduction() {
		return this.landmine.getDamageReduction();
	}

	@Override
	public double getBaseDamage() {
		return this.landmine.getBaseDamage();
	}

	@Override
	public Level level() {
		return this.getLevel();
	}
}
