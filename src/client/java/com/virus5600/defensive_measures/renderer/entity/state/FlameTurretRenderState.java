package com.virus5600.defensive_measures.renderer.entity.state;

import com.virus5600.defensive_measures.entity.turrets.tier2.FlameTurretEntity;

/**
 * A custom render state class for the {@link FlameTurretEntity Flame Turret}, adding the custom
 * tracking for the lighter's pitch on its nozzle.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class FlameTurretRenderState extends BaseTurretRenderState {
	public float lighterPitch;
}
