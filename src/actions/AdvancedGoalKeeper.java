package actions;

import strategy.Action;
import ui.Field;

/**
 * Created by Wesley on 18/07/2015.
 */
public class AdvancedGoalKeeper extends Action {

    private boolean isPreviousDirectionForward = true;
    private double kp = 3;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards


    {
        parameters.put("goalLine", 110);
        parameters.put("topPoint", 0);
        parameters.put("bottomPoint", 180);
    }

    @Override
    public void execute() {

        double targetX = parameters.get("goalLine");
        double targetY = getYPositionForGoalKeeper();
        double dist = getDistanceToTarget(bot, targetX, targetY);

        boolean isCurrentDirectionForward;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        double actualAngleError;

        if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
            bot.angularVelocity = actualAngleError * kp * -1;
            bot.linearVelocity = 0.8 * -1;
            isCurrentDirectionForward = false;
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = 0.8;
            isCurrentDirectionForward = true;
        }
        isPreviousDirectionForward = isCurrentDirectionForward;

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 10 && Math.abs(bot.getTheta()) > 10) {
            bot.linearVelocity *= dist/20.0;
        }


    }

    private double getYPositionForGoalKeeper() {
        //just use ballY
        double minY = parameters.get("topPoint");
        double maxY = parameters.get("bottomPoint");

        return ballY < maxY && ballY > minY ? ballY : ballY > maxY ? maxY : minY;
    }

    private void turn() {
        double targetX = parameters.get("goalLine");
        double targetY = 0;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }



}