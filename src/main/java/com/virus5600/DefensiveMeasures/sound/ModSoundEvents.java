package com.virus5600.DefensiveMeasures.sound;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSoundEvents {
	
	public final static SoundEvent TURRET_REMOVED_METAL = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.remove.metal"));
	public final static SoundEvent TURRET_REMOVED_WOOD = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.remove.wood"));
	public final static SoundEvent TURRET_CANNON_HURT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.hurt"), 16f);
	public final static SoundEvent TURRET_CANNON_DESTROYED = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.destroyed"), 8f);
	public final static SoundEvent TURRET_CANNON_SHOOT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.shoot"), 16f);
	
	private static SoundEvent registerSoundEvent(SoundEvent sound) {
		return Registry.register(Registry.SOUND_EVENT, sound.getId().getPath(), sound);
	}
	
	public static void registerSoundEvents() {
		DefensiveMeasures.LOGGER.debug("REGISTERING SOUND EVENTS FOR " + DefensiveMeasures.MOD_NAME);
		
		// CANNON
		registerSoundEvent(TURRET_CANNON_HURT);
		registerSoundEvent(TURRET_CANNON_DESTROYED);
		registerSoundEvent(TURRET_CANNON_SHOOT);
	}
}