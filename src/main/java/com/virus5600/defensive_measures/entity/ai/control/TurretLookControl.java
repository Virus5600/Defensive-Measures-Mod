package com.virus5600.defensive_measures.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

/**
 * A custom look control class for the turret entity, allowing for more precise control over the
 * yaw and pitch rotation speeds.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class TurretLookControl extends LookControl {
	protected float minYawChange;
	protected float minPitchChange;

	public TurretLookControl(Mob entity) {
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
		super.setLookAt(x, y, z, maxYawChange, maxPitchChange);

		this.minYawChange = minYawChange;
		this.minPitchChange = minPitchChange;
	}

	@Override
	public void tick() {
		if (this.resetXRotOnTick()) {
			this.mob.setXRot(0.0F);
		}

		if (this.lookAtCooldown > 0) {
			--this.lookAtCooldown;

			this.getYRotD()
				.ifPresent((yaw) ->
					this.mob.yHeadRot = this.changeAngleMinMax(
						this.mob.yHeadRot, yaw,
						this.minYawChange, this.yMaxRotSpeed
					)
				);

			this.getXRotD()
				.ifPresent((pitch) ->
					this.mob.setXRot(this.changeAngleMinMax(
						this.mob.getXRot(), pitch,
						this.minPitchChange, this.xMaxRotAngle
					))
				);
		} else {
			this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, 10.0F);
		}

		this.clampHeadRotationToBody();
	}

	protected float changeAngleMinMax(float start, float end, float minChange, float maxChange) {
		float f = Mth.degreesDifference(start, end);
		float g = Mth.clamp(f, minChange, maxChange);
		return start + g;
	}

	@Override
	protected void clampHeadRotationToBody() {
	}
}
