package com.virus5600.defensive_measures.sound;

import com.virus5600.defensive_measures.DefensiveMeasures;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Class that holds all the sound events for the mod. This is used to
 * register the sound events that will be used by the mod.
 * <br><br>
 * The custom sounds themselves are registered and stored in this
 * class as static final fields. The sound events are registered
 * in the {@link #registerSoundEvent(String)} method.
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ModSoundEvents {

	// V1.0.0-beta
	public final static SoundEvent TURRET_REMOVED_METAL = ModSoundEvents.registerSoundEvent("turret.remove.metal");
	public final static SoundEvent TURRET_REMOVED_WOOD = ModSoundEvents.registerSoundEvent("turret.remove.wood");
	public final static SoundEvent TURRET_REPAIR_METAL = ModSoundEvents.registerSoundEvent("turret.repair.metal");
	public final static SoundEvent TURRET_REPAIR_WOOD = ModSoundEvents.registerSoundEvent("turret.repair.wood");
	public final static SoundEvent TURRET_REPAIR_BOW = ModSoundEvents.registerSoundEvent("turret.repair.bow");

	// CANNON
	public final static SoundEvent TURRET_CANNON_HURT = ModSoundEvents.registerSoundEvent("turret.cannon.hurt");
	public final static SoundEvent TURRET_CANNON_DESTROYED = ModSoundEvents.registerSoundEvent("turret.cannon.destroyed");
	public final static SoundEvent TURRET_CANNON_SHOOT = ModSoundEvents.registerSoundEvent("turret.cannon.shoot");
	// BALLISTA
	public final static SoundEvent TURRET_BALLISTA_HURT = ModSoundEvents.registerSoundEvent("turret.ballista.hurt");
	public final static SoundEvent TURRET_BALLISTA_DESTROYED = ModSoundEvents.registerSoundEvent("turret.ballista.destroyed");
	public final static SoundEvent TURRET_BALLISTA_SHOOT = ModSoundEvents.registerSoundEvent("turret.ballista.shoot");
	// MG TURRET
	public final static SoundEvent TURRET_MG_HURT = ModSoundEvents.registerSoundEvent("turret.mg_turret.hurt");
	public final static SoundEvent TURRET_MG_DESTROYED = ModSoundEvents.registerSoundEvent("turret.mg_turret.destroyed");
	public final static SoundEvent TURRET_MG_SHOOT = ModSoundEvents.registerSoundEvent("turret.mg_turret.shoot");

	// v1.0.1-beta
	public static final SoundEvent BULLET_IMPACT_DEFAULT = ModSoundEvents.registerSoundEvent("generic.impact.bullet.default");
	public static final SoundEvent BULLET_IMPACT_FLESH = ModSoundEvents.registerSoundEvent("generic.impact.bullet.flesh");
	public static final SoundEvent BULLET_IMPACT_GLASS = ModSoundEvents.registerSoundEvent("generic.impact.bullet.glass");
	public static final SoundEvent BULLET_IMPACT_GRAINY = ModSoundEvents.registerSoundEvent("generic.impact.bullet.grainy");
	public static final SoundEvent BULLET_IMPACT_METAL = ModSoundEvents.registerSoundEvent("generic.impact.bullet.metal");
	public static final SoundEvent BULLET_IMPACT_STONE = ModSoundEvents.registerSoundEvent("generic.impact.bullet.stone");
	public static final SoundEvent BULLET_IMPACT_WOOD = ModSoundEvents.registerSoundEvent("generic.impact.bullet.wood");

	// ANTI-AIR TURRET
	public static final SoundEvent TURRET_ANTI_AIR_HURT = ModSoundEvents.registerSoundEvent("turret.anti_air_turret.hurt");
	public static final SoundEvent TURRET_ANTI_AIR_DESTROYED = ModSoundEvents.registerSoundEvent("turret.anti_air_turret.destroyed");
	public static final SoundEvent TURRET_ANTI_AIR_BEGIN_SHOOT = ModSoundEvents.registerSoundEvent("turret.anti_air_turret.shoot.begin");
	public static final SoundEvent TURRET_ANTI_AIR_SHOOT = ModSoundEvents.registerSoundEvent("turret.anti_air_turret.shoot");
	public static final SoundEvent TURRET_ANTI_AIR_END_SHOOT = ModSoundEvents.registerSoundEvent("turret.anti_air_turret.shoot.end");

	private static SoundEvent registerSoundEvent(final String soundID) {
		Identifier identifier = Identifier.of(DefensiveMeasures.MOD_ID, soundID);
		return Registry.register(
			Registries.SOUND_EVENT,
			identifier,
			SoundEvent.of(identifier)
		);
	}

	public static void registerSoundEvents() {
		DefensiveMeasures.LOGGER.info("REGISTERING SOUND EVENTS FOR {}...", DefensiveMeasures.MOD_NAME);
	}
}
