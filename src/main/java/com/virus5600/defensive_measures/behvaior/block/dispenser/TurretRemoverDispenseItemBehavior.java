package com.virus5600.defensive_measures.behvaior.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.interfaces.Itemable;
import com.virus5600.defensive_measures.item.equipments.TurretRemoverItem;

import java.util.List;

/**
 * A custom dispenser item behavior for the {@link TurretRemoverItem Turret Remover}, allowing it
 * to remove a {@link TurretEntity Turret Entity} when the dispenser uses said item.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class TurretRemoverDispenseItemBehavior extends OptionalDispenseItemBehavior {
	public TurretRemoverDispenseItemBehavior() {}

	public ItemStack execute(final BlockSource source, final ItemStack dispensed) {
		ServerLevel level = source.level();

		if (!level.isClientSide()) {
			BlockPos pos = source.pos()
				.relative(source.state()
					.getValue(DispenserBlock.FACING));

			this.setSuccess(tryRemoveTurret(level, pos));

			if (this.isSuccess()) {
				dispensed.hurtAndBreak(1, level, null, _ -> {});
			}
		}

		return dispensed;
	}

	protected boolean tryRemoveTurret(
		final ServerLevel level,
		final BlockPos pos
	) {
		List<TurretEntity> turrets = level.getEntitiesOfClass(
			TurretEntity.class,
			new AABB(pos),
			EntitySelector.NO_SPECTATORS
		);

		for (TurretEntity turret : turrets) {
			if (
				turret instanceof Itemable itemable
				&& turret.isAlive()
				&& !turret.isTearingDown()
			) {
				ItemEntity item = Itemable.tryItem(turret, itemable.getEntityItem().getItem(), level);
				turret.startTeardownAnim(true, item);

				return true;
			}
		}

		return false;
	}
}
