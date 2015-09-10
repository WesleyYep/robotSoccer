package actions;

import Paths.Path;
import org.opencv.core.Point;
import strategy.Action;
import utils.Geometry;
import utils.PairPoint;

public abstract class Defender extends Action {

	protected PairPoint defendZone;
	
	protected Defender(Point p1, Point p2) {
		defendZone = new PairPoint(p1, p2, Geometry.euclideanDistance(p1, p2), Geometry.angleBetweenTwoPoints(p1, p2));
	}

	protected PairPoint getDefendZone() {
		return defendZone;
	}
	
	protected void setDefendZone(Point p1, Point p2) {
		defendZone = new PairPoint(p1, p2, Geometry.euclideanDistance(p1, p2), Geometry.angleBetweenTwoPoints(p1, p2));
	}
	
	protected abstract Point getPosition();

}
