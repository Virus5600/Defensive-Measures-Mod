package com.virus5600.defensive_measures._helper;

import com.virus5600.defensive_measures.stat.ModStats;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;

import java.util.function.Function;

/**
 * Registry helper class for registering items, blocks, etc.
 * This was made after a lot of changes were done since 1.19.x fabric API and
 * due to the fact that every update seems to break registry code.
 * The aim of this class is to provide a simple way to register items, blocks, etc.
 * while keeping the code clean and easy to read. And in a way, allows the developer
 * to easily maintain the codes and update them when needed.
 *
 * @see <a href="https://github.com/Khazoda/basic-weapons/blob/latest-stable/src/main/java/com/seacroak/basicweapons/util/Reggie.java">Reggie.java 「(c) Khazoda」</a>
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.1
 */
public final class RegistryHelper {
	// Block Registry
	private static RegistryKey<Block> createBlockKey(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DefensiveMeasures.MOD_ID, name));
	}

	public static Block registerBlock(String path, Function<AbstractBlock.Settings, Block> function, AbstractBlock.Settings settings) {
		return Blocks.register(createBlockKey(path), function, settings);
	}

	// Item Registry
	private static RegistryKey<Item> createItemKey(String path) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DefensiveMeasures.MOD_ID, path));
	}

	public static Item registerItem(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
		return Items.register(createItemKey(path), factory, settings);
	}

	public static Item registerItem(String path, Function<Item.Settings, Item> factory) {
		return Items.register(createItemKey(path), factory, new Item.Settings());
	}

	public static Item registerItem(Block block) {
		return Items.register(block);
	}

	// Item Tag Registry
	public static TagKey<Item> createItemTagKey(String path) {
		return TagKey.of(RegistryKeys.ITEM, Identifier.of(DefensiveMeasures.MOD_ID, path));
	}

	// Entity Registry
	private static RegistryKey<EntityType<?>> createEntityKey(String path) {
		return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DefensiveMeasures.MOD_ID, path));
	}

	public static <T extends Entity> EntityType<T> registerEntity(String path, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = createEntityKey(path);

		return Registry.register(
			Registries.ENTITY_TYPE,
			key,
			builder.build(key)
		);
	}

	// Damage Type Registry
	public static RegistryKey<DamageType> getDamageTypeKey(String path) {
		return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(DefensiveMeasures.MOD_ID, path));
	}

	// Stats Registry
	public static Identifier registerStat(String id, StatFormatter formatter) {
		Identifier identifier = Identifier.of(DefensiveMeasures.MOD_ID, id);
		Registry.register(Registries.CUSTOM_STAT, identifier, identifier);
		Stats.CUSTOM.getOrCreateStat(identifier, formatter);
		return identifier;
	}

	public static <T> StatType<T> registerStatType(String id, Registry<T> registry) {
		Text text = Text.translatable("stat_type." + DefensiveMeasures.MOD_ID + "." + id);
		return Registry.register(Registries.STAT_TYPE, id, new StatType<>(registry, text));
	}

	// Screen Handler Type Registry
	public static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandlerType(String id, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(
			Registries.SCREEN_HANDLER,
			Identifier.of(DefensiveMeasures.MOD_ID, id),
			new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES)
		);
	}

	// Recipe Related Registry
	public static <T extends Recipe<?>>RecipeType<T> registerRecipeType(String id) {
		return Registry.register(
			Registries.RECIPE_TYPE,
			Identifier.of(DefensiveMeasures.MOD_ID, id),
			new RecipeType<T>() {
				public String toString() {
					return id;
				}
			}
		);
	}

	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String id, S recipeSerializer) {
		return Registry.register(
			Registries.RECIPE_SERIALIZER,
			Identifier.of(DefensiveMeasures.MOD_ID, id),
			recipeSerializer
		);
	}

	public static <S extends RecipeDisplay.Serializer<T>, T extends RecipeDisplay> S registerRecipeDisplay(String id, S recipeDisplaySerializer) {
		return Registry.register(
			Registries.RECIPE_DISPLAY,
			Identifier.of(DefensiveMeasures.MOD_ID, id),
			recipeDisplaySerializer
		);
	}

	public static RecipeBookCategory registerRecipeBookCat(String id) {
		return Registry.register(Registries.RECIPE_BOOK_CATEGORY, id, new RecipeBookCategory());
	}
}
