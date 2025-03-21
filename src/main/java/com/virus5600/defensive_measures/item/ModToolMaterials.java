package com.virus5600.defensive_measures.item;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * An enum representing the materials that a modded tool can be made of.
 * <br><br>
 * This enum is used to determine the material of a modded tool, which can affect the durability,
 * mining speed, attack damage bonus, enchantability, and the repair ingredients.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
 public enum ModToolMaterials {
	TURRET_REMOVER(100, 1.0F, 0.0F, null, 15, "turret_remover_repairable");;

	private final int itemDurability;
	private final float miningSpeed;
	private final float attackDamageBonus;
	private final TagKey<Block> incorrectBlocksForDrops;
	private final int enchantability;
	private final String repairIngredientsItemTag;
	private final ToolMaterial toolMaterialInstance;

	ModToolMaterials(int itemDurability, float miningSpeed, float attackDamageBonus, TagKey<Block> incorrectBlocksForDrops, int enchantability, String repairIngredientsItemTag) {
		this.itemDurability = itemDurability;
		this.miningSpeed = miningSpeed;
		this.attackDamageBonus = attackDamageBonus;
		this.incorrectBlocksForDrops = incorrectBlocksForDrops;
		this.enchantability = enchantability;
		this.repairIngredientsItemTag = repairIngredientsItemTag;

		this.toolMaterialInstance = new ToolMaterial(
			this.incorrectBlocksForDrops,
			this.itemDurability,
			this.miningSpeed,
			this.attackDamageBonus,
			this.enchantability,
			TagKey.of(RegistryKeys.ITEM, Identifier.of(repairIngredientsItemTag))
		);
	}

	public int getDurability() {
		return this.itemDurability;
	}

	public float getMiningSpeedMultiplier() {
		return this.miningSpeed;
	}

	public float getAttackDamageBonus() {
		return this.attackDamageBonus;
	}

	public TagKey<Block> getIncorrectBlocksForDrops() {
		return this.incorrectBlocksForDrops;
	}

	public int getEnchantability() {
		return this.enchantability;
	}

	public String getRepairIngredientsItemTag() {
		return this.repairIngredientsItemTag;
	}

	public ToolMaterial getToolMaterialInstance() {
		return this.toolMaterialInstance;
	}
}
