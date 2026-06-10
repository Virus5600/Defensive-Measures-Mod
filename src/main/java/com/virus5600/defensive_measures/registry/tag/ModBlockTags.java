package com.virus5600.defensive_measures.registry.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.virus5600.defensive_measures._helper.RegistryHelper;


public class ModBlockTags {
	// Categories
	public static final TagKey<Block> DIRT = RegistryHelper.createBlockTagKey("dirt");
	public static final TagKey<Block> GLASS = RegistryHelper.createBlockTagKey("glass");
	public static final TagKey<Block> GRAINY = RegistryHelper.createBlockTagKey("grainy");
	public static final TagKey<Block> GREENERY = RegistryHelper.createBlockTagKey("greenery");
	public static final TagKey<Block> METAL = RegistryHelper.createBlockTagKey("metal");
	public static final TagKey<Block> STONE = RegistryHelper.createBlockTagKey("stone");
	public static final TagKey<Block> TRAPS = RegistryHelper.createBlockTagKey("traps");
	public static final TagKey<Block> WOOD = RegistryHelper.createBlockTagKey("wood");

	// Groups
	public static final TagKey<Block> ELECTRIC_FENCE = RegistryHelper.createBlockTagKey("electric_fence");
}
