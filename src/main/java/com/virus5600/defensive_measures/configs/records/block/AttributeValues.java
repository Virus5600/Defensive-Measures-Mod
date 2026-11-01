package com.virus5600.defensive_measures.configs.records.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A sparse set of attribute fields. Numeric fields (drag/gravity/damage/piercing) use a
 * negative value to mean "not specified at this tier"; {@code fire} uses {@code null} for
 * the same purpose (needed so cascading can distinguish "omitted" from "explicitly false").
 * Resolved by cascading — see {@link #cascade} — and finalized with {@link #withDefaults()}.
 *
 * @param drag      The drag coefficient ({@code 0.0}-{@code 1.0}), where {@code 1.0} is no drag and {@code 0.0} is instant stop. Negative = unset.
 * @param gravity   The gravity factor ({@code 0.0}-{@code 0.5}), where {@code 0.0} is no gravity and {@code 0.5} is normal gravity. Negative = unset.
 * @param damage    The base damage ({@code 0}-{@code 1000}) of the projectile. Negative = unset. Do note that the turret may add its own base damage to this value, so the final damage may be higher than this value.
 * @param piercing  The piercing level of the projectile ({@code 0} means no piercing, higher values which caps at {@code 100} allow it to pierce more entities). Negative = unset.
 * @param fire      Whether the projectile sets entities on fire. {@code null} = unset at this tier.
 * @param explosive The {@link ExplosiveValues explosive} properties of the projectile, if any.
 *
 * @apiNote Use the getter methods to retrieve clamped values for {@link #drag()},
 * {@link #gravity()}, {@link #damage()}, {@link #piercing()}, {@link #fire()}, and {@link #explosive()}.
 *
 * @see ExplosiveValues
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
		return this.drag < 0 ?
			-1 : Math.clamp(this.drag, 0, 1);
	}

	public double gravity() {
		return this.gravity < 0 ?
			-1 : Math.clamp(this.gravity, 0.0, 0.5);
	}

	public double damage() {
		return this.damage < 0 ?
			-1 : Math.clamp(this.damage, 0, 1000);
	}

	public byte piercing() {
		return (byte) (this.piercing < 0 ?
			-1 : Math.clamp(this.piercing, 0, 100));
	}

	// ////////////// //
	// STATIC METHODS //
	// ////////////// //

	public static AttributeValues fromJson(JsonObject obj) {
		double drag = number(obj, "drag");
		double gravity = number(obj, "gravity");
		double damage = number(obj, "damage");
		byte piercing = obj.has("piercing") ?
			(byte) Math.clamp(obj.get("piercing").getAsInt(), -1, 100) : -1;
		Boolean fire = obj.has("fire") ? obj.get("fire").getAsBoolean() : null;

		ExplosiveValues explosive = null;

		if (obj.has("explosive")) {
			JsonElement ex = obj.get("explosive");

			if (ex.isJsonPrimitive() && ex.getAsJsonPrimitive().isBoolean()) {
				// "true" only says "enabled"; the numbers are filled in by lower tiers/defaults.
				explosive = ex.getAsBoolean() ?
					ExplosiveValues.ENABLED_UNSET : ExplosiveValues.DISABLED;
			}
			else if (ex.isJsonObject()) {
				JsonObject exObj = ex.getAsJsonObject();

				explosive = new ExplosiveValues(
					true,
					number(exObj, "damage"),
					number(exObj, "effectiveRadius"),
					number(exObj, "maxRadius")
				);
			}
		}

		return new AttributeValues(drag, gravity, damage, piercing, fire, explosive);
	}

	private static double number(JsonObject obj, String key) {
		return obj.has(key) ? obj.get(key).getAsDouble() : -1.0;
	}

	/**
	 * Cascades tiers in priority order (highest first). Each field is taken from the first tier
	 * that sets it; {@code null} tiers are skipped. Explosive sub-values cascade field by field
	 * too (see {@link ExplosiveValues#orElse}). Nothing is defaulted here, so the result can
	 * still contain unset fields — call {@link #withDefaults()} once the last tier is in.
	 */
	public static AttributeValues cascade(AttributeValues... tiers) {
		double drag = -1, gravity = -1, damage = -1;
		byte piercing = -1;
		Boolean fire = null;
		ExplosiveValues explosive = null;

		for (AttributeValues tier : tiers) {
			if (tier == null) {
				continue;
			}

			if (drag < 0) drag = tier.drag;
			if (gravity < 0) gravity = tier.gravity;
			if (damage < 0) damage = tier.damage;
			if (piercing < 0) piercing = tier.piercing;
			if (fire == null) fire = tier.fire;

			explosive = ExplosiveValues.orElse(explosive, tier.explosive);
		}

		return new AttributeValues(drag, gravity, damage, piercing, fire, explosive);
	}

	/** Fills any still-unset field in this instance from a fallback. */
	public AttributeValues fillFrom(AttributeValues fallback) {
		return cascade(this, fallback);
	}

	/**
	 * Finalizes this set: every unset field is filled from {@link #DEFAULT}, and an enabled
	 * explosive gets its unset sub-values from {@link ExplosiveValues#ENABLED}. The result has no
	 * unset fields (no negatives, no {@code null}s).
	 */
	public AttributeValues withDefaults() {
		AttributeValues merged = cascade(this, DEFAULT);

		return new AttributeValues(
			merged.drag, merged.gravity, merged.damage, merged.piercing,
			merged.fire, merged.explosive.withDefaults()
		);
	}
}
