package com.virus5600.defensive_measures.mixins.screens.recipebook;

import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;

import java.util.ArrayList;
import java.util.List;

/**
 * Mixin to modify the included categories in the SearchRecipeBookCategory enum.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Mixin(SearchRecipeBookCategory.class)
public class SearchRecipeBookCategoryMixin {
	@Shadow @Final @Mutable
	private List<RecipeBookCategory> includedCategories;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void dm$injectCustomCategories(String enumName, int ordinal, RecipeBookCategory[] categories, CallbackInfo ci) {
		if (enumName.equals("CRAFTING")) {
			List<RecipeBookCategory> expandedList = new ArrayList<>(this.includedCategories);
			expandedList.add(ModRecipeBookCategories.DM_TURRETS);
			this.includedCategories = List.copyOf(expandedList);
		}
	}
}
