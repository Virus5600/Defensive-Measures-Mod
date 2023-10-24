package com.virus5600.DefensiveMeasures.item.turrets.machine_gun;

import com.virus5600.DefensiveMeasures.item.turrets.TurretItem;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Rarity;

public class MachineGunTurretItem extends TurretItem {
	public MachineGunTurretItem(EntityType<? extends MobEntity> type, Settings settings) {
		super(
			type,
			// MAX STACK SIZE
			settings.maxCount(16)
			// RARITY
			.rarity(Rarity.RARE)
		);
	}
}
