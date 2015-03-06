package actions;

import Paths.StraightLinePath;
import bot.Robot;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class ChaseBall2 extends Action{
    @Override
    public String getName() {
        return "Chase Ball (smooth)";
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
        double distance = Math.sqrt(squared(r.getXPosition()-ballX) + squared(r.getYPosition()-ballY));
        r.linearVelocity = 1;
        r.angularVelocity = difference / (distance/100);



        //       System.out.println("ballTheta: " + ballTheta);
        //       System.out.println("robot ballTheta: " + Math.toRadians(r.getTheta()));
//        if (Math.abs(ballTheta - Math.toRadians(r.getTheta())) >= 0.8) {
//            if (ballTheta - Math.toRadians(r.getTheta()) > 0) {
//                r.angularVelocity = 2*Math.PI;
//            } else {
//                r.angularVelocity = -2*Math.PI;
//            }
//            r.linearVelocity = 0;
//        } else {
//            r.linearVelocity = 1;
//            r.angularVelocity = 0;
//        }




    }

    protected double squared (double x) {
        return x * x;
    }
}