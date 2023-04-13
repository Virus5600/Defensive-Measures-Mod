package com.virus5600.DefensiveMeasures.item.turrets.cannon;

import com.virus5600.DefensiveMeasures.item.turrets.TurretItem;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Rarity;

public class CannonTurretItem extends TurretItem {
	public CannonTurretItem(final EntityType<? extends MobEntity> type, final Settings settings) {
		super(
			type,
			// MAX STACK SIZE
			settings.maxCount(16)
			// RARITY
			.rarity(Rarity.RARE)
		);
	}
}
