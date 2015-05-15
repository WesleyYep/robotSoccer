package actions;

import bot.Robot;
import data.Coordinate;
import game.Tick;
import strategy.Action;


/**
 * Created by Wesley on 27/02/2015.
 */
public class StrikerTest extends Action {
    private boolean ready = false;
    private boolean isKicking = false;
    private boolean atCentre = false;

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        isKicking = ballComingIntoPath(r);
        if (isKicking) {
            return;
        }
        if (atCentre && Math.abs(r.getXPosition() - 110) < 10 && Math.abs(r.getYPosition() - 90) < 10 ) { //already at centre, now turn to goal
	        TurnTo.turn(r, new Coordinate(220, 90));
	        r.linearVelocity = 0;
	        if (Math.abs(r.getTheta())%360 < 5) {
	            r.angularVelocity = 0;
	        }
	    }
	    else {
	        MoveToSpot.move(r, new Coordinate(110, 90), 1);
	        if (r.linearVelocity == 0 && r.angularVelocity == 0) {
	            atCentre = true;
	        }
	    }
    }

    private boolean ballComingIntoPath(Robot r) {
    	//return true if ball is directly in front (or behind) of robot
    	if (Math.abs(r.getYPosition() - ballY) < 3) {
    		if (Math.abs(r.getTheta())%360 < 5) {
        		r.linearVelocity = 3;
    		} else if (Math.abs(r.getTheta())%360 > 175) {
    			r.linearVelocity = -3;
    		}
    		return true;
    	}
        //return false if ball is moving away
        if ((predY > ballY && ballY > r.getYPosition()) || (predY < ballY && ballY < r.getYPosition())) {
            return false;
        }

        //get an equation in the form y = mx + c
        double m = (predY-ballY) / (predX - ballX);
        double c = ballY - (m * ballX);
        //check if line crosses the line y = robotY and 0 < x < r.X
        double x = (r.getYPosition() - c) / m;
        if (x < 220 && x > r.getXPosition()) {
            //find distance of intersection point from current ball position
            double ballDistance = Math.sqrt(squared(ballX-x) + squared(ballY-r.getYPosition()));
            //find speed of ball
            double ballSpeed = (Math.sqrt(squared(predX-ballX) + squared(predY-ballY))) / Tick.PREDICT_TIME;
            //find time taken for ball to reach intersection point
            double time = ballDistance / ballSpeed;
            //only go if the time is under 3 seconds
            if (time < 1) {
                //get distance of robot to spot
                double robotDistance = Math.sqrt(squared(r.getXPosition()-x));
                r.linearVelocity = (robotDistance/time)/100;
                r.angularVelocity = 0;
                return true;
            }
        }
        return false;
    }

    protected static double squared (double x) {
        return x * x;
    }

}

