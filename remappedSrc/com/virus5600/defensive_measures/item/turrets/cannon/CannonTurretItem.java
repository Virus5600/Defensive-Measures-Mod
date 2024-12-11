package com.virus5600.defensive_measures.item.turrets.cannon;

import com.virus5600.defensive_measures.item.turrets.TurretItem;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Rarity;

public class CannonTurretItem extends TurretItem {
	public CannonTurretItem(EntityType<? extends MobEntity> type, net.minecraft.item.Item.Settings settings) {
		super(
			type,
			settings
				.maxCount(16)			// MAX STACK SIZE
				.rarity(Rarity.RARE)	// RARITY
		);
	}
}
