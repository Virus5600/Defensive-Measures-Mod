package com.virus5600.defensive_measures._helper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public final class RegistryHelper {
	// Holder
	public static Holder<Item> getHolder(Item item) {
		return BuiltInRegistries.ITEM.wrapAsHolder(item);
	}

	public static Holder<Block> getHolder(Block block) {
		return BuiltInRegistries.BLOCK.wrapAsHolder(block);
	}

	public static Holder<EntityType<?>> getHolder(EntityType<?> entityType) {
		return BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entityType);
	}

	public static boolean isOf(EntityType<?> entityType, TagKey<EntityType<?>> tag) {
		return getHolder(entityType).is(tag);
	}

	// Block Registry
	private static ResourceKey<Block> createBlockKey(String name) {
		return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, name));
	}

	public static Block registerBlock(String path, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties settings) {
		return Blocks.register(createBlockKey(path), function, settings);
	}

	// Item Registry
	private static ResourceKey<Item> createItemKey(String path) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, path));
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private static ResourceKey<Item> createBlockItemKey(Block block) {
		ResourceKey<Block> key = getHolder(block).unwrapKey().get();
		return ResourceKey.create(Registries.ITEM, key.identifier());
	}

	public static Item registerItem(String path, Function<Properties, Item> factory, Properties settings) {
		return registerItem(createItemKey(path), factory, settings);
	}

	public static Item registerItem(ResourceKey<Item> key, Function<Properties, Item> factory, Properties settings) {
		settings.setId(key);
		Item item = factory.apply(settings);

		if (item instanceof BlockItem blockItem) {
			blockItem.registerBlocks(Item.BY_BLOCK, item);
		}

		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static Item registerItem(String path, Function<Properties, Item> factory) {
		return registerItem(path, factory, new Properties());
	}

	public static Item registerItem(Block block) {
		Properties prop = new Properties();

		return registerItem(
			createBlockItemKey(block),
			settings -> new BlockItem(block, settings),
			prop
		);
	}

	// Tag Registry
	public static TagKey<Block> createBlockTagKey(String path) {
		return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, path));
	}

	public static TagKey<EntityType<?>> createEntityTypeTagKey(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, path));
	}

	public static TagKey<Item> createItemTagKey(String path) {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, path));
	}

	// Entity Registry
	private static ResourceKey<EntityType<?>> createEntityKey(String path) {
		return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, path));
	}

	public static <T extends Entity> EntityType<T> registerEntity(String path, EntityType.Builder<T> builder) {
		ResourceKey<EntityType<?>> key = createEntityKey(path);

		return Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			key,
			builder.build(key)
		);
	}

	// Damage Type Registry
	public static ResourceKey<DamageType> getDamageTypeKey(String path) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, path));
	}

	// Stats Registry
	public static Identifier registerStat(String id, StatFormatter formatter) {
		Identifier identifier = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, id);
		Registry.register(BuiltInRegistries.CUSTOM_STAT, identifier, identifier);
		Stats.CUSTOM.get(identifier, formatter);
		return identifier;
	}

	public static <T> StatType<T> registerStatType(String id, Registry<T> registry) {
		Component text = Component.translatable("stat_type." + DefensiveMeasures.MOD_ID + "." + id);
		return Registry.register(BuiltInRegistries.STAT_TYPE, id, new StatType<>(registry, text));
	}

	// Screen Handler Type Registry
	public static <T extends AbstractContainerMenu> MenuType<T> registerScreenHandlerType(String id, MenuType.MenuSupplier<T> factory) {
		return Registry.register(
			BuiltInRegistries.MENU,
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, id),
			new MenuType<>(factory, FeatureFlags.VANILLA_SET)
		);
	}

	// Recipe Related Registry
	public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(String id) {
		return Registry.register(
			BuiltInRegistries.RECIPE_TYPE,
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, id),
			new RecipeType<T>() {
				public String toString() {
					return id;
				}
			}
		);
	}

	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String id, S recipeSerializer) {
		return Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER,
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, id),
			recipeSerializer
		);
	}

	public static <S extends RecipeDisplay.Type<T>, T extends RecipeDisplay> S registerRecipeDisplay(String id, S recipeDisplaySerializer) {
		return Registry.register(
			BuiltInRegistries.RECIPE_DISPLAY,
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, id),
			recipeDisplaySerializer
		);
	}

	public static RecipeBookCategory registerRecipeBookCat(String id) {
		return Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, id, new RecipeBookCategory());
	}
}
