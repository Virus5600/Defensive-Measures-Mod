package com.virus5600.defensive_measures.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class LoopingSoundInstance extends AbstractTickableSoundInstance {
	private final Entity entity;

	protected SoundEvent loopSound;
	protected boolean randomPitch = false;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	public LoopingSoundInstance(
            Entity entity,
            SoundEvent loopSound, SoundSource category,
            boolean randomPitch
	) {
		this(entity, loopSound, category);

		this.randomPitch = randomPitch;
	}

	public LoopingSoundInstance(Entity entity, SoundEvent loopSound, SoundSource category) {
		super(loopSound, category, SoundInstance.createUnseededRandom());

		this.entity = entity;
		this.loopSound = loopSound;

		this.looping = true;
		this.delay = 0;

		this.volume = 1.0F;
		this.pitch = 0.9F + this.random.nextFloat() * 0.2F;

		this.updateSoundPos();
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public boolean canPlaySound() {
		return !this.entity.isSilent();
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public void tick() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (this.entity == null
			|| this.entity.isRemoved()
			|| !this.entity.isAlive()
			|| this.entity.isSilent()
			|| this.entity.level() == null
			|| !this.canPlaySound()
			|| (player != null && player.isDeadOrDying())
		) {
			this.stop();
			return;
		}

		this.updateSoundPos();

		// Keep pitch stable or randomize once
		if (this.randomPitch) {
			this.pitch = 1.0F + (this.random.nextFloat() - 0.5F) * 0.1F;
		}
		else {
			this.pitch = 1.0F;
		}

		this.volume = 1.0F;
	}

	public void updateSoundPos() {
		this.x = this.entity.getX();
		this.y = this.entity.getY();
		this.z = this.entity.getZ();
	}
}
