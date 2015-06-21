package actions;

import bot.Robot;
import data.Coordinate;
import game.Tick;
import strategy.Action;
import strategy.GameState;


/**
 * Created by Wesley on 27/02/2015.
 */
public class MoveAndTurn extends Action {
    private int targetX = 0;
    private int targetY = 0;
    private double oldDistanceToTarget = 0;
    private int countTimesThatSeemStuck = 0;

    //non-static initialiser block
    {
        if(!(parameters.containsKey("spotX") && parameters.containsKey("spotY"))) {
            //don't bother if these already exist
            parameters.put("spotX", 100);
            parameters.put("spotY", 70);
            parameters.put("turnSpotX", 110);
            parameters.put("turnSpotY", 90);
        }
    }

    @Override
    public void execute() {
        Robot r = bot;

        //check if robot is stuck
        double newTargetDistance = getDistanceToTarget(r);
        //  System.out.println(Math.abs(oldDistanceToTarget - newTargetDistance));
        if (Math.abs(oldDistanceToTarget - newTargetDistance) < 0.4) {
            countTimesThatSeemStuck++;
        } else if (r.linearVelocity >= 0){
            countTimesThatSeemStuck = 0;
        }
        if (countTimesThatSeemStuck > 20) {
            countTimesThatSeemStuck = 0;
            return;
        } else if (countTimesThatSeemStuck > 10) {
            r.linearVelocity = -0.5;
            r.angularVelocity = 10;
            countTimesThatSeemStuck++;
            return;
        }


        //move and turn
        if (Math.abs(r.getXPosition() - parameters.get("spotX")) < 5 && Math.abs(r.getYPosition() - parameters.get("spotY")) < 5 ) { //already at spot, now turn to target
            TurnTo.turn(r, new Coordinate(parameters.get("turnSpotX"), parameters.get("turnSpotY")));
            double targetTheta = getTargetTheta(r, parameters.get("turnSpotX"), parameters.get("turnSpotY"));
            r.linearVelocity = 0;
            if (Math.abs(targetTheta) < 5) {
                r.angularVelocity = 0;
            }
            countTimesThatSeemStuck = 0;
        }
        else {
            targetX = parameters.get("spotX");
            targetY = parameters.get("spotY");
            MoveToSpot.move(r, new Coordinate(targetX, targetY), 0.5);
            oldDistanceToTarget = getDistanceToTarget(r);
        }
    }

    private double getTargetTheta(Robot r, double x, double y) {
        double targetTheta = Math.atan2(r.getYPosition() - y, x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        return Math.toDegrees(difference);
    }


    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}
