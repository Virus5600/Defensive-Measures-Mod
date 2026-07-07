package com.virus5600.defensive_measures.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.gui.screen.book.WorkshopBlueprintComponent;
import com.virus5600.defensive_measures.screen.WorkshopScreenHandler;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class WorkshopScreen extends BaseRecipeBookScreen<WorkshopScreenHandler> {
	private static final Identifier TEXTURE;

	public WorkshopScreen(
		WorkshopScreenHandler screenHandler, Inventory inventory, Component title
	) {
		super(
			screenHandler, new WorkshopBlueprintComponent(screenHandler), inventory, title,
			336, 185
		);
	}

	protected void init() {
		this.titleLabelX = 4;
		this.titleLabelY = 6;

		this.inventoryLabelX = 169;
		this.inventoryLabelY = 92;

		super.init();
	}

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

	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		super.extractBackground(graphics, mouseX, mouseY, deltaTicks);

		int xOrigin = this.leftPos;
		int yOrigin = (this.height - this.imageHeight) / 2;
		Dimension size = this.getUISize();

		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED, getTexture(),
			xOrigin, yOrigin,
			size.width, size.height
		);
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
	}
}
