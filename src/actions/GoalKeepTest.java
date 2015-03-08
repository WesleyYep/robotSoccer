package actions;

import Paths.StraightLinePath;
import bot.Robot;
import strategy.Action;
import ui.Field;

public class GoalKeepTest extends Action{
	private double goalKeepCentreX = 10;
    private double goalKeepCentreY = 100;
    private int goalKeepTopLimit = 70;
    private int goalKeepBottomLimit = 110;

	@Override
    public String getName() {
        return "Goal Keep 1";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        double targetTheta = Math.atan2(r.getYPosition() - goalKeepCentreY, goalKeepCentreX - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
        double yDifference = r.getYPosition() - ballY;
        double difference2 = Math.PI/2 - Math.toRadians(r.getTheta()); //angle difference from vertical
        double distance = Math.sqrt(squared(r.getXPosition()-goalKeepCentreX) + squared(r.getYPosition()-goalKeepCentreY));

        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        if (difference2 > Math.PI) {
            difference2 -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference2 += (2 * Math.PI);
        }

        //firstly check if gk is already on his line
        if (Math.abs(r.getXPosition() - goalKeepCentreX) < 10) {
            if (Math.abs(difference2) >= TurnToFaceBall.ERROR_MARGIN) {
                if (difference2 > 0) {
                    r.angularVelocity = 2*Math.PI;
                } else {
                    r.angularVelocity = -2*Math.PI;
                }
                r.linearVelocity = 0;
            } else if (Math.abs(difference2) >= TurnToFaceBall.ERROR_MARGIN /2) {
                if (difference2 > 0) {
                    r.angularVelocity = Math.PI/2;
                } else {
                    r.angularVelocity = -Math.PI/2;
                }
                r.linearVelocity = 0;
            } else if (Math.abs(difference2) >= TurnToFaceBall.ERROR_MARGIN /4) {
                if (difference2 > 0) {
                    r.angularVelocity = Math.PI/4;
                } else {
                    r.angularVelocity = -Math.PI/4;
                }
                r.linearVelocity = 0;
            } else if (Math.abs(difference2) >= TurnToFaceBall.ERROR_MARGIN /8) {
                if (difference2 > 0) {
                    r.angularVelocity = Math.PI/8;
                } else {
                    r.angularVelocity = -Math.PI/8;
                }
                r.linearVelocity = 0;
            } else { //facing vertical
                r.angularVelocity = 0;
                r.linearVelocity = yDifference/100;
            }

        } else {
            //gk not on line
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



    }

    protected double squared (double x) {
        return x * x;
    }


}
