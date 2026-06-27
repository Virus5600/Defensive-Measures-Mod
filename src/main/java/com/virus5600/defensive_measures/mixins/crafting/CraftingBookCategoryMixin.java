package com.virus5600.defensive_measures.mixins.crafting;

import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingBookCategory.class)
public enum CraftingBookCategoryMixin {
	DM_TURRETS("turrets", 4)
	;

	@Shadow
	CraftingBookCategoryMixin(String name, int id) {
	}
}
