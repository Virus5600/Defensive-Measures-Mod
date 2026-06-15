package com.virus5600.defensive_measures.item.turrets.mg_turret;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.tier1.MGTurretEntity;
import com.virus5600.defensive_measures.item.turrets.TurretItem;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * The item that spawns the {@link MGTurretEntity MG Turret}.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MGTurretItem extends TurretItem {
	public MGTurretItem(EntityType<? extends Mob> type, Properties settings) {
		super(
			type,
			settings
				.stacksTo(16)
				.rarity(Rarity.UNCOMMON)
		);
	}

	@Override
	public void appendHoverText(ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, @NonNull Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
		super.appendHoverText(stack, context, displayComponent, textConsumer, type);

		for (int i = 1; i <= 3; i++) {
			textConsumer.accept(
				Component.translatable("itemTooltip.dm.mg_turret.line" + i)
					.withStyle(ChatFormatting.GRAY)
			);
		}
	}

	public float getTurretMaxHealth() {
		float maxHealth = 0;
		Type superClass = this.type.getClass().getGenericSuperclass();
		if (superClass instanceof TurretEntity) {
			maxHealth = ((MGTurretEntity) superClass).getMaxHealth();
		}

		return maxHealth;
	}
}
