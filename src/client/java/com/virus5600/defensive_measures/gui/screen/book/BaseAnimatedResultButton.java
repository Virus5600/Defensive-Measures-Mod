package com.virus5600.defensive_measures.gui.screen.book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BaseAnimatedResultButton extends AbstractWidget {
	private final SlotSelectTime currentIndexProvider;
	private static final Identifier SLOT_MANY_CRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_many_craftable");
	private static final Identifier SLOT_CRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_craftable");
	private static final Identifier SLOT_MANY_UNCRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_many_uncraftable");
	private static final Identifier SLOT_UNCRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_uncraftable");
	private static final Component MORE_RECIPES_TEXT = Component.translatable("gui.recipebook.moreRecipes");

	private RecipeCollection resultCollection;
	private List<Result> results;
	private boolean allResultEqual;
	private float bounce;

	public BaseAnimatedResultButton(SlotSelectTime currentIndexProvider) {
		super(0, 0, 25, 25, CommonComponents.EMPTY);

		this.resultCollection = RecipeCollection.EMPTY;
		this.results = List.of();
		this.currentIndexProvider = currentIndexProvider;
	}

	public void showResultCollection(RecipeCollection resultCollection, boolean filteringCraftable, BaseRecipeBookResults results, ContextMap ctx) {
		List<RecipeDisplayEntry> entries = resultCollection.getSelectedRecipes(filteringCraftable ?
			RecipeCollection.CraftableStatus.CRAFTABLE :
			RecipeCollection.CraftableStatus.ANY
		);

		this.resultCollection = resultCollection;
		this.results = entries.stream()
			.map((entry) -> new Result(entry.id(), entry.resultItems(ctx)))
			.toList();
		this.allResultEqual = areAllResultsEqual(this.results);

		Stream<RecipeDisplayId> recipeIdStream = entries.stream().map(RecipeDisplayEntry::id);
		ClientRecipeBook clientRecipeBook = results.getRecipeBook();

		Objects.requireNonNull(clientRecipeBook, "Client recipe book is null. Ensure that the client is fully initialized before calling this method.");
		List<RecipeDisplayId> recipeIdList = recipeIdStream.filter(clientRecipeBook::willHighlight).toList();

		if (!recipeIdList.isEmpty()) {
			Objects.requireNonNull(results, "Results widget is null. Ensure that the results widget is properly initialized before calling this method.");

			recipeIdList.forEach(results::onRecipeDisplayed);
			this.bounce = 15.0F;
		}
	}

	public RecipeCollection getResultCollection() {
		return this.resultCollection;
	}

	public void renderWidget(@NonNull GuiGraphics ctx, int mouseX, int mouseY, float deltaTicks) {
		Identifier id = SLOT_UNCRAFTABLE_TEXTURE;

		if (this.resultCollection.hasCraftable()) {
			id = this.hasMultipleResults() ?
				SLOT_MANY_CRAFTABLE_TEXTURE : SLOT_CRAFTABLE_TEXTURE;
		}
		else if (this.hasMultipleResults()) {
			id = SLOT_MANY_UNCRAFTABLE_TEXTURE;
		}

		boolean shouldBounce = this.bounce > 0.0F;

		if (shouldBounce) {
			float f = 1.0F + 0.1F * (float)Math.sin((this.bounce / 15.0F * (float)Math.PI));
			ctx.pose().pushMatrix();
			ctx.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
			ctx.pose().scale(f, f);
			ctx.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
			this.bounce -= deltaTicks;
		}

		ItemStack itemStack = this.getDisplayStack();
		ctx.blitSprite(
			RenderPipelines.GUI_TEXTURED, id,
			this.getX(), this.getY(), this.width, this.height
		);

		int i = 4;
		if (this.hasMultipleResults() && this.allResultEqual) {
			ctx.renderItem(itemStack, this.getX() + i + 1, this.getY() + i + 1, 0);
			--i;
		}

		ctx.renderFakeItem(itemStack, this.getX() + i, this.getY() + i);

		if (shouldBounce) {
			ctx.pose().popMatrix();
		}
	}

	public boolean hasSingleResult() {
		return this.results.size() == 1;
	}

	public RecipeDisplayId getCurrentId() {
		int i = this.currentIndexProvider.currentIndex() % this.results.size();
		return this.results.get(i).id;
	}

	public ItemStack getDisplayStack() {
		int currentIndex = this.currentIndexProvider.currentIndex();
		int resultSize = this.results.size();

		if (resultSize == 0) {
			return ItemStack.EMPTY;
		}

		int targetStackIndex = currentIndex / resultSize;
		int targetResultIndex = currentIndex - resultSize * targetStackIndex;

		return this.results.get(targetResultIndex).getDisplayStack(targetStackIndex);
	}

	public List<Component> getTooltip(ItemStack stack) {
		List<Component> list = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), stack));
		if (this.hasMultipleResults()) {
			list.add(MORE_RECIPES_TEXT);
		}

		return list;
	}

	public void updateWidgetNarration(NarrationElementOutput builder) {
		builder.add(
			NarratedElementType.TITLE,
			Component.translatable(
				"narration.recipe",
				this.getDisplayStack().getHoverName()
			)
		);

		if (this.hasMultipleResults()) {
			builder.add(
				NarratedElementType.USAGE,
				Component.translatable("narration.button.usage.hovered"),
				Component.translatable("narration.recipe.usage.more")
			);
		} else {
			builder.add(
				NarratedElementType.USAGE,
				Component.translatable("narration.button.usage.hovered")
			);
		}
	}

	private boolean hasMultipleResults() {
		return this.results.size() > 1;
	}

	private static boolean areAllResultsEqual(List<Result> results) {
		Iterator<ItemStack> iterator = results.stream()
			.flatMap((result) -> result.displayItems().stream())
			.iterator();

		if (iterator.hasNext()) {
			ItemStack currentStack = iterator.next();

			while (iterator.hasNext()) {
				ItemStack nextStack = iterator.next();
				if (!ItemStack.isSameItemSameComponents(currentStack, nextStack)) {
					return false;
				}
			}
		}
		return true;
	}

	// ///////////// //
	// CUSTOM RECORD //
	// ///////////// //
	@Environment(EnvType.CLIENT)
	protected record Result(RecipeDisplayId id, List<ItemStack> displayItems) {
		public ItemStack getDisplayStack(int currentIndex) {
			if (this.displayItems.isEmpty()) {
				return ItemStack.EMPTY;
			}
			else {
				int i = currentIndex % this.displayItems.size();
				return this.displayItems().get(i);
			}
		}
	}
}
