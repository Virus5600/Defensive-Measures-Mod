package com.virus5600.defensive_measures.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import com.virus5600.defensive_measures._util.MathUtil;

import org.joml.Matrix3x2fStack;

import java.awt.*;

/**
 * Represents a sprite with its texture, position, and size. Used to easily store data of a
 * sprite and where it will be drawn when the {@link #drawSprite(GuiGraphicsExtractor)} or
 * {@link #drawSprite(GuiGraphicsExtractor, int, int)} is called.
 * <br><br>
 * A sprite should be placed under the {@code /assets/<namespace>/textures/gui/sprites}. Doing
 * so will allow the sprite to be found by the engine. See examples below.
 * <br><br>
 * Placed in: {@code /assets/minecraft/textures/gui/sprites/sample_sprite.png}
 * Defined in code as: {@code Identifier.withDefaultNamespace("sample_sprite");}
 * <br><br>
 * Another example:<br>
 * Placed in: {@code /assets/minecraft/textures/gui/sprites/container/container_sprite.png}
 * Defined in code as: {@code Identifier.withDefaultNamespace("container/container_sprite");}
 * <br><br>
 * When initializing this record, there's also an optional rotational value (in degrees), allowing
 * sprites to be drawn at a specific rotation, allowing for more versatility. While it accepts
 * rotation, the draw method will not apply the rotation if the provided rotation is 0 or is a
 * multiple of 360. Although, it will allow negative values and floating point values.
 *
 * @param texture         The path where the texture is located.
 * @param position        The position where the sprite will be drawn on the screen.
 * @param size            The size of the sprite to be drawn on the screen.
 * @param rotationDegrees The rotation of the sprite in degrees.
 *
 * @apiNote Sprite texture should be placed under the {@code /textures/gui/sprites} path.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public record SpriteData(Identifier texture, ScreenPosition position, Dimension size, float rotationDegrees) {
	public SpriteData(Identifier texture, ScreenPosition position, Dimension size) {
		this(texture, position, size, 0);
	}

	/**
	 * Draws the sprite in the specified position.
	 *
	 * @param graphics The {@link GuiGraphicsExtractor} to draw the sprite on.
	 */
	public void drawSprite(GuiGraphicsExtractor graphics) {
		this.drawSprite(graphics, 0, 0);
	}

	/**
	 * Draws the sprite in the specified position with offset.
	 *
	 * @param graphics The {@link GuiGraphicsExtractor} to draw the sprite on.
	 * @param xOffset The x-offset for the sprite position.
	 * @param yOffset The y-offset for the sprite position.
	 */
	public void drawSprite(GuiGraphicsExtractor graphics, int xOffset, int yOffset) {
		// If no rotation or just a rotation that returns it to the same rotation, draw and return immediately.
		if (rotationDegrees == 0 || rotationDegrees % 360 == 0) {
			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, texture,
				xOffset + position.x(), yOffset + position.y(),
				size.width, size.height
			);

			return;
		}


		int xPos = xOffset + position.x();
		int yPos = yOffset + position.y();
		int centerX = this.size.width / 2;
		int centerY = this.size.height / 2;
		Matrix3x2fStack stack = graphics.pose();

		stack.pushMatrix();

		stack.translate(xPos + centerX, yPos + centerY);
		stack.rotate(MathUtil.degToRad(rotationDegrees));

		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED, texture,
			-centerX, -centerY,
			size.width, size.height
		);

		stack.popMatrix();
	}
}
