package com.virus5600.DefensiveMeasures.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

@SuppressWarnings("deprecation")
public enum ModToolMaterials implements ToolMaterial {
	TURRET_REMOVER(MiningLevels.HAND, 100, 1.0F, 0.0F, 15, new HashMap<Item, Integer>() {
		private static final long serialVersionUID = 711919662739124134L;
		{
			put(Items.GOLD_NUGGET, 2);
			put(Items.IRON_NUGGET, 2);
			put(Items.GOLD_INGOT, 20);
			put(Items.IRON_INGOT, 20);
			put(Items.REDSTONE, 20);
			put(Items.REDSTONE_BLOCK, 100);
		}
	});

	private final int miningLevel;
	private final int itemDurability;
	private final float miningSpeed;
	private final float attackDamage;
	private final int enchantability;
	private final Lazy<Ingredient> repairIngredient;
	private final Map<Item, Integer> repairAmount;

	private ModToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Map<Item, Integer> repairIngredient) {
		this.miningLevel = miningLevel;
		this.itemDurability = itemDurability;
		this.miningSpeed = miningSpeed;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairIngredient = repairIngredients(repairIngredient.keySet());
		this.repairAmount = repairIngredient;
	}

	public int getDurability() {
		return this.itemDurability;
	}

	public float getMiningSpeedMultiplier() {
		return this.miningSpeed;
	}

	public float getAttackDamage() {
		return this.attackDamage;
	}

	public int getMiningLevel() {
		return this.miningLevel;
	}

	public int getEnchantability() {
		return this.enchantability;
	}

	public Ingredient getRepairIngredient() {
		return (Ingredient)this.repairIngredient.get();
	}

	public int getRepairAmt(Item item) {
		return this.repairAmount.get(item);
	}

	private static Lazy<Ingredient> repairIngredients(Set<Item> ingredients) {
		Supplier<Ingredient> supplier = () -> {
			return Ingredient.ofItems(ingredients.toArray(Item[]::new));
		};
		return new Lazy<Ingredient>(supplier);
	}
}
