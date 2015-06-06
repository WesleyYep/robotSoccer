package data;

import org.opencv.core.Point;
import ui.Field;
import utils.Geometry;

import javax.swing.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * <p>Represents an object which is inside the robot soccer field</p>
 * <p>Look at {@link bot.Robot} and {@link ui.Ball}</p>
 */
public abstract class RobotSoccerObject extends JPanel {
	public Coordinate c;
	private static final int QUEUE_SIZE = 100;
	private static final int ERROR_MARGIN = 3;
	private Queue<Coordinate> coordinateQueue;

	public RobotSoccerObject(Coordinate c) {
		this.c = c;
		coordinateQueue = new ArrayBlockingQueue<Coordinate>(QUEUE_SIZE);
	}

	public void setX (double x) {
		c.x = x;
	}

	public void setY (double y) {
		c.y = y;
	}

	/**
	 * <p>Determines if the current object is stuck in the field.</p>
	 * <p>Uses mean</p>
	 * <p>Must input the objects current coordinate in the field.</p>
	 * @param currentCoordinate
	 * @return is stuck
	 */

	public boolean isStuck(Coordinate currentCoordinate) {
		boolean isStuck = false;

		// add current coordinate to queue
		coordinateQueue.add(currentCoordinate);

		if (coordinateQueue.size() == QUEUE_SIZE) {
			double x = 0, y = 0;

			// Gets the first coordinate which will be used for deviation checks.
			Coordinate first = coordinateQueue.peek();

			Iterator<Coordinate> it = coordinateQueue.iterator();
			while(it.hasNext()) {
				Coordinate c = it.next();
				x += c.x;
				y += c.y;
			}

			// Calculate mean.
			x /= QUEUE_SIZE;
			y /= QUEUE_SIZE;

			Point p1 = new Point(x, y);
			Point p2 = new Point(first.x, first.y);

			int distance = (int)Geometry.euclideanDistance(p1, p2);

			if (distance <= ERROR_MARGIN) {
				isStuck = true;
				coordinateQueue.clear();
			}

		}

		return isStuck;
	}

	/**
	 * <p>Checks to see if the object is near the boundary of the field.</p>
	 * @return near boundary
	 */
	public boolean isNearBoundary() {
		double x = c.x;
		double y = c.y;

		int errorMargin = 10;

		// Bottom boundary
		if (y >= 0 && y <= errorMargin ) {
			return true;
		}
		// Top boundary
		else if (y >= Field.OUTER_BOUNDARY_HEIGHT-errorMargin && y <= Field.OUTER_BOUNDARY_HEIGHT) {
			return true;
		}
		// Left boundary
		else if (x >= 0 && x <= errorMargin ) {
			return true;
		}
		// Right boundary
		else if (x >= Field.OUTER_BOUNDARY_WIDTH-errorMargin && x <= Field.OUTER_BOUNDARY_WIDTH) {
			return true;
		}

		return false;
	}
}
