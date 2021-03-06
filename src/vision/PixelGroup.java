package vision;

import data.Coordinate;

/**
 * Created by Wesley on 7/02/2015.
 */
public class PixelGroup {

	//this class represents matching colour pixels along one row of a buffered image

	public Coordinate mostLeftCorner;
	public Coordinate mostRightCorner;
	public Coordinate mostTopCorner;
	public Coordinate mostBottomCorner;

	public PixelGroup (int x, int y) {
		mostLeftCorner = new Coordinate(x, y);
		mostRightCorner = new Coordinate(x, y);
		mostTopCorner = new Coordinate(x, y);
		mostBottomCorner = new Coordinate(x, y);
	}

	public double getSize() {
		return (mostBottomCorner.y - mostTopCorner.y) + (mostRightCorner.x - mostLeftCorner.x);
	}

	public Coordinate getCentre() {
		return new Coordinate((mostBottomCorner.x + mostTopCorner.x) / 2, (mostBottomCorner.y + mostTopCorner.y) / 2);
	}

	public double getTheta() {
		double topLeftLength = Math.sqrt(squared(mostTopCorner.x - mostLeftCorner.x) + squared(mostTopCorner.y - mostLeftCorner.y));
		double topRightLength = Math.sqrt(squared(mostTopCorner.x - mostRightCorner.x) + squared(mostTopCorner.y - mostRightCorner.y));

		if (topLeftLength > topRightLength) {
			return Math.atan2(mostLeftCorner.y - mostTopCorner.y, mostTopCorner.x - mostLeftCorner.x);
		} else {
			return Math.atan2(mostRightCorner.y - mostTopCorner.y, mostTopCorner.x - mostRightCorner.x);
		}
	}

	protected double squared (double x) {
		return x * x;
	}

}
