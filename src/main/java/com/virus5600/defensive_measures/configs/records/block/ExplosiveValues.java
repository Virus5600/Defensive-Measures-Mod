package com.virus5600.defensive_measures.configs.records.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Explosive sub-values follow the same negative-sentinel rule for damage/radius.
 *
 * @param enabled         Whether the explosive feature is enabled for this projectile. If {@code false}, all other fields are ignored.
 * @param damage          The damage ({@code 0}-{@code 1000}) dealt by the explosion. Negative = unset.
 * @param effectiveRadius The effective radius ({@code 1.0}-{@code 100.0}) of the explosion; basically the radius where the damage gets applied fully. Negative = unset.
 * @param maxRadius       The maximum radius ({@code effectiveRadius}-{@code 100.0}) of the explosion. Negative = unset.
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
	/**
	 * Explosive that is switched on but has no numbers of its own yet (what {@code "explosive": true}
	 * parses to). Lets lower tiers or the entity {@code default} supply the damage/radii before
	 * {@link #withDefaults()} falls back to {@link #ENABLED}.
	 */
	public static final ExplosiveValues ENABLED_UNSET = new ExplosiveValues(true, -1.0, -1.0, -1.0);

	// /////// //
	// GETTERS //
	// /////// //

	public double damage() {
		return this.damage < 0 ?
			-1 : Math.clamp(this.damage, 1, 1000);
	}

	public double effectiveRadius() {
		if (this.effectiveRadius < 0) {
			return -1;
		}

		double upperBound = this.maxRadius < 0 ?
			100 : this.maxRadius();

		return Math.clamp(this.effectiveRadius, 1.0, upperBound);
	}

	public double maxRadius() {
		if (this.maxRadius < 0) {
			return -1;
		}

		double minimumRadius = this.effectiveRadius < 0 ?
			1 : Math.clamp(this.effectiveRadius, 1.0, 100);

		return Math.clamp(this.maxRadius, minimumRadius, 100);
	}

	// ///////// //
	// CASCADING //
	// ///////// //

	/**
	 * Combines two tiers' explosive settings, {@code high} winning. The {@code enabled} flag comes
	 * from the highest tier that has an explosive at all; each number comes from the highest tier
	 * that set it.
	 *
	 * @return the combined values, or whichever side is non-null (or {@code null} if both are).
	 */
	static ExplosiveValues orElse(ExplosiveValues high, ExplosiveValues low) {
		if (high == null) return low;
		if (low == null) return high;

		return new ExplosiveValues(
			high.enabled,
			high.damage < 0 ? low.damage : high.damage,
			high.effectiveRadius < 0 ? low.effectiveRadius : high.effectiveRadius,
			high.maxRadius < 0 ? low.maxRadius : high.maxRadius
		);
	}

	/**
	 * Finalizes this explosive: disabled becomes {@link #DISABLED}; enabled has every unset
	 * number filled from {@link #ENABLED}.
	 */
	public ExplosiveValues withDefaults() {
		if (!this.enabled) {
			return DISABLED;
		}

		return new ExplosiveValues(
			true,
			this.damage < 0 ? ENABLED.damage : this.damage,
			this.effectiveRadius < 0 ? ENABLED.effectiveRadius : this.effectiveRadius,
			this.maxRadius < 0 ? ENABLED.maxRadius : this.maxRadius
		);
	}
}
