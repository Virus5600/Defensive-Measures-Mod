package com.virus5600.defensive_measures.gui.screen.book.overlay;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.*;

import com.virus5600.defensive_measures.DefensiveMeasures;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BaseOverlayRecipeComponent implements GuiEventListener, Renderable {
	public static final int MAX_ROW = 4;
	public static final int MAX_ROW_LARGE = 5;
	public static final int BUTTON_SIZE = 25;
	public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "container/crafting_container");

	private final List<BaseOverlayRecipeButton> recipeButtons = Lists.newArrayList();
	private final SlotSelectTime slotSelectTime;
	private final boolean isFurnaceMenu;

	private @Nullable RecipeDisplayId lastRecipeClicked;
	private RecipeCollection collection;
	private boolean isVisible;
	private int x;
	private int y;

	// /////////// //
	// CONSTRUCTOR //
	// /////////// //

	public BaseOverlayRecipeComponent(final SlotSelectTime slotSelectTime, final boolean isFurnaceMenu) {
		this.collection = RecipeCollection.EMPTY;
		this.slotSelectTime = slotSelectTime;
		this.isFurnaceMenu = isFurnaceMenu;
	}

	// ////////////////////// //
	// INITIALIZATION METHODS //
	// ////////////////////// //

	public void init(
		final RecipeCollection collection, final ContextMap context, final boolean isFiltering,
		final int buttonX, final int buttonY, final int centerX, final int centerY,
		final float buttonWidth
	) {
		this.collection = collection;
		List<RecipeDisplayEntry> craftable = collection.getSelectedRecipes(RecipeCollection.CraftableStatus.CRAFTABLE);
		List<RecipeDisplayEntry> unCraftable = isFiltering ? Collections.emptyList() : collection.getSelectedRecipes(RecipeCollection.CraftableStatus.NOT_CRAFTABLE);
		int craftables = craftable.size();
		int total = craftables + unCraftable.size();
		int maxRow = total <= 16 ? this.getMaxRow() : this.getMaxRowLarge();
		int rows = (int) Math.ceil((double) total / (double) maxRow);
		this.x = buttonX;
		this.y = buttonY;
		float rightPos = (float) (this.x + Math.min(total, maxRow) * this.getButtonSize());
		float maxLeftPos = (float) (centerX + 50);
		float bottomPos = (float) (this.y + rows * this.getButtonSize());
		float maxBottomPos = (float) (centerY + 50);
		float topPos = (float)this.y;
		float maxTopPos = (float)(centerY - 100);
		this.isVisible = true;

		this.recipeButtons.clear();

		if (rightPos > maxLeftPos) {
			this.x = (int) ((float) this.x - buttonWidth *
				(float) ((int) ((rightPos - maxLeftPos) / buttonWidth)));
		}
		if (bottomPos > maxBottomPos) {
			this.y = (int) ((float) this.y - buttonWidth *
				(float) Mth.ceil((bottomPos - maxBottomPos) / buttonWidth));
		}
		if (topPos < maxTopPos) {
			this.y = (int) ((float) this.y - buttonWidth *
				(float) Mth.ceil((topPos - maxTopPos) / buttonWidth));
		}

		for (int i = 0; i < total; ++i) {
			boolean canCraft = i < craftables;
			RecipeDisplayEntry recipe = canCraft ? craftable.get(i) : unCraftable.get(i - craftables);
			int x = this.x + 5 + this.getButtonSize() * (i % maxRow);
			int y = this.y + 5 + this.getButtonSize() * (i / maxRow);

			if (i % maxRow != 0) {
				x += 2;
			}
			else {
				if (i != 0) {
					y += 2;
				}
			}

			if (this.isFurnaceMenu) {
				this.recipeButtons.add(
					this.getSmeltingOverlayRecipeButton(
						x, y,
						recipe.id(), recipe.display(),
						context, canCraft
					)
				);
			}
			else {
				this.recipeButtons.add(
					this.getCraftingOverlayRecipeButton(
						x, y,
						recipe.id(), recipe.display(),
						context, canCraft
					)
				);
			}
		}

		this.lastRecipeClicked = null;
	}

	// /////////////////// //
	// OVERRIDABLE METHODS //
	// /////////////////// //

	protected Identifier getOverlayTexture() {
		return TEXTURE;
	}

	protected int getMaxRow() {
		return MAX_ROW;
	}

	protected int getMaxRowLarge() {
		return MAX_ROW_LARGE;
	}

	protected int getButtonSize() {
		return BUTTON_SIZE;
	}

	protected ModOverlaySmeltingRecipeButton getSmeltingOverlayRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final ContextMap context, final boolean isCraftable
	) {
		return new ModOverlaySmeltingRecipeButton(
			x, y,
			id, recipe,
			this, context, isCraftable
		);
	}

	protected ModOverlayCraftingRecipeButton getCraftingOverlayRecipeButton(
		final int x, final int y,
		final RecipeDisplayId id, final RecipeDisplay recipe,
		final ContextMap context, final boolean isCraftable
	) {
		return new ModOverlayCraftingRecipeButton(
			x, y,
			id, recipe,
			this, context, isCraftable
		);
	}

	// /////// //
	// METHODS //
	// /////// //

	public RecipeCollection getRecipeCollection() {
		return this.collection;
	}

	@Nullable
	public RecipeDisplayId getLastRecipeClicked() {
		return this.lastRecipeClicked;
	}

	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		if (event.button() == 0) {
			for (BaseOverlayRecipeButton btn : this.recipeButtons) {
				if (btn.mouseClicked(event, doubleClick)) {
					this.lastRecipeClicked = btn.getRecipeDisplayId();
					return true;
				}
			}
		}

		return false;
	}

	public boolean isMouseOver(final double mouseX, final double mouseY) {
		return false;
	}

	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		if (this.isVisible) {
			int size = this.recipeButtons.size();
			int maxRow = size <= 16 ? this.getMaxRow() : this.getMaxRowLarge();
			int width = Math.min(size, maxRow);
			int height = Mth.ceil((float) size / (float) maxRow);

			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, this.getOverlayTexture(),
				this.x, this.y,
				width * this.getButtonSize() + 12,
				height * this.getButtonSize() + 10
			);

			for(BaseOverlayRecipeButton component : this.recipeButtons) {
				component.extractRenderState(graphics, mouseX, mouseY, a);
			}
		}
	}

	public void setVisible(final boolean visible) {
		this.isVisible = visible;
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	public void setFocused(final boolean focused) {
	}

	public boolean isFocused() {
		return false;
	}

	public SlotSelectTime getSlotSelectTime() {
		return this.slotSelectTime;
	}
}
