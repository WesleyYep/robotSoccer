package actions;

import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class AdvancedGoalKeeperStriker extends Action {
    private double kp = 2;
    private int state = 0; //state 0 is the normal defence line state

    {
        parameters.put("goalLine", 130);
        parameters.put("topPoint", 0);
        parameters.put("bottomPoint", 180);
    }

    @Override
    public void execute() {

        //state 0 is the normal defence line state
        if (state == 0) {

            double targetX = parameters.get("goalLine");
            double targetY = getYPositionForGoalKeeper();
            double dist = getDistanceToTarget(bot, targetX, targetY);

            boolean isCurrentDirectionForward;

            //get angle to target
            double angleToTarget = getTargetTheta(bot, targetX, targetY);
            double actualAngleError;

            double speed = 0.5;
            if (Math.abs(bot.getTheta()) < 95 && Math.abs(bot.getTheta()) > 85) {
                //    System.out.println("on goal line");
                speed = 0.8;
            }

            if ((Math.abs(angleToTarget) > 90)) {
                if (angleToTarget < 0) {
                    actualAngleError = Math.toRadians(-180 - angleToTarget);
                } else {
                    actualAngleError = Math.toRadians(180 - angleToTarget);
                }
                bot.angularVelocity = actualAngleError * kp * -1;
                bot.linearVelocity = Math.abs(angleToTarget) > 160 ? speed * -1 : 0;
            } else {
                actualAngleError = Math.toRadians(angleToTarget);
                bot.angularVelocity = actualAngleError * kp;
                bot.linearVelocity = Math.abs(angleToTarget) < 20 ? speed : 0;
            }

            if (dist <= 3) {
                bot.linearVelocity = 0;
                turn();
            } else if (dist < 10 && Math.abs(bot.getTheta()) > 10) {
                bot.linearVelocity *= dist / 20.0;
            }

            //check for state change
            if (ballX > bot.getXPosition() && getDistanceToTarget(bot, ballX, ballY) < 10) {
               state = 1;
            }

        }
        else if (state == 1) {
            int angleMod = 1;

            if (bot.getYPosition() > 90) {
                angleMod = -1;
            }

            double angleToTarget = getTargetTheta(bot, ballX, ballY);
            double actualAngleError;

            if ((bot.getYPosition() < 90 && Math.abs(angleToTarget) < 10) || (bot.getYPosition() > 90 && Math.abs(angleToTarget) > 170)) {
                state = 2;
            }

            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
            bot.angularVelocity = kp * actualAngleError * angleMod;
            bot.linearVelocity = 0;

            //check for state change
            if (getDistanceToTarget(bot, ballX, ballY) > 30) {
                state = 0;
            }
        }
        else if (state == 2) {
            double angleToTarget = getTargetTheta(bot, ballX, ballY);
            double actualAngleError;
            double angleToGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees

            if ((Math.abs(angleToTarget) > 90)) {
                if (angleToTarget < 0) {
                    actualAngleError = Math.toRadians(-180 - angleToTarget);
                } else {
                    actualAngleError = Math.toRadians(180 - angleToTarget);
                }
                bot.angularVelocity = actualAngleError * kp * -1 + Math.toRadians(angleToGoal);
                bot.linearVelocity = -0.2;
            } else {
                actualAngleError =  Math.toRadians(angleToTarget);
                bot.angularVelocity = actualAngleError * kp + Math.toRadians(angleToGoal);
                bot.linearVelocity = 0.2;
            }

            if (Math.abs(angleToGoal) < 10) {
                bot.angularVelocity = 0;
                bot.linearVelocity = 3;
            }

            //check for state change
            if (getDistanceToTarget(bot, ballX, ballY) > 30) {
                state = 0;
            }
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