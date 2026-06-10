package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;

import org.jspecify.annotations.NonNull;

public class BaseRecipeGroupButtonWidget extends ImageButton {
	private static final WidgetSprites TEXTURES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/tab"), Identifier.withDefaultNamespace("recipe_book/tab_selected"));
	public static final int WIDTH = 35;
	public static final int HEIGHT = 27;
	private final BaseRecipeBookWidget.Tab tab;
	private static final float BOUNCE_TIME = 15.0F;
	private float bounce;
	private boolean groupFocused = false;

	public BaseRecipeGroupButtonWidget(int x, int y, BaseRecipeBookWidget.Tab tab, Button.OnPress onPress) {
		super(x, y, WIDTH, HEIGHT, TEXTURES, onPress);
		this.tab = tab;
	}

	public void checkForNewRecipes(ClientRecipeBook recipeBook, boolean filteringCraftable) {
		RecipeCollection.CraftableStatus recipeFilterMode = filteringCraftable ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY;

		for (RecipeCollection recipeResultCollection : recipeBook.getCollection(this.tab.category())) {
			for (RecipeDisplayEntry recipeDisplayEntry : recipeResultCollection.getSelectedRecipes(recipeFilterMode)) {
				if (recipeBook.willHighlight(recipeDisplayEntry.id())) {
					this.bounce = BOUNCE_TIME;
					return;
				}
			}
		}

	}

	public void renderContents(@NonNull GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		if (this.bounce > 0.0F) {
			float f = 1.0F + 0.1F * (float) Math.sin((this.bounce / 15.0F * (float) Math.PI));
			context.pose().pushMatrix();
			context.pose().translate((float) (this.getX() + 8), (float) (this.getY() + 12));
			context.pose().scale(1.0F, f);
			context.pose().translate((float) (-(this.getX() + 8)), (float) (-(this.getY() + 12)));
		}

		Identifier identifier = this.sprites.get(true, this.groupFocused);
		int i = this.getX();
		if (this.groupFocused) {
			i -= 2;
		}

		context.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, i, this.getY(), this.width, this.height);
		this.renderIcons(context);
		if (this.bounce > 0.0F) {
			context.pose().popMatrix();
			this.bounce -= deltaTicks;
		}

	}

	protected void handleCursor(@NonNull GuiGraphics context) {
		if (!this.groupFocused) {
			super.handleCursor(context);
		}

	}

	private void renderIcons(GuiGraphics context) {
		int i = this.groupFocused ? -2 : 0;
		if (this.tab.secondaryIcon().isPresent()) {
			context.renderFakeItem(this.tab.primaryIcon(), this.getX() + 3 + i, this.getY() + 5);
			context.renderFakeItem(this.tab.secondaryIcon().get(), this.getX() + 14 + i, this.getY() + 5);
		} else {
			context.renderFakeItem(this.tab.primaryIcon(), this.getX() + 9 + i, this.getY() + 5);
		}

	}

	public ExtendedRecipeBookCategory getCategory() {
		return this.tab.category();
	}

	public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
		List<RecipeCollection> list = recipeBook.getCollection(this.tab.category());
		this.visible = false;

		for (RecipeCollection recipeResultCollection : list) {
			if (recipeResultCollection.hasAnySelected()) {
				this.visible = true;
				break;
			}
		}

		return this.visible;
	}

	public void focus() {
		this.groupFocused = true;
	}

	public void unfocus() {
		this.groupFocused = false;
	}
}
