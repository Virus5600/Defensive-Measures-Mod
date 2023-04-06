package com.virus5600.DefensiveMeasures.item.turrets.ballista;

import com.virus5600.DefensiveMeasures.item.turrets.TurretItem;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Rarity;

public class BallistaTurretItem extends TurretItem {
	public BallistaTurretItem(EntityType<? extends MobEntity> type, Settings settings) {
		super(
			type,
			// MAX STACK SIZE
			settings.maxCount(16)
			// RARITY
			.rarity(Rarity.RARE)
		);
	}
}
