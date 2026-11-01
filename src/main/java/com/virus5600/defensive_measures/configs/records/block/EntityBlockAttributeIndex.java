package com.virus5600.defensive_measures.configs.records.block;

import java.util.Map;

/**
 * The final resolved attributes for a specific entity, after cascading all three config tiers
 * plus the entity's {@code default}. Every {@link AttributeValues} in here is fully resolved
 * (see {@link AttributeValues#withDefaults()}), so nothing is left unset.
 *
 * @param entityDefault The resolved {@code default} attributes of the entity.
 * @param blocks        Concrete block ID (i.e.: {@code minecraft:stone}; tags already expanded) to
 *                      its resolved attributes.
 */
public record EntityBlockAttributeIndex(AttributeValues entityDefault, Map<String, AttributeValues> blocks) {
	public EntityBlockAttributeIndex {
		blocks = Map.copyOf(blocks);
	}

	/**
	 * Returns the resolved attributes for a specific block ref (or ID; i.e.:
	 * {@code minecraft:stone}). If the block is not configured, the entity's resolved
	 * {@code default} attributes are returned instead.
	 *
	 * @param blockRef The concrete block reference (plain block ID).
	 *
	 * @return The resolved attributes for the block, or the entity's {@code default} if none match.
	 */
	public AttributeValues getBlockAttributes(String blockRef) {
		return this.blocks.getOrDefault(blockRef, this.entityDefault);
	}
}
