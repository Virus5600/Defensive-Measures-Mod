package com.virus5600.defensive_measures.configs;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.configs.interfaces.ReloadableConfig;
import com.virus5600.defensive_measures.configs.records.block.*;
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesBlockProjectile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Loads, merges, and exposes Block Projectile attribute configuration from three tiers:
 * Built-in (JAR resource) &lt; Global ({@code config/block-projectiles.json}) &lt;
 * Per-World ({@code saves/<world>/config/block-projectiles.json}).
 * <br><br>
 * This is a config file, not a datapack resource, but {@link ModConfigs} hooks it into the
 * lifecycle: tags loaded &rarr; {@link #init()}, datapack reload &rarr; {@link #reload},
 * server started &rarr; {@link #loadPerWorld}, server stopped &rarr; {@link #clearPerWorld()}.
 *
 * <h2>Schema</h2>
 * <pre>
 * {
 *   "entities": {
 *     "&lt;entityId&gt;": {
 *       "default": { &lt;attributes&gt; },
 *       "blocks": [
 *         {
 *           "block": ["minecraft:cobblestone", "#minecraft:anvil"],
 *           &lt;attributes&gt;
 *         }
 *       ]
 *     }
 *   }
 * }
 * </pre>
 * Where {@code <attributes>} is any of: {@code drag}, {@code gravity}, {@code damage},
 * {@code piercing}, {@code fire}, and {@code explosive} (a boolean, or an object with
 * {@code damage}, {@code effectiveRadius} and {@code maxRadius}). Accepted ranges are clamped by
 * {@link AttributeValues} and {@link ExplosiveValues}, which are the single source of truth for
 * them.
 *
 * <h2>Keys</h2>
 * <ul>
 *   <li>{@code entities}: the entities that shoot the Block Projectile (i.e.: the Dirt Turret).
 *   Entities that don't shoot it are ignored.</li>
 *   <li>{@code default}: <strong>required</strong>. The fallback for every attribute not set by
 *   a block group. If no tier defines one for an entity, a warning is logged and
 *   {@link AttributeValues#DEFAULT} is used.</li>
 *   <li>{@code blocks}: groups of blocks sharing the same attributes. {@code block} lists block
 *   IDs and/or {@code #tag} IDs (tags are expanded against the item registry). When a block is
 *   covered by both an explicit ID and a tag within one tier, the explicit ID wins.</li>
 *   <li>{@code drag}, {@code gravity}: velocity multiplier and downward pull applied per tick.</li>
 *   <li>{@code damage}: damage dealt when the projectile hits an entity. Not to be confused
 *   with {@code explosive.damage}, which is the damage of the explosion.</li>
 *   <li>{@code piercing}: how many entities a non-explosive projectile passes through. Ignored
 *   when {@code explosive} is enabled.</li>
 *   <li>{@code fire}: whether the projectile is on fire and ignites what it hits.</li>
 *   <li>{@code explosive}: {@code true} enables the default explosion, an object customizes it,
 *   {@code false} disables it.</li>
 * </ul>
 *
 * For the {@link ExplosiveValues explosive} object, the keys are:
 * <ul>
 *   <li>{@code enabled}: whether the projectile is explosive.</li>
 *   <li>{@code damage}: the damage of the explosion.</li>
 *   <li>{@code effectiveRadius}: the radius of the explosion's effect.</li>
 *   <li>{@code maxRadius}: the maximum radius of the explosion.</li>
 * </ul>
 *
 * <h2>Resolution</h2>
 * For each entity and block, every attribute comes from the first of: the block's group in
 * Per-World, Global, Built-in, then the entity's cascaded {@code default}, then
 * {@link AttributeValues#DEFAULT}. Explosive sub-values cascade field by field the same way.
 * A resolved attribute is therefore never "unset".
 * <br><br>
 * Any entity can have a config entry, but it only takes effect on entities implementing
 * {@link UsesBlockProjectile} or code that explicitly queries this manager.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see AttributeValues
 * @see ExplosiveValues
 */
public class BlockProjectileConfigManager implements ReloadableConfig {
	private static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT).create();
	private static final String MOD_ID = DefensiveMeasures.MOD_ID;
	private static final String CONFIG_FILE_NAME = "block-projectiles.json";
	private static final String BUILT_IN_RESOURCE_PATH = "/data/" + MOD_ID + "/configs/" + CONFIG_FILE_NAME;

	/** Returned for entities with no config entry at all. */
	private static final EntityBlockAttributeIndex FALLBACK = new EntityBlockAttributeIndex(AttributeValues.DEFAULT, Map.of());

	private static Map<Identifier, RawEntityConfig> builtIn = Map.of();
	private static Map<Identifier, RawEntityConfig> global = Map.of();
	private static Map<Identifier, RawEntityConfig> perWorld = Map.of();

	/**
	 * Final resolved result: entityId -> (block -> resolved attributes). Populated by
	 * {@link #merge()}; this is the only thing queried at runtime. Replaced wholesale on every
	 * merge, so a changed reference means the config was reloaded.
	 */
	private static volatile Map<Identifier, EntityBlockAttributeIndex> resolved = Map.of();

	// ///////// //
	// LIFECYCLE //
	// ///////// //

	/**
	 * Loads the Built-in and Global tiers and merges. Keeps whatever Per-World tier is currently
	 * loaded, so it is safe to call again mid-session (i.e.: when tags are reloaded).
	 */
	public static synchronized void init() {
		DefensiveMeasures.LOGGER.info("Loading block projectile config...");

		loadStaticTiers();
		merge();
	}

	/**
	 * Loads the Per-World tier for the currently running server, then re-merges.
	 * Call this on {@code ServerLifecycleEvents.SERVER_STARTED}.
	 */
	public static synchronized void loadPerWorld(MinecraftServer server) {
		perWorld = readPerWorld(server);

		merge();
	}

	/**
	 * Clears the Per-World tier (i.e.: on {@code ServerLifecycleEvents.SERVER_STOPPED}) so a
	 * previous save's overrides don't leak into the next world loaded in the same session.
	 */
	public static synchronized void clearPerWorld() {
		perWorld = Map.of();

		merge();
	}

	/**
	 * Re-reads all three tiers and re-merges.
	 */
	public static synchronized void reload(MinecraftServer server) {
		loadStaticTiers();
		perWorld = readPerWorld(server);

		merge();
	}

	private static void loadStaticTiers() {
		builtIn = readBuiltIn();
		global = readGlobal();
	}

	// //////// //
	// QUERYING //
	// //////// //

	/**
	 * Returns the final resolved config, after merging all three tiers. The raw tiered configs
	 * are not exposed.
	 *
	 * @return an unmodifiable map of entity IDs to their resolved block attribute indices.
	 */
	public static Map<Identifier, EntityBlockAttributeIndex> getResolved() {
		return resolved;
	}

	/**
	 * @return the resolved config for the entity, or an index whose default is
	 * {@link AttributeValues#DEFAULT} and that has no blocks if the entity has no config entry.
	 */
	public static EntityBlockAttributeIndex getConfigForEntity(Identifier entityId) {
		return resolved.getOrDefault(entityId, FALLBACK);
	}

	public static EntityBlockAttributeIndex getConfigForEntity(EntityType<?> entityType) {
		return getConfigForEntity(RegistryHelper.getIdentifier(entityType));
	}

	public static EntityBlockAttributeIndex createFrom(
		double drag, double gravity,
		int damage, byte piercing,
		boolean fire, ExplosiveValues explosive
	) {
		return createFrom(new AttributeValues(
			drag, gravity,
			damage, piercing,
			fire, explosive
		));
	}

	public static EntityBlockAttributeIndex createFrom(AttributeValues values) {
		return new EntityBlockAttributeIndex(values, Map.of());
	}

	// //////////// //
	// FILE READING //
	// //////////// //

	private static Map<Identifier, RawEntityConfig> readBuiltIn() {
		try (InputStream in = BlockProjectileConfigManager.class.getResourceAsStream(BUILT_IN_RESOURCE_PATH)) {
			if (in == null) {
				log("Built-in " + CONFIG_FILE_NAME + " not found on classpath at " + BUILT_IN_RESOURCE_PATH + " — using empty defaults.");

				return Map.of();
			}

			try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
				return parse(reader, "built-in");
			}
		} catch (IOException e) {
			log("Failed to read built-in " + CONFIG_FILE_NAME + ": " + e.getMessage());

			return Map.of();
		}
	}

	private static Map<Identifier, RawEntityConfig> readGlobal() {
		Path path = FabricLoader.getInstance()
			.getConfigDir()
			.resolve(CONFIG_FILE_NAME);

		return readFromDisk(path, "global");
	}

	private static Map<Identifier, RawEntityConfig> readPerWorld(MinecraftServer server) {
		if (server == null) {
			return Map.of();
		}

		// LevelResource.ROOT resolves to the save's root folder
		Path path = server.getWorldPath(LevelResource.ROOT)
			.resolve("config")
			.resolve(CONFIG_FILE_NAME);

		return readFromDisk(path, "per-world");
	}

	private static Map<Identifier, RawEntityConfig> readFromDisk(Path path, String tierName) {
		if (!Files.exists(path)) {
			return Map.of();
		}

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			return parse(reader, tierName);
		} catch (IOException e) {
			log("Failed to read " + tierName + " " + CONFIG_FILE_NAME + " at " + path + ": " + e.getMessage());

			return Map.of();
		}
	}

	private static Map<Identifier, RawEntityConfig> parse(Reader reader, String tierName) {
		try {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);

			return root == null ?
				Map.of() : RawEntityConfig.parseTier(root, tierName);
		} catch (JsonParseException e) {
			log("Malformed " + tierName + " " + CONFIG_FILE_NAME + ", ignoring: " + e.getMessage());

			return Map.of();
		}
	}

	// ///// //
	// MERGE //
	// ///// //

	/**
	 * Resolves every entity found in any tier and publishes the result.
	 */
	private static void merge() {
		Set<Identifier> entityIds = new HashSet<>();

		entityIds.addAll(builtIn.keySet());
		entityIds.addAll(global.keySet());
		entityIds.addAll(perWorld.keySet());

		Map<Identifier, EntityBlockAttributeIndex> out = new HashMap<>();

		for (Identifier entityId : entityIds) {
			out.put(entityId, resolveEntity(entityId));
		}

		resolved = Map.copyOf(out);

		DefensiveMeasures.LOGGER.debug("Turret Ammo Attrs: {}", resolved);
	}

	/**
	 * Resolves one entity: cascades its {@code default} across the tiers, flattens each tier's
	 * block groups into a per-block map, then cascades every block as
	 * Per-World &rarr; Global &rarr; Built-in &rarr; entity default &rarr; {@link AttributeValues#DEFAULT}.
	 */
	private static EntityBlockAttributeIndex resolveEntity(Identifier entityId) {
		RawEntityConfig pw = perWorld.get(entityId);
		RawEntityConfig gl = global.get(entityId);
		RawEntityConfig bi = builtIn.get(entityId);

		AttributeValues pwDefault = pw == null ? null : pw.defaultAttributes();
		AttributeValues glDefault = gl == null ? null : gl.defaultAttributes();
		AttributeValues biDefault = bi == null ? null : bi.defaultAttributes();

		AttributeValues rawDefault;

		if (pwDefault == null && glDefault == null && biDefault == null) {
			log("Entity '" + entityId + "' has no \"default\" in any tier of " + CONFIG_FILE_NAME + " — using the built-in default attributes.");

			rawDefault = AttributeValues.DEFAULT;
		}
		else {
			rawDefault = AttributeValues.cascade(pwDefault, glDefault, biDefault);
		}

		Map<String, AttributeValues> pwBlocks = flatten(pw);
		Map<String, AttributeValues> glBlocks = flatten(gl);
		Map<String, AttributeValues> biBlocks = flatten(bi);

		Set<String> blockIds = new LinkedHashSet<>();

		blockIds.addAll(biBlocks.keySet());
		blockIds.addAll(glBlocks.keySet());
		blockIds.addAll(pwBlocks.keySet());

		Map<String, AttributeValues> blocks = new LinkedHashMap<>();

		for (String blockId : blockIds) {
			blocks.put(
				blockId,
				AttributeValues.cascade(
					pwBlocks.get(blockId),
					glBlocks.get(blockId),
					biBlocks.get(blockId),
					rawDefault
				).withDefaults()
			);
		}

		return new EntityBlockAttributeIndex(rawDefault.withDefaults(), blocks);
	}

	/**
	 * Flattens one tier's block groups for an entity into concrete block ID -> raw attributes.
	 * Tags are expanded once here. Within the tier, an explicit ID beats a tag that also covers
	 * it, and the first group listing a block wins.
	 */
	private static Map<String, AttributeValues> flatten(RawEntityConfig entity) {
		if (entity == null) {
			return Map.of();
		}

		Map<String, AttributeValues> fromTags = new LinkedHashMap<>();
		Map<String, AttributeValues> explicit = new LinkedHashMap<>();

		for (RawBlockGroup group : entity.blockGroups()) {
			for (String ref : group.blockRefs()) {
				if (ref.startsWith("#")) {
					for (String blockId : expandTag(ref)) {
						fromTags.putIfAbsent(blockId, group.attributes());
					}

					continue;
				}

				Identifier blockId = Identifier.tryParse(ref);

				if (blockId == null) {
					log("Skipping malformed block id '" + ref + "' in " + CONFIG_FILE_NAME);
					continue;
				}

				explicit.putIfAbsent(blockId.toString(), group.attributes());
			}
		}

		Map<String, AttributeValues> out = new LinkedHashMap<>(fromTags);
		out.putAll(explicit);

		return out;
	}

	/**
	 * Expands a {@code #tag} ref into every item ID currently in that tag. Unknown or malformed
	 * tags expand to nothing (skipped, not fatal).
	 */
	private static List<String> expandTag(String ref) {
		Identifier tagId = Identifier.tryParse(ref.substring(1));

		if (tagId == null) {
			log("Skipping malformed block tag ref '" + ref + "' in " + CONFIG_FILE_NAME);
			return List.of();
		}

		TagKey<Item> tag = RegistryHelper.createItemTagKey(tagId);
		Optional<HolderSet.Named<Item>> holders = BuiltInRegistries.ITEM.get(tag);

		if (holders.isEmpty()) {
			log("Block tag '" + ref + "' referenced in " + CONFIG_FILE_NAME + " has no members (or doesn't exist) — skipping.");
			return List.of();
		}

		List<String> out = new ArrayList<>();

		for (Holder<Item> holder : holders.get()) {
			out.add(BuiltInRegistries.ITEM.getKey(holder.value()).toString());
		}

		return out;
	}

	private static void log(String msg) {
		DefensiveMeasures.LOGGER.warn("[BlockProjectileConfigManager] {}", msg);
	}
}
