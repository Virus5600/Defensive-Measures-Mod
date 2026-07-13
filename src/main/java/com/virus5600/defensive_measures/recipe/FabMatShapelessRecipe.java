package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.block.misc.tier3.FabricationMatrixBlock;
import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.recipe.annotations.Shapeless;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A crafting recipe specifically made for the {@link FabricationMatrixBlock Fabrication Matrix},
 * based on the {@link ShapelessRecipe} recipe type, but expanded to 9x9.
 * <br><br>
 * This recipe allows a matrix of 9x9, which is larger than the standard crafting table's 3x3
 * matrix. This opens up a wider range of crafting possibilities, enabling more complex and
 * intricate recipes.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Shapeless
public class FabMatShapelessRecipe extends BaseCraftingRecipe<CraftingInput> {
	public static final MapCodec<FabMatShapelessRecipe> MAP_CODEC;
	public static final StreamCodec<RegistryFriendlyByteBuf, FabMatShapelessRecipe> STREAM_CODEC;
	public static final RecipeSerializer<FabMatShapelessRecipe> SERIALIZER;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	public FabMatShapelessRecipe(CommonInfo commonInfo, ModCraftingBookInfo bookInfo, List<Ingredient> ingredients, ItemStackTemplate result) {
		super(commonInfo, bookInfo, ingredients, result);
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected CustomShapedRecipePattern pattern() {
		return this.pattern;
	}

	protected PlacementInfo createPlacementInfo() {
		String errorMsg = "Ingredients list cannot be null for recipe" + this.commonInfo;
		Objects.requireNonNull(this.ingredients, errorMsg);

		return PlacementInfo.create(this.ingredients);
	}

	public List<Optional<Ingredient>> getIngredients() {
		if (this.ingredients == null) {
			throw new  IllegalStateException("pattern is null");
		}

		return this.ingredients
			.stream()
			.map(Optional::of)
			.toList();
	}

	public Item getItemForSlotDisplay() {
		return ModItems.FABRICATION_MATRIX;
	}

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public RecipeType<? extends Recipe<CraftingInput>> getType() {
		return ModRecipeTypes.FABRICATION_MATRIX_RECIPE_TYPE;
	}

	@Override
	public RecipeSerializer<? extends BaseCraftingRecipe<CraftingInput>> getSerializer() {
		return SERIALIZER;
	}

	public boolean matches(final CraftingInput craftingRecipeInput, final Level level) {
		if (this.ingredients == null) {
			throw new IllegalStateException("ingredients is null");
		}

		if (craftingRecipeInput.ingredientCount() != this.ingredients.size()) {
			return false;
		}
		else {
			return craftingRecipeInput.size() == 1 && this.ingredients.size() == 1 ?
				this.ingredients
					.getFirst()
					.test(craftingRecipeInput.getItem(0)) :
				craftingRecipeInput.stackedContents()
					.canCraft(this, null);
		}
	}

	public ItemStack assemble(CraftingInput input) {
		return this.result.create();
	}

	// ////// //
	// STATIC //
	// ////// //

	static {
		MAP_CODEC = BaseCraftingRecipe.createShapelessMapCodec(
			9, 9,
			ModCraftingBookInfo.MAP_CODEC,
			FabMatShapelessRecipe::new
		);

		STREAM_CODEC = StreamCodec.composite(
			CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
			ModCraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
			Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
			recipe -> recipe.ingredients,
			ItemStackTemplate.STREAM_CODEC,
			recipe -> recipe.result,
			FabMatShapelessRecipe::new
		);

		SERIALIZER = ModRecipeSerializers.FABMAT_SHAPELESS_SERIALIZER;
	}
}
