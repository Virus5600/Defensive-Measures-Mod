package com.virus5600.DefensiveMeasures.sound;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSoundEvents {
	
	// V1.0
	public final static SoundEvent TURRET_REMOVED_METAL = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.remove.metal"));
	public final static SoundEvent TURRET_REMOVED_WOOD = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.remove.wood"));
	// CANNON
	public final static SoundEvent TURRET_CANNON_HURT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.hurt"), 16f);
	public final static SoundEvent TURRET_CANNON_DESTROYED = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.destroyed"), 8f);
	public final static SoundEvent TURRET_CANNON_SHOOT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.shoot"), 16f);
	// BALLISTA
	public final static SoundEvent TURRET_BALLISTA_HURT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.ballista.hurt"), 16f);
	public final static SoundEvent TURRET_BALLISTA_DESTROYED = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.ballista.destroyed"), 8f);
	public final static SoundEvent TURRET_BALLISTA_SHOOT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.ballista.shoot"), 16f);
	
	private static SoundEvent registerSoundEvent(SoundEvent sound) {
		return Registry.register(Registry.SOUND_EVENT, sound.getId().getPath(), sound);
	}
	
	public static void registerSoundEvents() {
		DefensiveMeasures.LOGGER.debug("REGISTERING SOUND EVENTS FOR " + DefensiveMeasures.MOD_NAME);
		
		// V1.0
		// CANNON
		registerSoundEvent(TURRET_CANNON_HURT);
		registerSoundEvent(TURRET_CANNON_DESTROYED);
		registerSoundEvent(TURRET_CANNON_SHOOT);
		// BALLISTA
		registerSoundEvent(TURRET_BALLISTA_HURT);
		registerSoundEvent(TURRET_BALLISTA_DESTROYED);
		registerSoundEvent(TURRET_BALLISTA_SHOOT);
	}
}