package com.virus5600.defensive_measures.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import org.jetbrains.annotations.VisibleForTesting;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * With the vanilla crafting system class rigid, the need to create a flexible one drived the developer
 * to create a new base class that can hold the centralized common logic of all crafting system-related
 * classes. This class was derived from {@link ShapedRecipePattern} class to allow a more dynamic and
 * flexible crafting system that can support larger crafting grids and more complex recipes, which
 * is necessary for the crafting systems being implemented in this mod.
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see ShapedRecipePattern
 */
public final class CustomShapedRecipePattern {
	public static final StreamCodec<RegistryFriendlyByteBuf, CustomShapedRecipePattern> STREAM_CODEC;
	public static final char EMPTY_SLOT = ' ';

	private final int width;
	private final int height;
	private final List<Optional<Ingredient>> ingredients;
	private final Optional<DataPair> data;
	private final int ingredientCount;
	private final boolean symmetrical;

	public CustomShapedRecipePattern(int width, int height, List<Optional<Ingredient>> ingredients, Optional<DataPair> data) {
		this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.data = data;
		this.ingredientCount = (int)ingredients.stream().flatMap(Optional::stream).count();
		this.symmetrical = Util.isSymmetrical(width, height, ingredients);
	}

	// /////// //
	// METHODS //
	// /////// //

	public static MapCodec<CustomShapedRecipePattern> createCodec(int rows, int cols) {
		Codec<Character> keyEntryCodec = Codec.STRING.comapFlatMap((keyEntry) -> {
			if (keyEntry.length() != 1) {
				return DataResult.error(() -> "Invalid key entry: '%s' is an invalid symbol (must be 1 character only).".formatted(keyEntry));
			}
			else {
				return String.valueOf(EMPTY_SLOT).equals(keyEntry) ? DataResult.error(() -> "Invalid key entry: '%s' is a reserved symbol.".formatted(EMPTY_SLOT))
					: DataResult.success(keyEntry.charAt(0));
			}
		}, String::valueOf);

		MapCodec<DataPair> dataCodec = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
				ExtraCodecs.strictUnboundedMap(keyEntryCodec, Ingredient.CODEC)
					.fieldOf("key")
					.forGetter(DataPair::key),
				Codec.STRING.listOf()
					.fieldOf("pattern")
					.forGetter(DataPair::pattern)
			).apply(instance, DataPair::new)
		);

		return dataCodec.flatXmap(
			data -> DataPair.validate(data, rows, cols).flatMap(CustomShapedRecipePattern::unpack),
			recipe -> (recipe.data != null && recipe.data.isPresent())
				? DataResult.success(recipe.data.get())
				: DataResult.error(() -> "Cannot encode unpacked recipe")
		);
	}

	private static CustomShapedRecipePattern createFromNetwork(Integer width, Integer height, List<Optional<Ingredient>> ingredients) {
		return new CustomShapedRecipePattern(width, height, ingredients, Optional.empty());
	}

	public static CustomShapedRecipePattern of(final Map<Character, Ingredient> key, final String... pattern) {
		return of(key, List.of(pattern));
	}

	public static CustomShapedRecipePattern of(final Map<Character, Ingredient> key, final List<String> pattern) {
		DataPair data = new DataPair(key, pattern);
		return unpack(data).getOrThrow();
	}

	private static DataResult<CustomShapedRecipePattern> unpack(DataPair data) {
		String[] shrunkPattern = shrink(data.pattern());
		int width = shrunkPattern[0].length();
		int height = shrunkPattern.length;
		List<Optional<Ingredient>> ingredients = new ArrayList<>(width * height);
		CharSet unusedSymbols = new CharArraySet(data.key().keySet());

		for(String rowStr : shrunkPattern) {
			for(int colIndex = 0; colIndex < rowStr.length(); ++colIndex) {
				char symbol = rowStr.charAt(colIndex);
				Optional<Ingredient> optional;
				if (symbol == EMPTY_SLOT) {
					optional = Optional.empty();
				} else {
					Ingredient ingredient = data.key().get(symbol);
					if (ingredient == null) {
						return DataResult.error(() -> "Pattern references symbol '" + symbol + "' but it's not defined in the key");
					}

					optional = Optional.of(ingredient);
				}

				unusedSymbols.remove(symbol);
				ingredients.add(optional);
			}
		}

		if (!unusedSymbols.isEmpty()) {
			return DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + unusedSymbols);
		}
		else {
			return DataResult.success(new CustomShapedRecipePattern(width, height, ingredients, Optional.of(data)));
		}
	}

	@VisibleForTesting
	static String[] shrink(List<String> pattern) {
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
		while (index < line.length() && line.charAt(index) == EMPTY_SLOT) {
			index++;
		}
		return index;
	}

	private static int findLastSymbol(String line) {
		int index = line.length() - 1;
		while (index >= 0 && line.charAt(index) == EMPTY_SLOT) {
			index--;
		}
		return index;
	}

	public boolean matches(CraftingInput input) {
		if (input.ingredientCount() == this.ingredientCount) {
			if (input.width() >= this.width && input.height() >= this.height) {
				for (int offsetY = 0; offsetY <= input.height() - this.height; ++offsetY) {
					for (int offsetX = 0; offsetX <= input.width() - this.width; ++offsetX) {
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

	private boolean matches(CraftingInput input, boolean mirrored, int offsetX, int offsetY) {
		for(int row = 0; row < this.height; ++row) {
			for(int col = 0; col < this.width; ++col) {
				Optional<Ingredient> optional;
				if (mirrored) {
					optional = this.ingredients.get(this.width - col - 1 + row * this.width);
				} else {
					optional = this.ingredients.get(col + row * this.width);
				}

				ItemStack itemStack = input.getItem(col + offsetX, row + offsetY);
				if (!Ingredient.testOptionalIngredient(optional, itemStack)) {
					return false;
				}
			}
		}
		return true;
	}

	public int width() {
		return this.width;
	}

	public int height() {
		return this.height;
	}

	public List<Optional<Ingredient>> ingredients() {
		return this.ingredients;
	}

	// ////// //
	// STATIC //
	// ////// //

	static {
		STREAM_CODEC =
			StreamCodec.composite(
				ByteBufCodecs.VAR_INT, CustomShapedRecipePattern::width,
				ByteBufCodecs.VAR_INT, CustomShapedRecipePattern::height,
				Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CustomShapedRecipePattern::ingredients,
				CustomShapedRecipePattern::createFromNetwork
			);
	}

	// ///////////////////////// //
	// FACTORY METHODS (DATAGEN) //
	// ///////////////////////// //

	private static CustomShapedRecipePattern create(int width, int height, List<Optional<Ingredient>> ingredients, Optional<DataPair> data) {
		return new CustomShapedRecipePattern(width, height, ingredients, data);
	}

	public static CustomShapedRecipePattern create(int maxRows, int maxCols, Map<Character, Ingredient> key, String... pattern) {
		return create(maxRows, maxCols, key, List.of(pattern));
	}

	public static CustomShapedRecipePattern create(int maxRows, int maxCols, Map<Character, Ingredient> key, List<String> pattern) {
		DataPair data = new DataPair(key, pattern);

		return DataPair.validate(data, maxRows, maxCols)
			.flatMap(CustomShapedRecipePattern::unpack)
			.getOrThrow();
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
