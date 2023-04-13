package com.virus5600.DefensiveMeasures.sound;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ModSoundEvents {
	private ModSoundEvents() { }

	// V1.0
	public static final SoundEvent TURRET_REMOVED_METAL = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.remove.metal"));
	public static final SoundEvent TURRET_REMOVED_WOOD = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.remove.wood"));

	public static final SoundEvent TURRET_REPAIRED_METAL = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.iron.repair"));
	public static final SoundEvent TURRET_REPAIRED_BOW = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.bow.repair"));
	public static final SoundEvent TURRET_REPAIRED_WOOD = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.wood.repair"));

	// CANNON
	public static final SoundEvent TURRET_CANNON_HURT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.hurt"), 16f);
	public static final SoundEvent TURRET_CANNON_DESTROYED = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.destroyed"), 16f);
	public static final SoundEvent TURRET_CANNON_SHOOT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.cannon.shoot"), 16f);
	// BALLISTA
	public static final SoundEvent TURRET_BALLISTA_HURT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.ballista.hurt"), 16f);
	public static final SoundEvent TURRET_BALLISTA_DESTROYED = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.ballista.destroyed"), 16f);
	public static final SoundEvent TURRET_BALLISTA_SHOOT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.ballista.shoot"), 16f);
	// MG TURRET
	public static final SoundEvent TURRET_MG_HURT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.mg_turret.hurt"), 16f);
	public static final SoundEvent TURRET_MG_DESTROYED = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.mg_turret.destroyed"), 16f);
	public static final SoundEvent TURRET_MG_SHOOT = new SoundEvent(new Identifier(DefensiveMeasures.MOD_ID, "turret.mg_turret.shoot"), 16f);

	private static SoundEvent registerSoundEvent(final SoundEvent sound) {
		return Registry.register(Registry.SOUND_EVENT, sound.getId(), sound);
	}

	public static void registerSoundEvents() {
		DefensiveMeasures.LOGGER.debug("REGISTERING SOUND EVENTS FOR " + DefensiveMeasures.MOD_NAME);

		// V1.0 Turrets
		// CANNON
		registerSoundEvent(TURRET_CANNON_HURT);
		registerSoundEvent(TURRET_CANNON_DESTROYED);
		registerSoundEvent(TURRET_CANNON_SHOOT);
		// BALLISTA
		registerSoundEvent(TURRET_BALLISTA_HURT);
		registerSoundEvent(TURRET_BALLISTA_DESTROYED);
		registerSoundEvent(TURRET_BALLISTA_SHOOT);

		// MG TURRET
		registerSoundEvent(TURRET_MG_HURT);
		registerSoundEvent(TURRET_MG_DESTROYED);
		registerSoundEvent(TURRET_MG_SHOOT);

		// V1.0 SFX
		registerSoundEvent(TURRET_REMOVED_METAL);
		registerSoundEvent(TURRET_REMOVED_WOOD);

		registerSoundEvent(TURRET_REPAIRED_METAL);
		registerSoundEvent(TURRET_REPAIRED_BOW);
		registerSoundEvent(TURRET_REPAIRED_WOOD);
	}
}
