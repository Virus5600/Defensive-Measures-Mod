package com.virus5600.defensive_measures.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

public enum ModToolMaterials implements ToolMaterial {
	TURRET_REMOVER(100, 1.0F, 0.0F, null, 15, new Item[] {
		Items.GOLD_NUGGET,
		Items.IRON_NUGGET,
		Items.REDSTONE
	});

	private final int itemDurability;
	private final float miningSpeed;
	private final float attackDamage;
	private final TagKey<Block> inverseTags;
	private final int enchantability;
	private final Item[] repairIngredients;

	private ModToolMaterials(int itemDurability, float miningSpeed, float attackDamage, TagKey<Block> inverseTags, int enchantability, Item[] repairIngredients) {
		this.itemDurability = itemDurability;
		this.miningSpeed = miningSpeed;
		this.attackDamage = attackDamage;
		this.inverseTags = inverseTags;
		this.enchantability = enchantability;
		this.repairIngredients = repairIngredients;
	}

	@Override
	public int getDurability() {
		return this.itemDurability;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return this.miningSpeed;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public TagKey<Block> getInverseTag() {
		return this.inverseTags;
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(this.repairIngredients);
	}
}
