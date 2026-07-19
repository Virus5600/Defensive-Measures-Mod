package com.virus5600.defensive_measures.block.entity.traps;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.virus5600.defensive_measures.block.entity.ModBlockEntities;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

import org.jspecify.annotations.Nullable;

/**
 * The base block entity that all {@link BaseLandmineBlockEntity Landmine} blocks will use, allowing
 * them to track who placed them. This only serves as the base class to allow extensions for a more
 * customized implementation when the base class is insufficient.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BaseLandmineBlockEntity extends BlockEntity {
	@Nullable private UUID ownerId;

	public BaseLandmineBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.LAND_MINE, pos, state);
	}

	// ////// //
	// METHOD //
	// ////// //

	public void setOwner(Entity owner) {
		this.ownerId = owner.getUUID();
		this.setChanged();
	}

	@Nullable
	public Entity getOwner(ServerLevel level) {
		if (this.ownerId == null) {
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
}
