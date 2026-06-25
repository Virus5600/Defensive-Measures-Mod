package com.virus5600.defensive_measures.entity;

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
