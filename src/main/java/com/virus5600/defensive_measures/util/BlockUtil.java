package com.virus5600.defensive_measures.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import com.google.common.collect.ImmutableList;

import com.virus5600.defensive_measures.entity.projectiles.MGBulletEntity;

import java.util.List;

/**
 * Contains all utility methods that can be used for the {@link net.minecraft.block.Block Block class}
 * such as identifying if it matches a superclass or interface. This is to supplement some
 * shortcomings of the primary vanilla {@code Block} class and create flexibility towards applying
 * features and modifications.
 *
 * @see net.minecraft.block.Block Block
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class BlockUtil {

	// ////// //
	// PUBLIC //
	// ////// //

	// IMMUTABLE LISTS //
	/**
	 * List of all dirt blocks in the vanilla game. This list includes all dirt blocks from the
	 * vanilla game. Modded dirt blocks are not included in this list and should be added manually
	 * through the {@link BlockUtil#DIRT} list.
	 * <br><br>
	 * This list is immutable and cannot be modified, but can be used to identify vanilla dirt
	 * blocks.
	 */
	public static final ImmutableList<Block> VANILLA_DIRT;

	/**
	 * List of all glass blocks in the vanilla game. This list includes all normal, stained, and
	 * tinted glass blocks from the vanilla game. Modded glass blocks are not included in this list
	 * and should be added manually through the {@link BlockUtil#GLASS} list.
	 * <br><br>
	 * This list is immutable and cannot be modified, but can be used to identify vanilla glass
	 * blocks.
	 */
	public static final ImmutableList<Block> VANILLA_GLASS;

	/**
	 * List of all grainy blocks in the vanilla game. This list includes all sand blocks,
	 * gravel blocks, and other grainy blocks from the vanilla game. Modded grainy blocks are not
	 * included in this list and should be added manually through the {@link BlockUtil#GRAINY} list.
	 * <br><br>
	 * This list is immutable and cannot be modified, but can be used to identify vanilla grainy
	 * blocks.
	 */
	public static final ImmutableList<Block> VANILLA_GRAINY;

	/**
	 * List of all greenery blocks in the vanilla game. This list includes all greenery blocks from
	 * the vanilla game. Modded greenery blocks are not included in this list and should be added
	 * manually through the {@link BlockUtil#GREENERY} list.
	 * <br><br>
	 * This list is immutable and cannot be modified, but can be used to identify vanilla greenery
	 * blocks.
	 */
	public static final ImmutableList<Block> VANILLA_GREENERY;

	/**
	 * List of all metal blocks in the vanilla game. This list includes all metallic blocks from the
	 * vanilla game. Modded metal blocks are not included in this list and should be added manually
	 * through the {@link BlockUtil#METAL} list.
	 * <br><br>
	 * This list is immutable and cannot be modified, but can be used to identify vanilla metal
	 * blocks.
	 */
	public static final ImmutableList<Block> VANILLA_METAL;

	/**
	 * List of all stone blocks in the vanilla game. This list includes all stone blocks from the
	 * vanilla game. Modded stone blocks are not included in this list and should be added manually
	 * through the {@link BlockUtil#STONE} list. Brick blocks are included in this list as they are
	 * considered stone blocks.
	 * <br><br>
	 * This list is immutable and cannot be modified, but can be used to identify vanilla stone
	 * blocks.
	 */
	public static final ImmutableList<Block> VANILLA_STONE;

	/**
	 * List of all wood blocks in the vanilla game. This list includes all wood blocks from the
	 * vanilla game. Modded wood blocks are not included in this list and should be added manually
	 * through the {@link BlockUtil#WOOD} list.
	 * <br><br>
	 * This list is immutable and cannot be modified, but can be used to identify vanilla wood
	 * blocks.
	 */
	public static final ImmutableList<Block> VANILLA_WOOD;

	// MUTABLE LISTS //
	/**
	 * List of all dirt blocks in the game. This list initially contains all dirt blocks from the
	 * {@link #VANILLA_DIRT vanilla game}. Modded dirt blocks can be added to this list to identify
	 * them as dirt blocks by simply using {@code DIRT.add(block)}.
	 * <br><br>
	 * This list is mutable and can be modified to include modded dirt blocks.
	 */
	public static List<Block> DIRT;

	/**
	 * List of all glass blocks in the game. This list initially contains all glass blocks from the
	 * {@link #VANILLA_GLASS vanilla game}. Modded glass blocks can be added to this list to
	 * identify them as glass blocks by simply using {@code GLASS.add(block)}.
	 * <br><br>
	 * This list is mutable and can be modified to include modded glass blocks.
	 */
	public static List<Block> GLASS;

	/**
	 * List of all grainy blocks in the game. This list initially contains all grainy blocks from the
	 * {@link #VANILLA_GRAINY vanilla game}. Modded grainy blocks can be added to this list to
	 * identify them as grainy blocks by simply using {@code GRAINY.add(block)}.
	 * <br><br>
	 * This list is mutable and can be modified to include modded grainy blocks.
	 */
	public static List<Block> GRAINY;

	/**
	 * List of all greenery blocks in the game. This list initially contains all greenery blocks from
	 * the {@link #VANILLA_GREENERY vanilla game}. Modded greenery blocks can be added to this list to
	 * identify them as greenery blocks by simply using {@code GREENERY.add(block)}.
	 * <br><br>
	 * This list is mutable and can be modified to include modded greenery blocks.
	 */
	public static List<Block> GREENERY;

	/**
	 * List of all metal blocks in the game. This list initially contains all metal blocks from the
	 * {@link #VANILLA_METAL vanilla game}. Modded metal blocks can be added to this list to
	 * identify them as metal blocks by simply using {@code METAL.add(block)}.
	 * <br><br>
	 * This list is mutable and can be modified to include modded metal blocks.
	 */
	public static List<Block> METAL;

	/**
	 * List of all stone blocks in the game. This list initially contains all stone blocks from the
	 * {@link #VANILLA_STONE vanilla game}. Modded stone blocks can be added to this list to
	 * identify them as metal blocks by simply using {@code STONE.add(block)}.
	 * <br><br>
	 * This list is mutable and can be modified to include modded stone blocks.
	 */
	public static List<Block> STONE;

	/**
	 * List of all wood blocks in the game. This list initially contains all wood blocks from the
	 * {@link #VANILLA_WOOD vanilla game}. Modded wood blocks can be added to this list to
	 * identify them as wood blocks by simply using {@code WOOD.add(block)}.
	 * <br><br>
	 * This list is mutable and can be modified to include modded wood blocks.
	 */
	public static List<Block> WOOD;

	// /////// //
	// METHODS //
	// /////// //

	/**
	 * Identifies whether the provided {@code block} is one of the dirt blocks
	 * defined in the {@link BlockUtil#DIRT} list.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isDirt(Block block) {
		return DIRT.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the vanilla dirt blocks
	 * defined in the {@link BlockUtil#VANILLA_DIRT} list. This list only contains vanilla
	 * dirt blocks and does not include modded dirt blocks. If you want to include modded
	 * dirt blocks, use the {@link BlockUtil#DIRT} list instead.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaDirt(Block block) {
		return VANILLA_DIRT.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the glass blocks
	 * defined in the {@link BlockUtil#GLASS} list.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isGlass(Block block) {
		return GLASS.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the vanilla glass blocks
	 * defined in the {@link BlockUtil#VANILLA_GLASS} list. This list only contains vanilla
	 * glass blocks and does not include modded glass blocks. If you want to include modded
	 * glass blocks, use the {@link BlockUtil#GLASS} list instead.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaGlass(Block block) {
		return VANILLA_GLASS.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the grainy blocks
	 * defined in the {@link BlockUtil#GRAINY} list.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isGrainy(Block block) {
		return GRAINY.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the vanilla glass blocks
	 * defined in the {@link BlockUtil#VANILLA_GRAINY} list. This list only contains vanilla
	 * glass blocks and does not include modded glass blocks. If you want to include modded
	 * glass blocks, use the {@link BlockUtil#GRAINY} list instead.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaGrainy(Block block) {
		return VANILLA_GRAINY.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the greenery blocks
	 * defined in the {@link BlockUtil#GREENERY} list.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isGreenery(Block block) {
		return GREENERY.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the vanilla glass blocks
	 * defined in the {@link BlockUtil#VANILLA_GREENERY} list. This list only contains vanilla
	 * glass blocks and does not include modded glass blocks. If you want to include modded
	 * glass blocks, use the {@link BlockUtil#GREENERY} list instead.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaGreenery(Block block) {
		return VANILLA_GREENERY.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the metal blocks
	 * defined in the {@link BlockUtil#METAL} list.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isMetal(Block block) {
		return METAL.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the vanilla glass blocks
	 * defined in the {@link BlockUtil#VANILLA_METAL} list. This list only contains vanilla
	 * glass blocks and does not include modded glass blocks. If you want to include modded
	 * glass blocks, use the {@link BlockUtil#METAL} list instead.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaMetal(Block block) {
		return VANILLA_METAL.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the stone blocks
	 * defined in the {@link BlockUtil#STONE} list.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isStone(Block block) {
		return STONE.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the vanilla glass blocks
	 * defined in the {@link BlockUtil#VANILLA_STONE} list. This list only contains vanilla
	 * glass blocks and does not include modded glass blocks. If you want to include modded
	 * glass blocks, use the {@link BlockUtil#STONE} list instead.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaStone(Block block) {
		return VANILLA_STONE.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the wood blocks
	 * defined in the {@link BlockUtil#WOOD} list.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isWood(Block block) {
		return WOOD.contains(block);
	}

	/**
	 * Identifies whether the provided {@code block} is one of the vanilla glass blocks
	 * defined in the {@link BlockUtil#VANILLA_WOOD} list. This list only contains vanilla
	 * glass blocks and does not include modded glass blocks. If you want to include modded
	 * glass blocks, use the {@link BlockUtil#WOOD} list instead.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaWood(Block block) {
		return VANILLA_WOOD.contains(block);
	}

	/**
	 * Identifies the category of the provided {@code block} based on the lists defined in this
	 * class. The category is determined by the type of block it is, such as dirt, glass, grainy,
	 * greenery, metal, stone, wood, or others.
	 * <br><br>
	 * If the block does not match any of the categories, it will be classified as {@code OTHERS}.
	 *
	 * @param block Block in question
	 *
	 * @see BlockCategory
	 * @return BlockCategory
	 */
	public static BlockCategory getBlockCategory(Block block) {
		if (isDirt(block)) {
			return BlockCategory.DIRT;
		} else if (isGlass(block)) {
			return BlockCategory.GLASS;
		} else if (isGrainy(block)) {
			return BlockCategory.GRAINY;
		} else if (isGreenery(block)) {
			return BlockCategory.GREENERY;
		} else if (isMetal(block)) {
			return BlockCategory.METAL;
		} else if (isStone(block)) {
			return BlockCategory.STONE;
		} else if (isWood(block)) {
			return BlockCategory.WOOD;
		}
		return BlockCategory.OTHERS;
	}

	/**
	 * Identifies the category of the provided {@code block} based on the lists defined in this
	 * class. The category is determined by the type of block it is, such as dirt, glass, grainy,
	 * greenery, metal, stone, wood, or others.
	 * <br><br>
	 * This method, unlike the {@link BlockUtil#getBlockCategory(Block) getBlockCategory} method,
	 * only classifies the block based on the vanilla game blocks. This is useful for identifying
	 * vanilla blocks without the need to include modded blocks.
	 * <br><br>
	 * If the block does not match any of the categories, it will be classified as {@code OTHERS}.
	 *
	 * @param block Block in question
	 *
	 * @see BlockCategory
	 * @return BlockCategory
	 */
	public static BlockCategory getBlockVanillaCategory(Block block) {
		if (isVanillaDirt(block)) {
			return BlockCategory.DIRT;
		} else if (isVanillaGlass(block)) {
			return BlockCategory.GLASS;
		} else if (isVanillaGrainy(block)) {
			return BlockCategory.GRAINY;
		} else if (isVanillaGreenery(block)) {
			return BlockCategory.GREENERY;
		} else if (isVanillaMetal(block)) {
			return BlockCategory.METAL;
		} else if (isVanillaStone(block)) {
			return BlockCategory.STONE;
		} else if (isVanillaWood(block)) {
			return BlockCategory.WOOD;
		}
		return BlockCategory.OTHERS;
	}

	/**
	 * Identifies whether the provided {@code block} is a vanilla block. This method checks if the
	 * block is one of the vanilla blocks defined in this class, such as dirt, glass, grainy,
	 * greenery, metal, stone, or wood.
	 *
	 * @param block Block in question
	 * @return boolean
	 */
	public static boolean isVanillaBlock(Block block) {
		return isVanillaDirt(block)
			|| isVanillaGlass(block)
			|| isVanillaGrainy(block)
			|| isVanillaGreenery(block)
			|| isVanillaMetal(block)
			|| isVanillaStone(block)
			|| isVanillaWood(block);
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

	// /////////////////// //
	// STATIC DECLARATIONS //
	// /////////////////// //

	static {
		// /////////// //
		// DIRT BLOCKS //
		// /////////// //
		VANILLA_DIRT = ImmutableList.of(
			// Dirt Blocks
			Blocks.COARSE_DIRT,
			Blocks.DIRT,
			Blocks.DIRT_PATH,
			Blocks.FARMLAND,
			Blocks.GRASS_BLOCK,
			Blocks.MUD,
			Blocks.MYCELIUM,
			Blocks.PODZOL,
			Blocks.ROOTED_DIRT
		);

		// Returns a copy of the vanilla dirt list, allowing for modifications.
		DIRT = List.copyOf(VANILLA_DIRT);

		// //////////// //
		// GLASS BLOCKS //
		// //////////// //
		VANILLA_GLASS = ImmutableList.of(
			// Normal Variants
			Blocks.GLASS,
			Blocks.GLASS_PANE,

			// Stained Variants
			Blocks.BLACK_STAINED_GLASS,
			Blocks.BLACK_STAINED_GLASS_PANE,
			Blocks.BLUE_STAINED_GLASS,
			Blocks.BLUE_STAINED_GLASS_PANE,
			Blocks.BROWN_STAINED_GLASS,
			Blocks.BROWN_STAINED_GLASS_PANE,
			Blocks.CYAN_STAINED_GLASS,
			Blocks.CYAN_STAINED_GLASS_PANE,
			Blocks.GRAY_STAINED_GLASS,
			Blocks.GRAY_STAINED_GLASS_PANE,
			Blocks.GREEN_STAINED_GLASS,
			Blocks.GREEN_STAINED_GLASS_PANE,
			Blocks.LIGHT_BLUE_STAINED_GLASS,
			Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
			Blocks.LIGHT_GRAY_STAINED_GLASS,
			Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
			Blocks.LIME_STAINED_GLASS,
			Blocks.LIME_STAINED_GLASS_PANE,
			Blocks.MAGENTA_STAINED_GLASS,
			Blocks.MAGENTA_STAINED_GLASS_PANE,
			Blocks.ORANGE_STAINED_GLASS,
			Blocks.ORANGE_STAINED_GLASS_PANE,
			Blocks.PINK_STAINED_GLASS,
			Blocks.PINK_STAINED_GLASS_PANE,
			Blocks.PURPLE_STAINED_GLASS,
			Blocks.PURPLE_STAINED_GLASS_PANE,
			Blocks.RED_STAINED_GLASS,
			Blocks.RED_STAINED_GLASS_PANE,
			Blocks.WHITE_STAINED_GLASS,
			Blocks.WHITE_STAINED_GLASS_PANE,
			Blocks.YELLOW_STAINED_GLASS,
			Blocks.YELLOW_STAINED_GLASS_PANE,

			// Tinted Variants
			Blocks.TINTED_GLASS,

			// Light Sources
			Blocks.GLOWSTONE,
			Blocks.REDSTONE_LAMP,
			Blocks.SEA_LANTERN
		);

		// Returns a copy of the vanilla glass list, allowing for modifications.
		GLASS = List.copyOf(VANILLA_GLASS);

		// ///////////// //
		// GRAINY BLOCKS //
		// ///////////// //
		VANILLA_GRAINY = ImmutableList.of(
			// Gravel and Sand Blocks
			Blocks.GRAVEL,
			Blocks.RED_SAND,
			Blocks.SAND,
			Blocks.SOUL_SAND,
			Blocks.SOUL_SOIL,
			Blocks.SUSPICIOUS_GRAVEL,
			Blocks.SUSPICIOUS_SAND,

			// Concrete Powder Blocks
			Blocks.BLACK_CONCRETE_POWDER,
			Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.GREEN_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER,
			Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.LIME_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.PINK_CONCRETE_POWDER,
			Blocks.PURPLE_CONCRETE_POWDER,
			Blocks.RED_CONCRETE_POWDER,
			Blocks.WHITE_CONCRETE_POWDER,
			Blocks.YELLOW_CONCRETE_POWDER
		);

		// Returns a copy of the vanilla grainy list, allowing for modifications.
		GRAINY = List.copyOf(VANILLA_GRAINY);

		// /////////////// //
		// GREENERY BLOCKS //
		// /////////////// //
		VANILLA_GREENERY = ImmutableList.of(
			Blocks.ACACIA_LEAVES,
			Blocks.ACACIA_SAPLING,
			Blocks.ALLIUM,
			Blocks.ATTACHED_MELON_STEM,
			Blocks.AZALEA_LEAVES,
			Blocks.AZURE_BLUET,
			Blocks.BEETROOTS,
			Blocks.BIG_DRIPLEAF,
			Blocks.BIG_DRIPLEAF_STEM,
			Blocks.BIRCH_LEAVES,
			Blocks.BIRCH_SAPLING,
			Blocks.BLUE_ORCHID,
			Blocks.CACTUS,
			Blocks.CARROTS,
			Blocks.CARVED_PUMPKIN,
			Blocks.CAVE_VINES,
			Blocks.CAVE_VINES_PLANT,
			Blocks.CHERRY_LEAVES,
			Blocks.CHERRY_SAPLING,
			Blocks.CHORUS_FLOWER,
			Blocks.CHORUS_PLANT,
			Blocks.CLOSED_EYEBLOSSOM,
			Blocks.COCOA,
			Blocks.CORNFLOWER,
			Blocks.DANDELION,
			Blocks.DARK_OAK_LEAVES,
			Blocks.DARK_OAK_SAPLING,
			Blocks.DEAD_BUSH,
			Blocks.FERN,
			Blocks.FLOWERING_AZALEA_LEAVES,
			Blocks.GLOW_LICHEN,
			Blocks.HANGING_ROOTS,
			Blocks.JUNGLE_LEAVES,
			Blocks.JUNGLE_SAPLING,
			Blocks.KELP,
			Blocks.LARGE_FERN,
			Blocks.LILAC,
			Blocks.LILY_OF_THE_VALLEY,
			Blocks.LILY_PAD,
			Blocks.MANGROVE_LEAVES,
			Blocks.MANGROVE_PROPAGULE,
			Blocks.MANGROVE_ROOTS,
			Blocks.MELON,
			Blocks.MELON_STEM,
			Blocks.MOSS_BLOCK,
			Blocks.MOSS_CARPET,
			Blocks.MUDDY_MANGROVE_ROOTS,
			Blocks.OAK_LEAVES,
			Blocks.OPEN_EYEBLOSSOM,
			Blocks.ORANGE_TULIP,
			Blocks.OXEYE_DAISY,
			Blocks.PALE_HANGING_MOSS,
			Blocks.PALE_MOSS_BLOCK,
			Blocks.PALE_MOSS_CARPET,
			Blocks.PALE_OAK_LEAVES,
			Blocks.PEONY,
			Blocks.PINK_PETALS,
			Blocks.PINK_TULIP,
			Blocks.PITCHER_CROP,
			Blocks.PITCHER_PLANT,
			Blocks.POTATOES,
			Blocks.PUMPKIN,
			Blocks.PUMPKIN_STEM,
			Blocks.RED_TULIP,
			Blocks.ROSE_BUSH,
			Blocks.SEAGRASS,
			Blocks.SHORT_GRASS,
			Blocks.SMALL_DRIPLEAF,
			Blocks.SPORE_BLOSSOM,
			Blocks.SPRUCE_LEAVES,
			Blocks.SUGAR_CANE,
			Blocks.SUNFLOWER,
			Blocks.SWEET_BERRY_BUSH,
			Blocks.TALL_GRASS,
			Blocks.TALL_SEAGRASS,
			Blocks.TORCHFLOWER,
			Blocks.TORCHFLOWER_CROP,
			Blocks.TWISTING_VINES,
			Blocks.TWISTING_VINES_PLANT,
			Blocks.VINE,
			Blocks.WEEPING_VINES,
			Blocks.WEEPING_VINES_PLANT,
			Blocks.WHEAT,
			Blocks.WITHER_ROSE
		);

		// Returns a copy of the vanilla greenery list, allowing for modifications.
		GREENERY = List.copyOf(VANILLA_GREENERY);

		// //////////// //
		// METAL BLOCKS //
		// //////////// //
		VANILLA_METAL = ImmutableList.of(
			// Copper Blocks
			Blocks.COPPER_BLOCK,
			Blocks.EXPOSED_COPPER,
			Blocks.OXIDIZED_COPPER,
			Blocks.WAXED_COPPER_BLOCK,
			Blocks.WAXED_EXPOSED_COPPER,
			Blocks.WAXED_OXIDIZED_COPPER,
			Blocks.WAXED_WEATHERED_COPPER,
			Blocks.WEATHERED_COPPER,

			Blocks.RAW_COPPER_BLOCK,

			Blocks.CHISELED_COPPER,
			Blocks.EXPOSED_CHISELED_COPPER,
			Blocks.OXIDIZED_CHISELED_COPPER,
			Blocks.WAXED_CHISELED_COPPER,
			Blocks.WAXED_EXPOSED_CHISELED_COPPER,
			Blocks.WAXED_OXIDIZED_CHISELED_COPPER,
			Blocks.WAXED_WEATHERED_CHISELED_COPPER,
			Blocks.WEATHERED_CHISELED_COPPER,

			Blocks.COPPER_GRATE,
			Blocks.EXPOSED_COPPER_GRATE,
			Blocks.OXIDIZED_COPPER_GRATE,
			Blocks.WAXED_COPPER_GRATE,
			Blocks.WAXED_EXPOSED_COPPER_GRATE,
			Blocks.WAXED_OXIDIZED_COPPER_GRATE,
			Blocks.WAXED_WEATHERED_COPPER_GRATE,
			Blocks.WEATHERED_COPPER_GRATE,

			Blocks.CUT_COPPER,
			Blocks.EXPOSED_CUT_COPPER,
			Blocks.OXIDIZED_CUT_COPPER,
			Blocks.WEATHERED_CUT_COPPER,
			Blocks.WAXED_CUT_COPPER,
			Blocks.WAXED_EXPOSED_CUT_COPPER,
			Blocks.WAXED_OXIDIZED_CUT_COPPER,
			Blocks.WAXED_WEATHERED_CUT_COPPER,

			Blocks.CUT_COPPER_SLAB,
			Blocks.EXPOSED_CUT_COPPER_SLAB,
			Blocks.OXIDIZED_CUT_COPPER_SLAB,
			Blocks.WEATHERED_CUT_COPPER_SLAB,
			Blocks.WAXED_CUT_COPPER_SLAB,
			Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
			Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
			Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,

			Blocks.CUT_COPPER_STAIRS,
			Blocks.EXPOSED_CUT_COPPER_STAIRS,
			Blocks.OXIDIZED_CUT_COPPER_STAIRS,
			Blocks.WEATHERED_CUT_COPPER_STAIRS,
			Blocks.WAXED_CUT_COPPER_STAIRS,
			Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
			Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
			Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,

			Blocks.COPPER_BULB,
			Blocks.EXPOSED_COPPER_BULB,
			Blocks.OXIDIZED_COPPER_BULB,
			Blocks.WEATHERED_COPPER_BULB,
			Blocks.WAXED_COPPER_BULB,
			Blocks.WAXED_EXPOSED_COPPER_BULB,
			Blocks.WAXED_OXIDIZED_COPPER_BULB,
			Blocks.WAXED_WEATHERED_COPPER_BULB,

			Blocks.COPPER_DOOR,
			Blocks.EXPOSED_COPPER_DOOR,
			Blocks.OXIDIZED_COPPER_DOOR,
			Blocks.WEATHERED_COPPER_DOOR,
			Blocks.WAXED_COPPER_DOOR,
			Blocks.WAXED_EXPOSED_COPPER_DOOR,
			Blocks.WAXED_OXIDIZED_COPPER_DOOR,
			Blocks.WAXED_WEATHERED_COPPER_DOOR,

			Blocks.COPPER_TRAPDOOR,
			Blocks.EXPOSED_COPPER_TRAPDOOR,
			Blocks.OXIDIZED_COPPER_TRAPDOOR,
			Blocks.WAXED_COPPER_TRAPDOOR,
			Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR,
			Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR,
			Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR,
			Blocks.WEATHERED_COPPER_TRAPDOOR,

			// Iron Blocks
			Blocks.ACTIVATOR_RAIL,
			Blocks.ANVIL,
			Blocks.CAULDRON,
			Blocks.CHAIN,
			Blocks.CHIPPED_ANVIL,
			Blocks.DAMAGED_ANVIL,
			Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
			Blocks.HOPPER,
			Blocks.IRON_BARS,
			Blocks.IRON_BLOCK,
			Blocks.IRON_DOOR,
			Blocks.IRON_TRAPDOOR,
			Blocks.LAVA_CAULDRON,
			Blocks.RAIL,
			Blocks.RAW_IRON_BLOCK,
			Blocks.WATER_CAULDRON,

			// Gold Blocks
			Blocks.BELL,
			Blocks.DETECTOR_RAIL,
			Blocks.GOLD_BLOCK,
			Blocks.GOLD_ORE,
			Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
			Blocks.POWERED_RAIL,
			Blocks.RAW_GOLD_BLOCK,

			// Netherite Blocks
			Blocks.NETHERITE_BLOCK,

			// Other Metal Blocks
			Blocks.CAULDRON,
			Blocks.HEAVY_CORE,
			Blocks.LANTERN,
			Blocks.LAVA_CAULDRON,
			Blocks.LIGHTNING_ROD,
			Blocks.POWDER_SNOW_CAULDRON,
			Blocks.SOUL_LANTERN,
			Blocks.SMITHING_TABLE,
			Blocks.SPAWNER,
			Blocks.TRIAL_SPAWNER,
			Blocks.VAULT,
			Blocks.WATER_CAULDRON
		);

		// Returns a copy of the vanilla metal list, allowing for modifications.
		METAL = List.copyOf(VANILLA_METAL);

		// //////////// //
		// STONE BLOCKS //
		// //////////// //
		VANILLA_STONE = ImmutableList.of(
			// Andesite Blocks
			Blocks.ANDESITE,
			Blocks.ANDESITE_SLAB,
			Blocks.ANDESITE_STAIRS,
			Blocks.ANDESITE_WALL,
			Blocks.POLISHED_ANDESITE,
			Blocks.POLISHED_ANDESITE_SLAB,
			Blocks.POLISHED_ANDESITE_STAIRS,

			// Basalt Blocks
			Blocks.BASALT,
			Blocks.POLISHED_BASALT,
			Blocks.SMOOTH_BASALT,

			// Blackstone Blocks
			Blocks.BLACKSTONE,
			Blocks.BLACKSTONE_SLAB,
			Blocks.BLACKSTONE_STAIRS,
			Blocks.BLACKSTONE_WALL,
			Blocks.CHISELED_POLISHED_BLACKSTONE,
			Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
			Blocks.GILDED_BLACKSTONE,
			Blocks.POLISHED_BLACKSTONE,
			Blocks.POLISHED_BLACKSTONE_BRICKS,
			Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
			Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
			Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS,
			Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS,
			Blocks.POLISHED_BLACKSTONE_BRICK_WALL,
			Blocks.POLISHED_BLACKSTONE_BRICK_WALL,
			Blocks.POLISHED_BLACKSTONE_BUTTON,
			Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE,
			Blocks.POLISHED_BLACKSTONE_SLAB,
			Blocks.POLISHED_BLACKSTONE_STAIRS,
			Blocks.POLISHED_BLACKSTONE_WALL,

			// Bricks Blocks
			Blocks.BRICKS,
			Blocks.BRICK_SLAB,
			Blocks.BRICK_STAIRS,
			Blocks.BRICK_WALL,

			// Cobblestone Blocks
			Blocks.COBBLESTONE,
			Blocks.COBBLESTONE_SLAB,
			Blocks.COBBLESTONE_STAIRS,
			Blocks.COBBLESTONE_WALL,
			Blocks.INFESTED_COBBLESTONE,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.MOSSY_COBBLESTONE_SLAB,
			Blocks.MOSSY_COBBLESTONE_STAIRS,
			Blocks.MOSSY_COBBLESTONE_WALL,

			// Concrete Blocks
			Blocks.BLACK_CONCRETE,
			Blocks.BLUE_CONCRETE,
			Blocks.BROWN_CONCRETE,
			Blocks.CYAN_CONCRETE,
			Blocks.GRAY_CONCRETE,
			Blocks.GREEN_CONCRETE,
			Blocks.LIGHT_BLUE_CONCRETE,
			Blocks.LIGHT_GRAY_CONCRETE,
			Blocks.LIME_CONCRETE,
			Blocks.MAGENTA_CONCRETE,
			Blocks.ORANGE_CONCRETE,
			Blocks.PINK_CONCRETE,
			Blocks.PURPLE_CONCRETE,
			Blocks.RED_CONCRETE,
			Blocks.WHITE_CONCRETE,
			Blocks.YELLOW_CONCRETE,

			// Deepslate Blocks
			Blocks.CHISELED_DEEPSLATE,
			Blocks.COBBLED_DEEPSLATE,
			Blocks.COBBLED_DEEPSLATE_SLAB,
			Blocks.COBBLED_DEEPSLATE_STAIRS,
			Blocks.COBBLED_DEEPSLATE_WALL,
			Blocks.CRACKED_DEEPSLATE_BRICKS,
			Blocks.CRACKED_DEEPSLATE_TILES,
			Blocks.DEEPSLATE,
			Blocks.DEEPSLATE_BRICKS,
			Blocks.DEEPSLATE_BRICK_SLAB,
			Blocks.DEEPSLATE_BRICK_STAIRS,
			Blocks.DEEPSLATE_BRICK_WALL,
			Blocks.DEEPSLATE_TILES,
			Blocks.DEEPSLATE_TILE_SLAB,
			Blocks.DEEPSLATE_TILE_STAIRS,
			Blocks.DEEPSLATE_TILE_WALL,
			Blocks.INFESTED_DEEPSLATE,
			Blocks.POLISHED_DEEPSLATE,
			Blocks.POLISHED_DEEPSLATE_SLAB,
			Blocks.POLISHED_DEEPSLATE_STAIRS,
			Blocks.POLISHED_DEEPSLATE_WALL,
			Blocks.REINFORCED_DEEPSLATE,

			// Diorite Blocks
			Blocks.DIORITE,
			Blocks.DIORITE_SLAB,
			Blocks.DIORITE_STAIRS,
			Blocks.DIORITE_WALL,
			Blocks.POLISHED_DIORITE,
			Blocks.POLISHED_DIORITE_SLAB,
			Blocks.POLISHED_DIORITE_STAIRS,

			// Dripstone Blocks
			Blocks.DRIPSTONE_BLOCK,
			Blocks.POINTED_DRIPSTONE,

			// Endstone Blocks
			Blocks.END_STONE,
			Blocks.END_STONE_BRICKS,
			Blocks.END_STONE_BRICK_SLAB,
			Blocks.END_STONE_BRICK_STAIRS,
			Blocks.END_STONE_BRICK_WALL,

			// Glazed Terracotta Blocks
			Blocks.BLACK_GLAZED_TERRACOTTA,
			Blocks.BLUE_GLAZED_TERRACOTTA,
			Blocks.BROWN_GLAZED_TERRACOTTA,
			Blocks.CYAN_GLAZED_TERRACOTTA,
			Blocks.GRAY_GLAZED_TERRACOTTA,
			Blocks.GREEN_GLAZED_TERRACOTTA,
			Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
			Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
			Blocks.LIME_GLAZED_TERRACOTTA,
			Blocks.MAGENTA_GLAZED_TERRACOTTA,
			Blocks.ORANGE_GLAZED_TERRACOTTA,
			Blocks.PINK_GLAZED_TERRACOTTA,
			Blocks.PURPLE_GLAZED_TERRACOTTA,
			Blocks.RED_GLAZED_TERRACOTTA,
			Blocks.WHITE_GLAZED_TERRACOTTA,
			Blocks.YELLOW_GLAZED_TERRACOTTA,

			// Granite Blocks
			Blocks.GRANITE,
			Blocks.GRANITE_SLAB,
			Blocks.GRANITE_STAIRS,
			Blocks.GRANITE_WALL,
			Blocks.POLISHED_GRANITE,
			Blocks.POLISHED_GRANITE_SLAB,
			Blocks.POLISHED_GRANITE_STAIRS,

			// Nether Stuff
			Blocks.CHISELED_NETHER_BRICKS,
			Blocks.CRACKED_NETHER_BRICKS,
			Blocks.CRYING_OBSIDIAN,
			Blocks.NETHERRACK,
			Blocks.NETHER_BRICKS,
			Blocks.NETHER_BRICK_FENCE,
			Blocks.NETHER_BRICK_SLAB,
			Blocks.NETHER_BRICK_STAIRS,
			Blocks.NETHER_BRICK_WALL,
			Blocks.NETHER_WART_BLOCK,
			Blocks.OBSIDIAN,
			Blocks.RED_NETHER_BRICKS,
			Blocks.RED_NETHER_BRICK_SLAB,
			Blocks.RED_NETHER_BRICK_STAIRS,
			Blocks.RED_NETHER_BRICK_WALL,

			// Ore Blocks
			Blocks.ANCIENT_DEBRIS,
			Blocks.DEEPSLATE_COAL_ORE,
			Blocks.DEEPSLATE_COPPER_ORE,
			Blocks.DEEPSLATE_DIAMOND_ORE,
			Blocks.DEEPSLATE_EMERALD_ORE,
			Blocks.DEEPSLATE_GOLD_ORE,
			Blocks.DEEPSLATE_IRON_ORE,
			Blocks.DEEPSLATE_LAPIS_ORE,
			Blocks.DEEPSLATE_REDSTONE_ORE,
			Blocks.COAL_BLOCK,
			Blocks.COAL_ORE,
			Blocks.COPPER_ORE,
			Blocks.DIAMOND_ORE,
			Blocks.DIAMOND_BLOCK,
			Blocks.EMERALD_ORE,
			Blocks.EMERALD_BLOCK,
			Blocks.GOLD_ORE,
			Blocks.IRON_ORE,
			Blocks.LAPIS_ORE,
			Blocks.LAPIS_BLOCK,
			Blocks.NETHER_GOLD_ORE,
			Blocks.NETHER_QUARTZ_ORE,
			Blocks.REDSTONE_ORE,

			// Prismarine Blocks
			Blocks.DARK_PRISMARINE,
			Blocks.DARK_PRISMARINE_SLAB,
			Blocks.DARK_PRISMARINE_STAIRS,
			Blocks.PRISMARINE,
			Blocks.PRISMARINE_BRICKS,
			Blocks.PRISMARINE_BRICK_SLAB,
			Blocks.PRISMARINE_BRICK_STAIRS,
			Blocks.PRISMARINE_SLAB,
			Blocks.PRISMARINE_STAIRS,
			Blocks.PRISMARINE_WALL,

			// Purpur Blocks
			Blocks.PURPUR_BLOCK,
			Blocks.PURPUR_PILLAR,
			Blocks.PURPUR_SLAB,
			Blocks.PURPUR_STAIRS,

			// Sandstone Blocks
			Blocks.CHISELED_SANDSTONE,
			Blocks.CUT_SANDSTONE,
			Blocks.CUT_SANDSTONE_SLAB,
			Blocks.SANDSTONE,
			Blocks.SANDSTONE_SLAB,
			Blocks.SANDSTONE_STAIRS,
			Blocks.SMOOTH_SANDSTONE,
			Blocks.SMOOTH_SANDSTONE_SLAB,
			Blocks.SMOOTH_SANDSTONE_STAIRS,

			// Stone Blocks
			Blocks.CHISELED_STONE_BRICKS,
			Blocks.CRACKED_STONE_BRICKS,
			Blocks.INFESTED_CHISELED_STONE_BRICKS,
			Blocks.INFESTED_CRACKED_STONE_BRICKS,
			Blocks.INFESTED_MOSSY_STONE_BRICKS,
			Blocks.INFESTED_STONE,
			Blocks.INFESTED_STONE_BRICKS,
			Blocks.MOSSY_STONE_BRICKS,
			Blocks.MOSSY_STONE_BRICK_SLAB,
			Blocks.MOSSY_STONE_BRICK_STAIRS,
			Blocks.MOSSY_STONE_BRICK_WALL,
			Blocks.SMOOTH_STONE,
			Blocks.SMOOTH_STONE_SLAB,
			Blocks.STONE,
			Blocks.STONE_BRICKS,
			Blocks.STONE_BRICK_SLAB,
			Blocks.STONE_BRICK_STAIRS,
			Blocks.STONE_BRICK_WALL,
			Blocks.STONE_BUTTON,
			Blocks.STONE_PRESSURE_PLATE,
			Blocks.STONE_SLAB,
			Blocks.STONE_STAIRS,

			// Terracotta Blocks
			Blocks.BLACK_TERRACOTTA,
			Blocks.BLUE_TERRACOTTA,
			Blocks.BROWN_TERRACOTTA,
			Blocks.CYAN_TERRACOTTA,
			Blocks.GRAY_TERRACOTTA,
			Blocks.GREEN_TERRACOTTA,
			Blocks.LIGHT_BLUE_TERRACOTTA,
			Blocks.LIGHT_GRAY_TERRACOTTA,
			Blocks.LIME_TERRACOTTA,
			Blocks.MAGENTA_TERRACOTTA,
			Blocks.ORANGE_TERRACOTTA,
			Blocks.PINK_TERRACOTTA,
			Blocks.PURPLE_TERRACOTTA,
			Blocks.RED_TERRACOTTA,
			Blocks.WHITE_TERRACOTTA,
			Blocks.YELLOW_TERRACOTTA,

			// Tuff Blocks
			Blocks.CHISELED_TUFF,
			Blocks.CHISELED_TUFF_BRICKS,
			Blocks.POLISHED_TUFF,
			Blocks.POLISHED_TUFF_SLAB,
			Blocks.POLISHED_TUFF_STAIRS,
			Blocks.POLISHED_TUFF_WALL,
			Blocks.TUFF,
			Blocks.TUFF_BRICKS,
			Blocks.TUFF_BRICK_SLAB,
			Blocks.TUFF_BRICK_STAIRS,
			Blocks.TUFF_BRICK_WALL,
			Blocks.TUFF_SLAB,
			Blocks.TUFF_STAIRS,
			Blocks.TUFF_WALL,

			// Other Stone Blocks
			Blocks.BASALT,
			Blocks.CALCITE,
			Blocks.MAGMA_BLOCK,
			Blocks.OBSIDIAN,
			Blocks.POLISHED_BASALT,
			Blocks.QUARTZ_BLOCK,
			Blocks.QUARTZ_BRICKS,
			Blocks.QUARTZ_PILLAR,
			Blocks.QUARTZ_SLAB,
			Blocks.QUARTZ_STAIRS,
			Blocks.SMOOTH_BASALT,
			Blocks.SMOOTH_QUARTZ,
			Blocks.SMOOTH_QUARTZ_SLAB,
			Blocks.SMOOTH_QUARTZ_STAIRS,

			// Interactable Blocks
			Blocks.BLAST_FURNACE,
			Blocks.CHAIN_COMMAND_BLOCK,
			Blocks.COMMAND_BLOCK,
			Blocks.COMPARATOR,
			Blocks.DISPENSER,
			Blocks.DROPPER,
			Blocks.ENCHANTING_TABLE,
			Blocks.ENDER_CHEST,
			Blocks.END_PORTAL_FRAME,
			Blocks.FURNACE,
			Blocks.GRINDSTONE,
			Blocks.LODESTONE,
			Blocks.OBSERVER,
			Blocks.PISTON,
			Blocks.REPEATER,
			Blocks.REPEATING_COMMAND_BLOCK,
			Blocks.RESPAWN_ANCHOR,
			Blocks.SMOKER,
			Blocks.STICKY_PISTON,
			Blocks.STONECUTTER
		);

		// Returns a copy of the vanilla stone list, allowing for modifications.
		STONE = List.copyOf(VANILLA_STONE);

		// /////////// //
		// WOOD BLOCKS //
		// /////////// //
		VANILLA_WOOD = ImmutableList.of(
			// Acacia Blocks
			Blocks.ACACIA_BUTTON,
			Blocks.ACACIA_DOOR,
			Blocks.ACACIA_FENCE,
			Blocks.ACACIA_FENCE_GATE,
			Blocks.ACACIA_HANGING_SIGN,
			Blocks.ACACIA_LOG,
			Blocks.ACACIA_PLANKS,
			Blocks.ACACIA_PRESSURE_PLATE,
			Blocks.ACACIA_SLAB,
			Blocks.ACACIA_STAIRS,
			Blocks.ACACIA_TRAPDOOR,
			Blocks.ACACIA_WOOD,
			Blocks.STRIPPED_ACACIA_LOG,
			Blocks.STRIPPED_ACACIA_WOOD,

			// Azalea Blocks
			Blocks.AZALEA,
			Blocks.FLOWERING_AZALEA,

			// Bamboo Blocks
			Blocks.BAMBOO,
			Blocks.BAMBOO_BUTTON,
			Blocks.BAMBOO_DOOR,
			Blocks.BAMBOO_FENCE,
			Blocks.BAMBOO_FENCE_GATE,
			Blocks.BAMBOO_MOSAIC,
			Blocks.BAMBOO_MOSAIC_SLAB,
			Blocks.BAMBOO_MOSAIC_STAIRS,
			Blocks.BAMBOO_PLANKS,
			Blocks.BAMBOO_PRESSURE_PLATE,
			Blocks.BAMBOO_SLAB,
			Blocks.BAMBOO_STAIRS,
			Blocks.BAMBOO_TRAPDOOR,
			Blocks.BAMBOO_BLOCK,

			// Birch Blocks
			Blocks.BIRCH_BUTTON,
			Blocks.BIRCH_DOOR,
			Blocks.BIRCH_FENCE,
			Blocks.BIRCH_FENCE_GATE,
			Blocks.BIRCH_HANGING_SIGN,
			Blocks.BIRCH_LOG,
			Blocks.BIRCH_PLANKS,
			Blocks.BIRCH_PRESSURE_PLATE,
			Blocks.BIRCH_SLAB,
			Blocks.BIRCH_STAIRS,
			Blocks.BIRCH_TRAPDOOR,
			Blocks.BIRCH_WOOD,
			Blocks.STRIPPED_BIRCH_LOG,
			Blocks.STRIPPED_BIRCH_WOOD,

			// Cherry Blocks
			Blocks.CHERRY_BUTTON,
			Blocks.CHERRY_DOOR,
			Blocks.CHERRY_FENCE,
			Blocks.CHERRY_FENCE_GATE,
			Blocks.CHERRY_HANGING_SIGN,
			Blocks.CHERRY_LOG,
			Blocks.CHERRY_PLANKS,
			Blocks.CHERRY_PRESSURE_PLATE,
			Blocks.CHERRY_SLAB,
			Blocks.CHERRY_STAIRS,
			Blocks.CHERRY_TRAPDOOR,
			Blocks.CHERRY_WOOD,
			Blocks.STRIPPED_CHERRY_LOG,
			Blocks.STRIPPED_CHERRY_WOOD,

			// Crimson Blocks
			Blocks.CRIMSON_BUTTON,
			Blocks.CRIMSON_DOOR,
			Blocks.CRIMSON_FENCE,
			Blocks.CRIMSON_FENCE_GATE,
			Blocks.CRIMSON_HYPHAE,
			Blocks.CRIMSON_PLANKS,
			Blocks.CRIMSON_PRESSURE_PLATE,
			Blocks.CRIMSON_SLAB,
			Blocks.CRIMSON_STAIRS,
			Blocks.CRIMSON_STEM,
			Blocks.CRIMSON_TRAPDOOR,
			Blocks.STRIPPED_CRIMSON_HYPHAE,
			Blocks.STRIPPED_CRIMSON_STEM,

			// Dark Oak Blocks
			Blocks.DARK_OAK_BUTTON,
			Blocks.DARK_OAK_DOOR,
			Blocks.DARK_OAK_FENCE,
			Blocks.DARK_OAK_FENCE_GATE,
			Blocks.DARK_OAK_HANGING_SIGN,
			Blocks.DARK_OAK_LOG,
			Blocks.DARK_OAK_PLANKS,
			Blocks.DARK_OAK_PRESSURE_PLATE,
			Blocks.DARK_OAK_SLAB,
			Blocks.DARK_OAK_STAIRS,
			Blocks.DARK_OAK_TRAPDOOR,
			Blocks.DARK_OAK_WOOD,
			Blocks.STRIPPED_DARK_OAK_LOG,
			Blocks.STRIPPED_DARK_OAK_WOOD,

			// Jungle Blocks
			Blocks.JUNGLE_BUTTON,
			Blocks.JUNGLE_DOOR,
			Blocks.JUNGLE_FENCE,
			Blocks.JUNGLE_FENCE_GATE,
			Blocks.JUNGLE_HANGING_SIGN,
			Blocks.JUNGLE_LOG,
			Blocks.JUNGLE_PLANKS,
			Blocks.JUNGLE_PRESSURE_PLATE,
			Blocks.JUNGLE_SLAB,
			Blocks.JUNGLE_STAIRS,
			Blocks.JUNGLE_TRAPDOOR,
			Blocks.JUNGLE_WOOD,
			Blocks.STRIPPED_JUNGLE_LOG,
			Blocks.STRIPPED_JUNGLE_WOOD,

			// Mangrove Blocks
			Blocks.MANGROVE_BUTTON,
			Blocks.MANGROVE_DOOR,
			Blocks.MANGROVE_FENCE,
			Blocks.MANGROVE_FENCE_GATE,
			Blocks.MANGROVE_HANGING_SIGN,
			Blocks.MANGROVE_LOG,
			Blocks.MANGROVE_PLANKS,
			Blocks.MANGROVE_PRESSURE_PLATE,
			Blocks.MANGROVE_SLAB,
			Blocks.MANGROVE_STAIRS,
			Blocks.MANGROVE_TRAPDOOR,
			Blocks.MANGROVE_WOOD,
			Blocks.STRIPPED_MANGROVE_LOG,
			Blocks.STRIPPED_MANGROVE_WOOD,

			// Oak Blocks
			Blocks.OAK_BUTTON,
			Blocks.OAK_DOOR,
			Blocks.OAK_FENCE,
			Blocks.OAK_FENCE_GATE,
			Blocks.OAK_HANGING_SIGN,
			Blocks.OAK_LOG,
			Blocks.OAK_PLANKS,
			Blocks.OAK_PRESSURE_PLATE,
			Blocks.OAK_SLAB,
			Blocks.OAK_STAIRS,
			Blocks.OAK_TRAPDOOR,
			Blocks.OAK_WOOD,
			Blocks.PETRIFIED_OAK_SLAB,
			Blocks.STRIPPED_OAK_LOG,
			Blocks.STRIPPED_OAK_WOOD,

			// Pale Oak Blocks
			Blocks.PALE_OAK_BUTTON,
			Blocks.PALE_OAK_DOOR,
			Blocks.PALE_OAK_FENCE,
			Blocks.PALE_OAK_FENCE_GATE,
			Blocks.PALE_OAK_HANGING_SIGN,
			Blocks.PALE_OAK_LOG,
			Blocks.PALE_OAK_PLANKS,
			Blocks.PALE_OAK_PRESSURE_PLATE,
			Blocks.PALE_OAK_SLAB,
			Blocks.PALE_OAK_STAIRS,
			Blocks.PALE_OAK_TRAPDOOR,
			Blocks.PALE_OAK_WOOD,
			Blocks.STRIPPED_PALE_OAK_LOG,
			Blocks.STRIPPED_PALE_OAK_WOOD,

			// Warped Blocks
			Blocks.WARPED_BUTTON,
			Blocks.WARPED_DOOR,
			Blocks.WARPED_FENCE,
			Blocks.WARPED_FENCE_GATE,
			Blocks.WARPED_HYPHAE,
			Blocks.WARPED_PLANKS,
			Blocks.WARPED_PRESSURE_PLATE,
			Blocks.WARPED_SLAB,
			Blocks.WARPED_STAIRS,
			Blocks.WARPED_STEM,
			Blocks.WARPED_TRAPDOOR,
			Blocks.STRIPPED_WARPED_HYPHAE,
			Blocks.STRIPPED_WARPED_STEM,
			Blocks.WARPED_WART_BLOCK,

			// Other Wood Blocks
			Blocks.BEEHIVE,
			Blocks.BEE_NEST,
			Blocks.BOOKSHELF,
			Blocks.SCAFFOLDING,

			// Interactive Blocks
			Blocks.BARREL,
			Blocks.CAMPFIRE,
			Blocks.DAYLIGHT_DETECTOR,
			Blocks.FLETCHING_TABLE,
			Blocks.JUKEBOX,
			Blocks.LADDER,
			Blocks.LOOM,
			Blocks.NOTE_BLOCK,
			Blocks.SOUL_CAMPFIRE
		);

		// Returns a copy of the vanilla wood list, allowing for modifications.
		WOOD = List.copyOf(VANILLA_WOOD);
	}
}
