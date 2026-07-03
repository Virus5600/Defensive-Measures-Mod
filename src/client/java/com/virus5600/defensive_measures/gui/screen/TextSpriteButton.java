package com.virus5600.defensive_measures.gui.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TextSpriteButton extends ImageButton {
	private final Font font;
	private final Component text;
	private final Component underlinedText;
	private @NonNull TextColor textColor;

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
		this.setTextColor(null);
	}

	public void setTextColor(@Nullable final TextColor textColor) {
		if (textColor != null) {
			this.textColor = textColor;
		}
		else {
			this.textColor = new TextColor(0xFFFFFF, 0xCCCCCC);
		}
	}

	public TextColor getTextColor() {
		return textColor;
	}

	public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractContents(graphics, mouseX, mouseY, a);

		int xPos = this.getX() + (this.width / 2) - (this.font.width(this.underlinedText) / 2);
		int yPos = this.getY() + (this.height / 2) - (this.font.lineHeight / 2);
		Component messageToRender = this.isHoveredOrFocused() ? this.underlinedText : this.text;
		int rawColor = this.textColor.get(this.isActive(), this.isHoveredOrFocused());
		int color = rawColor | Mth.ceil(this.alpha * 255.0F) << 24;

		graphics.text(
			this.font, messageToRender,
			xPos, yPos,
			color
		);

		System.out.println(
			"Color: " + color + " (#" + Integer.toHexString(color) + ") from "
			+ rawColor + " (#" + Integer.toHexString(rawColor) + ")"
		);
	}

	public record TextColor(int enabled, int disabled, int enabledFocus, int disabledFocus) {
		public TextColor() {
			this(16777215);
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
}
