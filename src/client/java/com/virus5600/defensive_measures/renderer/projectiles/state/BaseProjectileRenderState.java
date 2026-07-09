package com.virus5600.defensive_measures.renderer.projectiles.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

/**
 * The base of all projectile render states, allowing the tracking of yaw and pitch; a barebones
 * implementation and tracking of projectile states.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BaseProjectileRenderState extends EntityRenderState {
	public final AnimationState loopAnimationState = new AnimationState();

	public float yaw;
	public float pitch;
}
