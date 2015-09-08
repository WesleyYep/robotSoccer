package utils;

import org.opencv.core.Point;

public class PairPoint extends Pair {

	private double euclideanDistance;
	private double theta;

	public PairPoint(Point first, Point second, double euclideanDistance, double theta) {
        super(first, second);
		this.euclideanDistance = euclideanDistance;
		this.theta = theta;
	}

	public double getEuclideanDistance() {
		return euclideanDistance;
	}

	public double getTheta() {
		return theta;
	} //degrees
}