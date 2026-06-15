package com.virus5600.defensive_measures._helper.accessor;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;

import com.virus5600.defensive_measures.sound.LoopingShootSoundInstance;

/**
 * Chaining from the {@link SoundManagerAccess} interface, this interface calls the private field
 * {@link Channel#stopped()}, allowing sound instances like the {@link LoopingShootSoundInstance}
 * class to utilize said method and check if the audio buffer is stopped, allowing for a more
 * seamless stitching of sounds.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface SoundSystemAccess {
	/**
	 * Determines if the {@link SoundInstance}'s {@link SoundManager sound manager} is stopped and
	 * not playing anymore.
	 *
	 * @param soundInstance The sound instance to check.
	 *
	 * @return whether the manager is already stopped or not.
	 */
	boolean dm$isStopped(SoundInstance soundInstance);
}

