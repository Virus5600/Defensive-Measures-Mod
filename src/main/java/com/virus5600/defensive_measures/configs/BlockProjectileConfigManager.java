package com.virus5600.defensive_measures.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import com.virus5600.defensive_measures.entity.turrets.interfaces.UsesBlockProjectile;

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
 * Built-in (JAR resource) < Global ({@code config/block-projectiles.json}) <
 * Per-World ({@code saves/<world>/config/block-projectiles.json}).
 * <br><br>
 * This is a config file, not a datapack resource — it is NOT wired into vanilla's
 * {@code /reload}. Call {@link #reload(MinecraftServer)} explicitly (e.g. from a custom
 * command) or {@link #loadStatic()} / {@link #loadPerWorld(MinecraftServer)} individually.
 * <br><br>
 * The config file is a JSON object that follows this schema:
 * <ul>
 * 	<li>{@code entities}: {@code <object>}
 * 		<ul>
 * 			<li>{@code <entityId>}: {@code <object>}
 * 				<ul>
 * 					<li>{@code default}: {@code <object>}</li>
 * 					<li>{@code blocks}: {@code <array><object></array>}
 * 						<ul>
 * 							<li>{@code block}: {@code <array><string></array>}</li>
 * 							<li>{@code drag}: {@code 0.0 - 1.0}</li>
 * 							<li>{@code gravity}: {@code 0.000 - 0.100}</li>
 * 							<li>{@code damage}: {@code 0 - 1000}</li>
 * 							<li>{@code piercing}: {@code 0 - 100}</li>
 * 							<li>{@code fire}: {@code <boolean>}</li>
 * 							<li>{@code explosive}: {@code <boolean>} | {@code <object>}
 * 								<ul>
 * 									<li>{@code damage}: {@code 0 - 1000}</li>
 * 									<li>{@code effectiveRadius}: {@code 1.0 - 100.0}</li>
 * 									<li>{@code maxRadius}: {@code 1.0 - 100.0}</li>
 * 								</ul>
 * 							</li>
 * 						</ul>
 * 					</li>
 * 				</ul>
 * 			</li>
 * 		</ul>
 * 	</li>
 * </ul>
 *
 * <hr/>
 * As for what each field means (key descriptions):
 *
 * <table>
 * 	<thead>
 * 		<tr>
 * 			<th width="20%">Key</th>
 * 			<th width="20%">Type</th>
 * 			<th width="20%">Default</th>
 * 			<th width="40%">Description</th>
 * 		</tr>
 * 	</thead>
 * 	<tbody>
 * 		<tr>
 * 			<td>{@code entities}</td>
 * 			<td>JSON object</td>
 * 			<td><strong>TBD</strong></td>
 * 			<td>Contains the list of entities that shoots the Block Projectile like the <strong>Catapult Tower</strong> ({@code dm:catapult_tower}). An entity that does not shoot this projectile will be ignored.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId}</td>
 * 			<td>Entity ID</td>
 * 			<td><strong>TBD</strong></td>
 * 			<td>The entity ID that will have its projectile modified (i.e.: {@code dm:catapult_tower}). If a default turret isn't defined in the file yet the {@code replace} key's value is {@code true}, it will use the default configuration.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.default}</td>
 * 			<td>JSON object</td>
 * 			<td><strong>TBD</strong></td>
 * 			<td>Contains a default fallback for all values in the other attributes. Basically, it hold a fallback value for the {@code drag}, {@code gravity}, {@code damage}, {@code piercing}, {@code fire}, and {@code explosive} attributes.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks}</td>
 * 			<td>Array (JSON object)</td>
 * 			<td><strong>TBD</strong></td>
 * 			<td>An array containing the attributes for this/these block/s.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks.block}</td>
 * 			<td>Array (Item ID OR Item Tag ID)</td>
 * 			<td><strong>TBD</strong></td>
 * 			<td>An array of item IDs or item tag IDs that the entity it is attached to will be able to shoot. An example of this would be the Catapult Tower ({@code dm:catapult_tower}) lobbing a Cobblestone, so {@code minecraft:cobblestone} is defined as one of its item ID, or {@code #minecraft:anvil} to include all the anvils.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks.drag}</td>
 * 			<td>{@code <float>} ({@code 0.0} to {@code 1.0})</td>
 * 			<td>{@code 0.99}</td>
 * 			<td>The velocity multiplier after the drag is applied. Value will clamp between 0 and 1 as negative value will leave the projectile inverting its velocity every tick. Values more than 1 will instead increase its velocity. The default {@code 0.99} value means that it loses 1% of its velocity every tick, simulating drag as it moves.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks.gravity}</td>
 * 			<td>{@code <float>} ({@code 0.000} to {@code 0.100})</td>
 * 			<td>{@code 0.05}</td>
 * 			<td>The gravity defines how much downward velocity will be applied to the projectile every tick. Clamping it to {@code 0.0} to {@code 0.1} allows the projectile to act more in line with the Minecraft world's fictional gravity.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks.damage}</td>
 * 			<td>{@code <int>} ({@code 0} to {@code 1000})</td>
 * 			<td>{@code 4}</td>
 * 			<td>Do not confuse this with the {@code entities.entityId.blockId.explosive.damage}. This {@code damage} key defines the damage dealt when the projectile <strong>HITS</strong> or <strong>COLLIDES</strong> with an entity. Capping the damage to 1000 is a design choice to allow said turret to balance out with potentially unhinge modded blocks and mods with inflated monster HP.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks.piercing}</td>
 * 			<td>{@code <int>} ({@code 0} to {@code 100})</td>
 * 			<td>{@code 0}</td>
 * 			<td>The piercing of the mod works differently than the vanilla piercing. The defined value for the {@code piercing} key will determine how many unarmored entities the projectile can pass through before getting destroyed. Armored entities may reduce the number of entities it could pierce and may even reduce the projectile's speed.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks.fire}</td>
 * 			<td>{@code <boolean>}</td>
 * 			<td>{@code false}</td>
 * 			<td>Sets the projectile on fire when launched. This allows the projectile to also set the entity it hits on fire, providing additional fire damage to anyone it hits.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.blocks.explosive}</td>
 * 			<td>{@code <boolean>} or JSON Object</td>
 * 			<td>{@code false}</td>
 * 			<td>Determines whether the block projectile launched/shot by the turret explodes on contact. When this key is set to {@code true} or has a defined custom value inside, it disregards the {@code piercing} key regardless of its value.<br><br>Having its value as {@code true} makes the projectile use the hardcoded default value. However, providing an object will allow the explosion to be a bit customized.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.explosive.damage}</td>
 * 			<td>{@code <int>} ({@code 0} to {@code 1000})</td>
 * 			<td>{@code 5}</td>
 * 			<td>Do not confuse this with the {@code entities.entityId.blockId.damage}. This {@code damage} key defines the damage dealt by the explosion of this projectile. Capping the damage to 1000 is a design choice to allow the projectile to allow said turret to balance out with potentially unhinge modded blocks and mods with inflated monster HP.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code entities.entityId.explosive.effectiveRadius}</td>
 * 			<td>{@code <float>} ({@code 1.0} to {@code 100.0})</td>
 * 			<td>{@code 2.5}</td>
 * 			<td>Determines the effective radius of the explosion, damaging all entities within said radius.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@code  entities.entityId.explosive.maxRadius}</td>
 * 			<td>{@code <float>} ({@code 1.0} to {@code 100.0})</td>
 * 			<td>{@code 5.0}</td>
 * 			<td>Determines the maximum radius of the explosion, which is the furthest distance an entity can be from the explosion center and still be affected.</td>
 * 		</tr>
 * 	</tbody>
 * </table>
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BlockProjectileConfigManager implements ReloadableConfig {
	private static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT).create();
	private static final String MOD_ID = DefensiveMeasures.MOD_ID;
	private static final String CONFIG_FILE_NAME = "block-projectiles.json";
	private static final String BUILT_IN_RESOURCE_PATH = "/data/" + MOD_ID + "/configs/" + CONFIG_FILE_NAME;

	private static RawConfig builtIn = RawConfig.EMPTY;
	private static RawConfig global = RawConfig.EMPTY;
	private static RawConfig perWorld = RawConfig.EMPTY;

	/**
	 * Final resolved result: entityId -> (block -> resolved attributes)
	 * Populated by merge(); this is the only thing queried at runtime.
	 */
	private static Map<Identifier, EntityBlockAttributeIndex> resolved = Map.of();
	private static int generation = 0;

	public static void init() {
		DefensiveMeasures.LOGGER.info("Loading block projectile config...");

		loadStatic();
	}

	// ////////////////// //
	// GENERATION COUNTER //
	// ////////////////// //

	public static int generation() {
		return generation;
	}

	// ////// //
	// GETTER //
	// ////// //

	/**
	 * Returns the final resolved config, after merging all three tiers. This is the only thing
	 * queried at runtime; the raw tiered configs are not exposed.
	 * <br/><br/>
	 * The returned map uses {@link Identifier} as the key and {@link EntityBlockAttributeIndex} as
	 * the value. It is also important to note that through this configuration, it is possible
	 * to have an entity that does not implements {@link UsesBlockProjectile} but still has a
	 * config entry. While this is possible, the config will only take effect if said entity
	 * implements {@link UsesBlockProjectile} or has a code that explicitly calls this config
	 * manager and uses a config entry for said entity. Otherwise, the config entry will be ignored
	 * and have no effect on the entity.
	 * <br/><br/>
	 * Furthermore, while the config manager allows for any entity to have a config entry, it is
	 * important to note that if said entity uses a config entry but does not implement
	 * {@link UsesBlockProjectile}, it has risks of causing unexpected behavior or crashes. In this
	 * case, the mod and developer of the entity are to be blamed for not following the proper
	 * implementation of the config manager and the {@link UsesBlockProjectile} interface.
	 *
	 * @return an unmodifiable map of entity IDs to their resolved block attribute indices.
	 */
	public static Map<Identifier, EntityBlockAttributeIndex> getResolved() {
		return Map.copyOf(resolved);
	}

	public static EntityBlockAttributeIndex getConfigForEntity(Identifier entityId) {
		return resolved.getOrDefault(
			entityId,
			new EntityBlockAttributeIndex(
				AttributeValues.EMPTY.withDefaults(),
				List.of()
			)
		);
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
		return new EntityBlockAttributeIndex(
			values,
			List.of()
		);
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
					.fillFrom(entityDefault)
					.withDefaults();

				resolvedGroups.add(new ResolvedBlockGroup(ref, resolvedValues));
			}

			out.put(entityId, new EntityBlockAttributeIndex(entityDefault, resolvedGroups));
		}

		resolved = Map.copyOf(out);
		generation++;

		DefensiveMeasures.LOGGER.debug("Turret Ammo Attrs: {}", resolved);
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

		// Ignores tags for those that are as-is (i.e. direct block ID)
		for (RawBlockGroup group : entity.blockGroups()) {
			for (String ref : group.blockRefs()) {
				if (!ref.startsWith("#") && ref.equals(concreteBlockRef)) {
					return group.attributes();
				}
			}
		}

		// Tag matches only
		for (RawBlockGroup group : entity.blockGroups()) {
			for (String ref : group.blockRefs()) {
				if (ref.startsWith("#") && expandRef(ref).contains(concreteBlockRef)) {
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
			: new ResolvedAttributes.Explosive(explosive.enabled(), explosive.damage(), explosive.effectiveRadius());

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
	 *
	 * @apiNote Use the getter methods to retrieve clamped values for {@link #drag()},
	 * {@link #gravity()}, {@link #damage()}, {@link #piercing()}, {@link #fire()}, and {@link #explosive()}.
	 */
	public record AttributeValues(
		double drag, double gravity, double damage, byte piercing,
		Boolean fire, ExplosiveValues explosive
	) {
		public static final Codec<AttributeValues> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.optionalFieldOf("drag", -1.0).forGetter(AttributeValues::drag),
			Codec.DOUBLE.optionalFieldOf("gravity", -1.0).forGetter(AttributeValues::gravity),
			Codec.DOUBLE.optionalFieldOf("damage", -1.0).forGetter(AttributeValues::damage),
			Codec.BYTE.optionalFieldOf("piercing", (byte) -1).forGetter(AttributeValues::piercing),
			Codec.BOOL.optionalFieldOf("fire", false).forGetter(AttributeValues::fire),
			ExplosiveValues.CODEC.optionalFieldOf("explosive", ExplosiveValues.DISABLED).forGetter(AttributeValues::explosive)
		).apply(instance, AttributeValues::new));

		// ////////////// //
		// DEFAULT VALUES //
		// ////////////// //

		/**
		 * Represents an empty set of attribute values, where all numeric fields are negative and
		 * boolean fields are null. This is used as a sentinel value to indicate that no attributes
		 * were specified at this tier.
		 */
		public static final AttributeValues EMPTY = new AttributeValues(-1.0, -1.0, -1.0, (byte) -1, null, null);
		/**
		 * Represents the default attribute values for a block projectile, where in:
		 * <ul>
		 *     <li>{@code drag} is {@code 0.99}</li>
		 *     <li>{@code gravity} is {@code 0.05}</li>
		 *     <li>{@code damage} is {@code 1}</li>
		 *     <li>{@code piercing} is {@code -1} (unset)</li>
		 *     <li>{@code fire} is {@code false}</li>
		 *     <li>{@code explosive} is {@link ExplosiveValues#DISABLED ExplosiveValues.DISABLED}</li>
		 * </ul>
		 * These values are used as the final fallback when no other tiers or entity-specific
		 * defaults provide a value for a given attribute.
		 *
		 * @see ExplosiveValues#DISABLED ExplosiveValues.DISABLED
		 */
		public static final AttributeValues DEFAULT = new AttributeValues(0.99, 0.05, 1, (byte) 0, false, ExplosiveValues.DISABLED);
		/**
		 * Represents a simulated <a href="https://en.wikipedia.org/wiki/Kinetic_bombardment">"Rods from God"</a> projectile, where in:
		 * <ul>
		 *     <li>{@code drag} is {@code 1.0}</li>
		 *     <li>{@code gravity} is {@code 0.1}</li>
		 *     <li>{@code damage} is {@code 500}</li>
		 *     <li>{@code piercing} is {@code 0}</li>
		 *     <li>{@code fire} is {@code false}</li>
		 *     <li>{@code explosive} is a new instance of {@link ExplosiveValues} with the following values:
		 *         <ul>
		 *             <li>{@code enabled} is {@code true}</li>
		 *             <li>{@code damage} is {@code 375}</li>
		 *             <li>{@code effectiveRadius} is {@code 50}</li>
		 *             <li>{@code maxRadius} is {@code 100}</li>
		 *         </ul>
		 *     </li>
		 * </ul>
		 *
		 * <img src="https://i.imgur.com/1yTgjIL.png" alt="Rods from God" width="300"/>
		 *
		 * <p>Wanna spawn one?</p>
		 * <pre>
		 * {@code /summon dm:block_projectile ~ ~100 ~ {Block: "minecraft:iron_bars", Attr: {drag: 1.0, gravity: 0.5, damage: 500, piercing: 0, fire: false, explosive: {enabled: true, damage: 375, effectiveRadius: 50, maxRadius: 100}}}}
		 * </pre>
		 *
		 * @see ExplosiveValues
		 */
		public static final AttributeValues RODS_FROM_GOD = new AttributeValues(
			1.0, 0.5, 500, (byte) 0,
			false, new ExplosiveValues(true, 375, 50, 100)
		);
		/**
		 * An inert version of {@link #RODS_FROM_GOD}, where the explosive is disabled. This can be
		 * used for testing or for scenarios where you want the projectile to have the same physical
		 * properties but without causing an explosion.
		 *
		 * <p>Wanna spawn one?</p>
		 * <pre>
		 * {@code /summon dm:block_projectile ~ ~100 ~ {Block: "minecraft:iron_bars", Attr: {drag: 1.0, gravity: 0.5, damage: 500, piercing: 0, fire: false}}}
		 * </pre>
		 *
		 * @see #RODS_FROM_GOD
		 */
		public static final AttributeValues RODS_FROM_GOD_INERT = new AttributeValues(
			1.0, 0.5, 500, (byte) 0,
			false, ExplosiveValues.DISABLED
		);

		// /////// //
		// GETTERS //
		// /////// //

		public double drag() {
			return this.drag == -1 ?
				-1 : Math.clamp(this.drag, 0, 1);
		}

		public double gravity() {
			return this.gravity == -1 ?
				-1 : Math.clamp(this.gravity, 0.0, 0.5);
		}

		public double damage() {
			return this.damage == -1 ?
				-1 : Math.clamp(this.damage, 0, 1000);
		}

		public byte piercing() {
			return (byte) (this.piercing == -1 ?
				-1 : Math.clamp(this.piercing, 0, 100));
		}

		// ////////////// //
		// STATIC METHODS //
		// ////////////// //

		static AttributeValues fromJson(JsonObject obj) {
			double drag = obj.has("drag") ? obj.get("drag").getAsDouble() : -1.0;
			double gravity = obj.has("gravity") ? obj.get("gravity").getAsDouble() : -1.0;
			double damage = obj.has("damage") ? obj.get("damage").getAsDouble() : -1.0;
			byte piercing = obj.has("piercing") ? obj.get("piercing").getAsByte() : -1;
			Boolean fire = obj.has("fire") ? obj.get("fire").getAsBoolean() : null;

			ExplosiveValues explosive = null;

			if (obj.has("explosive")) {
				JsonElement ex = obj.get("explosive");

				if (ex.isJsonPrimitive() && ex.getAsJsonPrimitive().isBoolean()) {
					explosive = ex.getAsBoolean() ?
						ExplosiveValues.ENABLED : ExplosiveValues.DISABLED;
				}
				else if (ex.isJsonObject()) {
					JsonObject exObj = ex.getAsJsonObject();

					double exDamage = exObj.has("damage") ?
						exObj.get("damage").getAsDouble() : -1.0;
					double exEffectiveRadius = exObj.has("effectiveRadius") ?
						exObj.get("effectiveRadius").getAsDouble() : -1.0;
					double exMaxRadius = exObj.has("maxRadius") ?
						exObj.get("maxRadius").getAsDouble() : -1.0;

					explosive = new ExplosiveValues(true, exDamage, exEffectiveRadius, exMaxRadius);
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
				(byte) pickNumeric(highest, mid, lowest, AttributeValues::piercing),
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

		private static Boolean pickBoolean(AttributeValues a, AttributeValues b, AttributeValues c, Function<AttributeValues, Boolean> getter, Boolean defaultValue) {
			Boolean value = pickBoolean(a, b, c, getter);
			return value == null ? defaultValue : value;
		}

		private static ExplosiveValues pickExplosive(AttributeValues a, AttributeValues b, AttributeValues c) {
			if (a != null && a.explosive() != null) return a.explosive();
			if (b != null && b.explosive() != null) return b.explosive();
			if (c != null) return c.explosive();
			return null;
		}

		AttributeValues withDefaults() {
			return new AttributeValues(
				drag, gravity, damage, piercing,
				fire != null ? fire : Boolean.FALSE,
				explosive != null ? explosive : ExplosiveValues.DISABLED
			);
		}

		@Override
		public String toString() {
			return "AttributeValues{" +
				"drag=" + drag +
				", gravity=" + gravity +
				", damage=" + damage +
				", piercing=" + piercing +
				", fire=" + fire +
				", explosive=" + explosive +
				'}';
		}
	}

	/**
	 * Explosive sub-values follow the same negative-sentinel rule for damage/radius.
	 *
	 * @param enabled whether the projectile is explosive
	 * @param damage the damage dealt by the explosion
	 * @param effectiveRadius the effective radius of the explosion; basically the radius where the damage gets applied fully
	 * @param maxRadius the maximum radius of the explosion
	 *
	 * @apiNote Use the getter methods to retrieve clamped values for {@link #damage()},
	 * {@link #effectiveRadius()}, and {@link #maxRadius()}.
	 */
	public record ExplosiveValues(boolean enabled, double damage, double effectiveRadius, double maxRadius) {
		public static final Codec<ExplosiveValues> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("enabled", false).forGetter(ExplosiveValues::enabled),
			Codec.DOUBLE.optionalFieldOf("damage", 5.0).forGetter(ExplosiveValues::damage),
			Codec.DOUBLE.optionalFieldOf("effectiveRadius", 3.0).forGetter(ExplosiveValues::effectiveRadius),
			Codec.DOUBLE.optionalFieldOf("maxRadius", 5.0).forGetter(ExplosiveValues::maxRadius)
		).apply(instance, ExplosiveValues::new));

		// ////////////// //
		// DEFAULT VALUES //
		// ////////////// //

		/**
		 * Represents an explosive configuration that is disabled, with all numeric fields set to
		 * -1.0. This is used as a sentinel value to indicate that the explosive feature is not
		 * enabled for this projectile, and that no specific explosive attributes have been set at
		 * this tier.
		 */
		public static final ExplosiveValues DISABLED = new ExplosiveValues(false, -1.0, -1.0, -1.0);
		/**
		 * Represents the default explosive configuration that is enabled, with values all its
		 * attributes. As defined, the values are:
		 * <ul>
		 *     <li>{@code enabled} is {@code true}</li>
		 *     <li>{@code damage} is {@code 5.0}</li>
		 *     <li>{@code effectiveRadius} is {@code 3.0}</li>
		 *     <li>{@code maxRadius} is {@code 5.0}</li>
		 * </ul>
		 * These values are used as the final fallback when no other tiers or entity-specific
		 * defaults provide a value for a given explosive attribute.
		 */
		public static final ExplosiveValues ENABLED = new ExplosiveValues(true, 5, 3, 5);

		// /////// //
		// GETTERS //
		// /////// //

		public double damage() {
			return this.damage == -1 ?
				-1 : Math.clamp(this.damage, 1, 1000);
		}

		public double effectiveRadius() {
			return this.effectiveRadius == -1 ?
				-1 : Math.clamp(this.effectiveRadius, 1.0, this.maxRadius());
		}

		public double maxRadius() {
			double minimumRadius = this.effectiveRadius == -1 ?
				1 : this.effectiveRadius;

			return this.maxRadius == -1 ?
				-1 : Math.clamp(this.maxRadius, minimumRadius, 100);
		}
	}

	/**
	 * A resolved block group for a specific entity, after cascading all three config tiers plus
	 * the entity's {@code default}. Contains the concrete block ref and its resolved attributes.
	 *
	 * @param blockRef   The concrete block reference (plain block ID) for this group.
	 * @param attributes The resolved attributes for this block group, after cascading all three config tiers plus the entity's {@code default}.
	 */
	public record ResolvedBlockGroup(String blockRef, AttributeValues attributes) {}

	/**
	 * The final resolved attribute set for a specific entity, after cascading all three
	 * config tiers plus the entity's {@code default}. Contains the entity's resolved
	 * {@code default} plus a list of resolved block groups, each with its concrete block ref and
	 * resolved attributes.
	 *
	 * @param entityDefault The resolved default attributes for the entity, after cascading all three config tiers.
	 * @param blockGroups   The list of resolved block groups for the entity, each containing a concrete block ref and its resolved attributes.
	 */
	public record EntityBlockAttributeIndex(
		AttributeValues entityDefault, List<ResolvedBlockGroup> blockGroups
	) {
		/**
		 * Returns the resolved attributes for a specific block ref
		 * (or ID; i.e.: {@code minecraft:stone}). However, if the block ref is not found in the
		 * list, the method will return the entity's resolved {@code default} attributes instead.
		 *
		 * @param blockRef The concrete block reference (plain block ID) for which to retrieve the resolved attributes.
		 *
		 * @return The resolved attributes for the specified block ref, or the entity's resolved {@code default} if no block group matches.
		 */
		public AttributeValues getBlockAttributes(String blockRef) {
			return blockGroups.stream()
				.filter(group -> group.blockRef().equals(blockRef))
				.findFirst()
				.map(ResolvedBlockGroup::attributes)
				.orElse(entityDefault);
		}

		@Override
		public String toString() {
			String blockGroupsStr = blockGroups.stream()
				.map(group -> group.blockRef() + "=" + group.attributes())
				.reduce((a, b) -> a + ", " + b)
				.orElse("");

			return "EntityBlockAttributeIndex{" +
				"entityDefault=[" + entityDefault + "]" +
				", blockGroups=[" + blockGroupsStr +"]" +
				'}';
		}
	}

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
