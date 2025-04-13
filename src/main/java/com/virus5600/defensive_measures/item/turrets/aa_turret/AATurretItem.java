package com.virus5600.defensive_measures.item.turrets.aa_turret;

import com.virus5600.defensive_measures.entity.turrets.AATurretEntity;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.item.turrets.TurretItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.lang.reflect.Type;
import java.util.List;

/**
 * The item that spawns the {@link AATurretEntity AA Turret}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class AATurretItem extends TurretItem {
	public AATurretItem(EntityType<? extends MobEntity> type, Settings settings) {
		super(
			type,
			settings
				.maxCount(16)			// MAX STACK SIZE
				.rarity(Rarity.RARE)	// RARITY
		);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);

		for (int i = 1; i <= 3; i++) {
			tooltip.add(
				Text.translatable("itemTooltip.dm.aa_turret.line" + i)
					.formatted(Formatting.DARK_AQUA)
			);
		}
	}

	public float getTurretMaxHealth() {
		float maxHealth = 0;
		Type superClass = this.type.getClass().getGenericSuperclass();
		if (superClass instanceof TurretEntity) {
			maxHealth = ((AATurretEntity) superClass).getMaxHealth();
		}

		return maxHealth;
	}
}
