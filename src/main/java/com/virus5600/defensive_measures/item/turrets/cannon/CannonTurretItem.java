package com.virus5600.defensive_measures.item.turrets.cannon;

import com.virus5600.defensive_measures.item.turrets.TurretItem;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.util.List;

public class CannonTurretItem extends TurretItem {
	public CannonTurretItem(EntityType<? extends MobEntity> type, Settings settings) {
		super(
			type,
			settings
				.maxCount(16)			// MAX STACK SIZE
				.rarity(Rarity.RARE)	// RARITY
		);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		for (int i = 1; i <= 3; i++) {
			tooltip.add(
				Text.translatable("itemTooltip.dm.cannon_turret.line" + i)
					.formatted(Formatting.DARK_AQUA)
			);
		}
	}
}
