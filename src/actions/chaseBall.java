package actions;

import Paths.StraightLinePath;
import bot.Robot;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class chaseBall extends Action{
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
        
        double theta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
 //       System.out.println("theta: " + theta);
 //       System.out.println("robot theta: " + Math.toRadians(r.getTheta()));
        if (Math.abs(theta - Math.toRadians(r.getTheta())) >= 0.8) {
            r.angularVelocity = 2*Math.PI;
            r.linearVelocity = 0;
        } else {
            r.linearVelocity = 1;
            r.angularVelocity = 0;
        }

    }
}
