package com.virus5600.defensive_measures.mixins.crafting;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;

/**
 * A custom mixin to let the {@link net.minecraft.world.level.block.CraftingTableBlock Crafting Table}
 * handle the new DM Turrets category.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Mixin(CraftingRecipe.class)
public interface CraftingRecipeMixin {
	@Inject(method = "recipeBookCategory", at = @At("HEAD"), cancellable = true)
	default void injectCustomRecipeBookCategory(CallbackInfoReturnable<RecipeBookCategory> cir) {
		CraftingRecipe self = (CraftingRecipe) this;

		switch (self.category().name()) {
			case "DM_TURRETS" -> cir.setReturnValue(ModRecipeBookCategories.DM_TURRETS);
		}
	}
}
