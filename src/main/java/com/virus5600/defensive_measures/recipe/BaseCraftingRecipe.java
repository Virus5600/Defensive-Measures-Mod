package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.recipe.book.ModCraftingRecipeCategory;

import org.jetbrains.annotations.Nullable;
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
	protected final CommonInfo commonInfo;
	protected final ModCraftingBookInfo bookInfo;
	private @Nullable PlacementInfo placementInfo;

	final CustomShapedRecipePattern recipe;
	final ItemStackTemplate result;
	private @Nullable PlacementInfo ingredientPlacement;

	public BaseCraftingRecipe(
		CommonInfo commonInfo, ModCraftingBookInfo bookInfo,
		CustomShapedRecipePattern recipe, ItemStackTemplate result
	) {
		this.commonInfo = commonInfo;
		this.bookInfo = bookInfo;

		this.recipe = recipe;
		this.result = result;
	}

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
		if (this.ingredientPlacement == null) {
			this.ingredientPlacement = PlacementInfo.createFromOptionals(this.recipe.ingredients());
		}

		return this.ingredientPlacement;
	}

	public boolean matches(@NonNull CraftingInput craftingRecipeInput, @NonNull Level world) {
		return this.recipe.matches(craftingRecipeInput);
	}

	@NonNull
	public ItemStack assemble(@NonNull CraftingInput craftingRecipeInput, @NonNull Provider wrapperLookup) {
		return this.result.create();
	}

	public int getWidth() {
		return this.recipe.width();
	}

	public int getHeight() {
		return this.recipe.height();
	}

	@NonNull
	public List<RecipeDisplay> display() {
		return List.of(new ShapedCraftingRecipeDisplay(
			this.recipe.width(),
			this.recipe.height(),
			this.recipe.ingredients()
				.stream()
				.map((ingredient) ->
					ingredient.map(Ingredient::display)
						.orElse(SlotDisplay.Empty.INSTANCE))
				.toList(),
			new SlotDisplay.ItemStackSlotDisplay(this.result),
			new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
		));
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
	> MapCodec<T> createMapCodec(
		int rows, int cols,
		MapCodec<D> bookInfoCodec,
		RecipeFactory<T, C, D> factory
	) {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(
				CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
				bookInfoCodec.forGetter(o -> (D) o.bookInfo),
				CustomShapedRecipePattern.createCodec(rows, cols).forGetter(o -> o.pattern()),
				ItemStackTemplate.CODEC.fieldOf("result").forGetter((o) -> o.result)
			).apply(instance, factory::create)
		);
	}
}
