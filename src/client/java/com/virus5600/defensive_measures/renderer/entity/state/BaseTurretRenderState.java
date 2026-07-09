package com.virus5600.defensive_measures.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class BaseTurretRenderState extends LivingEntityRenderState {
	public final AnimationState setupAnimationState = new AnimationState();
	public final AnimationState teardownAnimationState = new AnimationState();
	public final AnimationState idleAnimationState = new AnimationState();
	public final AnimationState shootAnimationState = new AnimationState();
	public final AnimationState deathAnimationState = new AnimationState();

	public UUID id;
	public UUID vehicleId;
	public Vec3 eyePos;
	public Vec3 currentBarrelPos;
	public int turretLvl;
	public boolean hasTarget;
	public boolean isLockedButNotAttacking;
	public boolean shooting;
	public boolean dead;
	public boolean isSettingUp;
	public boolean isTearingDown;
}
