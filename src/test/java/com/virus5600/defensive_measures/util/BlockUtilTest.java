package com.virus5600.defensive_measures.util;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;

import net.minecraft.block.Blocks;
import org.junit.jupiter.api.*;

import java.util.List;

public class BlockUtilTest {

	@BeforeAll
	static void beforeAll() {
		System.out.println("Running BlockUtilTest...");

		SharedConstants.createGameVersion();
		Bootstrap.initialize();
	}

	@Test
	void testVanillaDirtList() {
		List.of(
			Blocks.DIRT,
			Blocks.COARSE_DIRT,
			Blocks.PODZOL,
			Blocks.MYCELIUM,
			Blocks.MUD
		).forEach((block) -> Assertions.assertTrue(
			BlockUtil.VANILLA_DIRT.contains(block),
			"Block " + block + " should be in the vanilla dirt list"
		));

		List.of(
			Blocks.CAMPFIRE,
			Blocks.GRAVEL,
			Blocks.SAND,
			Blocks.STONE
		).forEach((block) -> Assertions.assertFalse(
			BlockUtil.VANILLA_DIRT.contains(block),
			"Block " + block + " should not be in the vanilla dirt list"
		));
	}

	@Test
	void testModDirtList() {
		Assertions.assertIterableEquals(
			BlockUtil.VANILLA_DIRT,
			BlockUtil.DIRT,
			"Mod dirt list should be equal to vanilla dirt list"
		);
	}

	@Test
	void testVanillaGlassList() {
		List.of(
			Blocks.GLASS,
			Blocks.SEA_LANTERN,
			Blocks.TINTED_GLASS,
			Blocks.GRAY_STAINED_GLASS,
			Blocks.REDSTONE_LAMP
		).forEach((block) -> Assertions.assertTrue(
			BlockUtil.VANILLA_GLASS.contains(block),
			"Block " + block + " should be in the vanilla glass list"
		));

		List.of(
			Blocks.CAMPFIRE,
			Blocks.DIRT,
			Blocks.SAND,
			Blocks.STONE
		).forEach((block) -> Assertions.assertFalse(
			BlockUtil.VANILLA_GLASS.contains(block),
			"Block " + block + " should not be in the vanilla glass list"
		));
	}

	@Test
	void testModGlassList() {
		Assertions.assertIterableEquals(
			BlockUtil.VANILLA_GLASS,
			BlockUtil.GLASS,
			"Mod glass list should be equal to vanilla glass list"
		);
	}

	@Test
	void testVanillaGrainyList() {
		List.of(
			Blocks.SAND,
			Blocks.RED_SAND,
			Blocks.SUSPICIOUS_GRAVEL,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.SOUL_SOIL
		).forEach((block) -> Assertions.assertTrue(
			BlockUtil.VANILLA_GRAINY.contains(block),
			"Block " + block + " should be in the vanilla grainy list"
		));

		List.of(
			Blocks.CAMPFIRE,
			Blocks.DIRT,
			Blocks.SANDSTONE,
			Blocks.POWDER_SNOW
		).forEach((block) -> Assertions.assertFalse(
			BlockUtil.VANILLA_GRAINY.contains(block),
			"Block " + block + " should not be in the vanilla grainy list"
		));
	}

	@Test
	void testModGrainyList() {
		Assertions.assertIterableEquals(
			BlockUtil.VANILLA_GRAINY,
			BlockUtil.GRAINY,
			"Mod grainy list should be equal to vanilla grainy list"
		);
	}

	@Test
	void testVanillaGreeneryList() {
		List.of(
			Blocks.ALLIUM,
			Blocks.BIRCH_LEAVES,
			Blocks.CACTUS,
			Blocks.COCOA,
			Blocks.WITHER_ROSE
		).forEach((block) -> Assertions.assertTrue(
			BlockUtil.VANILLA_GREENERY.contains(block),
			"Block " + block + " should be in the vanilla greenery list"
		));

		List.of(
			Blocks.FARMLAND,
			Blocks.DIRT,
			Blocks.SAND,
			Blocks.BIRCH_FENCE
		).forEach((block) -> Assertions.assertFalse(
			BlockUtil.VANILLA_GREENERY.contains(block),
			"Block " + block + " should not be in the vanilla greenery list"
		));
	}

	@Test
	void testModGreeneryList() {
		Assertions.assertIterableEquals(
			BlockUtil.VANILLA_GREENERY,
			BlockUtil.GREENERY,
			"Mod greenery list should be equal to vanilla greenery list"
		);
	}

	@Test
	void testVanillaMetalList() {
		List.of(
			Blocks.COPPER_BLOCK,
			Blocks.RAW_IRON_BLOCK,
			Blocks.LAVA_CAULDRON,
			Blocks.BELL,
			Blocks.SPAWNER
		).forEach((block) -> Assertions.assertTrue(
			BlockUtil.VANILLA_METAL.contains(block),
			"Block " + block + " should be in the vanilla metal list"
		));

		List.of(
			Blocks.REDSTONE_WIRE,
			Blocks.ANDESITE,
			Blocks.DIAMOND_BLOCK,
			Blocks.RESPAWN_ANCHOR
		).forEach((block) -> Assertions.assertFalse(
			BlockUtil.VANILLA_METAL.contains(block),
			"Block " + block + " should not be in the vanilla metal list"
		));
	}

	@Test
	void testModMetalList() {
		Assertions.assertIterableEquals(
			BlockUtil.VANILLA_METAL,
			BlockUtil.METAL,
			"Mod metal list should be equal to vanilla metal list"
		);
	}

	@Test
	void testVanillaStoneList() {
		List.of(
			Blocks.CRAFTER,
			Blocks.ANDESITE,
			Blocks.INFESTED_CHISELED_STONE_BRICKS,
			Blocks.RESPAWN_ANCHOR,
			Blocks.EMERALD_BLOCK
		).forEach((block) -> Assertions.assertTrue(
			BlockUtil.VANILLA_STONE.contains(block),
			"Block " + block + " should be in the vanilla stone list"
		));

		List.of(
			Blocks.SMITHING_TABLE,
			Blocks.REDSTONE_WIRE,
			Blocks.GOLD_BLOCK,
			Blocks.ANVIL
		).forEach((block) -> Assertions.assertFalse(
			BlockUtil.VANILLA_STONE.contains(block),
			"Block " + block + " should not be in the vanilla stone list"
		));
	}

	@Test
	void testModStoneList() {
		Assertions.assertIterableEquals(
			BlockUtil.VANILLA_STONE,
			BlockUtil.STONE,
			"Mod stone list should be equal to vanilla stone list"
		);
	}

	@Test
	void testVanillaWoodList() {
		List.of(
			Blocks.CRAFTING_TABLE,
			Blocks.FLETCHING_TABLE,
			Blocks.BAMBOO_SIGN,
			Blocks.BIRCH_WOOD,
			Blocks.CREAKING_HEART
		).forEach((block) -> Assertions.assertTrue(
			BlockUtil.VANILLA_WOOD.contains(block),
			"Block " + block + " should be in the vanilla wood list"
		));

		List.of(
			Blocks.RESIN_BLOCK,
			Blocks.BREWING_STAND,
			Blocks.SMITHING_TABLE,
			Blocks.TNT
		).forEach((block) -> Assertions.assertFalse(
			BlockUtil.VANILLA_WOOD.contains(block),
			"Block " + block + " should not be in the vanilla wood list"
		));
	}

	@Test
	void testModWoodList() {
		Assertions.assertIterableEquals(
			BlockUtil.VANILLA_WOOD,
			BlockUtil.WOOD,
			"Mod wood list should be equal to vanilla wood list"
		);
	}
}
