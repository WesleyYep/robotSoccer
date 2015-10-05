package actions;

import data.Coordinate;
import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class VerticalPusher extends Action {

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

        double speed = 0.5;
        if (Math.abs(bot.getTheta()) < 95 && Math.abs(bot.getTheta()) > 85) {
        //    System.out.println("on goal line");
            speed = 0.8;
        }

        if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
            bot.angularVelocity = actualAngleError * kp * -1;
            bot.linearVelocity = Math.abs(angleToTarget) > 160 ? speed * -1 : 0;
            isCurrentDirectionForward = false;
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = Math.abs(angleToTarget) < 20 ? speed : 0;
            isCurrentDirectionForward = true;
        }
        isPreviousDirectionForward = isCurrentDirectionForward;

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 10 && Math.abs(bot.getTheta()) > 10) {
            bot.linearVelocity *= dist/20.0;
        }

        double angleToBall = getTargetTheta(bot, ballX, ballY); //degrees
        //clear the ball
        int goalLine = parameters.get("goalLine");
        if (ballX <= goalLine + 10 && ballX > goalLine - 10) {
            if (ballY > bot.getYPosition() && ballY - bot.getYPosition() < 35 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToBall) < 10 || Math.abs(angleToBall) > 170 )) {
                bot.linearVelocity = -2;
                bot.angularVelocity = 0;
                return;
            } else {
                if (ballY < bot.getYPosition() && bot.getYPosition() - ballY < 35 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToBall) < 10 || Math.abs(angleToBall) > 170 )) {
                    bot.linearVelocity = 2;
                    bot.angularVelocity = 0;
                    return;
                }
            }
        }

    }

    private double getYPositionForGoalKeeper() {
        //just use ballY
        double minY = parameters.get("topPoint");
        double maxY = parameters.get("bottomPoint");

        double y;

        if (bot.getYPosition() < 90) {
            y = ballY - 10;
        } else {
            y = ballY + 10;
        }

        return y < maxY && y > minY ? y : y > maxY ? maxY : minY;
    }

    private void turn() {
        double targetX = parameters.get("goalLine");
        double targetY = 0;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }



}