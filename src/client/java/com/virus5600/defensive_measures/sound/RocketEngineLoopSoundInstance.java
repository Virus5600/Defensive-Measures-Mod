package com.virus5600.defensive_measures.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import com.virus5600.defensive_measures.entity.LoopingSoundEntity;

public class RocketEngineLoopSoundInstance extends LoopingSoundInstance {
	private final LoopingSoundEntity entity;

	public RocketEngineLoopSoundInstance(
            Entity entity,
            SoundEvent loopSound, SoundSource category,
            boolean randomPitch
	) {
		super(entity, loopSound, category, randomPitch);

		this.entity = (LoopingSoundEntity) entity;
	}

	public RocketEngineLoopSoundInstance(Entity entity, SoundEvent loopSound, SoundSource category) {
		super(entity, loopSound, category);

		this.entity =  (LoopingSoundEntity) entity;
	}

	@Override
	public boolean canPlaySound() {
		return super.canPlaySound() && this.entity.isPlaying();
	}
}
