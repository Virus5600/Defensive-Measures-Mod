package com.virus5600.defensive_measures.mixins.crafting;

import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Allows the addition of new {@link net.minecraft.world.level.block.CraftingTableBlock Crafting Table}
 * categories to the crafting book.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Mixin(CraftingBookCategory.class)
public enum CraftingBookCategoryMixin {
	DM_TURRETS("turrets", 4)
	;

	@Shadow
	CraftingBookCategoryMixin(String name, int id) {
	}
}
