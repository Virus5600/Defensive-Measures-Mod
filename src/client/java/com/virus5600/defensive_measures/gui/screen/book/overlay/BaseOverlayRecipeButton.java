package com.virus5600.defensive_measures.gui.screen.book.overlay;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

import java.util.List;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseOverlayRecipeButton extends AbstractWidget {
	private final BaseOverlayRecipeComponent component;
	private final RecipeDisplayId recipe;
	private final List<Pos> slots;
	private final boolean isCraftable;

	public BaseOverlayRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final BaseOverlayRecipeComponent component,
		final ContextMap context, final boolean isCraftable,
		final int gridWidth, final int gridHeight
	) {
		super(
			x, y,
			gridWidth, gridHeight,
			CommonComponents.EMPTY
		);

		this.component = component;
		this.isCraftable = isCraftable;
		this.recipe = id;
		this.slots = this.calculateIngredientsPositions(recipe, context, gridWidth, gridHeight);

		int slotSize = this.getSlotSize();
		int borderSize = this.getBorderSize();
		int combinedHorizontalGridSize = gridWidth - 1;
		int combinedVerticalGridSize = gridHeight - 1;

		this.width = (slotSize * gridWidth) + (borderSize * 2) + combinedHorizontalGridSize;
		this.height = (slotSize * gridHeight) + (borderSize * 2) + combinedVerticalGridSize;
	}

	protected Pos createGridPos(final int gridXPos, final int gridYPos, final List<ItemStack> itemStacks) {
		int borderSize = this.getBorderSize() * 2;
		int slotSize = this.getSlotSize() + 1;

		return new Pos(
			borderSize + (gridXPos * slotSize),
			borderSize + (gridYPos * slotSize),
			itemStacks
		);
	}

	public void updateWidgetNarration(final NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}

	public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED, this.getSprite(this.isCraftable),
			this.getX(), this.getY(),
			this.width,
			this.height
		);

		float gridPosX = (float) (this.getX() + 1);
		float gridPosY = (float) (this.getY() + 1);

		for(Pos pos : this.slots) {
			graphics.pose().pushMatrix();
			graphics.pose().translate(gridPosX + (float)pos.x, gridPosY + (float)pos.y);
			graphics.pose().scale(pos.scale);
			graphics.pose().translate(-8.0F, -8.0F);
			graphics.item(pos.selectIngredient(this.component.getSlotSelectTime().currentIndex()), 0, 0);
			graphics.pose().popMatrix();
		}
	}

	public RecipeDisplayId getRecipeDisplayId() {
		return this.recipe;
	}

	// //////////////// //
	// ABSTRACT METHODS //
	// //////////////// //

	protected abstract List<Pos> calculateIngredientsPositions(
		final RecipeDisplay recipe, final ContextMap context,
		final int gridWidth, final int gridHeight
	);

	protected abstract Identifier getSprite(boolean isCraftable);

	protected abstract int getBorderSize();

	protected abstract int getSlotSize();

	// ///////////////////////// //
	// INTERNAL PROTECTED RECORD //
	// ///////////////////////// //
	public record Pos(int x, int y, float scale, List<ItemStack> ingredients) {
		public static final float ITEM_RENDER_SCALE = 0.375F;

		public Pos(int x, int y, List<ItemStack> ingredients) {
			this(x, y, ITEM_RENDER_SCALE, ingredients);
		}

		public Pos {
			if (ingredients.isEmpty()) {
				throw new IllegalArgumentException("Ingredient list must be non-empty");
			}
		}

		public ItemStack selectIngredient(final int currentIndex) {
			return this.ingredients.get(currentIndex % this.ingredients.size());
		}
	}
}
