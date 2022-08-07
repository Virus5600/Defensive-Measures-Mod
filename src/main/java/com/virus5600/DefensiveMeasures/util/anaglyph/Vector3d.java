package com.virus5600.DefensiveMeasures.util.anaglyph;

public final class Vector3d {
	public static final Vector3d zero = new Vector3d(0, 0, 0);
	public static final Vector3d one = new Vector3d(1, 1, 1);
	public static final Vector3d up = new Vector3d(0, 1, 0);
	public static final Vector3d down = new Vector3d(0, -1, 0);
	public static final Vector3d left = new Vector3d(-1, 0, 0);
	public static final Vector3d right = new Vector3d(1, 0, 0);
	public static final Vector3d forward = new Vector3d(0, 0, 1);
	public static final Vector3d back = new Vector3d(0, 0, -1);
	public static final Vector3d positiveInf = new Vector3d(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	public static final Vector3d negativeInf = new Vector3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
	
	private double x;
	private double y;
	private double z;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double x() {return x;}
	public double y() {return y;}
	public double z() {return z;}
	
	public Vector3d setX(double x) {this.x = x; return this;}
	public Vector3d setY(double y) {this.y = y; return this;}
	public Vector3d setZ(double z) {this.z = z; return this;}
	
	public Vector3d set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	/**
	 * Returns the length of this Vector. This is also known as the "magnitude" of the Vector.
	 * @return double
	 */
	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	/**
	 * Adds this {@link Vector3d} to the one provided.
	 * @param v The second addend that will be used to add this vector
	 * @return Vector3d
	 */
	public Vector3d add(Vector3d v) {
		x += v.x;
		y += v.y;
		z += v.z;
		
		return this;
	}
	
	/**
	 * Subtracts this {@link Vector3d} to the provided {@code Vector3d}.
	 * @param v The subtrahend {@code Vector3d} object
	 * @return Vector3d
	 */
	public Vector3d subtract(Vector3d v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		
		return this;
	}
	
	/**
	 * Multiplies this vector to {@code multiplier}
	 * @param multiplier Number this vector will be multiplied with
	 * @return Vector3d The multiplier
	 */
	public Vector3d multiply(double multiplier) {
		x *= multiplier;
		y *= multiplier;
		z *= multiplier;
		
		return this;
	}
	
	/**
	 * Multiplies this vector to another {@link Vector3d}
	 * @param v The multiplier vector
	 * @return Vector3d
	 */
	public Vector3d multiply(Vector3d v) {
		// X = ((v1.y * v2.z) - (v1.z * v2.y))
		x = ((y * v.z) - (z * v.y));
		// Y = -((v1.x * v2.z) - (v1.z * v2.x))
		y = -((x * v.z) - (z * v.x));
		// Z = ((v1.x * v2.y) - (v1.y * v2.x))
		z = ((x * v.y) - (y * v.x));
		
		return this;
	}
	
	/**
	 * Normalizes this vector.
	 * @return Vector3d
	 */
	public Vector3d normalize() {
		double mag = this.magnitude();
		
		x /= mag;
		y /= mag;
		z /= mag;

		return this;
	}
}