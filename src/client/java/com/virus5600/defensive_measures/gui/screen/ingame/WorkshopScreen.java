package com.virus5600.defensive_measures.gui.screen.ingame;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.gui.SpriteData;
import com.virus5600.defensive_measures.gui.screen.book.WorkshopBlueprintComponent;
import com.virus5600.defensive_measures.screen.WorkshopScreenHandler;

import java.awt.*;
import java.util.List;

/**
 * A screen (or menu in Mojmap) for the Workshop, extending the {@link BaseRecipeBookScreen} to
 * inherit the recipe book, which is called {@link WorkshopBlueprintComponent Blueprint} in for
 * this bench.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class WorkshopScreen extends BaseRecipeBookScreen<WorkshopScreenHandler> {
	private static final Identifier TEXTURE;
	private static final List<SpriteData> WORKSHOP_CLUTTERS;

	public WorkshopScreen(
		WorkshopScreenHandler screenHandler, Inventory inventory, Component title
	) {
		super(
			screenHandler, new WorkshopBlueprintComponent(screenHandler), inventory, title,
			336, 185
		);
	}

	// ////////////////////// //
	// INITIALIZATION METHODS //
	// ////////////////////// //

	protected void init() {
		this.titleLabelX = 4;
		this.titleLabelY = 6;

		this.inventoryLabelX = 169;
		this.inventoryLabelY = 92;

		super.init();
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //

	protected ScreenPosition getRecipeBookButtonPos(int leftPos, int topPos, int menuWidth, int menuHeight, int screenWidth, int screenHeight) {
		return new ScreenPosition(
			leftPos + (menuWidth / 2) + (int) (menuWidth * 0.0725),
			topPos + (int) (menuHeight * 0.125)
		);
	}

	@Override
	protected WidgetSprites getButtonTexture() {
		return new WidgetSprites(
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/button"),
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/button_highlighted")
		);
	}

	/**
	 * Returns the list of {@link SpriteData sprites} that are just decorations on the UI to fill
	 * up negative spaces.
	 *
	 * @return The list of {@link SpriteData sprites} that are just decorations on the UI to fill
	 *         up negative spaces.
	 */
	public List<SpriteData> getClutterSprites() {
		return WORKSHOP_CLUTTERS;
	}

	// /////////////// //
	// PROCESS METHODS //
	// /////////////// //

	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		super.extractBackground(graphics, mouseX, mouseY, deltaTicks);

		int xContainerOrigin = this.leftPos;
		int yContainerOrigin = (this.height - this.imageHeight) / 2;
		Dimension size = this.getUISize();

		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED, getTexture(),
			xContainerOrigin, yContainerOrigin,
			size.width, size.height
		);

		boolean paintClutter = this.isNarrow() && !this.getRecipeBook().isVisible();
		paintClutter = paintClutter || !this.isNarrow();

		if (paintClutter) {
			// Draw the clutter of the block onto the UI (Yes, it's part of the BG)
			// - Redstone, 2 Iron Ingots, and the Crafting Table Pliers.
			int xSpritesOrigin = xContainerOrigin + (int) (this.imageWidth * 0.6875);
			int ySpritesOrigin = yContainerOrigin + (int) (this.imageHeight * 0.125);

			for (SpriteData sprite : this.getClutterSprites()) {
				sprite.drawSprite(graphics, xSpritesOrigin, ySpritesOrigin);
			}
		}
	}

	// ////////////// //
	// STATIC METHODS //
	// ////////////// //

	protected static Identifier getTexture() {
		return TEXTURE;
	}

	// ///////////////////// //
	// STATIC INITIALIZATION //
	// ///////////////////// //
	static {
		TEXTURE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "container/crafting_container");
		WORKSHOP_CLUTTERS = List.of(
			new SpriteData(
				Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "design/workshop/iron_ingot"),
				new ScreenPosition(-8, 0),
				new Dimension(50, 40),
				30
			),
			new SpriteData(
				Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "design/workshop/redstone"),
				new ScreenPosition(48, -4),
				new Dimension(28, 28),
				45
			),
			new SpriteData(
				Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "design/workshop/pliers"),
				new ScreenPosition(12, 38),
				new Dimension(20, 20),
				145
			),
			new SpriteData(
				Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "design/workshop/iron_ingot"),
				new ScreenPosition(38, 30),
				new Dimension(50, 40),
				287.5F
			)
		);
	}
}
