package com.virus5600.DefensiveMeasures.util.anaglyph;

public class Quaternion {
	private double x;
	private double y;
	private double z;
	private double w;
	
	public Quaternion(final Quaternion q) {
		this(q.x, q.y, q.z, q.w);
	}
	
	public Quaternion(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = Math.PI / 2;
	}
	
	public Quaternion(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public void set(final Quaternion q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
	}
	
	public double x() {return x;}
	public double y() {return y;}
	public double z() {return z;}
	public double w() {return w;}
	
	public Quaternion setX(double x) {this.x = x; return this;}
	public Quaternion setY(double y) {this.y = y; return this;}
	public Quaternion setZ(double z) {this.z = z; return this;}
	public Quaternion setW(double w) {this.w = w; return this;}
	
	public Quaternion set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		
		return this;
	}
	
	public Quaternion set(Vector3d axis, double angle) {
		double s = (double) Math.sin(angle / 2);
		w = (double) Math.cos(angle / 2);
		x = axis.x() * s;
		y = axis.y() * s;
		z = axis.z() * s;
		
		return this;
	}
	
	/**
	 * Multiplies this {@link Quaternion} to the provided multiplier {@code Quaternion}.
	 * @param q The multiplier
	 * @return Quaternion
	 */
	public Quaternion multiply(Quaternion q) {
		double nw = w * q.w - x * q.x - y * q.y - z * q.z;
		double nx = w * q.x + x * q.w + y * q.z - z * q.y;
		double ny = w * q.y + y * q.w + z * q.x - x * q.z;
		z = w * q.z + z * q.w + x * q.y - y * q.x;
		w = nw;
		x = nx;
		y = ny;
		return this;
	}
	
	/**
	 * Divides this {@link Quaternion} to the scale.
	 * @param scale Amount to divide relative to the current scale
	 * @return Quaternion
	 */
	public Quaternion divide(double scale) {
		if (scale != 1) {
			w /= scale;
			x /= scale;
			y /= scale;
			z /= scale;
		}
		return this;
	}
	
	/**
	 * Multiplies this {@link Quaternion} to the scale.
	 * @param scale Amount to multiply relative to the current scale
	 * @return Quaternion
	 */
	public Quaternion scale(double scale) {
		if (scale != 1) {
			w *= scale;
			x *= scale;
			y *= scale;
			z *= scale;
		}
		return this;
	}
	
	/**
	 * Returns the <b>dot</b> product between this and the provided {@code Quaternion}.
	 * @param q The {@code Quaternion} to get the product with
	 * @return double
	 */
	public double dot(Quaternion q) {
		return x * q.x + y * q.y + z * q.z + w * q.w;
	}
	
	/**
	 * Identifies whether this {@link Quaternion} is similar to the provided Quaternion.
	 * @param q The {@code Quaternion} to test with
	 * @return boolean
	 */
	public boolean equals(Quaternion q) {
		return x == q.x && y == q.y && z == q.z && w == q.w;
	}
	
	/**
	 * Retrieves the normalized value of this {@link Quaternion}
	 * @return
	 */
	public double normalize() {
		return Math.sqrt(dot(this));
	}
	
	/**
	 * Normalizes this {@link Quaternion}
	 * @return Quaternion
	 */
	public Quaternion normalizeQuaternion() {
		return divide(normalize());
	}
	
	/**
	 * Provides a new interpolated {@link Quaternion} based from this and the provided {@code Quaternion}
	 * @param q The {@code Quaternion} to interpolate to
	 * @param t Time to interpolate between this to the {@code q}
	 * @return Quaternion
	 */
	public Quaternion interpolate(Quaternion q, double t) {
		return new Quaternion(this).interpolateThis(q, t);
	}
	
	/**
	 * Converts this Quaternion into a matrix, returning it as a float array.
	 */
	public float[] toMatrix() {
		float[] matrixs = new float[16];
		toMatrix(matrixs);
		return matrixs;
	}
	
	/**
	 * Modifies this {@link Quaternion} and returns it as an interpolated product based from this and the provided {@code Quaternion}
	 * @param q The {@code Quaternion} to interpolate to
	 * @param t Time to interpolate between this to the {@code q}
	 * @return Quaternion
	 */
	public Quaternion interpolateThis(Quaternion q, double t) {
		if (!equals(q)) {
			double d = dot(q);
			double qx, qy, qz, qw;

			if (d < 0f) {
				qx = -q.x;
				qy = -q.y;
				qz = -q.z;
				qw = -q.w;
				d = -d;
			} else {
				qx = q.x;
				qy = q.y;
				qz = q.z;
				qw = q.w;
			}

			double f0, f1;

			if ((1 - d) > 0.1f) {
				double angle = (double) Math.acos(d);
				double s = (double) Math.sin(angle);
				double tAngle = t * angle;
				f0 = (double) Math.sin(angle - tAngle) / s;
				f1 = (double) Math.sin(tAngle) / s;
			} else {
				f0 = 1 - t;
				f1 = t;
			}

			x = f0 * x + f1 * qx;
			y = f0 * y + f1 * qy;
			z = f0 * z + f1 * qz;
			w = f0 * w + f1 * qw;
		}

		return this;
	}
	
	/**
	 * Converts this Quaternion into a matrix, placing the values into the given array.
	 * @param matrixs 16-length float array.
	 */
	public final void toMatrix(float[] matrixs) {
		matrixs[3] = 0.0f;
		matrixs[7] = 0.0f;
		matrixs[11] = 0.0f;
		matrixs[12] = 0.0f;
		matrixs[13] = 0.0f;
		matrixs[14] = 0.0f;
		matrixs[15] = 1.0f;

		matrixs[0] = (float) (1.0f - (2.0f * ((y * y) + (z * z))));
		matrixs[1] = (float) (2.0f * ((x * y) - (z * w)));
		matrixs[2] = (float) (2.0f * ((x * z) + (y * w)));
		
		matrixs[4] = (float) (2.0f * ((x * y) + (z * w)));
		matrixs[5] = (float) (1.0f - (2.0f * ((x * x) + (z * z))));
		matrixs[6] = (float) (2.0f * ((y * z) - (x * w)));
		
		matrixs[8] = (float) (2.0f * ((x * z) - (y * w)));
		matrixs[9] = (float) (2.0f * ((y * z) + (x * w)));
		matrixs[10] = (float) (1.0f - (2.0f * ((x * x) + (y * y))));
	}
	
	// STATIC METHODS
	
	public static Quaternion eulerToQuaternion(double yaw, double pitch, double roll) {
		double qx = Math.sin(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) - Math.cos(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2);
		double qy = Math.cos(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2);
		double qz = Math.cos(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2) - Math.sin(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2);
		double qw = Math.cos(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2);
		
		return new Quaternion(qx, qy, qz, qw);
	}
	
	public static Vector3d quaternionToEuler(Quaternion q) {
		double t0 = 2.0 * (q.w * q.x + q.y * q.z);
		double t1 = 1.0 - 2.0 * (q.x * q.x + q.y * q.y);
		double roll = Math.atan2(t0, t1);
		
		double t2 = +2.0 * (q.w * q.y - q.z * q.x);
		t2 = t2 > +1.0 ? 1.0 : t2;
		t2 = t2 < -1.0 ? -1.0 : t2;
		double pitch = Math.asin(t2);
		
		double t3 = +2.0 * (q.w * q.z + q.x * q.y);
		double t4 = +1.0 - 2.0 * (q.y * q.y + q.z * q.z);
		double yaw = Math.atan2(t3, t4);
		
		return new Vector3d(yaw, pitch, roll);
	}
}