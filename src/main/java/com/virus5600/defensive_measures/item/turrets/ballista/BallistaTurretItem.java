package com.virus5600.defensive_measures.item.turrets.ballista;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import com.virus5600.defensive_measures.entity.turrets.BallistaTurretEntity;
import com.virus5600.defensive_measures.util.base.superclasses.entity.TurretEntity;
import com.virus5600.defensive_measures.util.base.superclasses.item.TurretItem;

import java.lang.reflect.Type;
import java.util.List;

/**
 * The item that spawns the {@link BallistaTurretEntity Ballista Turret}.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class BallistaTurretItem extends TurretItem {
	public BallistaTurretItem(EntityType<? extends MobEntity> type, Settings settings) {
		super(
			type,
			settings
				.maxCount(16)			// MAX STACK SIZE
				.rarity(Rarity.RARE)	// RARITY
		);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);

		for (int i = 1; i <= 3; i++) {
			tooltip.add(
				Text.translatable("itemTooltip.dm.ballista.line" + i)
					.formatted(Formatting.DARK_AQUA)
			);
		}
	}

	public float getTurretMaxHealth() {
		float maxHealth = 0;

		Type superClass = this.type.getClass().getGenericSuperclass();
		if (superClass instanceof TurretEntity) {
			maxHealth = ((BallistaTurretEntity) superClass).getMaxHealth();
		}

		return maxHealth;
	}
}
