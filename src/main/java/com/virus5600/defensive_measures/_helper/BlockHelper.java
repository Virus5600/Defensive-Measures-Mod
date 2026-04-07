package com.virus5600.defensive_measures._helper;

import com.virus5600.defensive_measures.registry.tag.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import com.virus5600.defensive_measures.entity.projectiles.MGBulletEntity;

/**
 * Contains all utility methods that can be used for the {@link Block Block} and
 * {@link BlockState Block State} classes such as identifying if it matches a superclass or interface.
 * This is to supplement some shortcomings of the primary vanilla {@code state} and {@code BlockState}
 * class and create flexibility towards applying features and modifications.
 *
 * @see Block Block
 * @see BlockState BlockState
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.1.0
 */
public final class BlockHelper {

	// /////// //
	// METHODS //
	// /////// //

	/**
	 * Identifies whether the provided {@code state} is one of the dirt blocks
	 * defined in the {@link ModBlockTags#DIRT} tag.
	 *
	 * @param state Block's state in question
	 * @return boolean
	 */
	public static boolean isDirt(BlockState state) {
		return state.isIn(ModBlockTags.DIRT);
	}

	/**
	 * Identifies whether the provided {@code state} is one of the glass blocks
	 * defined in the {@link ModBlockTags#GLASS} tag.
	 *
	 * @param state Block's state in question
	 * @return boolean
	 */
	public static boolean isGlass(BlockState state) {
		return state.isIn(ModBlockTags.GLASS);
	}

	/**
	 * Identifies whether the provided {@code state} is one of the grainy blocks
	 * defined in the {@link ModBlockTags#GRAINY} list.
	 *
	 * @param state Block's state in question
	 * @return boolean
	 */
	public static boolean isGrainy(BlockState state) {
		return state.isIn(ModBlockTags.GRAINY);
	}

	/**
	 * Identifies whether the provided {@code state} is one of the greenery blocks
	 * defined in the {@link ModBlockTags#GREENERY} list.
	 *
	 * @param state Block's state in question
	 * @return boolean
	 */
	public static boolean isGreenery(BlockState state) {
		return state.isIn(ModBlockTags.GREENERY);
	}

	/**
	 * Identifies whether the provided {@code state} is one of the metal blocks
	 * defined in the {@link ModBlockTags#METAL} list.
	 *
	 * @param state Block's state in question
	 * @return boolean
	 */
	public static boolean isMetal(BlockState state) {
		return state.isIn(ModBlockTags.METAL);
	}

	/**
	 * Identifies whether the provided {@code state} is one of the stone blocks
	 * defined in the {@link ModBlockTags#STONE} list.
	 *
	 * @param state Block's state in question
	 * @return boolean
	 */
	public static boolean isStone(BlockState state) {
		return state.isIn(ModBlockTags.STONE);
	}

	/**
	 * Identifies whether the provided {@code state} is one of the wood blocks
	 * defined in the {@link ModBlockTags#WOOD} list.
	 *
	 * @param state Block's state in question
	 * @return boolean
	 */
	public static boolean isWood(BlockState state) {
		return state.isIn(ModBlockTags.WOOD);
	}

	/**
	 * Identifies the category of the provided {@code state} based on the lists defined in this
	 * class. The category is determined by the type of block it is, such as dirt, glass, grainy,
	 * greenery, metal, stone, wood, or others.
	 * <br><br>
	 * If the block does not match any of the categories, it will be classified as {@code OTHERS}.
	 *
	 * @param state Block's state in question
	 *
	 * @see BlockCategory
	 * @return BlockCategory
	 */
	public static BlockCategory getBlockCategory(BlockState state) {
		if (isDirt(state)) {
			return BlockCategory.DIRT;
		} else if (isGlass(state)) {
			return BlockCategory.GLASS;
		} else if (isGrainy(state)) {
			return BlockCategory.GRAINY;
		} else if (isGreenery(state)) {
			return BlockCategory.GREENERY;
		} else if (isMetal(state)) {
			return BlockCategory.METAL;
		} else if (isStone(state)) {
			return BlockCategory.STONE;
		} else if (isWood(state)) {
			return BlockCategory.WOOD;
		}
		return BlockCategory.OTHERS;
	}

	// //////////// //
	// CUSTOM ENUMS //
	// //////////// //

	/**
	 * This enum provides a way to identify the category of blocks based
	 * on their composition and appearance, allowing for easier classification
	 * of blocks when needed by the mod, such as the use of dynamic bullet hit
	 * sound from {@link MGBulletEntity MG Bullet's}
	 * hit sound.
	 * <br><br>
	 * As of <b>Version 1.1.x-beta-1.21.4</b>, the currently defined block
	 * categories are:
	 * <ul>
	 *     <li>{@link #DIRT Dirt} blocks</li>
	 *     <li>{@link #GLASS Glass} blocks</li>
	 *     <li>{@link #GRAINY Grainy} blocks</li>
	 *     <li>{@link #GREENERY Greenery} blocks</li>
	 *     <li>{@link #METAL Metal} blocks</li>
	 *     <li>{@link #STONE Stone} blocks</li>
	 *     <li>{@link #WOOD Wood} blocks</li>
	 *     <li>{@link #OTHERS Other} blocks</li>
	 * </ul>
	 * For more information on the block categories, see their
	 * respective documentation.
	 *
	 * @since 1.0.0
	 * @author <a href="https://github.com/Virus5600">Virus5600</a>
	 * @version 1.0.0
	 */
	public enum BlockCategory {
		/**
		 * Represents the blocks that are classified as dirt blocks such
		 * as Dirt, Coarse Dirt, Grass Block, Mycelium, Podzol,
		 * even Muds and other dirt-like blocks.
		 */
		DIRT,
		/**
		 * Represents the blocks that are classified as glass blocks or
		 * basically, fragile blocks that can be broken easily such as
		 * Glass, Glass Panes, Stained Glass, Stained Glass Panes,
		 * and some light sources like Glowstone, Redstone Lamp, and Sea
		 * Lantern.
		 */
		GLASS,
		/**
		 * Represents the blocks that are classified as grainy blocks such
		 * as Gravel, Sand, Soul Sand, Soul Soil, Suspicious Gravel,
		 * Suspicious Sand, and Concrete Powder blocks.
		 */
		GRAINY,
		/**
		 * These are basically the shrubs and plants. This includes the tree
		 * leaves, grass, flowers, and other botanical transparent blocks.
		 */
		GREENERY,
		/**
		 * Represents the blocks that are classified as metal blocks such
		 * as the metal blocks and its craftable variants like doors, trapdoors,
		 * and other metal blocks.
		 */
		METAL,
		/**
		 * Blocks that are often found in caves and mountains. Aside from those,
		 * craftable blocks like bricks, cobblestones, and other stone-like
		 * blocks are also included in this category.
		 */
		STONE,
		/**
		 * All wood type blocks are defined in this category. This includes
		 * the entire log and plank types, their craftable counterparts such
		 * as doors and trapdoors, and other wood-like blocks.
		 * <br><br>
		 * This, however, excludes the transparent wood blocks like (normal)
		 * signs, item frames, and other transparent wood blocks.
		 */
		WOOD,
		/**
		 * All other blocks that do not fit in any of the categories above are
		 * classified under this category. This includes blocks like pots,
		 * beds, carpets, and other miscellaneous blocks.
		 */
		OTHERS
	}
}
