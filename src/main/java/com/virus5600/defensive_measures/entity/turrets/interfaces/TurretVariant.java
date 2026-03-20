package com.virus5600.defensive_measures.entity.turrets.interfaces;

import java.util.Arrays;
import java.util.Comparator;

public interface TurretVariant {
	int getId();

	static TurretVariant byId(int id, TurretVariant[] variants) {
		return Arrays.stream(variants)
			.sorted(Comparator.
				comparingInt(TurretVariant::getId))
			.toArray(TurretVariant[]::new)[id];
	}
}
