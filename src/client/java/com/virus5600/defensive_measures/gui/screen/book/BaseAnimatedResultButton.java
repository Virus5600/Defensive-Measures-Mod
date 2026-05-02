package com.virus5600.defensive_measures.gui.screen.book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BaseAnimatedResultButton extends ClickableWidget {
	private final CurrentIndexProvider currentIndexProvider;
	private static final Identifier SLOT_MANY_CRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_many_craftable");
	private static final Identifier SLOT_CRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_craftable");
	private static final Identifier SLOT_MANY_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_many_uncraftable");
	private static final Identifier SLOT_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla("recipe_book/slot_uncraftable");
	private static final Text MORE_RECIPES_TEXT = Text.translatable("gui.recipebook.moreRecipes");

	private RecipeResultCollection resultCollection;
	private List<Result> results;
	private boolean allResultEqual;
	private float bounce;

	public BaseAnimatedResultButton(CurrentIndexProvider currentIndexProvider) {
		super(0, 0, 25, 25, ScreenTexts.EMPTY);

		this.resultCollection = RecipeResultCollection.EMPTY;
		this.results = List.of();
		this.currentIndexProvider = currentIndexProvider;
	}

	public void showResultCollection(RecipeResultCollection resultCollection, boolean filteringCraftable, BaseRecipeBookResults results, ContextParameterMap ctx) {
		List<RecipeDisplayEntry> entries = resultCollection.filter(filteringCraftable ?
			RecipeResultCollection.RecipeFilterMode.CRAFTABLE :
			RecipeResultCollection.RecipeFilterMode.ANY
		);

		this.resultCollection = resultCollection;
		this.results = entries.stream()
			.map((entry) -> new Result(entry.id(), entry.getStacks(ctx)))
			.toList();
		this.allResultEqual = areAllResultsEqual(this.results);

		Stream<NetworkRecipeId> recipeIdStream = entries.stream().map(RecipeDisplayEntry::id);
		ClientRecipeBook clientRecipeBook = results.getRecipeBook();

		Objects.requireNonNull(clientRecipeBook, "Client recipe book is null. Ensure that the client is fully initialized before calling this method.");
		List<NetworkRecipeId> recipeIdList = recipeIdStream.filter(clientRecipeBook::isHighlighted).toList();

		if (!recipeIdList.isEmpty()) {
			Objects.requireNonNull(results, "Results widget is null. Ensure that the results widget is properly initialized before calling this method.");

			recipeIdList.forEach(results::onRecipeDisplayed);
			this.bounce = 15.0F;
		}
	}

	public RecipeResultCollection getResultCollection() {
		return this.resultCollection;
	}

	public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float deltaTicks) {
		Identifier id = SLOT_UNCRAFTABLE_TEXTURE;

		if (this.resultCollection.hasCraftableRecipes()) {
			id = this.hasMultipleResults() ?
				SLOT_MANY_CRAFTABLE_TEXTURE : SLOT_CRAFTABLE_TEXTURE;
		}
		else if (this.hasMultipleResults()) {
			id = SLOT_MANY_UNCRAFTABLE_TEXTURE;
		}

		boolean shouldBounce = this.bounce > 0.0F;

		if (shouldBounce) {
			float f = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float)Math.PI));
			ctx.getMatrices().pushMatrix();
			ctx.getMatrices().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
			ctx.getMatrices().scale(f, f);
			ctx.getMatrices().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
			this.bounce -= deltaTicks;
		}

		ItemStack itemStack = this.getDisplayStack();
		ctx.drawGuiTexture(
			RenderPipelines.GUI_TEXTURED, id,
			this.getX(), this.getY(), this.width, this.height
		);

		int i = 4;
		if (this.hasMultipleResults() && this.allResultEqual) {
			ctx.drawItem(itemStack, this.getX() + i + 1, this.getY() + i + 1, 0);
			--i;
		}

		ctx.drawItemWithoutEntity(itemStack, this.getX() + i, this.getY() + i);

		if (shouldBounce) {
			ctx.getMatrices().popMatrix();
		}
	}

	public boolean hasSingleResult() {
		return this.results.size() == 1;
	}

	public NetworkRecipeId getCurrentId() {
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

	public List<Text> getTooltip(ItemStack stack) {
		List<Text> list = new ArrayList<>(Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack));
		if (this.hasMultipleResults()) {
			list.add(MORE_RECIPES_TEXT);
		}

		return list;
	}

	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(
			NarrationPart.TITLE,
			Text.translatable(
				"narration.recipe",
				this.getDisplayStack().getName()
			)
		);

		if (this.hasMultipleResults()) {
			builder.put(
				NarrationPart.USAGE,
				Text.translatable("narration.button.usage.hovered"),
				Text.translatable("narration.recipe.usage.more")
			);
		} else {
			builder.put(
				NarrationPart.USAGE,
				Text.translatable("narration.button.usage.hovered")
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
				if (!ItemStack.areItemsAndComponentsEqual(currentStack, nextStack)) {
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
	protected record Result(NetworkRecipeId id, List<ItemStack> displayItems) {
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
