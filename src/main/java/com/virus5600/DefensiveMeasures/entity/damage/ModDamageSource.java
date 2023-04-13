package com.virus5600.DefensiveMeasures.entity.damage;

import net.minecraft.entity.damage.DamageSource;

public class ModDamageSource extends DamageSource {

	public static final DamageSource ARROWHEAD = new ModDamageSource("arrowhead").setBypassesArmor().setFromFalling();

	protected ModDamageSource(final String name) {
		super(name);
	}
}
