package com.virus5600.defensive_measures.mixin;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.virus5600.defensive_measures._helper.accessor.SoundManagerAccess;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin implements SoundManagerAccess {
	@Shadow @Final private SoundSystem soundSystem;

	@Override
	public SoundSystem dm$getSoundSystem() {
		return this.soundSystem;
	}
}

