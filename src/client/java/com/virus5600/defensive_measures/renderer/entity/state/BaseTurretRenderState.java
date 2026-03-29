package com.virus5600.defensive_measures.renderer.entity.state;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

import com.virus5600.defensive_measures.entity.turrets.interfaces.TurretVariant;

public class BaseTurretRenderState extends LivingEntityRenderState {
	public final AnimationState shootAnimationState = new AnimationState();
	public final AnimationState deathAnimationState = new AnimationState();

	public TurretVariant variant;
	public boolean shooting;
}
