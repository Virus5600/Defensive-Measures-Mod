package com.virus5600.defensive_measures.mixins.screens.recipebook;

import com.virus5600.defensive_measures.recipe.book.ModRecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.virus5600.defensive_measures.item.ModItems;

import java.util.ArrayList;
import java.util.List;

@Mixin(CraftingRecipeBookComponent.class)
public class CraftingRecipeBookComponentMixin {
	@Shadow @Final @Mutable
	private static List<RecipeBookComponent.TabInfo> TABS;

	// Injecting at TAIL of <clinit> means this runs right after Mojang's List.of(...) finishes.
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void injectTurretsTab(CallbackInfo ci) {
		List<RecipeBookComponent.TabInfo> modifiedTabs = new ArrayList<>(TABS);
		modifiedTabs.add(new RecipeBookComponent.TabInfo(ModItems.CANNON_TURRET, ModRecipeBookCategories.DM_TURRETS));

		TABS = List.copyOf(modifiedTabs);
	}
}
