package actions;

import Paths.StraightLinePath;
import bot.Robot;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class ChaseBall extends Action {
	
    @Override
    public String getName() {
        return "Chase Ball";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        //r.linearVelocity = 0.5;
        if (path == null || path.hasReachedTarget()) {
            path = new StraightLinePath(r, (int)r.getXPosition(), (int)r.getYPosition(), (int)ballX, (int)ballY);
            path.setPoints();
        }

        //Should try to use the path here, rather than just hard coding some velocities

        double ballTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
        double difference = ballTheta - Math.toRadians(r.getTheta());
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }

        if (Math.abs(difference) >= 0.7) {
            if (difference > 0) {
                r.angularVelocity = 2*Math.PI;
            } else {
                r.angularVelocity = -2*Math.PI;
            }
            r.linearVelocity = 0;
        } else {
            r.linearVelocity = 1;
            r.angularVelocity = 0;
        }


    }

    protected double squared (double x) {
        return x * x;
    }
}