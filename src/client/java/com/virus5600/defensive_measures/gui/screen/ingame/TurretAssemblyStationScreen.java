package com.virus5600.defensive_measures.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.gui.screen.book.BlueprintWidget;
import com.virus5600.defensive_measures.screen.TurretAssemblyStationScreenHandler;

@Environment(EnvType.CLIENT)
public class TurretAssemblyStationScreen extends BaseRecipeBookScreen<TurretAssemblyStationScreenHandler> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "textures/gui/container/turret_assembly_station.png");

	public TurretAssemblyStationScreen(TurretAssemblyStationScreenHandler screenHandler, Inventory inventory, Component title) {
		super(screenHandler, new BlueprintWidget(screenHandler), inventory, title);
	}

	protected void init() {
		this.imageWidth = 256;
		this.imageHeight = 242;

		this.titleLabelX = 29;
		this.titleLabelY = 6;

		this.inventoryLabelX = 65;
		this.inventoryLabelY = this.imageHeight - 96;

		super.init();
	}

	protected ScreenPosition getRecipeBookButtonPos() {
		return new ScreenPosition(
			this.leftPos + 27,
			this.height / 2 + 55
		);
	}

	@Override
	protected WidgetSprites getButtonTexture() {
		return new WidgetSprites(
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/button"),
			Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "blueprint/button_highlighted")
		);
	}

	protected void renderBg(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
		int x = this.leftPos;
		int y = (this.height - this.imageHeight) / 2;

		context.blit(
			RenderPipelines.GUI_TEXTURED, TEXTURE,
			x, y, 0.0F, 0.0F,
			this.imageWidth, this.imageHeight,
			256, 256
		);
	}
}
