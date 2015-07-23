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
	public Coordinate c;
	public ArrayList<RobotSoccerObject> observers;

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
        setFocusable(true);
        requestFocusInWindow();

    }

	public void setX (double x) {
		c.x = x;
	}

	public void setY (double y) {
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

	public void addObserver(RobotSoccerObject r) {
		observers.add(r);
	}

	/**
	 * <p>Removes robotsoccerobject from listeners array.</p>
	 * @param r
	 */

	public void removeObserver(RobotSoccerObject r) {
		observers.remove(r);
	}

	/**
	 * <p>Method to be called when RobotSoccerObject gets stuck</p>
	 * <p>Observer must implement this method.</p>
	 */
	public void react(boolean isStuck) {}

	/**
	 * <p>Determines if the current object is stuck in the field.</p>
	 * <p>Uses mean</p>
	 * <p>Must input the objects current coordinate in the field.</p>
	 * @param currentCoordinate
	 * @return is stuck
	 */

	public final boolean isStuck(Coordinate currentCoordinate) {

        // add coordinate to queue
        if (coordinateQueue.size() == QUEUE_SIZE) {
            // initialise variables
            int distance;
            double xMean, yMean;

            // check if queue needs to be cleared
            if (isStuck){
                xMean = xTotal / QUEUE_SIZE;
                yMean = yTotal / QUEUE_SIZE;

                distance = (int)Geometry.euclideanDistance(new Point(xMean, yMean), new Point(currentCoordinate.x, currentCoordinate.y));

                if (distance > ERROR_MARGIN) {
                    // reset values
                    xTotal = 0;
                    yTotal = 0;
                    setStuck(false);
                    coordinateQueue.clear();
                    return isStuck;
                }

            }

            Coordinate first = coordinateQueue.poll();

            coordinateQueue.add(currentCoordinate);

            xTotal = xTotal - first.x + currentCoordinate.x;
            yTotal = yTotal - first.y + currentCoordinate.y;

            xMean = xTotal / QUEUE_SIZE;
            yMean = yTotal / QUEUE_SIZE;

            // distance between first point and average point
			Point p1 = new Point(first.x, first.y);
			Point p2 = new Point(xMean, yMean);

			distance = (int) Geometry.euclideanDistance(p1, p2);

			if (distance <= ERROR_MARGIN) {
				setStuck(true);
			} else {
                setStuck(false);
            }

        } else {
            coordinateQueue.add(currentCoordinate);

            xTotal += currentCoordinate.x;
            yTotal += currentCoordinate.y;

            setStuck(false);
        }

        return isStuck;
	}

	/**
	 * <p>Checks to see if the object is near the boundary of the field.</p>
	 * @return near boundary
	 */
	public final boolean isNearBoundary() {
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
