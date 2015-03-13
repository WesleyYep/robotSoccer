package actions;

import org.opencv.core.Point;

import strategy.Action;
import utils.Image;
import utils.PairPoint;
import bot.Robot;

public class Defender extends Action {

	private PairPoint defendZone;
	
	public Defender(Point p1, Point p2) {
		defendZone = new PairPoint(p1, p2, Image.euclideanDistance(p1, p2), Image.angleBetweenTwoPoints(p1, p2));
	}
	
	@Override
	public String getName() {
		return "Defender";
	}

	@Override
	public void execute() {
		// Get the robot which is assigned to this action.
		Robot r = bots.getRobot(index);
		
		r.linearVelocity = 1;
		r.angularVelocity = 0;
		
	}

}
