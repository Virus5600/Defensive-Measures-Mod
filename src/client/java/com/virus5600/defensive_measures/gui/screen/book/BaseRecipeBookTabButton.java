package com.virus5600.defensive_measures.gui.screen.book;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

public class BaseRecipeBookTabButton extends ImageButton {
	private static final WidgetSprites TEXTURES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/tab"), Identifier.withDefaultNamespace("recipe_book/tab_selected"));
	public static final int WIDTH = 35;
	public static final int HEIGHT = 27;
	private final RecipeBookComponent.TabInfo tabInfo;
	private static final float BOUNCE_TIME = 15.0F;
	private float animationTime;
	private boolean selected = false;

	public BaseRecipeBookTabButton(int x, int y, RecipeBookComponent.TabInfo tabInfo, Button.OnPress onPress) {
		super(x, y, WIDTH, HEIGHT, TEXTURES, onPress);
		this.tabInfo = tabInfo;
	}

	public void startAnimation(ClientRecipeBook recipeBook, boolean isFiltering) {
		RecipeCollection.CraftableStatus recipesToShow = isFiltering ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY;

		for (RecipeCollection recipeCollection : recipeBook.getCollection(this.tabInfo.category())) {
			for (RecipeDisplayEntry recipe : recipeCollection.getSelectedRecipes(recipesToShow)) {
				if (recipeBook.willHighlight(recipe.id())) {
					this.animationTime = BOUNCE_TIME;
					return;
				}
			}
		}
	}

	public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.animationTime > 0.0F) {
			float squeeze = 1.0F + 0.1F * (float) Math.sin((this.animationTime / 15.0F * (float) Math.PI));
			graphics.pose().pushMatrix();
			graphics.pose().translate((float) (this.getX() + 8), (float) (this.getY() + 12));
			graphics.pose().scale(1.0F, squeeze);
			graphics.pose().translate((float) (-(this.getX() + 8)), (float) (-(this.getY() + 12)));
		}

		Identifier identifier = this.sprites.get(true, this.selected);
		int xPos = this.getX();
		if (this.selected) {
			xPos -= 2;
		}

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, xPos, this.getY(), this.width, this.height);
		this.extractIcon(graphics);
		if (this.animationTime > 0.0F) {
			graphics.pose().popMatrix();
			this.animationTime -= deltaTicks;
		}
	}

	protected void handleCursor(@NonNull GuiGraphicsExtractor context) {
		if (!this.selected) {
			super.handleCursor(context);
		}
	}

	private void extractIcon(GuiGraphicsExtractor graphics) {
		int moveLeft = this.selected ? -2 : 0;

		if (this.tabInfo.secondaryIcon().isPresent()) {
			graphics.fakeItem(this.tabInfo.primaryIcon(), this.getX() + 3 + moveLeft, this.getY() + 5);
			graphics.fakeItem(this.tabInfo.secondaryIcon().get(), this.getX() + 14 + moveLeft, this.getY() + 5);
		} else {
			graphics.fakeItem(this.tabInfo.primaryIcon(), this.getX() + 9 + moveLeft, this.getY() + 5);
		}
	}

	public ExtendedRecipeBookCategory getCategory() {
		return this.tabInfo.category();
	}

	public boolean updateVisibility(ClientRecipeBook book) {
		List<RecipeCollection> collections = book.getCollection(this.tabInfo.category());
		this.visible = false;

		for (RecipeCollection collection : collections) {
			if (collection.hasAnySelected()) {
				this.visible = true;
				break;
			}
		}

		return this.visible;
	}

	public void select() {
		this.selected = true;
	}

	public void unselect() {
		this.selected = false;
	}
}
