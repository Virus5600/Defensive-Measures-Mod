package com.virus5600.defensive_measures.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class TurretLookControl extends LookControl {
	protected float minYawChange;
	protected float minPitchChange;

	public TurretLookControl(MobEntity entity) {
		super(entity);
	}

	public void lookAt(Entity entity, float minYawChange, float maxYawChange, float minPitchChange, float maxPitchChange) {
		this.lookAt(
			entity.getX(), entity.getEyeY(), entity.getZ(),
			minYawChange, maxYawChange,
			minPitchChange, maxPitchChange
		);
	}

	public void lookAt(double x, double y, double z, float minYawChange, float maxYawChange, float minPitchChange, float maxPitchChange) {
		super.lookAt(x, y, z, maxYawChange, maxPitchChange);

		this.minYawChange = minYawChange;
		this.minPitchChange = minPitchChange;
	}

	@Override
	public void tick() {
		if (this.shouldStayHorizontal()) {
			this.entity.setPitch(0.0F);
		}

		if (this.lookAtTimer > 0) {
			--this.lookAtTimer;

			this.getTargetYaw()
				.ifPresent((yaw) ->
					this.entity.headYaw = this.changeAngleMinMax(
						this.entity.headYaw, yaw,
						this.minYawChange, this.maxYawChange
					)
				);

			this.getTargetPitch()
				.ifPresent((pitch) ->
					this.entity.setPitch(this.changeAngleMinMax(
						this.entity.getPitch(), pitch,
						this.minYawChange, this.maxPitchChange
					))
				);
		} else {
			this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, 10.0F);
		}

		this.clampHeadYaw();
	}

	protected float changeAngleMinMax(float start, float end, float minChange, float maxChange) {
		float f = MathHelper.subtractAngles(start, end);
		float g = MathHelper.clamp(f, minChange, maxChange);
		return start + g;
	}

	@Override
	protected void clampHeadYaw() {
	}
}
