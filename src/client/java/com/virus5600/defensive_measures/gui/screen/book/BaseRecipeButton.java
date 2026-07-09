package com.virus5600.defensive_measures.gui.screen.book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

/**
 * A button that represents a recipe collection in the recipe book.
 * <br><br>
 * I don't know what this is ffs.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BaseRecipeButton extends AbstractWidget {
	private final SlotSelectTime currentIndexProvider;
	private static final Identifier SLOT_MANY_CRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_many_craftable");
	private static final Identifier SLOT_CRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_craftable");
	private static final Identifier SLOT_MANY_UNCRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_many_uncraftable");
	private static final Identifier SLOT_UNCRAFTABLE_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_uncraftable");
	private static final Component MORE_RECIPES_TEXT = Component.translatable("gui.recipebook.moreRecipes");

	private RecipeCollection collection;
	private List<ResolvedEntry> selectedEntries;
	private boolean allRecipesHaveSameResultDisplay;
	private float animationTime;

	public BaseRecipeButton(SlotSelectTime currentIndexProvider) {
		super(0, 0, 25, 25, CommonComponents.EMPTY);

		this.collection = RecipeCollection.EMPTY;
		this.selectedEntries = List.of();
		this.currentIndexProvider = currentIndexProvider;
	}

	public void init(RecipeCollection collection, boolean isFiltering, BaseRecipeBookPage page, ContextMap ctx) {
		List<RecipeDisplayEntry> fittingRecipes = collection.getSelectedRecipes(isFiltering ?
			RecipeCollection.CraftableStatus.CRAFTABLE :
			RecipeCollection.CraftableStatus.ANY
		);

		this.collection = collection;
		this.selectedEntries = fittingRecipes.stream()
			.map((entry) -> new ResolvedEntry(entry.id(), entry.resultItems(ctx)))
			.toList();
		this.allRecipesHaveSameResultDisplay = allRecipesHaveSameResultDisplay(this.selectedEntries);

		Stream<RecipeDisplayId> recipeIdStream = fittingRecipes.stream().map(RecipeDisplayEntry::id);
		ClientRecipeBook book = page.getRecipeBook();

		Objects.requireNonNull(book, "Client recipe book is null. Ensure that the client is fully initialized before calling this method.");
		List<RecipeDisplayId> newlyShownRecipes = recipeIdStream.filter(book::willHighlight).toList();

		if (!newlyShownRecipes.isEmpty()) {
			Objects.requireNonNull(page, "Results widget is null. Ensure that the results widget is properly initialized before calling this method.");

			newlyShownRecipes.forEach(page::recipeShown);
			this.animationTime = 15.0F;
		}
	}

	public RecipeCollection getCollection() {
		return this.collection;
	}

	public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		Identifier sprite = SLOT_UNCRAFTABLE_TEXTURE;

		if (this.collection.hasCraftable()) {
			sprite = this.hasMultipleRecipes() ?
				SLOT_MANY_CRAFTABLE_TEXTURE : SLOT_CRAFTABLE_TEXTURE;
		}
		else if (this.hasMultipleRecipes()) {
			sprite = SLOT_MANY_UNCRAFTABLE_TEXTURE;
		}

		boolean shouldAnimate = this.animationTime > 0.0F;

		if (shouldAnimate) {
			float squeeze = 1.0F + 0.1F * (float)Math.sin((this.animationTime / 15.0F * (float)Math.PI));
			graphics.pose().pushMatrix();
			graphics.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
			graphics.pose().scale(squeeze, squeeze);
			graphics.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
			this.animationTime -= deltaTicks;
		}

		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED, sprite,
			this.getX(), this.getY(), this.width, this.height
		);
		ItemStack currentItemStack = this.getDisplayStack();

		int offset = 4;
		if (this.hasMultipleRecipes() && this.allRecipesHaveSameResultDisplay) {
			graphics.item(currentItemStack, this.getX() + offset + 1, this.getY() + offset + 1, 0);
			--offset;
		}

		graphics.fakeItem(currentItemStack, this.getX() + offset, this.getY() + offset);

		if (shouldAnimate) {
			graphics.pose().popMatrix();
		}
	}

	public boolean hasSingleResult() {
		return this.selectedEntries.size() == 1;
	}

	public RecipeDisplayId getCurrentId() {
		int i = this.currentIndexProvider.currentIndex() % this.selectedEntries.size();
		return this.selectedEntries.get(i).id;
	}

	public ItemStack getDisplayStack() {
		int currentIndex = this.currentIndexProvider.currentIndex();
		int resultSize = this.selectedEntries.size();

		if (resultSize == 0) {
			return ItemStack.EMPTY;
		}

		int targetStackIndex = currentIndex / resultSize;
		int targetResultIndex = currentIndex - resultSize * targetStackIndex;

		return this.selectedEntries.get(targetResultIndex).getDisplayStack(targetStackIndex);
	}

	public List<Component> getTooltip(ItemStack stack) {
		List<Component> list = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), stack));
		if (this.hasMultipleRecipes()) {
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

		if (this.hasMultipleRecipes()) {
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

	private boolean hasMultipleRecipes() {
		return this.selectedEntries.size() > 1;
	}

	private static boolean allRecipesHaveSameResultDisplay(List<ResolvedEntry> results) {
		Iterator<ItemStack> itemsIterator = results.stream()
			.flatMap((result) -> result.displayItems().stream())
			.iterator();

		if (itemsIterator.hasNext()) {
			ItemStack firstItem = itemsIterator.next();

			while (itemsIterator.hasNext()) {
				ItemStack nextStack = itemsIterator.next();
				if (!ItemStack.isSameItemSameComponents(firstItem, nextStack)) {
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
	protected record ResolvedEntry(RecipeDisplayId id, List<ItemStack> displayItems) {
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
