package com.virus5600.defensive_measures.renderer.projectiles.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class BaseProjectileRenderState extends EntityRenderState {
	public final AnimationState loopAnimationState = new AnimationState();

	public float yaw;
	public float pitch;
}
