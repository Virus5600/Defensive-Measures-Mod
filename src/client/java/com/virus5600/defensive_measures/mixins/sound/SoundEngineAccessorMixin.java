package com.virus5600.defensive_measures.mixins.sound;

import java.util.Map;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.accessor.sound.SoundSystemAccess;

/**
 * Mixin class that provides access to the private fields and methods of the SoundEngine class.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 *
 * @see SoundManagerMixin
 */
@Mixin(SoundEngine.class)
public abstract class SoundEngineAccessorMixin implements SoundSystemAccess {
	@Shadow @Final private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

	@Override
	public boolean dm$isStopped(SoundInstance soundInstance) {
		ChannelAccess.ChannelHandle sourceManager = this.instanceToChannel.get(soundInstance);

		if (sourceManager == null) {
			DefensiveMeasures.LOGGER.warn("Missing sound manager for sound instance: {}", soundInstance);
			return true;
		}

		return sourceManager.isStopped();
	}
}
