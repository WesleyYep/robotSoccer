package utils;

import org.opencv.core.Point;

public class PairPoint {

	private Point first;
	private Point second;
	private double euclideanDistance;
	private double theta;

	public PairPoint(Point first, Point second, double euclideanDistance, double theta) {
		this.first = first;
		this.second = second;
		this.euclideanDistance = euclideanDistance;
		this.theta = theta;
	}

	public Point getFirst() {
		return first;
	}

	public Point getSecond() {
		return second;
	}

	public double getEuclideanDistance() {
		return euclideanDistance;
	}

	public double getTheta() {
		return theta;
	}
}