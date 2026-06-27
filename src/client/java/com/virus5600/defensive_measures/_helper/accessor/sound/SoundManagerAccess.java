package com.virus5600.defensive_measures._helper.accessor.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.SoundEngine;

import com.virus5600.defensive_measures.mixins.sound.SoundManagerMixin;

/**
 * Exposes the sound system from the {@link SoundManager} class, allowing access to the
 * much needed {@link Channel#stopped()} method
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface SoundManagerAccess {
	/**
	 * Exposes the {@link SoundManager#soundEngine} field via the {@link SoundManagerMixin}
	 *
	 * @return {@link SoundEngine}
	 */
	SoundEngine dm$getSoundSystem();
}
