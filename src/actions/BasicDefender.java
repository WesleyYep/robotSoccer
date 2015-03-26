package actions;

import org.opencv.core.Point;

import utils.Geometry;
import bot.Robot;
import Paths.Path;

public class BasicDefender extends Defender {

	public BasicDefender(Point p1, Point p2, Path path) {
		super(p1, p2, path);
	}

	@Override
	public String getName() {
		return "Basic Defender";
	}

	@Override
	public void execute() {
		Robot r = bots.getRobot(index);
		
		Point positionToBe = getPosition();
		
		
		
	}

	@Override
	protected Point getPosition() {
		Point p1 = defendZone.getFirst();
		Point p2 = defendZone.getSecond();
		Point p3 = new Point(ballX, ballY);
		
		double[] angles = Geometry.anglesInTriangle(p1, p2, p3);
		
		if (angles[0] > 90) {
			return p1;
		} else if (angles[1] > 90) {
			return p2;
		} else {
			// either p1p3p2 is > 90 or all angles less than 90.
			
			// using either p1 or p2, in this case p1. find the adjacent side length using cosine.
			double distance = Math.cos(angles[0]) * Geometry.euclideanDistance(p1, p3);
			
			// Find the point that is x distance from point 1 along the vector.
			// TODO needs better way.
			double[] vector = new double[2];
			vector[0] = p2.x - p1.x;
			vector[1] = p2.y - p1.y;
			
			double magnitude = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
			
			double[] normalisedVector = new double[2];
			normalisedVector[0] = vector[0] / magnitude;
			normalisedVector[1] = vector[1] / magnitude;
			
			return new Point(p1.x + distance * normalisedVector[0], p1.y + distance * normalisedVector[1]);
			
		}
	}

}
