package data;

import org.opencv.core.Point;
import ui.Field;
import utils.Geometry;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * <p>Represents an object which is inside the robot soccer field</p>
 * <p>Look at {@link bot.Robot} and {@link ui.Ball}</p>
 */
public abstract class RobotSoccerObject extends JPanel {
	protected Coordinate c;
	protected ArrayList<RobotSoccerObject> observers;

	private static final int QUEUE_SIZE = 100;
	private static final int ERROR_MARGIN = 3;
	private Queue<Coordinate> coordinateQueue;
	private boolean isStuck;
	private double xTotal, yTotal;

	public RobotSoccerObject(Coordinate c) {
		this.c = c;
		coordinateQueue = new ArrayBlockingQueue<Coordinate>(QUEUE_SIZE);
		observers = new ArrayList<RobotSoccerObject>();
		isStuck = false;
		xTotal = 0;
		yTotal = 0;
	}

	protected void setX (double x) {
		c.x = x;
	}

	protected void setY (double y) {
		c.y = y;
	}

	/**
	 * <p>Updates stuck position of RobotSoccerObject.</p>
	 * <p>Notifies all listeners</p>
	 * @param isStuck
	 */

	private void setStuck(boolean isStuck) {
		if (this.isStuck != isStuck) {
			notifyObservers(isStuck);
		}
		this.isStuck = isStuck;
	}

	/**
	 * <p>Notifies all observers of object stuck variable</p>
	 * @param isStuck
	 */

	private void notifyObservers(boolean isStuck) {
		for (RobotSoccerObject r : observers) {
			r.react(isStuck);
		}
	}

	/**
	 * <p>Adds robotsoccerobject to listener</p>
	 * @param r
	 */

	protected void addObserver(RobotSoccerObject r) {
		observers.add(r);
	}

	/**
	 * <p>Removes robotsoccerobject from listeners array.</p>
	 * @param r
	 */

	protected void removeObserver(RobotSoccerObject r) {
		observers.remove(r);
	}

	/**
	 * <p>Method to be called when RobotSoccerObject gets stuck</p>
	 * <p>Observer must implement this method.</p>
	 */
	protected void react(boolean isStuck) {}

	/**
	 * <p>Determines if the current object is stuck in the field.</p>
	 * <p>Uses mean</p>
	 * <p>Must input the objects current coordinate in the field.</p>
	 * @param currentCoordinate
	 * @return is stuck
	 */

	protected final boolean isStuck(Coordinate currentCoordinate) {

		boolean doCheck = false;
		boolean isStuck = false;
		Coordinate first = null;

		if (coordinateQueue.size() == QUEUE_SIZE) {

			Coordinate previous = coordinateQueue.poll();

			coordinateQueue.add(currentCoordinate);

			xTotal = xTotal - previous.x + currentCoordinate.x;
			yTotal = yTotal - previous.y + currentCoordinate.y;

			first = coordinateQueue.peek();
			doCheck = true;
		} else {
			coordinateQueue.add(currentCoordinate);

			if (coordinateQueue.size() == QUEUE_SIZE) {
				// using first as reference point.
				first = coordinateQueue.peek();

				// iterate through coordinate array.
				Iterator<Coordinate> it = coordinateQueue.iterator();
				while(it.hasNext()) {
					Coordinate c = it.next();
					xTotal += c.x;
					yTotal += c.y;
				}

				doCheck = true;
			}
		}

		if (doCheck && first != null) {
			// Calculate mean
			double xMean = xTotal / QUEUE_SIZE;
			double yMean = yTotal / QUEUE_SIZE;

			// distance between first point and average point
			Point p1 = new Point(first.x, first.y);
			Point p2 = new Point(xMean, yMean);

			int distance = (int) Geometry.euclideanDistance(p1, p2);

			if (distance <= ERROR_MARGIN) {
				isStuck = true;
				coordinateQueue.clear();
				// reset xTotal and yTotal
				xTotal = 0;
				yTotal = 0;
			}
		}

		setStuck(isStuck);
		return isStuck;
	}

	/**
	 * <p>Checks to see if the object is near the boundary of the field.</p>
	 * @return near boundary
	 */
	protected final boolean isNearBoundary() {
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
