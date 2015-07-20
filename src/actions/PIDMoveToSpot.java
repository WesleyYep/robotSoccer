package actions;

import bot.Robot;
import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class PIDMoveToSpot extends Action {

    private long lastTime = 0;
//    private LimitedQueue errorsList = new LimitedQueue(10);
    private boolean isPreviousDirectionForward = true;
    private boolean isCharging = true;
    private double kp = 3;
    private double ki = 0;

    {
        parameters.put("spotX", 100);
        parameters.put("spotY", 70);
        parameters.put("turnSpotX", 110);
        parameters.put("turnSpotY", 90);
    }

    @Override
    public void execute() {

        double targetX = parameters.get("spotX");
        double targetY = parameters.get("spotY");

        boolean presetToForward = false;  // if true, robot will definitely go forward
        boolean presetToBackward = false; //if true, robot will definitely go backwards

//        if (bot.isStuck(new Coordinate(bot.getXPosition(), bot.getYPosition()))) {
//            if (!presetToBackward && ! presetToForward) {
//                System.out.println("bot is stuck :(");
//                if (isPreviousDirectionForward) {
//                    presetToBackward = true;
//                } else {
//                    presetToForward = true;
//                }
//            }
//        } else {
//            presetToBackward = false;
//            presetToForward = false;
//        }

        //check for obstacles
//        for (int i = 0; i < opponentRobots.getRobots().length; i++) {
//            Robot opp = opponentRobots.getRobot(i);
//            if ((isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) < 20)
//                    || (!isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) > 160)
//                    && getDistanceToTarget(bot, opp.getXPosition(), opp.getYPosition()) < 20) {
//                if (isPreviousDirectionForward) {
//                    presetToBackward = true;
//                } else {
//                    presetToForward = true;
//                }
//            }
//        }

        boolean isCurrentDirectionForward;
        double timePeriod;
        long currentTime = System.currentTimeMillis();

        timePeriod = lastTime == 0 ? 0 : currentTime - lastTime;
        lastTime = currentTime;

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
            bot.linearVelocity = 0.5 * -1;
            isCurrentDirectionForward = false;
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = 0.5;
            isCurrentDirectionForward = true;
        }
//        if (isCurrentDirectionForward == isPreviousDirectionForward) {
//            errorsList.add(actualAngleError * timePeriod/1000.0);
//        } else {
//            errorsList.clear();
//        }
        isPreviousDirectionForward = isCurrentDirectionForward;
//        bot.angularVelocity += errorsList.getTotal() * ki;

        double dist = getDistanceToTarget(bot, targetX, targetY);
        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 10) {
            bot.linearVelocity *= dist/20.0;
        }


    }

    private void turn() {
        double targetX = parameters.get("turnSpotX");
        double targetY = parameters.get("turnSpotY");

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }


    private double getDistanceToTarget(Robot r, double targetX, double targetY) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }


}
