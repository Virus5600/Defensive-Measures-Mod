package com.virus5600.defensive_measures.entity;

import com.virus5600.defensive_measures.command.PlayAnimationCommand;

/**
 * Provides the list of common turret animations. When more animations are added, they will be
 * added to this enum as well to let the {@link PlayAnimationCommand} know what animations are
 * available for the turret entity to play.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public enum CommonTurretAnimations {
	SETUP("setup"),
	TEARDOWN("teardown"),

	IDLE("idle"),
	SHOOT("shoot"),
	DEATH("death")
	;

	private final String name;

	CommonTurretAnimations(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return "CommonTurretAnimations[name=\"" + this.name + "\"]";
	}
}
