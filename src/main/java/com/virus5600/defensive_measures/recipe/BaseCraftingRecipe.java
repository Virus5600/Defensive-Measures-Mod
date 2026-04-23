package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.recipe.input.RecipeInput;

import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;

import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;

/**
 * BaseCraftingRecipe is an abstract class that serves as the base for all crafting recipes in the
 * mod. It implements the {@link Recipe} interface and provides common logic and properties for all
 * crafting recipes, such as the recipe's group, category, result, and ingredient placement. This
 * class is designed to be extended by specific crafting recipe classes, such as {@link TASShapedRecipe},
 * which will implement the specific logic for matching and crafting the recipe based on the input.<br>
 * <br>
 * By centralizing the common logic in this base class, it allows for easier maintenance and
 * consistency across all crafting recipes in the mod.
 *
 * @param <T> the type of {@link RecipeInput} this recipe takes in, which is usually {@link CraftingRecipeInput}
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see Recipe
 * @see ShapedRecipe
 */
public abstract class BaseCraftingRecipe<T extends CraftingRecipeInput> implements BaseCraftingRecipeInterface<T> {
	final CustomShapedRecipe recipe;
	final ItemStack result;
	final String group;
	final ModCraftingRecipeCategory category;
	final boolean showNotification;
	private @Nullable IngredientPlacement ingredientPlacement;

	public BaseCraftingRecipe(String group, ModCraftingRecipeCategory category, CustomShapedRecipe recipe, ItemStack result, boolean showNotification) {
		this.group = group;
		this.category = category;
		this.recipe = recipe;
		this.result = result;
		this.showNotification = showNotification;
	}

	public BaseCraftingRecipe(String group, ModCraftingRecipeCategory category, CustomShapedRecipe recipe, ItemStack result) {
		this(group, category, recipe, result, true);
	}

	public String getGroup() {
		return this.group;
	}

	public ModCraftingRecipeCategory getCategory() {
		return this.category;
	}

	@VisibleForTesting
	public  IngredientPlacement getIngredientPlacement() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = IngredientPlacement.forMultipleSlots(this.recipe.getIngredients());
		}

		return this.ingredientPlacement;
	}

	public boolean showNotification() {
		return this.showNotification;
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		return this.recipe.matches(craftingRecipeInput);
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		return this.result.copy();
	}

	public int getWidth() {
		return this.recipe.getWidth();
	}

	public int getHeight() {
		return this.recipe.getHeight();
	}

	public List<RecipeDisplay> getDisplays() {
		return List.of(new ShapedCraftingRecipeDisplay(
			this.recipe.getWidth(),
			this.recipe.getHeight(),
			this.recipe.getIngredients()
				.stream()
				.map((ingredient) ->
					ingredient.map(Ingredient::toDisplay)
						.orElse(SlotDisplay.EmptySlotDisplay.INSTANCE))
				.toList(),
			new SlotDisplay.StackSlotDisplay(this.result),
			new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
		));
	}

	// ////////////////////// //
	// CODECS AND SERIALIZERS //
	// ////////////////////// //

	public static <T extends BaseCraftingRecipe<?>> RecipeSerializer<T> createSerializer(
		MapCodec<T> codec,
		PacketCodec<RegistryByteBuf, T> packetCodec
	) {
		return new RecipeSerializer<>() {
			@Override
			public MapCodec<T> codec() {
				return codec;
			}

			@Override
			public PacketCodec<RegistryByteBuf, T> packetCodec() {
				return packetCodec;
			}
		};
	}

	public static <T extends BaseCraftingRecipe<?>, C> MapCodec<T> createCodec(
		int rows, int cols,
		Codec<C> categoryCodec,
		C defaultCategory,
		java.util.function.Function<T, C> categoryGetter,
		RecipeFactory<T, C> factory) {

		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("group", "").forGetter(BaseCraftingRecipe::getGroup),
			categoryCodec.fieldOf("category").orElse(defaultCategory).forGetter(categoryGetter),
			CustomShapedRecipe.createCodec(rows, cols).forGetter(recipe -> recipe.recipe),
			ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
			Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(BaseCraftingRecipe::showNotification)
		).apply(instance, factory::create));
	}
}
