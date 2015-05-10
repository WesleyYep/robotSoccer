package actions;

import bot.Robot;
import data.Coordinate;
import game.Tick;
import strategy.Action;


/**
 * Created by Wesley on 27/02/2015.
 */
public class StrikerTest extends Action {
    private boolean atCentre = false;
    private boolean ready = false;
    private double isKicking = 0;

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

       /* if (isKicking > 0) {
            r.linearVelocity = isKicking;
            r.angularVelocity = 0;
            if (r.getXPosition() > 200 || Math.abs(r.getTheta()) > 5) {
                isKicking = 0;
            }
        }
        else */if (ready) {
            isKicking = ballComingIntoPath(r);
            if (isKicking == 0) {
                ready = false;
            }
        }
        else if (atCentre && Math.abs(r.getXPosition() - 110) < 10 && Math.abs(r.getYPosition() - 90) < 10 ) { //already at centre, now turn to goal
            TurnTo.turn(r, new Coordinate(220, 90));
            r.linearVelocity = 0;
            if (Math.abs(r.angularVelocity) < 0.1 && Math.abs(r.getTheta()) < 5) {
                r.angularVelocity = 0;
                ready = true;
            }
        }
        else {
            ready = false;
            MoveToSpot.move(r, new Coordinate(110, 90));
            if (r.linearVelocity == 0 && r.angularVelocity == 0) {
                atCentre = true;
            }
        }
    }

    private double ballComingIntoPath(Robot r) {
        //return false if ball is moving away
        if ((predY > ballY && ballY > r.getYPosition()) || (predY < ballY && ballY < r.getYPosition())) {
            return 0;
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
            if (time < 3) {
                //get distance of robot to spot
                double robotDistance = Math.sqrt(squared(r.getXPosition()-x) + squared(ballY-r.getYPosition()));
                r.linearVelocity = (robotDistance/time)/100;
                r.angularVelocity = 0;
                return r.linearVelocity;
            }
        }
        return 0;
    }

    protected static double squared (double x) {
        return x * x;
    }

}

