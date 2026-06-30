package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.MapCodec;
import com.virus5600.defensive_measures.item.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.block.TurretAssemblyStationBlock;
import com.virus5600.defensive_measures.recipe.annotations.Shapeless;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A crafting recipe specifically made for the {@link TurretAssemblyStationBlock Turret Assembly Station},
 * based on the {@link ShapelessRecipe} recipe type, but expanded to 7x7.
 * <br><br>
 * This recipe allows a matrix of 7x7, which is larger than the standard crafting table's 3x3
 * matrix. This opens up a wider range of crafting possibilities, enabling more complex and
 * intricate recipes.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Shapeless
public class TASShapelessRecipe extends BaseCraftingRecipe<CraftingInput> {
	public static final MapCodec<TASShapelessRecipe> MAP_CODEC;
	public static final StreamCodec<RegistryFriendlyByteBuf, TASShapelessRecipe> STREAM_CODEC;
	public static final RecipeSerializer<TASShapelessRecipe> SERIALIZER;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	public TASShapelessRecipe(CommonInfo commonInfo, ModCraftingBookInfo bookInfo, List<Ingredient> ingredients, ItemStackTemplate result) {
		super(commonInfo, bookInfo, ingredients, result);
	}

	// //////////////// //
	// OVERRIDE METHODS //
	// //////////////// //

	@NonNull
	public List<RecipeDisplay> display() {
		if (this.ingredients == null) {
			throw new IllegalStateException("ingredients is null");
		}

		return List.of(new ShapelessCraftingRecipeDisplay(
			this.ingredients
				.stream()
				.map(Ingredient::display)
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
		if (this.ingredients == null) {
			throw new  IllegalStateException("pattern is null");
		}

		return this.ingredients
			.stream()
			.map(Optional::of)
			.toList();
	}

	protected PlacementInfo createPlacementInfo() {
		String errorMsg = "Ingredients list cannot be null for recipe" + this.commonInfo;
		Objects.requireNonNull(this.ingredients, errorMsg);

		return PlacementInfo.create(this.ingredients);
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
		return ModRecipeSerializers.TAS_SHAPELESS_SERIALIZER;
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

	@NonNull
	public ItemStack assemble(@NonNull CraftingInput input) {
		return this.result.create();
	}

	// ////// //
	// STATIC //
	// ////// //

	static {
		MAP_CODEC = BaseCraftingRecipe.createShapelessMapCodec(
			7, 7,
			ModCraftingBookInfo.MAP_CODEC,
			TASShapelessRecipe::new
		);

		STREAM_CODEC = StreamCodec.composite(
			CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
			ModCraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
			Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
			recipe -> recipe.ingredients,
			ItemStackTemplate.STREAM_CODEC,
			recipe -> recipe.result,
			TASShapelessRecipe::new
		);

		SERIALIZER = ModRecipeSerializers.TAS_SHAPELESS_SERIALIZER;
	}
}
