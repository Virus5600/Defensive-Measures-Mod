package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.util.Identifier;

import java.util.List;

public class BaseRecipeGroupButtonWidget extends TexturedButtonWidget {
	private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/tab"), Identifier.ofVanilla("recipe_book/tab_selected"));
	public static final int WIDTH = 35;
	public static final int HEIGHT = 27;
	private final BaseRecipeBookWidget.Tab tab;
	private static final float BOUNCE_TIME = 15.0F;
	private float bounce;
	private boolean groupFocused = false;

	public BaseRecipeGroupButtonWidget(int x, int y, BaseRecipeBookWidget.Tab tab, ButtonWidget.PressAction onPress) {
		super(x, y, WIDTH, HEIGHT, TEXTURES, onPress);
		this.tab = tab;
	}

	public void checkForNewRecipes(ClientRecipeBook recipeBook, boolean filteringCraftable) {
		RecipeResultCollection.RecipeFilterMode recipeFilterMode = filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY;

		for (RecipeResultCollection recipeResultCollection : recipeBook.getResultsForCategory(this.tab.category())) {
			for (RecipeDisplayEntry recipeDisplayEntry : recipeResultCollection.filter(recipeFilterMode)) {
				if (recipeBook.isHighlighted(recipeDisplayEntry.id())) {
					this.bounce = BOUNCE_TIME;
					return;
				}
			}
		}

	}

	public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.bounce > 0.0F) {
			float f = 1.0F + 0.1F * (float) Math.sin((double) (this.bounce / 15.0F * (float) Math.PI));
			context.getMatrices().pushMatrix();
			context.getMatrices().translate((float) (this.getX() + 8), (float) (this.getY() + 12));
			context.getMatrices().scale(1.0F, f);
			context.getMatrices().translate((float) (-(this.getX() + 8)), (float) (-(this.getY() + 12)));
		}

		Identifier identifier = this.textures.get(true, this.groupFocused);
		int i = this.getX();
		if (this.groupFocused) {
			i -= 2;
		}

		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i, this.getY(), this.width, this.height);
		this.renderIcons(context);
		if (this.bounce > 0.0F) {
			context.getMatrices().popMatrix();
			this.bounce -= deltaTicks;
		}

	}

	protected void setCursor(DrawContext context) {
		if (!this.groupFocused) {
			super.setCursor(context);
		}

	}

	private void renderIcons(DrawContext context) {
		int i = this.groupFocused ? -2 : 0;
		if (this.tab.secondaryIcon().isPresent()) {
			context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 3 + i, this.getY() + 5);
			context.drawItemWithoutEntity((ItemStack) this.tab.secondaryIcon().get(), this.getX() + 14 + i, this.getY() + 5);
		} else {
			context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 9 + i, this.getY() + 5);
		}

	}

	public RecipeBookGroup getCategory() {
		return this.tab.category();
	}

	public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
		List<RecipeResultCollection> list = recipeBook.getResultsForCategory(this.tab.category());
		this.visible = false;

		for (RecipeResultCollection recipeResultCollection : list) {
			if (recipeResultCollection.hasDisplayableRecipes()) {
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
