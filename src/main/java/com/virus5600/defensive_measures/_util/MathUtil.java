package com.virus5600.defensive_measures._util;

import net.minecraft.util.math.Vec3d;

/**
 * Contains all related math utility methods that can be used almost everywhere. This is to
 * suplement or shorten some procedures that would otherwise be too long or complicated to write in
 * a single line, such as calculating the {@link #getRelativePos(Vec3d, Vec3d, float, float) relative
 * position} of a point from an origin with a certain rotation, which is also repeatedly used
 * in the codebase.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class MathUtil {
	/**
	 * Identifies the position of a point relative to the origin provided.
	 * <br><br>
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param origin The origin that the calculations will use.
	 * @param xOffset The offset of the point at the local X-Axis of the entity.
	 * @param yOffset The offset of the point at the local Y-Axis of the entity.
	 * @param zOffset The offset of the point at the local Z-Axis of the entity.
	 * @param yaw The yaw rotation of the entity in degrees.
	 * @param pitch The pitch rotation of the entity in degrees.
	 *
	 * @return Vec3d the relative position of this point from the provided origin.
	 */
	public static Vec3d getRelativePos(Vec3d origin, double xOffset, double yOffset, double zOffset, float yaw, float pitch) {
		float pitchRad = degToRad(pitch);
		float yawRad = degToRad(yaw);

		Vec3d rotated = new Vec3d(xOffset, yOffset, zOffset)
			.rotateX(pitchRad)
			.rotateY(yawRad);

		return origin.add(rotated);
	}

	/**
	 * Identifies the position of a point relative to the origin provided.
	 * <br><br>
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param origin The origin that the calculations will use.
	 * @param xOffset The offset of the point at the local X-Axis of the entity.
	 * @param yOffset The offset of the point at the local Y-Axis of the entity.
	 * @param zOffset The offset of the point at the local Z-Axis of the entity.
	 * @param yaw The yaw rotation of the entity in degrees.
	 *
	 * @return Vec3d the relative position of this point from the provided origin.
	 */
	public static Vec3d getRelativePos(Vec3d origin, double xOffset, double yOffset, double zOffset, float yaw) {
		Vec3d rotated = new Vec3d(xOffset, yOffset, zOffset);

		float yawRad = degToRad(yaw);
		rotated = rotated.rotateY(yawRad);

		return origin.add(rotated);
	}

	/**
	 * Identifies the position of a point relative to the procided origin and rotation.
	 * <br><br>
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param origin The origin that the calculations will use.
	 * @param offsets The offsets of the point at the local X, Y, and Z-Axis of this turret.
	 * @param yaw The yaw rotation of the entity in degrees.
	 * @param pitch The pitch rotation of the entity in degrees.
	 *
	 * @return {@code Vec3d} the relative position of this point from the provided origin.
	 */
	public static Vec3d getRelativePos(Vec3d origin, Vec3d offsets, float yaw, float pitch) {
		return getRelativePos(origin, offsets.getX(), offsets.getY(), offsets.getZ(), yaw, pitch);
	}

	/**
	 * Identifies the position of a point relative to the procided origin and rotation.
	 * <br><br>
	 * For reference:
	 * <ul>
	 * 	<li>X-Axis == Pitch: Identifies the elevation rotation (Horizontal line axis)</li>
	 * 	<li>Y-Axis == Yaw: Identifies where you are looking (Vertical line axis)</li>
	 * 	<li>Z-Axis == Roll: It's the one facing you (The 3D line)</li>
	 * </ul>
	 *
	 * @param origin The origin that the calculations will use.
	 * @param offsets The offsets of the point at the local X, Y, and Z-Axis of this turret.
	 * @param yaw The yaw rotation of the entity in degrees.
	 *
	 * @return {@code Vec3d} The relative position of this point from the provided origin.
	 */
	public static Vec3d getRelativePos(Vec3d origin, Vec3d offsets, float yaw) {
		return getRelativePos(origin, offsets.getX(), offsets.getY(), offsets.getZ(), yaw);
	}

	/**
	 * Converts radians to degrees.
	 *
	 * @param rad The angle in radians to be converted to degrees.
	 *
	 * @return {@code float} the angle in degrees.
	 */
	public static float radToDeg(float rad) {
		return rad * (180.0f / (float) Math.PI);
	}

	/**
	 * Converts degrees to radians.
	 *
	 * @param deg The angle in degrees to be converted to radians.
	 *
	 * @return {@code float} the angle in radians.
	 */
	public static float degToRad(float deg) {
		return deg * ((float) Math.PI / 180.0f);
	}

	public static boolean randomBool() {
		return Math.random() < 0.5;
	}

	public static boolean randomBool(double chance) {
		return Math.random() < chance;
	}
}
