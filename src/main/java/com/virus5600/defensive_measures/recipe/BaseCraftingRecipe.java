package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

import com.virus5600.defensive_measures.recipe.annotations.Shaped;
import com.virus5600.defensive_measures.recipe.annotations.Shapeless;
import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
 * @see ShapelessRecipe
 */
public abstract class BaseCraftingRecipe<T extends CraftingInput> implements BaseCraftingRecipeInterface<T> {
	protected final CommonInfo commonInfo;
	protected final ModCraftingBookInfo bookInfo;
	protected final CustomShapedRecipePattern pattern;
	protected final List<Ingredient> ingredients;
	protected final ItemStackTemplate result;
	private @Nullable PlacementInfo placementInfo;
	private final boolean isShaped;

	public BaseCraftingRecipe(
		CommonInfo commonInfo, ModCraftingBookInfo bookInfo,
		CustomShapedRecipePattern pattern, ItemStackTemplate result
	) {
		this.commonInfo = commonInfo;
		this.bookInfo = bookInfo;

		this.pattern = pattern;
		this.ingredients = null;
		this.result = result;

		this.isShaped = true;

		if (this.getClass().isAnnotationPresent(Shapeless.class)) {
			throw new IllegalArgumentException("An annotated @Shapeless recipe must use a list of ingredients, not a CustomShapedRecipePattern.");
		}
	}

	public BaseCraftingRecipe(
		CommonInfo commonInfo, ModCraftingBookInfo bookInfo,
		List<Ingredient> ingredients, ItemStackTemplate result
	) {
		this.commonInfo = commonInfo;
		this.bookInfo = bookInfo;

		this.pattern = null;
		this.ingredients = ingredients;
		this.result = result;

		this.isShaped = false;

		if (this.getClass().isAnnotationPresent(Shaped.class)) {
			throw new IllegalArgumentException("An annotated @Shaped recipe must use a CustomShapedRecipePattern, not a list of ingredients.");
		}
	}

	// /////// //
	// METHODS //
	// /////// //

	@NonNull
	public String group() {
		return this.bookInfo.group();
	}

	public ModCraftingRecipeCategory category() {
		return this.bookInfo.category();
	}

	public boolean showNotification() {
		return this.commonInfo.showNotification();
	}

	@NonNull
	public PlacementInfo placementInfo() {
		if (this.placementInfo == null) {
			this.placementInfo = this.createPlacementInfo();
		}

		return this.placementInfo;
	}

	public int getWidth() {
		if (!this.isShaped) {
			throw new IllegalStateException("Cannot access a shaped recipe method on a shapeless recipe");
		}
		Objects.requireNonNull(this.pattern, "Pattern cannot be null");

		return this.pattern.width();
	}

	public int getHeight() {
		if (!this.isShaped) {
			throw new IllegalStateException("Cannot access a shaped recipe method on a shapeless recipe");
		}
		Objects.requireNonNull(this.pattern, "Pattern cannot be null");

		return this.pattern.height();
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	/**
	 * Gets the recipe pattern for this crafting recipe. This method must be implemented by
	 * subclasses to provide the specific pattern of ingredients for the recipe, which is used for
	 * matching the recipe input and displaying the recipe in the crafting book. The pattern is
	 * typically a grid of ingredients that corresponds to the crafting grid in the game, and it
	 * defines how the ingredients are arranged to create the resulting item. The pattern is
	 * essential for the crafting system to determine if the player's input matches the recipe and
	 * to display the recipe correctly in the crafting book and recipe displays.
	 *
	 * @return the {@link CustomShapedRecipePattern} that defines the arrangement of ingredients for this recipe
	 */
	protected abstract CustomShapedRecipePattern pattern();

	public abstract List<Optional<Ingredient>> getIngredients();

	protected abstract PlacementInfo createPlacementInfo();

	// ////////////////////// //
	// CODECS AND SERIALIZERS //
	// ////////////////////// //

	public static <T extends BaseCraftingRecipe<?>> RecipeSerializer<T> createSerializer(
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> packetCodec
	) {
		return new RecipeSerializer<>(codec, packetCodec);
	}

	public static <
		T extends BaseCraftingRecipe<?>,
		C extends StringRepresentable,
		D extends BookInfo<C>
	> MapCodec<T> createShapedMapCodec(
		int rows, int cols,
		MapCodec<D> bookInfoCodec,
		ShapedRecipeFactory<T, C, D> factory
	) {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(
				CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
				bookInfoCodec.forGetter(recipe -> (D) recipe.bookInfo),
				CustomShapedRecipePattern.createCodec(rows, cols).forGetter(recipe -> recipe.pattern()),
				ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> recipe.result)
			).apply(instance, factory::create)
		);
	}

	public static <
		T extends BaseCraftingRecipe<?>,
		C extends StringRepresentable,
		D extends BookInfo<C>
	> MapCodec<T> createShapelessMapCodec(
		int rows, int cols,
		MapCodec<D> bookInfoCodec,
		ShapelessRecipeFactory<T, C, D> factory
	) {
		return RecordCodecBuilder.mapCodec(shapeless -> shapeless.group(
			CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
			bookInfoCodec.forGetter(recipe -> (D) recipe.bookInfo),
			Ingredient.CODEC
				.listOf(1, rows * cols)
				.fieldOf("ingredients")
				.forGetter(recipe -> recipe.ingredients),
			ItemStackTemplate.CODEC
				.fieldOf("result")
				.forGetter(recipe -> recipe.result)
		).apply(shapeless, factory::create));
	}
}
