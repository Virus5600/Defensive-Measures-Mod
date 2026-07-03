package com.virus5600.defensive_measures.gui.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TextSpriteButton extends ImageButton {
	private final Font font;
	private final Component text;
	private final Component underlinedText;
	private @NonNull TextColor textColor;
	private TextAlignment textAlignment;
	private boolean hasShadow = false;
	private boolean doUnderline = false;
	private Dimension margin = new Dimension(0, 0);

	public TextSpriteButton(
		final int x, final int y, final int width, final int height,
		final Component text, final Font font,
		final WidgetSprites sprites,
		final Button.OnPress onPress
	) {
		this(x, y, width, height, text, font, sprites, onPress, text);
	}

	public TextSpriteButton(
		final int x, final int y, final int width, final int height,
		final Component text, final Font font,
		final WidgetSprites sprites,
		final Button.OnPress onPress,
		final Component narrationMsg
	) {
		super(x, y, width, height, sprites, onPress, narrationMsg);

		this.font = font;
		this.text = text;
		this.underlinedText = ComponentUtils.mergeStyles(text, Style.EMPTY.withUnderlined(true));
		this.setTextAlignment(TextAlignment.CENTER)
			.setTextColor(null);
	}

	// /////// //
	// METHODS //
	// /////// //

	public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractContents(graphics, mouseX, mouseY, a);

		ScreenPosition textPos = this.setPosition(this.textAlignment, this.margin.width, this.margin.height);
		Component messageToRender = (this.isHoveredOrFocused() && this.doUnderline) ? this.underlinedText : this.text;
		int rawColor = this.textColor.get(this.isActive(), this.isHoveredOrFocused());
		int color = rawColor | Mth.ceil(this.alpha * 255.0F) << 24;

		graphics.text(
			this.font, messageToRender,
			textPos.x(), textPos.y(),
			color, this.hasShadow
		);
	}

	protected ScreenPosition setPosition(TextAlignment alignment) {
		return this.setPosition(alignment, 0);
	}

	protected ScreenPosition setPosition(TextAlignment alignment, int margin) {
		return this.setPosition(alignment, margin, margin);
	}

	protected final ScreenPosition setPosition(TextAlignment alignment, int xMargin, int yMargin) {
		int textWidth = this.font.width(this.text);
		int textHeight = this.font.lineHeight;

		List<Function<Integer, Integer>> xFormula = List.of(
			// LEFT
			xm -> this.getX() + xm,
			// CENTER
			xm -> this.getX() + (this.width / 2) - (textWidth / 2) + xm,
			// RIGHT
			xm -> this.getX() + this.width - textWidth - xm
		);

		List<Function<Integer, Integer>> yFormula = List.of(
			// TOP
			ym -> this.getY() + ym,
			ym -> this.getY() + (this.height / 2) - (textHeight / 2) + ym,
			ym -> this.getY() + this.height - textHeight - ym
		);

		return new ScreenPosition(
			xFormula.get(alignment.xOrdinal()).apply(xMargin),
			yFormula.get(alignment.yOrdinal()).apply(yMargin)
		);
	}

	// ///////////////// //
	// GETTERS & SETTERS //
	// ///////////////// //
	public TextSpriteButton setTextAlignment(TextAlignment alignment) {
		this.textAlignment = alignment;
		return this;
	}

	public TextAlignment getTextAlignment() {
		return this.textAlignment;
	}

	public TextSpriteButton setMargin(int margin) {
		return this.setMargin(margin, margin);
	}

	public TextSpriteButton setMargin(int x, int y) {
		return this.setMargin(new Dimension(x, y));
	}

	public TextSpriteButton setMargin(Dimension margin) {
		this.margin = margin;

		return this;
	}

	public Dimension getMargin() {
		return this.margin;
	}

	public TextSpriteButton setTextColor(@Nullable final TextColor textColor) {
		this.textColor = Objects.requireNonNullElseGet(
			textColor,
			() -> new TextColor(0x505050, 0x404040)
		);

		return this;
	}

	public TextColor getTextColor() {
		return textColor;
	}

	public TextSpriteButton hasShadow(final boolean hasShadow) {
		this.hasShadow = hasShadow;
		return this;
	}

	public boolean hasShadow() {
		return this.hasShadow;
	}

	public TextSpriteButton doUnderline(final boolean doUnderline) {
		this.doUnderline = doUnderline;
		return this;
	}

	public boolean doUnderline() {
		return this.doUnderline;
	}

	// //////////// //
	// LOCAL RECORD //
	// //////////// //

	public record TextColor(int enabled, int disabled, int enabledFocus, int disabledFocus) {
		public TextColor() {
			this(0xFFFFFF, 0xAAAAAA);
		}

		public TextColor(final int color) {
			this(color, color);
		}

		public TextColor(final int enabled, final int focused) {
			this(enabled, enabled, focused, focused);
		}

		public TextColor(final int enabled, final int disabled, final int focused) {
			this(enabled, disabled, focused, disabled);
		}

		public int get(final boolean enabled, final boolean focused) {
			if (enabled) {
				return focused ? this.enabledFocus : this.enabled;
			}
			else {
				return focused ? this.disabledFocus : this.disabled;
			}
		}
	}

	// ////////// //
	// LOCAL ENUM //
	// ////////// //
	public enum TextAlignment {
		/**
		 * <table>
		 *     <tr>
		 *         <td>X</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		TOP_LEFT(0, 0),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>X</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		TOP_CENTER(1, 0),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>X</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		TOP_RIGHT(2, 0),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>X</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		LEFT(0, 1),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>X</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		CENTER(1, 1),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>X</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		RIGHT(2, 1),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>X</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		BOTTOM_LEFT(0, 2),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>X</td>
		 *         <td>-</td>
		 *     </tr>
		 * </table>
		 */
		BOTTOM_CENTER(1, 2),
		/**
		 * <table>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>-</td>
		 *     </tr>
		 *     <tr>
		 *         <td>-</td>
		 *         <td>-</td>
		 *         <td>X</td>
		 *     </tr>
		 * </table>
		 */
		BOTTOM_RIGHT(2, 2),
		;


		private final ScreenPosition positionOrdinal;

		TextAlignment(int x, int y) {
			this.positionOrdinal = new ScreenPosition(x, y);
		}

		TextAlignment(TextAlignment alignment) {
			this.positionOrdinal = alignment.positionOrdinal;
		}

		public int xOrdinal() {
			return this.positionOrdinal.x();
		}

		public int yOrdinal() {
			return this.positionOrdinal.y();
		}
	}

	// //////////////////// //
	// FUNCTIONAL INTERFACE //
	// //////////////////// //
	@FunctionalInterface
	private interface TriFunction<A, B, C, R> {
		R apply(final A a, final B b, final C c);
	}
}
