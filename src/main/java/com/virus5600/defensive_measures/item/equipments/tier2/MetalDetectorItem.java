package com.virus5600.defensive_measures.item.equipments.tier2;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.virus5600.defensive_measures._util.WorldUtil;
import com.virus5600.defensive_measures.item.ModToolMaterials;
import com.virus5600.defensive_measures.item.component.ModDataComponents;
import com.virus5600.defensive_measures.network.clientbound.item.BlockHighlightPacket;
import com.virus5600.defensive_measures.registry.tag.ModBlockTags;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;

/**
 * An item that allows the player to detect metalic traps.
 * <br><br>
 * This item (or device) actively works and uses its durability every inreal-life seconds to detect
 * metalic traps around the user's radius. This provides a soft counter against deadly metalic
 * traps such as landmines and such.
 * <br><br>
 * To use the item, the metal detector must be held in the player's main hand, or in the offhand.
 * This will actively deplete and damage the item but when in Creative, the item's durability will
 * not decrease.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MetalDetectorItem extends Item {
	/**
	 * A map that tracks the tick count for each metal detector item in the world. This is used to
	 * determine when to apply durability damage to the item.
	 */
	private static final Map<UUID, Integer> TICK_COUNT_MAP = Maps.newHashMap();
	/**
	 * Delay (in ticks) between each durability damage of the metal detector item.
	 */
	protected static final int DEGRADATION_DELAY = 20;

	public MetalDetectorItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties settings) {
		super(
			settings.tool(
				material,
				BlockTags.AIR,
				attackDamage, attackSpeed, 0.0F
			).rarity(
				material == ModToolMaterials.NETHERITE_METAL_DETECTOR ?
					Rarity.RARE :
					material == ModToolMaterials.IRON_METAL_DETECTOR ?
						Rarity.UNCOMMON : Rarity.COMMON
			)
		);
	}

	// In case someone wants to make an extension of this (as an API)
	public MetalDetectorItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties settings, Rarity rarity) {
		super(
			settings.tool(
				material,
				BlockTags.AIR,
				attackDamage, attackSpeed, 0.0F
			).rarity(rarity)
		);
	}

	// /////// //
	// METHODS //
	// /////// //

	@Override
	public void inventoryTick(final ItemStack stack, final ServerLevel level, final Entity owner, final @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, owner, slot);

		if (slot != null && owner instanceof LivingEntity le) {
			int tickCount = this.getTickCount(le.getUUID());

			switch (slot) {
				case MAINHAND, OFFHAND -> {
					if (++tickCount >= DEGRADATION_DELAY) {
						int range = stack.getOrDefault(ModDataComponents.DETECTION_RANGE, 4);
						tickCount = 0;

						stack.hurtAndConvertOnBreak(
							1, this.getDepletedItem(),
							le, slot
						);

						this.detectMetals(level, le, range);
					}
				}

				default -> {
					if (tickCount > 0) {
						--tickCount;
					}
				}
			}

			this.setTickCountEntry(le.getUUID(), tickCount);
		}
	}

	@Override
	public boolean canDestroyBlock(
		@NonNull ItemStack stack, @NonNull BlockState state,
		@NonNull Level world, @NonNull BlockPos pos,
		@NonNull LivingEntity user
	) {
		if (user instanceof Player player) {
			return !player.isCreative();
		}

		return false;
	}

	// ////////////// //
	// CUSTOM METHODS //
	// ////////////// //

	protected void setTickCountEntry(UUID uuid, int tickCount) {
		TICK_COUNT_MAP.put(uuid, tickCount);
	}

	protected int getTickCount(UUID uuid) {
		return TICK_COUNT_MAP.getOrDefault(uuid, 0);
	}

	/**
	 * Detects metalic traps around the given owner entity within the specified range.
	 *
	 * @param level The level in which to detect metals
	 * @param owner The entity around which to detect metals
	 * @param range The range within which to detect metals
	 */
	public void detectMetals(Level level, LivingEntity owner, int range) {
		Vec3 pos = owner.position();
		AABB area = new AABB(
			pos.x - range, pos.y - range, pos.z - range,
			pos.x + range, pos.y + range, pos.z + range
		);

		int minChunkX = SectionPos.blockToSectionCoord(area.minX);
		int maxChunkX = SectionPos.blockToSectionCoord(area.maxX);
		int minChunkZ = SectionPos.blockToSectionCoord(area.minZ);
		int maxChunkZ = SectionPos.blockToSectionCoord(area.maxZ);

		for (int cx = minChunkX; cx <= maxChunkX; cx++) {
			for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
				WorldUtil.scanChunk(
					level, area, cx, cz,
					blockPos -> {
						for (ServerPlayer player : PlayerLookup.around((ServerLevel) level, pos, 8)) {
							ServerPlayNetworking.send(
								player,
								new BlockHighlightPacket(
									blockPos,
									0xFF0000,	// Red color in ARGB format
									1 * 20			// Duration in ticks (1 second)
								)
							);
						}
					},
					ModBlockTags.METAL_DETECTABLE
				);
			}
		}
	}

	/**
	 * The item that the metal detector will convert to when it is depleted.
	 *
	 * @return The item that the metal detector will convert to when it is depleted
	 */
	public Item getDepletedItem() {
		// Temporarily set to "this"
		return this;
	}
}
