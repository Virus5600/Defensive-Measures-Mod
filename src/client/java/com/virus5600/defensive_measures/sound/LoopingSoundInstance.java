package com.virus5600.defensive_measures.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class LoopingSoundInstance extends MovingSoundInstance {
	private final Entity entity;

	protected SoundEvent loopSound;
	protected boolean randomPitch = false;

	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	public LoopingSoundInstance(
		Entity entity,
		SoundEvent loopSound, SoundCategory category,
		boolean randomPitch
	) {
		this(entity, loopSound, category);

		this.randomPitch = randomPitch;
	}

	public LoopingSoundInstance(Entity entity, SoundEvent loopSound, SoundCategory category) {
		super(loopSound, category, SoundInstance.createRandom());

		this.entity = entity;
		this.loopSound = loopSound;

		this.repeat = true;
		this.repeatDelay = 0;

		this.volume = 1.0F;
		this.pitch = 0.9F + this.random.nextFloat() * 0.2F;

		this.updateSoundPos();
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	@Override
	public boolean canPlay() {
		return !this.entity.isSilent();
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	@Override
	public void tick() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (this.entity == null
			|| this.entity.isRemoved()
			|| !this.entity.isAlive()
			|| this.entity.isSilent()
			|| this.entity.getEntityWorld() == null
			|| !this.canPlay()
			|| (player != null && player.isDead())
		) {
			this.setDone();
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
