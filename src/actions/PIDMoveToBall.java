package actions;

import bot.Robot;
import strategy.Action;
import utils.LimitedQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 18/07/2015.
 */
public class PIDMoveToBall extends Action {

    private long lastTime = 0;
    private LimitedQueue errorsList = new LimitedQueue(10);
    private boolean isPreviousDirectionForward = true;
    private boolean isCharging = true;

    {
        parameters.put("speed", 7);
        parameters.put("kp", 5); //0.5
        parameters.put("ki", 0);  //0.1
    }

    @Override
    public void execute() {

        boolean isCurrentDirectionForward;
        double timePeriod;
        long currentTime = System.currentTimeMillis();

        timePeriod = lastTime == 0 ? 0 : currentTime - lastTime;
        lastTime = currentTime;

        //get angle to ball
        double angleToBall = getTargetTheta(bot, ballX, ballY);
        double actualAngleError;

        if (Math.abs(angleToBall) > 90) {
            if (angleToBall < 0) {
                actualAngleError = Math.toRadians(-180 - angleToBall);
            } else {
                actualAngleError = Math.toRadians(180 - angleToBall);
            }
            bot.angularVelocity = actualAngleError * parameters.get("kp") * -1;
            bot.linearVelocity = parameters.get("speed")/10.0 * -1;
            isCurrentDirectionForward = false;
        } else {
            actualAngleError =  Math.toRadians(angleToBall);
            bot.angularVelocity = actualAngleError * parameters.get("kp");
            bot.linearVelocity = parameters.get("speed")/10.0;
            isCurrentDirectionForward = true;
        }
        if (isCurrentDirectionForward == isPreviousDirectionForward) {
            errorsList.add(actualAngleError * timePeriod/1000.0);
        } else {
            errorsList.clear();
        }
        isPreviousDirectionForward = isCurrentDirectionForward;
        bot.angularVelocity += errorsList.getTotal() * parameters.get("ki");

        //charge ball into goal
        double range = 10;
        if (isCharging) {
            range = 30;
        }
        if (getDistanceToTarget(bot, ballX, ballY) < range && Math.abs(actualAngleError) < Math.PI/10 /* radians*/) {
            bot.linearVelocity = isCurrentDirectionForward ? 1.5 : -1.5;
            isCharging = true;
        } else {
            isCharging = false;
        }

    }


    private double getDistanceToTarget(Robot r, double targetX, double targetY) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }


}
