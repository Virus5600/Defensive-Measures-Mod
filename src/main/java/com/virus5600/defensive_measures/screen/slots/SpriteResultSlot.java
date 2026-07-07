package com.virus5600.defensive_measures.screen.slots;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;

import com.virus5600.defensive_measures.DefensiveMeasures;

public class SpriteResultSlot extends ResultSlot {
	private final Identifier sprite;

	private int padding;

	public SpriteResultSlot(
		final Player player, final CraftingContainer craftSlots,
		final Container container, final int id,
		final int x, final int y
	) {
		this(
			player, craftSlots,
			container, id,
			x, y,
			Identifier.fromNamespaceAndPath(
				DefensiveMeasures.MOD_ID,
				"container/container_slot"
			)
		);
	}

	public SpriteResultSlot(
		final Player player, final CraftingContainer craftSlots,
		final Container container, final int id,
		final int x, final int y,
		final Identifier sprite
	) {
		super(
			player, craftSlots,
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

	public SpriteResultSlot setPadding(int padding) {
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
