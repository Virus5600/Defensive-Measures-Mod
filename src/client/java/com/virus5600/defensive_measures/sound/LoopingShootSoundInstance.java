package com.virus5600.defensive_measures.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import com.virus5600.defensive_measures._helper.accessor.SoundManagerAccess;
import com.virus5600.defensive_measures._helper.accessor.SoundSystemAccess;
import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A custom loopSound instance that plays a looping shooting loopSound for a turret. This loopSound instance is
 * anchored to the turret's position and will automatically stop playing when the turret is removed,
 * dead, or no longer has a target to shoot at. This allows for a more immersive and dynamic loopSound
 * experience, as the shooting loopSound will only play while the turret is actively shooting and will
 * not continue to play indefinitely if the turret is destroyed or removed from the world.<br>
 * <br>
 * When starting this instance, do not use the {@link SoundManager#play(SoundInstance)} or the
 * {@link SoundManager#play(SoundInstance, int)}. Instead, use the {@link #startLoop()}
 * method to play the sound instance properly. Using the two former methods will cause the start
 * sound to be skipped and the loop sound to play immediately, which is not the intended behavior.
 *
 * @see #startLoop()
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class LoopingShootSoundInstance extends MovingSoundInstance {
	private final TurretEntity turret;

	protected SoundEvent startSound;
	protected SoundEvent loopSound;
	protected SoundEvent endSound;
	protected boolean randomPitch = false;
	private boolean isLoopPlaying = false;
	private boolean isEndLoopTriggered = false;
	private TickableSoundInstance startSoundInstance;
	private TickableSoundInstance endSoundInstance;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	public LoopingShootSoundInstance(
		TurretEntity turret,
		SoundEvent startSound, SoundEvent loopSound, SoundEvent endSound,
		SoundCategory category, boolean randomPitch
	) {
		this(
			turret,
			startSound, loopSound, endSound,
			category
		);

		this.randomPitch = randomPitch;
	}

	public LoopingShootSoundInstance(
		TurretEntity turret,
		SoundEvent startSound, SoundEvent loopSound, SoundEvent endSound,
		SoundCategory category
	) {
		super(loopSound, category, SoundInstance.createRandom());

		this.turret = turret;

		this.repeat = true;
		this.repeatDelay = 0;

		this.startSound =  startSound;
		this.loopSound =  loopSound;
		this.endSound = endSound;

		this.volume = 1.0F;
		this.pitch = 0.9F + this.random.nextFloat() * 0.2F;

		this.updateSound();
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public boolean canPlay() {
		return !this.turret.isSilent();
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	@Override
	public void tick() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (this.turret == null || (player != null && player.isDead()) && !this.isEndLoopTriggered) {
			this.endLoop();
			return;
		}

		// Handles the case that the turret is removed/gone
		boolean isDead = this.turret.isDead();
		boolean isRemoved = this.turret.isRemoved();
		boolean hasTarget = this.turret.getTrackedLockedButNotAttacking() ||
			this.turret.getTrackedShooting() ||
			this.turret.hasTarget();

		// Once the start sound is done...
		if (this.startSoundInstance != null && this.startSoundInstance.isDone()) {
			// Start playing the loop by setting the volume to 100.
			if (!this.isLoopPlaying) {
				// If the end loop is already triggered...
				if (this.isEndLoopTriggered) {
					// ... and if the end sound is finished, mark this instance as done.
					if (this.endSoundInstance.isDone() && this.getSoundSystem().dm$isStopped(endSoundInstance)) {
						this.setDone();
					}
				}
				// If the end loop isn't triggered yet, then start playing the loop.
				else {
					this.volume = 1f;
					this.isLoopPlaying = true;
				}
			}


			// If dead, removed, or has no target, and the loop is already playing and the end loop
			// hasn't been triggered yet. Then end the loop, playing the end sound.
			if (this.isLoopPlaying &&
				!this.isEndLoopTriggered &&
				(isDead || isRemoved || !hasTarget)
			) {
				this.endLoop();
				return;
			}
		}

		// Keep the loopSound anchored to the turret (in case it is placed on a moving platform/ship)
		this.updateSoundPos();

		// Handles the pitch change.
		this.updateSound();
	}

	/**
	 * Starts the sound instance, playing the start sound of the loop then queuing the loop sound
	 * next, playing the sounds in sequential order.
	 *
	 * @apiNote Use this instead of {@link SoundManager#play(SoundInstance)} to play this sound
	 * instance, as it handles the sequential playing of the start and loop sounds properly. If you
	 * use {@link SoundManager#play(SoundInstance)} directly, it will only play the loop sound and
	 * skip the start sound, which is not the intended behavior for this sound instance.
	 */
	public void startLoop() {
		this.startSoundInstance = this.queueSound(this.startSound, true);
		this.volume = 0f;

		this.queueSound(this, true);
	}

	public void endLoop() {
		this.isLoopPlaying = false;
		this.isEndLoopTriggered = true;
		this.volume = 0f;

		this.endSoundInstance = this.queueSound(this.endSound, false);
	}

	public void updateSoundPos() {
		this.x = this.turret.getX();
		this.y = this.turret.getY();
		this.z = this.turret.getZ();
	}

	public void updateSound() {
		WeightedSoundSet wss = this.getSoundSet(MinecraftClient.getInstance().getSoundManager());

		if (wss != null) {
			this.pitch = this.getPitch();
			this.sound = wss.getSound(this.random);
		}
	}

	// /////// //
	// GETTERS //
	// /////// //

	protected SoundSystemAccess getSoundSystem() {
		return (SoundSystemAccess) ((SoundManagerAccess)
			MinecraftClient
				.getInstance()
				.getSoundManager())
			.dm$getSoundSystem();
	}

	public TickableSoundInstance queueSound(SoundEvent sound, boolean now) {
		LoopingShootSoundInstance shootSoundInstance = this;

		EntityTrackingSoundInstance soundInstance = new EntityTrackingSoundInstance(
			sound, this.getCategory(),
			1f, this.getPitch(),
			this.turret,
			this.getRandom().nextLong()
		) {
			@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
			private Optional<Boolean> shouldAlwaysPlay = Optional.empty();
			@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
			private Optional<Boolean> canPlay = Optional.empty();

			@Override
			public boolean shouldAlwaysPlay() {
				if (this.shouldAlwaysPlay.isEmpty()) {
					this.shouldAlwaysPlay = Optional.of(shootSoundInstance.shouldAlwaysPlay());
				}

				return this.shouldAlwaysPlay.get();
			}

			@Override
			public boolean canPlay() {
				if (this.canPlay.isEmpty()) {
					this.canPlay = Optional.of(shootSoundInstance.canPlay());
				}

				return this.canPlay.get();
			}

			@Override
			public void tick() {
				super.tick();

				if (turret == null) {
					this.setDone();
					return;
				}

				boolean isThisDone = ((SoundSystemAccess) ((SoundManagerAccess)
					MinecraftClient
						.getInstance()
						.getSoundManager())
					.dm$getSoundSystem())
					.dm$isStopped(this);

				if (isThisDone) {
					this.setDone();
				}
			}
		};

		return this.queueSound(soundInstance, now);
	}

	public TickableSoundInstance queueSound(TickableSoundInstance soundInstance, boolean now) {
		SoundManager manager =  MinecraftClient.getInstance().getSoundManager();

		if (now) {
			manager.play(soundInstance);
		}
		else {
			manager.playNextTick(soundInstance);
		}

		return soundInstance;
	}

	public float getVolume() {
		if (this.sound == null) {
			return 1f;
		}

		return this.volume * this.sound.getVolume().get(this.random);
	}

	public Random getRandom() {
		return this.random;
	}

	@Override
	public float getPitch() {
		return this.randomPitch ?
			super.getPitch() : this.pitch;
	}

	@Nullable
	@Override
	public WeightedSoundSet getSoundSet(SoundManager soundManager) {
		if (this.getId().equals(SoundManager.INTENTIONALLY_EMPTY_ID)) {
			this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
			return SoundManager.INTENTIONALLY_EMPTY_SOUND_SET;
		}
		else {
			WeightedSoundSet weightedSoundSet = soundManager.get(this.getId());
			if (weightedSoundSet == null) {
				this.sound = SoundManager.MISSING_SOUND;
			}
			else {
				this.sound = weightedSoundSet.getSound(this.random);
			}

			return weightedSoundSet;
		}
	}

	@Override
	public String toString() {
		boolean hasStarted = this.startSoundInstance != null &&
			this.getSoundSystem().dm$isStopped(this.startSoundInstance);

		Identifier currentId =  hasStarted ?
			this.getId() : this.startSound.id();

		return "LoopingShootSoundInstance[" +
			currentId +
			"]@" + this.hashCode();
	}
}
