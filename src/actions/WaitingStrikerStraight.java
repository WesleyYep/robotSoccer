package actions;

import bot.Robot;
import data.Coordinate;
import game.Tick;
import strategy.Action;
import strategy.GameState;


/**
 * Created by Wesley on 27/02/2015.
 */
public class WaitingStrikerStraight extends Action {
    private boolean ready = false;
    private boolean atCentre = false;
    private int targetX = 0;
    private int targetY = 0;
    private double oldDistanceToTarget = 0;
    private int countTimesThatSeemStuck = 0;

    //non-static initialiser block
    {
        if(!(parameters.containsKey("startingX") && parameters.containsKey("startingY"))) {
            //don't bother if these already exist
            parameters.put("startingX", 110);
            parameters.put("startingY", 90);
        }
    }

    @Override
    public void execute() {
        Robot r = bot;

        //this is number 1 priority
        if (ballComingIntoPath(r)) {
            GameState.getInstance().addToWhatsGoingOn("waitingStrikerKicking");
            return;
        }else {
            GameState.getInstance().removeFromWhatsGoingOn("waitingStrikerKicking");
        }

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
        if (Math.abs(r.getXPosition() - parameters.get("startingX")) < 10 && Math.abs(r.getYPosition() - parameters.get("startingY")) < 10 ) { //already at centre, now turn to goal
            TurnTo.turn(r, new Coordinate(220, 90));
            double targetTheta = getTargetTheta(r, 220, 90);
            r.linearVelocity = 0;
            if (Math.abs(targetTheta) < 5) {
                r.angularVelocity = 0;
                atCentre = true;
            }
            countTimesThatSeemStuck = 0;
        }
        else {
            targetX = parameters.get("startingX");
            targetY = parameters.get("startingY");
            MoveToSpot.move(r, new Coordinate(targetX, targetY), 1);
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

    private boolean ballComingIntoPath(Robot r) {
//return true if ball is directly in front (or behind) of robot
        if (Math.abs(r.getYPosition() - ballY) < 3 && ballX > r.getXPosition()) {
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

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}
