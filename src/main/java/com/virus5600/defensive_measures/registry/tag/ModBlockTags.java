package com.virus5600.defensive_measures.registry.tag;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class ModBlockTags {
	public static final TagKey<Block> DIRT = createTag("dirt");
	public static final TagKey<Block> GLASS = createTag("glass");
	public static final TagKey<Block> GRAINY = createTag("grainy");
	public static final TagKey<Block> GREENERY = createTag("greenery");
	public static final TagKey<Block> METAL = createTag("metal");
	public static final TagKey<Block> STONE = createTag("stone");
	public static final TagKey<Block> TRAPS = createTag("traps");
	public static final TagKey<Block> WOOD = createTag("wood");


	private static TagKey<Block> createTag(String name) {
		return TagKey.of(
			RegistryKeys.BLOCK,
			Identifier.of(DefensiveMeasures.MOD_ID, name)
		);
	}
}
