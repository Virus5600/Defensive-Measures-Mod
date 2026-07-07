package com.virus5600.defensive_measures.screen.slots;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class SpriteSlot extends Slot {
	private final Identifier sprite;

	private int padding;

	public SpriteSlot(
		final Container container, final int id,
		final int x, final int y
	) {
		this(
			container, id,
			x, y,
			Identifier.fromNamespaceAndPath(
				DefensiveMeasures.MOD_ID,
				"container/container_slot"
			)
		);
	}

	public SpriteSlot(
		final Container container, final int id,
		final int x, final int y,
		final Identifier sprite
	) {
		super(
			container, id,
			x, y
		);

		this.sprite = sprite;
		this.padding = 0;
	}

	// /////// //
	// METHODS //
	// /////// //

	// GETTERS & SETTERS //

	public SpriteSlot setPadding(int padding) {
		this.padding = padding;

		return this;
	}

	public int getPadding() {
		return this.padding;
	}

	public Identifier getSprite() {
		return this.sprite;
	}
}
