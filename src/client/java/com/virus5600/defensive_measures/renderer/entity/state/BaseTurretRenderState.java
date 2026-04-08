package com.virus5600.defensive_measures.renderer.entity.state;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

public class BaseTurretRenderState extends LivingEntityRenderState {
	public final AnimationState idleAnimationState = new AnimationState();
	public final AnimationState shootAnimationState = new AnimationState();
	public final AnimationState deathAnimationState = new AnimationState();

	public int turretLvl;
	public boolean shooting;
}
