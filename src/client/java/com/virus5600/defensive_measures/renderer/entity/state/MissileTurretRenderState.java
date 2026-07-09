package com.virus5600.defensive_measures.renderer.entity.state;

import com.virus5600.defensive_measures.entity.turrets.tier2.MissileTurretEntity;

/**
 * A custom render state class for the {@link MissileTurretEntity Missile Turret}, adding the
 * the custome "max range" tracking for an individual Missile Turret to allow its radar dish
 * atop to spin depending on its range.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MissileTurretRenderState extends BaseTurretRenderState {
	public float maxRange;
}
