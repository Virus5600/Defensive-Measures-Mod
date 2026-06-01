package com.virus5600.defensive_measures.mixin;

import java.util.Map;

import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.accessor.SoundSystemAccess;

@Mixin(SoundSystem.class)
public abstract class SoundSystemAccessorMixin implements SoundSystemAccess {
	@Shadow @Final private Map<SoundInstance, Channel.SourceManager> sources;

	@Override
	public boolean dm$isStopped(SoundInstance soundInstance) {
		Channel.SourceManager sourceManager = this.sources.get(soundInstance);

		if (sourceManager == null) {
			DefensiveMeasures.LOGGER.warn("Missing sound source for sound instance: {}", soundInstance);
			return true;
		}

		return sourceManager.isStopped();
	}
}

