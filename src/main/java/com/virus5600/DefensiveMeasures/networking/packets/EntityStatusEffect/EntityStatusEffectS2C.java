package com.virus5600.DefensiveMeasures.networking.packets.EntityStatusEffect;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;

public class EntityStatusEffectS2C extends EntityStatusEffectS2CPacket {

	public EntityStatusEffectS2C(int entityId, StatusEffectInstance effect) {
		super(entityId, effect);
	}
}
