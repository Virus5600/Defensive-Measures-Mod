package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * With the vanilla crafting system class rigid, the need to create a flexible one drived the developer
 * to create a new base class that can hold the centralized common logic of all crafting system-related
 * classes. This class was derived from {@link RawShapedRecipe} class to allow a more dynamic and
 * flexible crafting system that can support larger crafting grids and more complex recipes, which
 * is necessary for the crafting systems being implemented in this mod.
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see RawShapedRecipe
 */
public final class CustomShapedRecipe {
	public static final char SPACE = ' ';

	private final int width;
	private final int height;
	private final List<Optional<Ingredient>> ingredients;
	private final @Nullable DataPair data;
	private final int ingredientCount;
	private final boolean symmetrical;

	public CustomShapedRecipe(int width, int height, List<Optional<Ingredient>> ingredients, @Nullable DataPair data) {
		this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.data = data;
		this.ingredientCount = (int)ingredients.stream().flatMap(Optional::stream).count();
		this.symmetrical = Util.isSymmetrical(width, height, ingredients);
	}

	public boolean matches(CraftingRecipeInput input) {
		if (input.getStackCount() == this.ingredientCount) {
			if (input.getWidth() >= this.width && input.getHeight() >= this.height) {
				for (int offsetY = 0; offsetY <= input.getHeight() - this.height; ++offsetY) {
					for (int offsetX = 0; offsetX <= input.getWidth() - this.width; ++offsetX) {
						if (!this.symmetrical && this.matches(input, true, offsetX, offsetY)) {
							return true;
						}

						if (this.matches(input, false, offsetX, offsetY)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean matches(CraftingRecipeInput input, boolean mirrored, int offsetX, int offsetY) {
		for(int row = 0; row < this.height; ++row) {
			for(int col = 0; col < this.width; ++col) {
				Optional<Ingredient> optional;
				if (mirrored) {
					optional = this.ingredients.get(this.width - col - 1 + row * this.width);
				} else {
					optional = this.ingredients.get(col + row * this.width);
				}

				ItemStack itemStack = input.getStackInSlot(col + offsetX, row + offsetY);
				if (!Ingredient.matches(optional, itemStack)) {
					return false;
				}
			}
		}
		return true;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public List<Optional<Ingredient>> getIngredients() {
		return this.ingredients;
	}

	// ///////////////////////// //
	// FACTORY METHODS (DATAGEN) //
	// ///////////////////////// //

	private static CustomShapedRecipe create(int width, int height, List<Optional<Ingredient>> ingredients, @Nullable DataPair data) {
		return new CustomShapedRecipe(width, height, ingredients, data);
	}

	public static CustomShapedRecipe create(int maxRows, int maxCols, Map<Character, Ingredient> key, String... pattern) {
		return create(maxRows, maxCols, key, List.of(pattern));
	}

	public static CustomShapedRecipe create(int maxRows, int maxCols, Map<Character, Ingredient> key, List<String> pattern) {
		DataPair data = new DataPair(key, pattern);

		return DataPair.validate(data, maxRows, maxCols)
			.flatMap(CustomShapedRecipe::fromData)
			.getOrThrow();
	}

	// ////////////// //
	// CODEC CREATION //
	// ////////////// //

	public static final PacketCodec<RegistryByteBuf, CustomShapedRecipe> PACKET_CODEC =
		PacketCodec.tuple(
			net.minecraft.network.codec.PacketCodecs.VAR_INT, CustomShapedRecipe::getWidth,
			net.minecraft.network.codec.PacketCodecs.VAR_INT, CustomShapedRecipe::getHeight,
			Ingredient.OPTIONAL_PACKET_CODEC.collect(net.minecraft.network.codec.PacketCodecs.toList()), CustomShapedRecipe::getIngredients,
			CustomShapedRecipe::createFromNetwork
		);

	private static CustomShapedRecipe createFromNetwork(Integer width, Integer height, List<Optional<Ingredient>> ingredients) {
		return new CustomShapedRecipe(width, height, ingredients, null);
	}

	public static MapCodec<CustomShapedRecipe> createCodec(int rows, int cols) {
		Codec<Character> keyEntryCodec = Codec.STRING.comapFlatMap((keyEntry) -> {
			if (keyEntry.length() != 1) {
				return DataResult.error(() -> "Invalid key entry: '%s' is an invalid symbol (must be 1 character only).".formatted(keyEntry));
			}
			else {
				return String.valueOf(SPACE).equals(keyEntry) ? DataResult.error(() -> "Invalid key entry: '%s' is a reserved symbol.".formatted(SPACE))
					: DataResult.success(keyEntry.charAt(0));
			}
		}, String::valueOf);

		MapCodec<DataPair> dataCodec = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
				Codecs.strictUnboundedMap(keyEntryCodec, Ingredient.CODEC)
					.fieldOf("key")
					.forGetter(DataPair::key),
				Codec.STRING.listOf()
					.fieldOf("pattern")
					.forGetter(DataPair::pattern)
			).apply(instance, DataPair::new)
		);

		return dataCodec.flatXmap(
			data -> DataPair.validate(data, rows, cols).flatMap(CustomShapedRecipe::fromData),
			recipe -> recipe.data != null
				? DataResult.success(recipe.data)
				: DataResult.error(() -> "Cannot encode unpacked recipe")
		);
	}

	private static DataResult<CustomShapedRecipe> fromData(DataPair data) {
		String[] trimmedPattern = removePadding(data.pattern());
		int recipeWidth = trimmedPattern[0].length();
		int recipeHeight = trimmedPattern.length;
		List<Optional<Ingredient>> ingredientsList = new ArrayList<>(recipeWidth * recipeHeight);
		CharSet unusedSymbols = new CharArraySet(data.key().keySet());

		for(String rowStr : trimmedPattern) {
			for(int colIndex = 0; colIndex < rowStr.length(); ++colIndex) {
				char symbol = rowStr.charAt(colIndex);
				Optional<Ingredient> optional;
				if (symbol == SPACE) {
					optional = Optional.empty();
				} else {
					Ingredient ingredient = data.key().get(symbol);
					if (ingredient == null) {
						return DataResult.error(() -> "Pattern references symbol '" + symbol + "' but it's not defined in the key");
					}

					optional = Optional.of(ingredient);
				}

				unusedSymbols.remove(symbol);
				ingredientsList.add(optional);
			}
		}

		if (!unusedSymbols.isEmpty()) {
			return DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + unusedSymbols);
		} else {
			return DataResult.success(new CustomShapedRecipe(recipeWidth, recipeHeight, ingredientsList, data));
		}
	}

	@VisibleForTesting
	static String[] removePadding(List<String> pattern) {
		int firstColumn = Integer.MAX_VALUE;
		int lastColumn = 0;
		int topPadding = 0;
		int bottomPadding = 0;

		for(int rowIndex = 0; rowIndex < pattern.size(); ++rowIndex) {
			String rowStr = pattern.get(rowIndex);
			firstColumn = Math.min(firstColumn, findFirstSymbol(rowStr));
			int lastSymbolIndex = findLastSymbol(rowStr);
			lastColumn = Math.max(lastColumn, lastSymbolIndex);

			if (lastSymbolIndex < 0) {
				if (topPadding == rowIndex) {
					++topPadding;
				}

				++bottomPadding;
			} else {
				bottomPadding = 0;
			}
		}

		if (pattern.size() == bottomPadding) {
			return new String[0];
		} else {
			String[] trimmedRows = new String[pattern.size() - bottomPadding - topPadding];

			for(int newRowIndex = 0; newRowIndex < trimmedRows.length; ++newRowIndex) {
				trimmedRows[newRowIndex] = (pattern.get(newRowIndex + topPadding)).substring(firstColumn, lastColumn + 1);
			}

			return trimmedRows;
		}
	}

	private static int findFirstSymbol(String line) {
		int index = 0;
		while (index < line.length() && line.charAt(index) == SPACE) {
			index++;
		}
		return index;
	}

	private static int findLastSymbol(String line) {
		int index = line.length() - 1;
		while (index >= 0 && line.charAt(index) == SPACE) {
			index--;
		}
		return index;
	}

	// ///////////////////////////////////////// //
	// CUSTOM CODEC RECORD FOR DYNAMIC GRID SIZE //
	// ///////////////////////////////////////// //

	public record DataPair(Map<Character, Ingredient> key, List<String> pattern) {

		public static DataResult<DataPair> validate(DataPair data, int maxRows, int maxCols) {
			if (data.pattern().size() > maxRows) {
				return DataResult.error(() -> "Invalid pattern: too many rows, %d is maximum".formatted(maxRows));
			}
			if (data.pattern().isEmpty()) {
				return DataResult.error(() -> "Invalid pattern: empty pattern is not allowed");
			}

			int width = data.pattern().getFirst().length();
			for (String string : data.pattern()) {
				if (string.length() > maxCols) {
					return DataResult.error(() -> "Invalid pattern: too many columns, %d is maximum".formatted(maxCols));
				}
				if (width != string.length()) {
					return DataResult.error(() -> "Invalid pattern: each row must be the same width");
				}
			}

			return DataResult.success(data);
		}
	}
}
