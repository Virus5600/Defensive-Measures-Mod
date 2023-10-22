package com.virus5600.DefensiveMeasures.util;

import net.minecraft.util.math.Vec3d;

/**
 * An immutable vector composed of 3 doubles, extended from the original {@link Vec3d}. This is to serve as an extension with
 * additional functions missing from the superclass.
 *
 * <p>This vector class is used for representing position, velocity,
 * rotation, color, etc.
 *
 * <p>This vector has proper {@link #hashCode()} and {@link #equals(Object)}
 * implementations and can be used as a map key.
 *
 *
 * @see Vec3i
 * @see Vec3f
 */
public class Vec3dUtil extends Vec3d {

	/**
     * Construct the vector with provided double components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     *
     * @see Vec3d
     */
	public Vec3dUtil(final double x, final double y, final double z) {
		super(x, y, z);
	}

	/**
     * Clones the original vector to this one.
     *
     * @param vec The other vector
     */
	public Vec3dUtil(final Vec3d vec) {
		super(vec.x, vec.y, vec.z);
	}

	/**
     * Rotates the vector around a given arbitrary axis in 3 dimensional space.
     *
     * <p>
     * Rotation will follow the general Right-Hand-Rule, which means rotation
     * will be counterclockwise when the axis is pointing towards the observer.
     * <p>
     * Note that the vector length will change accordingly to the axis vector
     * length. If the provided axis is not a unit vector, the rotated vector
     * will not have its previous length. The scaled length of the resulting
     * vector will be related to the axis vector. If you are not perfectly sure
     * about the scaling of the vector, use
     * {@link Vec3d#rotateX(float)} or {@link Vec3d#rotateY(float)} or {@link Vec3d#rotateZ(float)}
     * <p>
     * This method is derived from
     * <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/util/Vector.java#539">Spigot Bukkit's <code>Vector#rotateAroundNonUnitAxis()</code></a>
     *
     * @param axis the axis to rotate the vector around.
     * @param angle the angle to rotate the vector around the axis
     * @return Vec3d
     * @see Vec3d
     */
	public Vec3d rotateAroundNonUnitAxis(final Vec3d axis, final double angle) {
		double x = getX();
		double y = getY();
		double z = getZ();
        double x2 = axis.getX();
        double y2 = axis.getY();
        double z2 = axis.getZ();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = this.dotProduct(axis);

        double xPrime = x2 * dotProduct * (1d - cosTheta)
                + x * cosTheta
                + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * (1d - cosTheta)
                + y * cosTheta
                + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * (1d - cosTheta)
                + z * cosTheta
                + (-y2 * x + x2 * y) * sinTheta;

        return new Vec3dUtil(xPrime, yPrime, zPrime);
	}

	/**
	 * Calculates the distance between two points using the geometry distance formula.
	 *
	 * @param x1 The x coordinate of the source
	 * @param y1 The y coordinate of the source
	 * @param z1 The z coordinate of the source
	 * @param x2 The x coordinate of the target
	 * @param y2 The y coordinate of the target
	 * @param z2 The z coordinate of the target
	 * @return double
	 */
	public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Vec3dUtil.distance(new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2));
	}

	/**
	 * Calculates the distance between two points using the geometry distance formula.
	 *
	 * @param p1 The {@link Vec3d} coordinate of the source
	 * @param p2 The {@link Vec3d} coordinate of the target
	 * @return double
	 */
	public static double distance(Vec3d p1, Vec3d p2) {
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2) + Math.pow(p2.z - p1.z, 2));
	}
}