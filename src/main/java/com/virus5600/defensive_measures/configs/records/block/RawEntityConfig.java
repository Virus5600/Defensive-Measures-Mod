package com.virus5600.defensive_measures.configs.records.block;

import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One entity's entry in a single config tier, exactly as written in the file (tags not yet
 * expanded, nothing cascaded).
 *
 * @param defaultAttributes The entity's {@code default} attributes, or {@code null} if this tier
 *                          doesn't define one.
 * @param blockGroups       The entity's {@code blocks} groups, in file order.
 */
public record RawEntityConfig(AttributeValues defaultAttributes, List<RawBlockGroup> blockGroups) {
	/**
	 * Parses one tier's JSON root into its entities. Invalid entity IDs and malformed entities
	 * are skipped with a warning; a missing or invalid {@code entities} object yields an empty map.
	 */
	public static Map<Identifier, RawEntityConfig> parseTier(JsonObject root, String tierName) {
		JsonElement entitiesEl = root.get("entities");

		if (entitiesEl == null) {
			return Map.of();
		}

		if (!entitiesEl.isJsonObject()) {
			DefensiveMeasures.LOGGER.warn("[BlockProjectileConfigManager] 'entities' in {} is not an object, ignoring.", tierName);
			return Map.of();
		}

		Map<Identifier, RawEntityConfig> out = new HashMap<>();

		for (Map.Entry<String, JsonElement> entry : entitiesEl.getAsJsonObject().entrySet()) {
			Identifier id = Identifier.tryParse(entry.getKey());

			if (id == null) {
				DefensiveMeasures.LOGGER.warn("[BlockProjectileConfigManager] Skipping invalid entity id '{}' in {}", entry.getKey(), tierName);
				continue;
			}

			if (!entry.getValue().isJsonObject()) {
				DefensiveMeasures.LOGGER.warn("[BlockProjectileConfigManager] Skipping entity '{}' in {}: not an object", id, tierName);
				continue;
			}

			try {
				out.put(id, fromJson(entry.getValue().getAsJsonObject(), tierName));
			} catch (RuntimeException e) {
				DefensiveMeasures.LOGGER.warn("[BlockProjectileConfigManager] Skipping malformed entity '{}' in {}: {}", id, tierName, e.getMessage());
			}
		}

		return Map.copyOf(out);
	}

	static RawEntityConfig fromJson(JsonObject obj, String tierName) {
		AttributeValues def = obj.has("default") && obj.get("default").isJsonObject()
			? AttributeValues.fromJson(obj.getAsJsonObject("default"))
			: null;

		List<RawBlockGroup> groups = new ArrayList<>();

		if (obj.has("blocks") && obj.get("blocks").isJsonArray()) {
			for (JsonElement el : obj.getAsJsonArray("blocks")) {
				if (!el.isJsonObject()) {
					continue;
				}

				try {
					groups.add(RawBlockGroup.fromJson(el.getAsJsonObject()));
				} catch (RuntimeException e) {
					DefensiveMeasures.LOGGER.warn("[BlockProjectileConfigManager] Skipping malformed block group in {}: {}", tierName, e.getMessage());
				}
			}
		}

		return new RawEntityConfig(def, groups);
	}
}
