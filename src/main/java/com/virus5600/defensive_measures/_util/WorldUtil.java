package com.virus5600.defensive_measures._util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.compress.utils.Lists;

/**
 * Contains all utility methods that can be used for the {@link net.minecraft.world.level.Level Level class}
 * such as identifying if it matches a superclass or interface. This is to supplement some
 * shortcomings of the primary vanilla {@code Level} class and create flexibility towards applying
 * features and modifications.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class WorldUtil {
	/**
	 * Scans a chunk for blocks that match a given filter and performs an action on each matching
	 * block position.
	 *
	 * @param level  The level to scan.
	 * @param area   The area to scan within the chunk.
	 * @param chunkX The X coordinate of the chunk to scan.
	 * @param chunkZ The Z coordinate of the chunk to scan.
	 * @param action The action to perform on each matching block position.
	 * @param filter The filter to apply to each block state.
	 */
	public static void scanChunk(Level level, AABB area, int chunkX, int chunkZ, Consumer<BlockPos> action, Predicate<BlockState> filter) {
		ChunkAccess chunk = level.getChunk(chunkX, chunkZ);
		int minSectionY = level.getSectionIndex(Mth.floor(area.minY));
		int maxSectionY = level.getSectionIndex(Mth.floor(area.maxY));
		int chunkWorldMinX = chunkX * 16;
		int chunkWorldMinZ = chunkZ * 16;
		int localMinX = Math.max(0, Mth.floor(area.minX) - chunkWorldMinX);
		int localMaxX = Math.min(15, Mth.floor(area.maxX) - chunkWorldMinX);
		int localMinZ = Math.max(0, Mth.floor(area.minZ) - chunkWorldMinZ);
		int localMaxZ = Math.min(15, Mth.floor(area.maxZ) - chunkWorldMinZ);

		for (int sy = minSectionY; sy <= maxSectionY; sy++) {
			LevelChunkSection section = chunk.getSection(sy);
			if (section.hasOnlyAir()) continue; // whole 16^3 cube is empty, skip it

			int sectionWorldMinY = level.getSectionYFromSectionIndex(sy) * 16;
			int localMinY = Math.max(0, Mth.floor(area.minY) - sectionWorldMinY);
			int localMaxY = Math.min(15, Mth.floor(area.maxY) - sectionWorldMinY);

			for (int lx = localMinX; lx <= localMaxX; lx++) {
				for (int ly = localMinY; ly <= localMaxY; ly++) {
					for (int lz = localMinZ; lz <= localMaxZ; lz++) {
						BlockState state = section.getBlockState(lx, ly, lz);

						if (filter.test(state)) {
							action.accept(new BlockPos(
								chunkWorldMinX + lx,
								sectionWorldMinY + ly,
								chunkWorldMinZ + lz
							));
						}
					}
				}
			}
		}
	}

	/**
	 * Scans a chunk for blocks that match a given tag and performs an action on each matching
	 * block position.
	 *
	 * @param level  The level to scan.
	 * @param area   The area to scan within the chunk.
	 * @param chunkX The X coordinate of the chunk to scan.
	 * @param chunkZ The Z coordinate of the chunk to scan.
	 * @param action The action to perform on each matching block position.
	 * @param tag    The tag to filter blocks by.
	 */
	public static void scanChunk(Level level, AABB area, int chunkX, int chunkZ, Consumer<BlockPos> action, TagKey<Block> tag) {
		scanChunk(level, area, chunkX, chunkZ, action, state -> state.is(tag));
	}

	/**
	 * Scans a chunk for blocks that match a given tag and returns a list of matching block
	 * positions.
	 *
	 * @param level  The level to scan.
	 * @param area   The area to scan within the chunk.
	 * @param chunkX The X coordinate of the chunk to scan.
	 * @param chunkZ The Z coordinate of the chunk to scan.
	 * @param tag    The tag to filter blocks by.
	 *
	 * @return       A list of matching block positions.
	 */
	public static List<BlockPos> scanChunk(Level level, AABB area, int chunkX, int chunkZ, TagKey<Block> tag) {
		List<BlockPos> match = Lists.newArrayList();
		scanChunk(level, area, chunkX, chunkZ, match::add, state -> state.is(tag));
		return match;
	}

	/**
	 * Scans a chunk for blocks that match any of the given blocks and performs an action on each
	 * matching block position.
	 *
	 * @param level  The level to scan.
	 * @param area   The area to scan within the chunk.
	 * @param chunkX The X coordinate of the chunk to scan.
	 * @param chunkZ The Z coordinate of the chunk to scan.
	 * @param action The action to perform on each matching block position.
	 * @param blocks The blocks to filter by.
	 */
	public static void scanChunk(Level level, AABB area, int chunkX, int chunkZ, Consumer<BlockPos> action, Block ...blocks) {
		scanChunk(level, area, chunkX, chunkZ, action, state -> {
			for (Block block : blocks) {
				if (state.is(block)) {
					return true;
				}
			}
			return false;
		});
	}

	/**
	 * Scans a chunk for blocks that match any of the given blocks and returns a list of matching
	 * block positions.
	 *
	 * @param level  The level to scan.
	 * @param area   The area to scan within the chunk.
	 * @param chunkX The X coordinate of the chunk to scan.
	 * @param chunkZ The Z coordinate of the chunk to scan.
	 * @param blocks The blocks to filter by.
	 *
	 * @return       A list of matching block positions.
	 */
	public static List<BlockPos> scanChunk(Level level, AABB area, int chunkX, int chunkZ, Block ...blocks) {
		List<BlockPos> match = Lists.newArrayList();
		scanChunk(level, area, chunkX, chunkZ, match::add, blocks);
		return match;
	}
}
