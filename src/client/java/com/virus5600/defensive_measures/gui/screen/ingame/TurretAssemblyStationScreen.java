package com.virus5600.defensive_measures.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.CraftingRecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures.screen.TurretAssemblyStationScreenHandler;

@Environment(EnvType.CLIENT)
public class TurretAssemblyStationScreen extends RecipeBookScreen<TurretAssemblyStationScreenHandler> {
	private static final Identifier TEXTURE = Identifier.of(DefensiveMeasures.MOD_ID, "textures/gui/container/turret_assembly_station.png");

	public TurretAssemblyStationScreen(TurretAssemblyStationScreenHandler screenHandler, PlayerInventory inventory, Text title) {
		super(screenHandler, new CraftingRecipeBookWidget(screenHandler), inventory, title);
	}

	protected void init() {
		this.backgroundWidth = 256;
		this.backgroundHeight = 242;

		this.titleX = 29;
		this.titleY = 6;

		this.playerInventoryTitleX = 65;
		this.playerInventoryTitleY = this.backgroundHeight - 96;

		super.init();
	}

	protected ScreenPos getRecipeBookButtonPos() {
		return new ScreenPos(
			this.x + 27,
			this.height / 2 + 55
		);
	}

	protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
		int i = this.x;
		int j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
	}
}
