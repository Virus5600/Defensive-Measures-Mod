package com.virus5600.defensive_measures.configs;


import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;
import com.virus5600.defensive_measures.configs.interfaces.ReloadableConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Loads, merges, and exposes Block Projectile attribute configuration from three tiers:
 * Built-in (JAR resource) &lt; Global ({@code config/block-projectiles.json}) &lt;
 * Per-World ({@code saves/&lt;world&gt;/config/block-projectiles.json}).
 * <br><br>
 * This is a config file, not a datapack resource — it is NOT wired into vanilla's
 * {@code /reload}. Call {@link #reload(MinecraftServer)} explicitly (e.g. from a custom
 * command) or {@link #loadStatic()} / {@link #loadPerWorld(MinecraftServer)} individually.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BlockProjectileConfigManager implements ReloadableConfig {
	private static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT).create();
	private static final String MOD_ID = DefensiveMeasures.MOD_ID;
	private static final String CONFIG_FILE_NAME = "block-projectiles.json";
	private static final String BUILT_IN_RESOURCE_PATH = "/data/" + MOD_ID + "/config/" + CONFIG_FILE_NAME;

	private static RawConfig builtIn = RawConfig.EMPTY;
	private static RawConfig global = RawConfig.EMPTY;
	private static RawConfig perWorld = RawConfig.EMPTY;

	/**
	 * Final resolved result: entityId -> (block -> resolved attributes)
	 * Populated by merge(); this is the only thing queried at runtime.
	 */
	private static Map<Identifier, EntityBlockAttributeIndex> resolved = Map.of();

	public static void init() {
		DefensiveMeasures.LOGGER.info("Loading block projectile config...");

		loadStatic();
	}

	// //////// //
	// LOADING  //
	// //////// //

	/**
	 * Loads the Built-in (JAR) and Global (config dir) tiers. Safe to call without a server
	 * (e.g. from mod init), since neither depends on a world being loaded.
	 */
	public static void loadStatic() {
		builtIn = readBuiltIn();
		global = readGlobal();

		merge();
	}

	/**
	 * Loads the Per-World tier for the currently running server, then re-merges.
	 * Call this on {@code ServerLifecycleEvents.SERVER_STARTED}.
	 */
	public static void loadPerWorld(MinecraftServer server) {
		perWorld = readPerWorld(server);

		merge();
	}

	/**
	 * Clears the Per-World tier (e.g. on {@code ServerLifecycleEvents.SERVER_STOPPED}) so a
	 * previous save's overrides don't leak into the next world loaded in the same session.
	 */
	public static void clearPerWorld() {
		perWorld = RawConfig.EMPTY;

		merge();
	}

	/**
	 * Re-reads all three tiers and re-merges. Intended for a manual reload command
	 * (e.g. {@code /dm:reload_config}), not vanilla {@code /reload}.
	 */
	public static void reload(MinecraftServer server) {
		builtIn = readBuiltIn();
		global = readGlobal();
		perWorld = readPerWorld(server);

		merge();
	}

	// //////////// //
	// FILE READING //
	// //////////// //

	/**
	 * Reads the built-in config from the JAR resource. If the resource is missing or malformed,
	 * returns an empty config.
	 *
	 * @return The built-in config, or {@link RawConfig#EMPTY} if not found or malformed.
	 */
	private static RawConfig readBuiltIn() {
		try (InputStream in = BlockProjectileConfigManager.class.getResourceAsStream(BUILT_IN_RESOURCE_PATH)) {
			if (in == null) {
				log("Built-in " + CONFIG_FILE_NAME + " not found on classpath at " + BUILT_IN_RESOURCE_PATH + " — using empty defaults.");

				return RawConfig.EMPTY;
			}

			try (Reader reader = new java.io.InputStreamReader(in, StandardCharsets.UTF_8)) {
				return parse(reader, "built-in");
			}
		} catch (IOException e) {
			log("Failed to read built-in " + CONFIG_FILE_NAME + ": " + e.getMessage());

			return RawConfig.EMPTY;
		}
	}

	/**
	 * Reads the global config from the config directory. If the file is missing or malformed,
	 * returns an empty config.
	 *
	 * @return The global config, or {@link RawConfig#EMPTY} if not found or malformed.
	 */
	private static RawConfig readGlobal() {
		Path path = FabricLoader.getInstance()
			.getConfigDir()
			.resolve(CONFIG_FILE_NAME);

		return readFromDisk(path, "global");
	}

	/**
	 * Reads the per-world config from the world's config directory. If the file is missing or malformed,
	 * returns an empty config.
	 *
	 * @param server The server instance.
	 * @return The per-world config, or {@link RawConfig#EMPTY} if not found or malformed.
	 */
	private static RawConfig readPerWorld(MinecraftServer server) {
		if (server == null) {
			return RawConfig.EMPTY;
		}

		// LevelResource.ROOT resolves to the save's root folder
		Path path = server.getWorldPath(LevelResource.ROOT)
			.resolve("config")
			.resolve(CONFIG_FILE_NAME);
		return readFromDisk(path, "per-world");
	}

	/**
	 * Reads a config file from disk at the given path. If the file is missing or malformed,
	 * returns an empty config.
	 *
	 * @param path     The path to the config file.
	 * @param tierName The name of the config tier (for logging purposes).
	 *
	 * @return The config read from disk, or {@link RawConfig#EMPTY} if not found or malformed.
	 */
	private static RawConfig readFromDisk(Path path, String tierName) {
		if (!Files.exists(path)) {
			return RawConfig.EMPTY;
		}

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			return parse(reader, tierName);
		} catch (IOException e) {
			log("Failed to read " + tierName + " " + CONFIG_FILE_NAME + " at " + path + ": " + e.getMessage());

			return RawConfig.EMPTY;
		}
	}

	private static RawConfig parse(Reader reader, String tierName) {
		try {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);

			if (root == null) {
				return RawConfig.EMPTY;
			}

			return RawConfig.fromJson(root, tierName);
		} catch (JsonSyntaxException e) {
			log("Malformed " + tierName + " " + CONFIG_FILE_NAME + ", ignoring: " + e.getMessage());

			return RawConfig.EMPTY;
		}
	}

	// ///// //
	// MERGE //
	// ///// //

	/**
	 * Merges Per-World over Global over Built-in, field-by-field, per entity+block group,
	 * with each tier's {@code default} used to fill any field not explicitly set at that tier
	 * before falling through to the next tier down.
	 */
	private static void merge() {
		Map<Identifier, EntityBlockAttributeIndex> out = new HashMap<>();

		Set<Identifier> allEntityIds = new HashSet<>();

		allEntityIds.addAll(builtIn.entities().keySet());
		allEntityIds.addAll(global.entities().keySet());
		allEntityIds.addAll(perWorld.entities().keySet());

		for (Identifier entityId : allEntityIds) {
			RawEntityConfig biEntity = builtIn.entities().get(entityId);
			RawEntityConfig glEntity = global.entities().get(entityId);
			RawEntityConfig pwEntity = perWorld.entities().get(entityId);

			AttributeValues entityDefault = AttributeValues.cascade(
				pwEntity != null ? pwEntity.defaultAttributes() : null,
				glEntity != null ? glEntity.defaultAttributes() : null,
				biEntity != null ? biEntity.defaultAttributes() : null
			);

			Set<String> allRawRefs = new LinkedHashSet<>();

			collectRefs(biEntity, allRawRefs);
			collectRefs(glEntity, allRawRefs);
			collectRefs(pwEntity, allRawRefs);

			// Explicit (non-tag) refs always take precedence over tag-derived matches for the
			// same block, regardless of tier or array order — so expand tags first, then let
			// explicit refs overwrite/claim any block they also cover.
			Set<String> explicitRefs = new LinkedHashSet<>();
			Set<String> tagRefs = new LinkedHashSet<>();

			for (String ref : allRawRefs) {
				(ref.startsWith("#") ? tagRefs : explicitRefs).add(ref);
			}

			Set<String> expandedFromTags = new LinkedHashSet<>();
			for (String ref : tagRefs) {
				expandedFromTags.addAll(expandRef(ref));
			}

			// Tag-derived blocks that are ALSO explicitly listed lose to the explicit entry.
			expandedFromTags.removeAll(explicitRefs);

			Set<String> allBlockRefs = new LinkedHashSet<>();
			allBlockRefs.addAll(explicitRefs);
			allBlockRefs.addAll(expandedFromTags);

			List<ResolvedBlockGroup> resolvedGroups = new ArrayList<>();

			for (String ref : allBlockRefs) {
				AttributeValues pwGroup = findGroupValuesForBlock(pwEntity, ref);
				AttributeValues glGroup = findGroupValuesForBlock(glEntity, ref);
				AttributeValues biGroup = findGroupValuesForBlock(biEntity, ref);

				AttributeValues resolvedValues = AttributeValues.cascade(pwGroup, glGroup, biGroup)
					.fillFrom(entityDefault);

				resolvedGroups.add(new ResolvedBlockGroup(ref, resolvedValues));
			}

			out.put(entityId, new EntityBlockAttributeIndex(entityDefault, resolvedGroups));
		}

		resolved = Map.copyOf(out);
	}

	/**
	 * Expands a single block/tag ref into its concrete plain-block-id form(s). A plain id
	 * ({@code "minecraft:cobblestone"}) expands to itself. A tag ref
	 * ({@code "#minecraft:anvil"}) expands to every block currently in that tag. Unknown/
	 * malformed refs expand to nothing (skipped, not fatal).
	 */
	private static List<String> expandRef(String ref) {
		if (!ref.startsWith("#")) {
			return List.of(ref);
		}

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
			Identifier itemId = BuiltInRegistries.ITEM.getKey(holder.value());
			out.add(itemId.toString());
		}

		return out;
	}

	/**
	 * Finds the raw attribute group whose {@code block} array (pre-expansion) contains, either
	 * directly or via a tag that expands to it, the given concrete block ref.
	 */
	private static AttributeValues findGroupValuesForBlock(RawEntityConfig entity, String concreteBlockRef) {
		if (entity == null) {
			return AttributeValues.EMPTY;
		}

		for (RawBlockGroup group : entity.blockGroups()) {
			for (String ref : group.blockRefs()) {
				if (expandRef(ref).contains(concreteBlockRef)) {
					return group.attributes();
				}
			}
		}

		return AttributeValues.EMPTY;
	}

	/**
	 * Collects every distinct block/tag ref string this entity's groups reference, across a single
	 * tier.
	 */
	private static void collectRefs(RawEntityConfig entity, Set<String> out) {
		if (entity == null) {
			return;
		}

		for (RawBlockGroup group : entity.blockGroups()) {
			out.addAll(group.blockRefs());
		}
	}

	// ///// //
	// QUERY //
	// ///// //

	/**
	 * Resolves the final attribute set for a turret entity ID + a specific block it wants to
	 * fire. Callers are expected to have already checked {@code UsesBlockProjectile#isValidAmmo}
	 * — this method does not consult the ammo tag, only the attribute config.
	 * <br><br>
	 * Numeric fields resolve to {@code -1}/{@code -1.0} if no tier (Per-World, Global, Built-in,
	 * or that entity's {@code default}) ever configured them — this is intentional; the loader
	 * does not apply spec-default fallbacks, so callers should treat a negative value as "not
	 * configured" and decide their own fallback.
	 *
	 * @return the resolved attributes for this entity+block, falling back to that entity's
	 * resolved {@code default} if no block group matches, or all-unset values if the entity
	 * itself has no config entry at all.
	 */
	public static ResolvedAttributes get(Identifier entityId, Identifier blockId) {
		EntityBlockAttributeIndex index = resolved.get(entityId);
		AttributeValues values = AttributeValues.EMPTY;

		if (index != null) {
			String plainRef = blockId.toString();

			values = index.blockGroups().stream()
				.filter(group -> group.blockRef().equals(plainRef))
				.findFirst()
				.map(ResolvedBlockGroup::attributes)
				.orElse(index.entityDefault());
		}

		boolean fire = values.fire() != null && values.fire();
		ExplosiveValues explosive = values.explosive();

		ResolvedAttributes.Explosive resolvedExplosive = explosive == null
			? new ResolvedAttributes.Explosive(false, -1.0, -1.0)
			: new ResolvedAttributes.Explosive(explosive.enabled(), explosive.damage(), explosive.radius());

		return new ResolvedAttributes(
			values.drag(), values.gravity(), values.damage(), values.piercing(),
			fire, resolvedExplosive
		);
	}

	private static void log(String msg) {
		DefensiveMeasures.LOGGER.warn("[BlockProjectileConfigManager] {}", msg);
	}

	// ///////////////// //
	// INTERNAL RECORDS  //
	// ///////////////// //

	private record RawConfig(Map<Identifier, RawEntityConfig> entities) {
		static final RawConfig EMPTY = new RawConfig(Map.of());

		static RawConfig fromJson(JsonObject root, String tierName) {
			JsonObject entitiesObj = root.getAsJsonObject("entities");

			if (entitiesObj == null) {
				return EMPTY;
			}

			Map<Identifier, RawEntityConfig> out = new HashMap<>();

			for (String key : entitiesObj.keySet()) {
				Identifier id = Identifier.tryParse(key);

				if (id == null) {
					log("Skipping invalid entity id '" + key + "' in " + tierName + " " + CONFIG_FILE_NAME);
					continue;
				}

				JsonElement el = entitiesObj.get(key);

				if (!el.isJsonObject()) {
					continue;
				}

				out.put(id, RawEntityConfig.fromJson(el.getAsJsonObject(), tierName));
			}

			return new RawConfig(out);
		}
	}

	private record RawEntityConfig(AttributeValues defaultAttributes, List<RawBlockGroup> blockGroups) {
		static RawEntityConfig fromJson(JsonObject obj, String tierName) {
			AttributeValues def = obj.has("default") && obj.get("default").isJsonObject()
				? AttributeValues.fromJson(obj.getAsJsonObject("default"))
				: AttributeValues.EMPTY;

			List<RawBlockGroup> groups = new ArrayList<>();
			if (obj.has("blocks") && obj.get("blocks").isJsonArray()) {
				for (JsonElement el : obj.getAsJsonArray("blocks")) {
					if (!el.isJsonObject()) continue;
					JsonObject groupObj = el.getAsJsonObject();
					try {
						groups.add(RawBlockGroup.fromJson(groupObj));
					} catch (Exception e) {
						log("Skipping malformed block group in " + tierName + " " + CONFIG_FILE_NAME + ": " + e.getMessage());
					}
				}
			}
			return new RawEntityConfig(def, groups);
		}
	}

	private record RawBlockGroup(List<String> blockRefs, AttributeValues attributes) {
		static RawBlockGroup fromJson(JsonObject obj) {
			List<String> refs = new ArrayList<>();
			if (obj.has("block") && obj.get("block").isJsonArray()) {
				obj.getAsJsonArray("block").forEach(el -> refs.add(el.getAsString()));
			}
			return new RawBlockGroup(refs, AttributeValues.fromJson(obj));
		}
	}

	/**
	 * A sparse set of attribute fields. Numeric fields (drag/gravity/damage/piercing) use a
	 * negative value to mean "not specified at this tier"; {@code fire} uses {@code null} for
	 * the same purpose (needed so cascading can distinguish "omitted" from "explicitly false").
	 * Resolved by cascading — see {@link #cascade}.
	 *
	 * @param drag      The drag coefficient (0.0-1.0), where 1.0 is no drag and 0.0 is instant stop. Negative = unset.
	 * @param gravity   The gravity factor (0.0-1.0), where 0.0 is no gravity and 1.0 is normal gravity. Negative = unset.
	 * @param damage    The base damage of the projectile. Negative = unset.
	 * @param piercing  The piercing level of the projectile (0.0 means no piercing, higher values allow it to pierce more entities). Negative = unset.
	 * @param fire      Whether the projectile sets entities on fire. Null = unset at this tier.
	 * @param explosive The explosive properties of the projectile, if any.
	 */
	private record AttributeValues(
		double drag, double gravity, double damage, double piercing,
		Boolean fire, ExplosiveValues explosive
	) {
		static final AttributeValues EMPTY = new AttributeValues(-1.0, -1.0, -1.0, -1.0, null, null);

		static AttributeValues fromJson(JsonObject obj) {
			double drag = obj.has("drag") ? obj.get("drag").getAsDouble() : -1.0;
			double gravity = obj.has("gravity") ? obj.get("gravity").getAsDouble() : -1.0;
			double damage = obj.has("damage") ? obj.get("damage").getAsDouble() : -1.0;
			double piercing = obj.has("piercing") ? obj.get("piercing").getAsDouble() : -1.0;
			Boolean fire = obj.has("fire") ? obj.get("fire").getAsBoolean() : null;

			ExplosiveValues explosive = null;

			if (obj.has("explosive")) {
				JsonElement ex = obj.get("explosive");

				if (ex.isJsonPrimitive() && ex.getAsJsonPrimitive().isBoolean()) {
					explosive = ex.getAsBoolean() ?
						ExplosiveValues.ENABLED_DEFAULT : ExplosiveValues.DISABLED;
				}
				else if (ex.isJsonObject()) {
					JsonObject exObj = ex.getAsJsonObject();
					double exDamage = exObj.has("damage") ?
						exObj.get("damage").getAsDouble() : -1.0;
					double exRadius = exObj.has("radius") ?
						exObj.get("radius").getAsDouble() : -1.0;
					explosive = new ExplosiveValues(true, exDamage, exRadius);
				}
			}

			return new AttributeValues(drag, gravity, damage, piercing, fire, explosive);
		}

		/** Cascades tiers in priority order (highest first); first non-null field per tier wins. */
		static AttributeValues cascade(AttributeValues highest, AttributeValues mid, AttributeValues lowest) {
			return new AttributeValues(
				pickNumeric(highest, mid, lowest, AttributeValues::drag),
				pickNumeric(highest, mid, lowest, AttributeValues::gravity),
				pickNumeric(highest, mid, lowest, AttributeValues::damage),
				pickNumeric(highest, mid, lowest, AttributeValues::piercing),
				pickBoolean(highest, mid, lowest, AttributeValues::fire),
				pickExplosive(highest, mid, lowest)
			);
		}

		/** Fills any still-unset field in this instance from a fallback (used for entity-default fallback). */
		AttributeValues fillFrom(AttributeValues fallback) {
			return cascade(this, fallback, EMPTY);
		}

		private static double pickNumeric(AttributeValues a, AttributeValues b, AttributeValues c, ToDoubleFunction<AttributeValues> getter) {
			if (a != null && getter.applyAsDouble(a) >= 0) {
				return getter.applyAsDouble(a);
			}

			if (b != null && getter.applyAsDouble(b) >= 0) {
				return getter.applyAsDouble(b);
			}

			if (c != null) {
				return getter.applyAsDouble(c);
			}

			return -1.0;
		}

		private static Boolean pickBoolean(AttributeValues a, AttributeValues b, AttributeValues c, Function<AttributeValues, Boolean> getter) {
			if (a != null && getter.apply(a) != null) {
				return getter.apply(a);
			}

			if (b != null && getter.apply(b) != null) {
				return getter.apply(b);
			}

			if (c != null) {
				return getter.apply(c);
			}

			return null;
		}

		private static ExplosiveValues pickExplosive(AttributeValues a, AttributeValues b, AttributeValues c) {
			if (a != null && a.explosive() != null) return a.explosive();
			if (b != null && b.explosive() != null) return b.explosive();
			if (c != null) return c.explosive();
			return null;
		}

	}

	/**
	 * Explosive sub-values follow the same negative-sentinel rule for damage/radius.
	 *
	 * @param enabled whether the projectile is explosive
	 * @param damage the damage dealt by the explosion
	 * @param radius the radius of the explosion
	 */
	private record ExplosiveValues(boolean enabled, double damage, double radius) {
		static final ExplosiveValues DISABLED = new ExplosiveValues(false, -1.0, -1.0);
		static final ExplosiveValues ENABLED_DEFAULT = new ExplosiveValues(true, -1.0, -1.0);
	}

	private record ResolvedBlockGroup(String blockRef, AttributeValues attributes) {}

	private record EntityBlockAttributeIndex(AttributeValues entityDefault, List<ResolvedBlockGroup> blockGroups) {}

	/**
	 * The final attribute set for a specific entity+block pairing, after cascading all three
	 * config tiers plus the entity's {@code default}. Numeric fields that were never set
	 * anywhere (Per-World, Global, Built-in, or that entity's {@code default}) resolve to
	 * {@code -1}/{@code -1.0} — callers/turrets are expected to check for this and apply their
	 * own fallback if desired, rather than the loader silently applying a spec default.
	 * {@code fire} and {@code explosive.enabled} resolve to {@code false} if never set by any
	 * tier, since booleans have no meaningful "unset but non-false" representation here.
	 */
	public record ResolvedAttributes(
		double drag, double gravity,
		double damage, double piercing,
		boolean fire, Explosive explosive
	) {
		public record Explosive(boolean enabled, double damage, double radius) {}
	}
}
