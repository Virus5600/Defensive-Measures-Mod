package com.virus5600.defensive_measures.renderer.entity.state;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class BaseTurretRenderState extends LivingEntityRenderState {
	public final AnimationState idleAnimationState = new AnimationState();
	public final AnimationState shootAnimationState = new AnimationState();
	public final AnimationState deathAnimationState = new AnimationState();

	public UUID id;
	public Vec3d eyePos;
	public int turretLvl;
	public boolean hasTarget;
	public boolean isLockedButNotAttacking;
	public boolean shooting;
	public boolean dead;
}
