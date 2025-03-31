package com.virus5600.defensive_measures.util;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.item.turrets.TurretItem;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;

import net.minecraft.item.Items;
import org.junit.jupiter.api.*;

import java.util.List;

public class ItemUtilTest {
	private boolean isTypeMatchWorking = false;

	@BeforeAll
	static void beforeAll() {
		System.out.println("Running ItemUtilTest...");

		SharedConstants.createGameVersion();
		Bootstrap.initialize();
	}

	@Test
	void testIsTypeMatchMethod() {
		Assertions.assertTrue(
			ItemUtil.isTypeMatch(TurretItem.class, ModItems.MG_TURRET),
			"\"MG Turret\" item should have been a \"Turret Item\"."
		);

		Assertions.assertFalse(
			ItemUtil.isTypeMatch(TurretItem.class, Items.ITEM_FRAME),
			"\"Item Frame\" item should not have been a \"Turret Item\"."
		);

		this.isTypeMatchWorking = true;
	}

	@Test
	void testGetObjectInstance() {
		this.testIsTypeMatchMethod();

		Assertions.assertTrue(
			this.isTypeMatchWorking,
			"`isTypeMatch` method is not working, which `getObjectInstance` method is dependent with."
		);

		TurretItem item = ItemUtil.getObjectInstance(TurretItem.class, ModItems.BALLISTA_TURRET);
		Assertions.assertNotNull(
			item,
			"\"Ballista\" item must not be `null`."
		);

		Assertions.assertInstanceOf(
			TurretItem.class,
			item,
			"\"Ballista\" item must have been a \"TurretItem\" instance, returned \"" + item.getClass().getSimpleName() + "\"."
		);
	}
}
