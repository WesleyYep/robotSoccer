package actions;

import bot.Robot;
import data.Coordinate;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class MoveToSpot extends Action{
    private Coordinate spot = new Coordinate(10,18);  //<-------- EDIT THIS TO CHANGE SPOT

    @Override
    public String getName() {
        return "Move to spot";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        //change to left/right side depending on where ball is
        if (ballY > 90) {
  //          System.out.println(r.getYPosition());
            spot.y = 162;
        } else {
            spot.y = 18;
        }

        double ballTheta = Math.atan2(r.getYPosition() - spot.y, spot.x - r.getXPosition());
        double difference = ballTheta - Math.toRadians(r.getTheta());
        double distance = Math.sqrt(squared(r.getXPosition()-spot.x) + squared(r.getYPosition()-spot.y));

        if (distance < 5) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
            return;
        }

        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }

        if (Math.abs(difference) >= TurnToFaceBall.ERROR_MARGIN) {
            if (difference > 0) {
                r.angularVelocity = 2*Math.PI;
            } else {
                r.angularVelocity = -2*Math.PI;
            }
            r.linearVelocity = 0;
        } else if (Math.abs(difference) >= TurnToFaceBall.ERROR_MARGIN /2) {
            if (difference > 0) {
                r.angularVelocity = Math.PI/2;
            } else {
                r.angularVelocity = -Math.PI/2;
            }
            r.linearVelocity = 0;
        } else if (Math.abs(difference) >= TurnToFaceBall.ERROR_MARGIN /4) {
            if (difference > 0) {
                r.angularVelocity = Math.PI/4;
            } else {
                r.angularVelocity = -Math.PI/4;
            }
            r.linearVelocity = 0;
        } else {
            r.linearVelocity = distance/100;
            r.angularVelocity = difference / (distance/100);
        }

    }

    protected double squared (double x) {
        return x * x;
    }
}
