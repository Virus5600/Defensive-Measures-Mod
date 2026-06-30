package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import com.virus5600.defensive_measures.recipe.display.FlexibleShapedCraftingRecipeDisplay;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.block.TurretAssemblyStationBlock;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.annotations.Shaped;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * A crafting recipe specifically made for the {@link TurretAssemblyStationBlock Turret Assembly Station},
 * based on the {@link ShapedRecipe} recipe type, but expanded to 7x7.
 * <br><br>
 * This recipe allows a matrix of 7x7, which is larger than the standard crafting table's 3x3
 * matrix. This opens up a wider range of crafting possibilities, enabling more complex and
 * intricate recipes.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Shaped
public class TASShapedRecipe extends BaseCraftingRecipe<CraftingInput> {
	public static final MapCodec<TASShapedRecipe> MAP_CODEC;
	public static final StreamCodec<RegistryFriendlyByteBuf, TASShapedRecipe> STREAM_CODEC;
	public static final RecipeSerializer<TASShapedRecipe> SERIALIZER;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	public TASShapedRecipe(CommonInfo commonInfo, ModCraftingBookInfo bookInfo, CustomShapedRecipePattern recipe, ItemStackTemplate result) {
		super(commonInfo, bookInfo, recipe, result);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	public @NonNull List<RecipeDisplay> display() {
		if (this.pattern == null) {
			throw new IllegalStateException("pattern is null");
		}

		return List.of(new FlexibleShapedCraftingRecipeDisplay(
			this.pattern.width(),
			this.pattern.height(),
			this.pattern.ingredients()
				.stream()
				.map((ingredient) ->
					ingredient.map(Ingredient::display)
						.orElse(SlotDisplay.Empty.INSTANCE))
				.toList(),
			new SlotDisplay.ItemStackSlotDisplay(this.result),
			new SlotDisplay.ItemSlotDisplay(ModItems.TURRET_ASSEMBLY_STATION)
		));
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected CustomShapedRecipePattern pattern() {
		return this.pattern;
	}

	public List<Optional<Ingredient>> getIngredients() {
		if (this.pattern == null) {
			throw new IllegalStateException("pattern is null");
		}

		return this.pattern.ingredients();
	}

	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.createFromOptionals(this.getIngredients());
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override @NonNull
	public RecipeType<? extends Recipe<CraftingInput>> getType() {
		return ModRecipeTypes.TAS_RECIPE_TYPE;
	}

	@Override @NonNull
	public RecipeSerializer<? extends BaseCraftingRecipe<CraftingInput>> getSerializer() {
		return ModRecipeSerializers.TAS_SERIALIZER;
	}

	public boolean matches(final CraftingInput craftingRecipeInput, final Level level) {
		if (this.pattern == null) {
			throw new IllegalStateException("pattern is null");
		}

		return this.pattern.matches(craftingRecipeInput);
	}

	@NonNull
	public ItemStack assemble(@NonNull CraftingInput input) {
		return this.result.create();
	}

	// ////// //
	// STATIC //
	// ////// //

	static {
		MAP_CODEC = BaseCraftingRecipe.createShapedMapCodec(
			7, 7,
			ModCraftingBookInfo.MAP_CODEC,
			TASShapedRecipe::new
		);

		STREAM_CODEC = StreamCodec.composite(
			CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
			ModCraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
			CustomShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
			ItemStackTemplate.STREAM_CODEC, (o) -> o.result, TASShapedRecipe::new
		);

		SERIALIZER = ModRecipeSerializers.TAS_SERIALIZER;
	}
}
