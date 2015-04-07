package actions;

import Paths.BezierCurvePath;
import bot.Robot;
import strategy.Action;
import ui.RobotSoccerMain;

/**
 * Created by Wesley on 27/02/2015.
 */
public class StrikerTest extends Action {
    @Override
    public String getName() {
        return "Striker test1";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        if (Math.abs(r.getTheta()) < 10 /*&& r.getYPosition() > 100*/) { //is robot facing horizontally towards other side
            if (path == null) {
                path = new BezierCurvePath(r, (int)r.getXPosition(), (int)r.getYPosition());
            }
        }

        if (path != null) {
            if (path.hasReachedTarget()) {
                path = null;
            } else {
                double x = path.getX();
                double nextX = path.getNextX();
                double y = path.getY();
                double nextY = path.getNextY();

                double nextTheta = Math.atan2(y - nextY, nextX - x);
                double difference = nextTheta - Math.toRadians(r.getTheta());
                if (difference > Math.PI) {
                    difference -= (2 * Math.PI);
                } else if (difference < -Math.PI) {
                    difference += (2 * Math.PI);
                }

                r.linearVelocity = 1;
                r.angularVelocity = difference/(RobotSoccerMain.TICK_TIME_MS/20.000);
       //         System.out.println("angular velocity: " + r.angularVelocity);
                path.step();
            }
        } else {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        }


    }
}
