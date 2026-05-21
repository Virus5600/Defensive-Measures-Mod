package com.virus5600.defensive_measures.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import com.virus5600.defensive_measures.entity.turrets.TurretEntity;

import org.jspecify.annotations.Nullable;

/**
 * A custom loopSound instance that plays a looping shooting loopSound for a turret. This loopSound instance is
 * anchored to the turret's position and will automatically stop playing when the turret is removed,
 * dead, or no longer has a target to shoot at. This allows for a more immersive and dynamic loopSound
 * experience, as the shooting loopSound will only play while the turret is actively shooting and will
 * not continue to play indefinitely if the turret is destroyed or removed from the world.
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
	private boolean hasPlayedOnce = false;

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

		this.updateSoundPos();

		this.queueSound(this.startSound, true);
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
		if (this.turret == null) {
			this.endLoop();
			return;
		}

		// Keep the loopSound anchored to the turret (in case it is placed on a moving platform/ship)
		this.updateSoundPos();

		// Handles the case that the turret is removed/gone
		boolean isDead = this.turret.isDead();
		boolean isRemoved = this.turret.isRemoved();
		boolean hasTarget = this.turret.getTrackedLockedButNotAttacking() ||
			this.turret.getTrackedShooting() ||
			this.turret.hasTarget();

		if (this.hasPlayedOnce && (isDead || isRemoved || !hasTarget)) {
			this.endLoop();
			return;
		}

		// Handles the pitch change.
		WeightedSoundSet wss = this.getSoundSet(MinecraftClient.getInstance().getSoundManager());
		if (wss != null) {
			this.pitch = this.getPitch();
			this.sound = wss.getSound(this.random);
		}

		if (!this.hasPlayedOnce) {
			this.hasPlayedOnce = true;
		}
	}

	public void endLoop() {
		this.setDone();

		this.queueSound(this.endSound, false);
	}

	public void updateSoundPos() {
		this.x = this.turret.getX();
		this.y = this.turret.getY();
		this.z = this.turret.getZ();
	}

	// /////// //
	// GETTERS //
	// /////// //

	protected void queueSound(SoundEvent sound, boolean now) {
		LoopingShootSoundInstance shootSoundInstance = this;
		EntityTrackingSoundInstance soundInstance = new EntityTrackingSoundInstance(
			sound, this.getCategory(),
			this.getVolume(), this.getPitch(),
			this.turret,
			this.random.nextLong()
		) {
			@Override
			public boolean shouldAlwaysPlay() {
				return shootSoundInstance.shouldAlwaysPlay();
			}

			@Override
			public boolean canPlay() {
				return shootSoundInstance.canPlay();
			}
		};

		if (now) {
			MinecraftClient.getInstance()
				.getSoundManager()
				.play(soundInstance);
		}
		else {
			MinecraftClient.getInstance()
				.getSoundManager()
				.playNextTick(soundInstance);
		}
	}

	public float getVolume() {
		if (this.sound == null) {
			return 0f;
		}

		return this.volume * this.sound.getVolume().get(this.random);
	}

	public boolean hasPlayedOnce() {
		return this.hasPlayedOnce;
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
		} else {
			WeightedSoundSet weightedSoundSet = soundManager.get(this.getId());
			if (weightedSoundSet == null) {
				this.sound = SoundManager.MISSING_SOUND;
			} else {
				this.sound = weightedSoundSet.getSound(this.random);
			}

			return weightedSoundSet;
		}
	}

	@Override
	public String toString() {
		return "LoopingShootSoundInstance[" + this.id + "]@" + this.hashCode();
	}
}
