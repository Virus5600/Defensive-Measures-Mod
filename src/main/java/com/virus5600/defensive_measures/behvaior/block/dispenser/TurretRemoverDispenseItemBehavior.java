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

import java.util.List;

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
