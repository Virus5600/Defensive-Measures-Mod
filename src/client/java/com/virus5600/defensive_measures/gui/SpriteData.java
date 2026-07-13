package com.virus5600.defensive_measures.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

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
 * This class supports an optional rotational value (in degrees) and an optional rotational
 * value that gets added to the rotation used by the sprite, allowing sprites to be drawn at a
 * specific rotation and add a rotational animation, allowing for more versatility. While it
 * accepts rotation, the draw method will not apply the rotation if the provided rotation is 0 or
 * is a multiple of 360. Although, it will allow negative values and floating point values.
 *
 * @apiNote Sprite texture should be placed under the {@code /textures/gui/sprites} path.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class SpriteData {
	private final Identifier texture;
	private final ScreenPosition position;
	private final Dimension size;
	private final float rotationDegrees;
	private final float addedRotationPerDraw;

	private float updatedRotationPerDraw;

	/**
	 * Creates a new {@link SpriteData} instance with the specified texture, position, and size,
	 * but without rotation.
	 *
	 * @param texture              The path where the texture is located.
	 * @param position             The position where the sprite will be drawn on the screen.
	 * @param size                 The size of the sprite to be drawn on the screen.
	 */
	public SpriteData(final Identifier texture, final ScreenPosition position, final Dimension size) {
		this(texture, position, size, 0, 0);
	}

	/**
	 * Creates a new {@link SpriteData} instance with the specified texture, position, size, and
	 * rotation.
	 *
	 * @param texture              The path where the texture is located.
	 * @param position             The position where the sprite will be drawn on the screen.
	 * @param size                 The size of the sprite to be drawn on the screen.
	 * @param rotationDegrees      The rotation of the sprite in degrees.
	 */
	public SpriteData(final Identifier texture, final ScreenPosition position, final Dimension size, final float rotationDegrees) {
		this(texture, position, size, rotationDegrees, 0);
	}

	/**
	 * Creates a new {@link SpriteData} instance with the specified texture, position, size,
	 * rotation, and added rotation per draw.
	 *
	 * @param texture              The path where the texture is located.
	 * @param position             The position where the sprite will be drawn on the screen.
	 * @param size                 The size of the sprite to be drawn on the screen.
	 * @param rotationDegrees      The rotation of the sprite in degrees.
	 * @param addedRotationPerDraw The amount of rotation to add to the sprite's rotation each time it is drawn.
	 */
	public SpriteData(final Identifier texture, final ScreenPosition position, final Dimension size, final float rotationDegrees, final float addedRotationPerDraw) {
		this.texture = texture;
		this.position = position;
		this.size = size;
		this.rotationDegrees = rotationDegrees;
		this.addedRotationPerDraw = addedRotationPerDraw;

		this.updatedRotationPerDraw = this.rotationDegrees;
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
		if (this.getFinalRotation() == 0 || this.getFinalRotation() % 360 == 0) {
			graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, this.texture,
				xOffset + this.position.x(), yOffset + this.position.y(),
				this.size.width, this.size.height
			);

			return;
		}


		int xPos = xOffset + this.position.x();
		int yPos = yOffset + this.position.y();
		float centerX = this.size.width / 2F;
		float centerY = this.size.height / 2F;
		Matrix3x2fStack stack = graphics.pose();

		stack.pushMatrix();

		stack.translate(xPos + centerX, yPos + centerY);
		stack.rotate(MathUtil.degToRad(this.getFinalRotation()));

		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED, this.texture,
			(int) -centerX, (int) -centerY,
			this.size.width, this.size.height
		);

		stack.popMatrix();
	}

	private float getFinalRotation() {
		if (this.addedRotationPerDraw != 0) {
			this.updatedRotationPerDraw += this.addedRotationPerDraw;
			this.updatedRotationPerDraw %= 360;

			return this.updatedRotationPerDraw;
		}

		return this.rotationDegrees;
	}
}
