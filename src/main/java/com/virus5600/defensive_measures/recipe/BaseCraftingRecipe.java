package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.jspecify.annotations.NonNull;

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
 * @param <T> the type of {@link RecipeInput} this recipe takes in, which is usually {@link CraftingInput}
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see Recipe
 * @see ShapedRecipe
 */
public abstract class BaseCraftingRecipe<T extends CraftingInput> implements BaseCraftingRecipeInterface<T> {
	final CustomShapedRecipe recipe;
	final ItemStack result;
	final String group;
	final ModCraftingRecipeCategory category;
	final boolean showNotification;
	private @Nullable PlacementInfo ingredientPlacement;

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

	@NonNull
	public String group() {
		return this.group;
	}

	public ModCraftingRecipeCategory getCategory() {
		return this.category;
	}

	@VisibleForTesting @NonNull
	public PlacementInfo placementInfo() {
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = PlacementInfo.createFromOptionals(this.recipe.getIngredients());
		}

		return this.ingredientPlacement;
	}

	public boolean showNotification() {
		return this.showNotification;
	}

	public boolean matches(@NonNull CraftingInput craftingRecipeInput, @NonNull Level world) {
		return this.recipe.matches(craftingRecipeInput);
	}

	@NonNull
	public ItemStack assemble(@NonNull CraftingInput craftingRecipeInput, @NonNull Provider wrapperLookup) {
		return this.result.copy();
	}

	public int getWidth() {
		return this.recipe.getWidth();
	}

	public int getHeight() {
		return this.recipe.getHeight();
	}

	@NonNull
	public List<RecipeDisplay> display() {
		return List.of(new ShapedCraftingRecipeDisplay(
			this.recipe.getWidth(),
			this.recipe.getHeight(),
			this.recipe.getIngredients()
				.stream()
				.map((ingredient) ->
					ingredient.map(Ingredient::display)
						.orElse(SlotDisplay.Empty.INSTANCE))
				.toList(),
			new SlotDisplay.ItemStackSlotDisplay(this.result),
			new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
		));
	}

	// ////////////////////// //
	// CODECS AND SERIALIZERS //
	// ////////////////////// //

	public static <T extends BaseCraftingRecipe<?>> RecipeSerializer<T> createSerializer(
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> packetCodec
	) {
		return new RecipeSerializer<>() {
			@Override @NonNull
			public MapCodec<T> codec() {
				return codec;
			}

			@Override @NonNull
			public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
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
			Codec.STRING.optionalFieldOf("group", "").forGetter(BaseCraftingRecipe::group),
			categoryCodec.fieldOf("category").orElse(defaultCategory).forGetter(categoryGetter),
			CustomShapedRecipe.createCodec(rows, cols).forGetter(recipe -> recipe.recipe),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
			Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(BaseCraftingRecipe::showNotification)
		).apply(instance, factory::create));
	}
}
