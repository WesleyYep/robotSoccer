package actions;

import bot.Robot;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class TurnToFaceBall extends Action{
    public static final double ERROR_MARGIN = 0.8;

    @Override
    public String getName() {
        return "Turn to ball (trial)";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        double ballTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
        double difference = ballTheta - Math.toRadians(r.getTheta());
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        
        r.linearVelocity = 0;
        if (Math.abs(difference) >= ERROR_MARGIN) {
            if (difference > 0) {
                r.angularVelocity = 2*Math.PI/2;
            } else {
                r.angularVelocity = -2*Math.PI/2;
            }
        } else if (Math.abs(difference) >= ERROR_MARGIN /2) {
            if (difference > 0) {
                r.angularVelocity = Math.PI/4;
            } else {
                r.angularVelocity = -Math.PI/4;
            }
        } /*else if (Math.abs(difference) >= ERROR_MARGIN /4) {
            if (difference > 0) {
                r.angularVelocity = Math.PI/8;
            } else {
                r.angularVelocity = -Math.PI/8;
            }
        } */else {
        	double distance = Math.sqrt(squared(r.getXPosition()-ballX) + squared(r.getYPosition()-ballY));
            r.linearVelocity = 1;
            r.angularVelocity = squared(2*difference) / (distance/100);
      //      System.out.println(r.angularVelocity);
        }


    }

    protected double squared (double x) {
        return x * x;
    }
}
