package actions;

import Paths.StraightLinePath;
import bot.Robot;
import strategy.Action;
import ui.Field;

public class GoalKeepTest extends Action{
	private double goalKeepCentreX = 10;
    private double goalKeepCentreY = 100;

	@Override
    public String getName() {
        return "Goal Keep 1";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        double targetTheta = Math.atan2(r.getYPosition() - goalKeepCentreY, goalKeepCentreX - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());

        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }

        //firstly check if gk is already on his line
        if (r.angularVelocity == 0 && Math.abs(r.getXPosition() - goalKeepCentreX) < 5) {
            double yDifference = r.getYPosition() - ballY;
            r.linearVelocity = yDifference/100;
            return;
        }

        double distance = Math.sqrt(squared(r.getXPosition()-goalKeepCentreX) + squared(r.getYPosition()-goalKeepCentreY));

        if (distance < 2) { //if the goalkeeper is near his line
            r.linearVelocity = 0;

            double difference2 = Math.PI/2 - Math.toRadians(r.getTheta());

            if (difference2 > Math.PI) {
                difference2 -= (2 * Math.PI);
            } else if (difference < -Math.PI) {
                difference2 += (2 * Math.PI);
            }
            r.angularVelocity = difference2*5;
        } else if (distance < 5) { //if goalkeeper is close to line
            r.linearVelocity = 0.2;
            r.angularVelocity = difference / (5*distance/100);
        } else if (distance < 10) { //goalkeeper needs to get back to line
            r.linearVelocity = 0.5;
            r.angularVelocity = difference / (2*distance/100);
        }  else {
            r.linearVelocity = 1;
            r.angularVelocity = difference / (distance/100);
        }



    }

    protected double squared (double x) {
        return x * x;
    }


}
