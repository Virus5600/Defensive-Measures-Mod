package com.virus5600.defensive_measures.mixins.sound;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.virus5600.defensive_measures._helper.accessor.sound.SoundManagerAccess;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin implements SoundManagerAccess {
	@Shadow @Final private SoundEngine soundEngine;

	@Override
	public SoundEngine dm$getSoundSystem() {
		return this.soundEngine;
	}
}
