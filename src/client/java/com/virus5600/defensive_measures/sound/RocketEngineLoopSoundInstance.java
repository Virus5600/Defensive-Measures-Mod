package com.virus5600.defensive_measures.sound;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import com.virus5600.defensive_measures.entity.LoopingSoundEntity;

public class RocketEngineLoopSoundInstance extends LoopingSoundInstance {
	private final LoopingSoundEntity entity;

	public RocketEngineLoopSoundInstance(
		Entity entity,
		SoundEvent loopSound, SoundCategory category,
		boolean randomPitch
	) {
		super(entity, loopSound, category, randomPitch);

		this.entity = (LoopingSoundEntity) entity;
	}

	public RocketEngineLoopSoundInstance(Entity entity, SoundEvent loopSound, SoundCategory category) {
		super(entity, loopSound, category);

		this.entity =  (LoopingSoundEntity) entity;
	}

	@Override
	public boolean canPlay() {
		return super.canPlay() && this.entity.isPlaying();
	}
}
