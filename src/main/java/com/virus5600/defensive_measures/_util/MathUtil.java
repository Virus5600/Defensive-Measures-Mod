package com.virus5600.defensive_measures._util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

/**
 * Contains all related math utility methods that can be used almost everywhere. This is to
 * suplement or shorten some procedures that would otherwise be too long or complicated to write in
 * a single line, such as calculating the {@link #getRelativePos(Vec3d, Vec3d, float, float) relative
 * position} of a point from an origin with a certain rotation, which is also repeatedly used
 * in the codebase.
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class MathUtil {

	// ///////////////// //
	// RELATIVE POSITION //
	// ///////////////// //

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

	// ///////////////////// //
	// ANGLE UNIT CONVERSION //
	// ///////////////////// //

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

	// ////////////// //
	// RANDOM BOOLEAN //
	// ////////////// //

	/**
	 * Returns a random boolean value with a 50% chance of being true and a 50% chance of being false.
	 *
	 * @return {@code boolean} a random boolean value with a 50% chance of being true and a 50% chance of being false.
	 */
	public static boolean randomBool() {
		return Math.random() > 0.5;
	}

	/**
	 * Returns a random boolean value based on the provided chance. For example, if the chance is
	 * 30, there is a 30% chance that this method will return true and a 70% chance that it will
	 * return false. A chance of more than 100 will be treated as 100, and a chance of less than 0
	 * will be treated as 0.
	 *
	 * @param chance The percentage chance that this method will return true. Must be between 0 and 100.
	 *
	 * @return {@code boolean} a random boolean value based on the provided chance.
	 */
	public static boolean randomBool(double chance) {
		chance = clamp(chance, 0.0, 100.0);

		return random() <= chance;
	}

	// ////// //
	// CLAMPS //
	// ////// //

	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public static long clamp(long value, long min, long max) {
		return Math.max(min, Math.min(max, value));
	}

	public static short clamp(short value, short min, short max) {
		return (short) Math.max(min, Math.min(max, value));
	}

	public static byte clamp(byte value, byte min, byte max) {
		return (byte) Math.max(min, Math.min(max, value));
	}

	// ///////////// //
	// RANDOM NUMBER //
	// ///////////// //

	/**
	 * Returns a random integer between 0 and 100 (inclusive).
	 *
	 * @return {@code int} a random integer between 0 and 100 (inclusive).
	 */
	public static int random() {
		return MathHelper.nextInt(Random.create(), 0, 100);
	}

	public static double random(double min, double max) {
		return MathHelper.nextDouble(Random.create(), min, max);
	}

	public static float random(float min, float max) {
		return MathHelper.nextFloat(Random.create(), min, max);
	}

	public static int random(int min, int max) {
		return MathHelper.nextInt(Random.create(), min, max);
	}

	public static long random(long min, long max) {
		return (long) (Math.random() * (max - min) + min);
	}

	public static short random(short min, short max) {
		return (short) (Math.random() * (max - min) + min);
	}

	public static byte random(byte min, byte max) {
		return (byte) (Math.random() * (max - min) + min);
	}
}
