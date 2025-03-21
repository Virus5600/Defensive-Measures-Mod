package com.virus5600.defensive_measures.util;

import com.virus5600.defensive_measures.DefensiveMeasures;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

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
 * @version 1.0.0
 */
public class RegistryUtil {
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
		return Items.register(createItemKey(path), factory);
	}

	public static Item registerItem(Block block) {
		return Items.register(block);
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
}
