package com.virus5600.defensive_measures.item.turrets.tier_0;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;
import com.virus5600.defensive_measures.entity.turrets.tier0.PelletTurretEntity;
import com.virus5600.defensive_measures.item.turrets.TurretItem;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * The item that spawns the {@link PelletTurretEntity}
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class PelletTurretItem extends TurretItem {
	/**
	 * The {@link CookingFuel cooking fuel} for the Pellet Turret {@link Item item}.
	 * Serves as a fuel source for 30 seconds (30 seconds * 20 ticks per second) and has a default speed multiplier for cooking.
	 */
	public static final CookingFuel COOKING_FUEL = new CookingFuel(
		new ResolvableInt.Constant(30 * 20),
		ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)
	);

	public PelletTurretItem(EntityType<? extends Mob> type, Properties settings) {
		super(
			type,
			settings
				.stacksTo(16)		// MAX STACK SIZE
				.rarity(Rarity.COMMON)	// RARITY
				.component(DataComponents.COOKING_FUEL, COOKING_FUEL)
		);
	}

	@Override
	public void appendHoverText(ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, @NonNull Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
		super.appendHoverText(stack, context, displayComponent, textConsumer, type);

		for (int i = 1; i <= 3; i++) {
			textConsumer.accept(
				Component.translatable("itemTooltip.dm.pellet_turret.line" + i)
					.withStyle(ChatFormatting.GRAY)
			);
		}
	}

	public float getTurretMaxHealth() {
		float maxHealth = 0;

		Type superClass = this.type.getClass().getGenericSuperclass();
		if (superClass instanceof TurretEntity) {
			maxHealth = ((PelletTurretEntity) superClass).getMaxHealth();
		}

		return maxHealth;
	}
}
