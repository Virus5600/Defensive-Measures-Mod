package com.virus5600.defensive_measures._helper.accessor;

import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;

import com.virus5600.defensive_measures.mixin.SoundManagerMixin;

/**
 * Exposes the sound system from the {@link SoundManager} class, allowing access to the
 * much needed {@link Source#isStopped()} method
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface SoundManagerAccess {
	/**
	 * Exposes the {@link SoundManager#soundSystem} field via the {@link SoundManagerMixin}
	 *
	 * @return {@link SoundSystem}
	 */
	SoundSystem dm$getSoundSystem();
}

