package com.virus5600.defensive_measures.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

import com.virus5600.defensive_measures.registry.tag.ModItemTags;

/**
 * An enum representing the materials that a modded tool can be made of.
 * <br><br>
 * This enum is used to determine the material of a modded tool, which can affect the durability,
 * mining speed, attack damage bonus, enchantability, and the repair ingredients.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public final class ModToolMaterials {
	public static final ToolMaterial TURRET_REMOVER;

	public static ToolMaterial createMaterial(TagKey<Block> incorrectBlockForDrops, int durability, float speed, float attackDmgBonus, int enchantmentValue, TagKey<Item> repairItems) {
		return new ToolMaterial(
			incorrectBlockForDrops,
			durability,
			speed,
			attackDmgBonus,
			enchantmentValue,
			repairItems
		);
	}

	static {
		TURRET_REMOVER = ModToolMaterials.createMaterial(
			BlockTags.INCORRECT_FOR_WOODEN_TOOL,
			100, 1.0f, 0.0f,  15,
			ModItemTags.TURRET_REMOVER_REPAIRABLE
		);
	}
}
