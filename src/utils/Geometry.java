package utils;

/**
 * <p>Consists of static utility classes when working with geometry</p>
 * @author Chang Kon, Wesley, John
 *
 */

public class Geometry {

	/**
	 * <p>Finds the euclidean distance between two points and returns it</p>
	 * @param p1
	 * @param p2
	 * @return euclidean distance between two points
	 */

	public static double euclideanDistance(org.opencv.core.Point p1, org.opencv.core.Point p2) {
		return Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
	}

	/**
	 * <p>Calculates the angle between the two points, respect to the x axis in degrees</p>
	 * @param p1
	 * @param p2
	 * @return angle between two points in degrees, respect to x axis. <strong>Note:</strong> range -180 to 180.
	 */

	public static double angleBetweenTwoPoints(org.opencv.core.Point p1, org.opencv.core.Point p2) {
		return Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));
	}

	/**
	 * <p>Finds the angles in a triangle. Calculated using <strong>Law of Cosines</strong> theorem.</p>
	 * <p>First element returns angle <strong>p2p1p3</strong></p>
	 * <p>Second element returns angle <strong>p1p2p3</strong></p>
	 * <p>Third element returns angle <strong>p1p3p2</strong></p>
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return double array, size 3.
	 * @see http://en.wikipedia.org/wiki/Law_of_cosines
	 */

	public static double[] anglesInTriangle(org.opencv.core.Point p1, org.opencv.core.Point p2, org.opencv.core.Point p3) {

		double[] angles = new double[3];

		double distancep1p2 = Geometry.euclideanDistance(p1, p2);
		double distancep1p3 = Geometry.euclideanDistance(p1, p3);
		double distancep2p3 = Geometry.euclideanDistance(p2, p3);

		/*
		 * theta = arccos((a^2 + b^2 - c^2) / (2 * a * b))
		 */

		angles[0] = Math.acos((Math.pow(distancep1p2, 2) + Math.pow(distancep1p3, 2) - Math.pow(distancep2p3, 2) / (2 * distancep1p2 * distancep1p3)));
		angles[1] = Math.acos((Math.pow(distancep2p3, 2) + Math.pow(distancep1p2, 2) - Math.pow(distancep1p3, 2) / (2 * distancep1p2 * distancep2p3)));
		angles[2] = Math.acos((Math.pow(distancep1p3, 2) + Math.pow(distancep2p3, 2) - Math.pow(distancep1p2, 2) / (2 * distancep1p3 * distancep2p3)));

		return angles;
	}

}
