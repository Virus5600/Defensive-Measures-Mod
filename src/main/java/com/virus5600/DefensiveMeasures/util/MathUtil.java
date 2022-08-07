package com.virus5600.DefensiveMeasures.util;

import net.minecraft.util.math.Vec3d;

/**
 * Contains all math utility methods that may not be in {@link Math} class.
 * This is to supplement some shortcomings of the primary vanilla {@code Math} class and create flexibility towards applying features and modifications.
 * @author Virus5600
 * @since 1.0.0
 * @see java.lang.Math Math
 * @see net.minecraft.util.math.Vec3d Vec3d
 */
public class MathUtil {
	/**
	 * Normalize the vector.
	 * @param v3d The un-normalized vector
	 * @return Vec3d
	 */
	public static Vec3d normalizeVector(Vec3d v3d) {
		double norm = 1.0/Math.sqrt((v3d.x * v3d.x) + (v3d.y * v3d.y) + (v3d.z * v3d.z));
		double x = v3d.x;
		double y = v3d.y;
		double z = v3d.z;

		x *= norm;
		y *= norm;
		z *= norm;
		
		return new Vec3d(x, y, z);
	}
	
	/**
	 * Returns the length<sup>2</sup> of the provided Vector. This is also known as the "magnitude<sup>2</sup>" of the Vector.
	 * @param v3d Vec3d to be calculated for length
	 * @return double
	 */
	public static double magnitudeSquared(Vec3d v3d) {
		return ((v3d.x * v3d.x) + (v3d.y * v3d.y) + (v3d.z * v3d.z));
	}
	
	/**
	 * Returns the length of the provided Vector. This is also known as the "magnitude" of the Vector.
	 * @param v3d Vec3d to be calculated for length
	 * @return double
	 */
	public static double magnitude(Vec3d v3d) {
		return Math.sqrt(magnitudeSquared(v3d));
	}
	
	/**
	 * Adds all the {@link Vec3d}s provided.
	 * @param v3d All the {@code Vec3d} object you wanted to add
	 * @return Vec3d
	 */
	public static Vec3d add(Vec3d... v3d) {
		double x = 0, y = 0, z = 0;
		
		for (Vec3d v : v3d) {
			x += v.x;
			y += v.y;
			z += v.z;
		}
		
		return new Vec3d(x, y, z);
	}
	
	/**
	 * Subtracts all the {@link Vec3d}s provided. The order they are given represents how they'll be subtracted.<br>
	 * <br>
	 * For example:<br>
	 * {@code subtract(new Vec3d(1, 1, 1), new Vec3d(2, 2, 2))}<br>
	 * <br>
	 * This will then be interpreted as:<br>
	 * {@code new Vec3d(1-2, 1-2, 1-2)}<br>
	 * {@code = new Vec3d(-1, -1, -1)}<br>
	 * 
	 * @param v3d All the {@code Vec3d} object you wanted to subtract
	 * @return Vec3d
	 */
	public static Vec3d subtract(Vec3d... v3d) {
		double x = 0, y = 0, z = 0;
		
		for (Vec3d v : v3d) {
			x -= v.x;
			y -= v.y;
			z -= v.z;
		}
		
		return new Vec3d(x, y, z);
	}
	
	/**
	 * Multiplies two {@link Vec3d}s
	 * @param v1 The multiplicand vector
	 * @param v2 The multiplier vector
	 * @return Vec3d
	 */
	public static Vec3d multiply(Vec3d v1, Vec3d v2) {
		double x = 0, y = 0, z = 0;
		
		// X = ((v1.y * v2.z) - (v1.z * v2.y))
		x = ((v1.y * v2.z) - (v1.z * v2.y));
		// Y = -((v1.x * v2.z) - (v1.z * v2.x))
		y = -((v1.x * v2.z) - (v1.z * v2.x));
		// Z = ((v1.x * v2.y) - (v1.y * v2.x))
		z = ((v1.x * v2.y) - (v1.y * v2.x));
		
		return new Vec3d(x, y, z);
	}
}