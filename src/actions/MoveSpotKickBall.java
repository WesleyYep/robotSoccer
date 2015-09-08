package actions;

import data.Coordinate;
import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class MoveSpotKickBall extends Action {

    //    private LimitedQueue errorsList = new LimitedQueue(10);
    private boolean isPreviousDirectionForward = true;
    private double kp = 3;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private int state = 0; //state 0 = moving to spot, spot 1 means kicking
    private boolean isCharging = false;

    {
        parameters.put("spotX", 100);
        parameters.put("spotY", 70);
        parameters.put("turnSpotX", 110);
        parameters.put("turnSpotY", 90);
    }

    @Override
    public void execute() {

        double targetX, targetY;

        if (state == 0) {
            targetX = parameters.get("spotX");
            targetY = parameters.get("spotY");
        } else {
            targetX = ballX;
            targetY = ballY;
        }
        double dist = getDistanceToTarget(bot, targetX, targetY);

        if (state == 0 && dist < 5) {
            state = 1;
            return;
        }

        if (bot.isStuck(new Coordinate(bot.getXPosition(), bot.getYPosition()))) {
            if (!presetToBackward && !presetToForward && dist > 10) {
                System.out.println("bot is stuck :(");
                if (isPreviousDirectionForward) {
                    presetToBackward = true;
                } else {
                    presetToForward = true;
                }
            }
        } else {
            presetToBackward = false;
            presetToForward = false;
        }

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

        isPreviousDirectionForward = isCurrentDirectionForward;

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 10) {
            bot.linearVelocity *= dist/20.0;
        }

        if (state == 1) {
            //charge ball into goal
            double range = 10;
            if (isCharging) {
                range = 30;
            }
            if (dist < range && Math.abs(actualAngleError) < Math.PI/10 /* radians*/) {
                bot.linearVelocity = isCurrentDirectionForward ? 1 : -1;
                if (targetX > 110) {
                    double angleToGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees
                    if (Math.abs(angleToGoal) > 45) {
                        if (angleToGoal > 0 && isCurrentDirectionForward || angleToGoal < 0 && !isCurrentDirectionForward) {
                            bot.angularVelocity = 30;
                        } else {
                            bot.angularVelocity = -30;
                        }
                    }
                }
                isCharging = true;
            } else {
                if (isCharging) {
                    state = 0;
                }
                isCharging = false;
            }
        }


    }

    private void turn() {
        double targetX = parameters.get("turnSpotX");
        double targetY = parameters.get("turnSpotY");

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }



}
