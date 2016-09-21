package actions;

import data.Coordinate;
import strategy.Action;
import ui.Field;

/**
 * Created by Wesley on 18/07/2015.
 */
public class GoalLineSideAttacker extends Action {

    private double kp = 3;
    private boolean spinning = false;

    {
        parameters.put("horizontalLine", 90);
        parameters.put("rightPoint", 180);
        parameters.put("leftPoint", 100);
    }

    @Override
    public void execute() {

        double targetY = parameters.get("horizontalLine");
        double targetX = getXPositionForGoalKeeper();
        double dist = getDistanceToTarget(bot, targetX, targetY);

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        double actualAngleError;

        double speed = 0.5;

        double angleToBall = getTargetTheta(bot, ballX, ballY); //degrees
        //charge the ball
        int horizontalLine = parameters.get("horizontalLine");
        if (ballY <= horizontalLine + 5 && ballY > horizontalLine - 5) {
            if (ballX > bot.getXPosition() && ballX - bot.getXPosition() < 35 && Math.abs(bot.getYPosition() - horizontalLine) < 5 &&(Math.abs(angleToBall) < 10 || Math.abs(angleToBall) > 170 )) {
                MoveToSpot.move(bot, new Coordinate(200, horizontalLine), 2, false);
                return;
            }
        }

        //spin if close
        if (getDistanceToTarget(bot, ballX, ballY) < 10) {
            spinning = true;
        } else if (getDistanceToTarget(bot, ballX, ballY) > 15) {
            spinning = false;
        }
        if (spinning) {
            if (ballY < bot.getYPosition()) {
                bot.angularVelocity = -25;
            } else {
                bot.angularVelocity = 25;
            }
            bot.linearVelocity = 0;
            return;
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
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = Math.abs(angleToTarget) < 20 ? speed : 0;
        }
        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 20) {
            //  }else if (dist < 10 && Math.abs(bot.getTheta()) > 10) {
            //bot.linearVelocity *= dist/20.0;
            if (bot.linearVelocity > 0) {
                bot.linearVelocity = dist > 15 ? 0.4 : dist > 10 ? 0.3 : dist > 5 ? 0.2 : 0.1;
            } else if (bot.linearVelocity < 0) {
                bot.linearVelocity = dist > 15 ? -0.4 : dist > 10 ? -0.3 : dist > 5 ? -0.2 : -0.1;
            }
        }

    }

    private double getXPositionForGoalKeeper() {
        double minX = parameters.get("leftPoint");
        double maxX = parameters.get("rightPoint");
        return ballX < maxX && ballX > minX ? ballX-10 : ballX > maxX ? maxX : minX;
    }

    private void turn() {
        double targetX = 220;
        double targetY = parameters.get("horizontalLine");

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }



}