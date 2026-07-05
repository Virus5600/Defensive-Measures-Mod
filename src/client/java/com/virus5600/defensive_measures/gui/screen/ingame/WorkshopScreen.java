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

@Environment(EnvType.CLIENT)
public class WorkshopScreen extends BaseRecipeBookScreen<WorkshopScreenHandler> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "textures/gui/container/turret_assembly_station.png");

	public WorkshopScreen(
		WorkshopScreenHandler screenHandler, Inventory inventory, Component title
	) {
		super(
			screenHandler, new WorkshopBlueprintComponent(screenHandler), inventory, title,
			256, 242
		);
	}

	protected void init() {
		this.titleLabelX = 29;
		this.titleLabelY = 6;

		this.inventoryLabelX = 65;
		this.inventoryLabelY = this.imageHeight - 96;

		super.init();
	}

	protected ScreenPosition getRecipeBookButtonPos(int leftPos, int topPos, int imageWidth, int imageHeight, int screenWidth, int screenHeight) {
		return new ScreenPosition(
			leftPos + 27,
			screenHeight / 2 + 55
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

		int x = this.leftPos;
		int y = (this.height - this.imageHeight) / 2;

		graphics.blit(
			RenderPipelines.GUI_TEXTURED, TEXTURE,
			x, y, 0.0F, 0.0F,
			this.imageWidth, this.imageHeight,
			256, 256
		);
	}
}
