package com.virus5600.defensive_measures.sound;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.sound.SoundEvent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class ModSoundEventsTest {
	private static JsonObject LANG;

	@BeforeAll
	static void beforeAll() {
		System.out.println("Running ModSoundEventsTest...");

		SharedConstants.createGameVersion();
		Bootstrap.initialize();

		ModSoundEvents.registerSoundEvents();

		// Load the language file
		try (InputStreamReader reader = new InputStreamReader(
			Objects.requireNonNull(
				ModSoundEvents.class.getResourceAsStream(
					"/assets/dm/lang/en_us.json"
				)
			),
			StandardCharsets.UTF_8
		)) {
			LANG = JsonParser.parseReader(reader)
				.getAsJsonObject();
		} catch (IOException e) {
			DefensiveMeasures.printErr(e);
		}
	}

	@Test
	void testRemoveSoundEvents() {
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_REMOVED_METAL);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_REMOVED_WOOD);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_REPAIR_METAL);
	}

	@Test
	void testRepairSoundEvents() {
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_REPAIR_WOOD);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_REPAIR_BOW);
	}

	@Test
	void testCannonSoundEvents() {
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_CANNON_HURT);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_CANNON_DESTROYED);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_CANNON_SHOOT);
	}

	@Test
	void testBallistaSoundEvents() {
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_BALLISTA_HURT);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_BALLISTA_DESTROYED);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_BALLISTA_SHOOT);
	}

	@Test
	void testMGTurretSoundEvents() {
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_MG_HURT);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_MG_DESTROYED);
		this.assertSoundEventWithSubtitle(ModSoundEvents.TURRET_MG_SHOOT);
	}

	@Test
	void testBulletImpactSoundEvents() {
		this.assertSoundEventWithSubtitle(ModSoundEvents.BULLET_IMPACT_DEFAULT);
		this.assertSoundEventWithSubtitle(ModSoundEvents.BULLET_IMPACT_FLESH);
		this.assertSoundEventWithSubtitle(ModSoundEvents.BULLET_IMPACT_GLASS);
		this.assertSoundEventWithSubtitle(ModSoundEvents.BULLET_IMPACT_GRAINY);
		this.assertSoundEventWithSubtitle(ModSoundEvents.BULLET_IMPACT_METAL);
		this.assertSoundEventWithSubtitle(ModSoundEvents.BULLET_IMPACT_STONE);
		this.assertSoundEventWithSubtitle(ModSoundEvents.BULLET_IMPACT_WOOD);
	}

	// ////////////// //
	// HELPER METHODS //
	// ////////////// //

	private void assertSoundEventWithSubtitle(@NotNull SoundEvent soundEvent) {
		String soundEventId = soundEvent.id().getPath();
		String subtitleKey = "subtitles." + soundEventId;
		String missingStr = "Subtitle key not found: " + subtitleKey;

		// Check if the subtitle key is present in the language file
		Assertions.assertTrue(
			LANG.has(subtitleKey),
			missingStr
		);

		// Check if the subtitle key is not null
		Assertions.assertNotNull(
			LANG.get(subtitleKey),
			missingStr
		);

		// Check if the subtitle key is not empty
		Assertions.assertNotEquals(
			"",
			LANG.get(subtitleKey).getAsString(),
			missingStr
		);

		// Check if the sound event is not null
		this.assertSoundEventExists(soundEvent);
	}

	private void assertSoundEventExists(@NotNull SoundEvent soundEvent) {
		Assertions.assertNotNull(
			soundEvent,
			"Sound event is null: " + soundEvent.id()
		);
	}
}
