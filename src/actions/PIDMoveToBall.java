package actions;

import bot.Robot;
import data.Coordinate;
import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class PIDMoveToBall extends Action {

    private long lastTime = 0;
 //   private LimitedQueue errorsList = new LimitedQueue(10);
    private boolean isPreviousDirectionForward = true;
    private boolean isCharging = true;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards

    {
        parameters.put("speed", 5);
        parameters.put("kp", 3); //0.5
        parameters.put("ki", 0);  //0.1
    }

    @Override
    public void execute() {


        if (bot.isStuck(new Coordinate(bot.getXPosition(), bot.getYPosition()))) {
            if (!presetToBackward && ! presetToForward) {
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

//        //check for obstacles
//        for (int i = 0; i < opponentRobots.getRobots().length; i++) {
//            Robot opp = opponentRobots.getRobot(i);
//            if (((isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) < 20)
//                    || (!isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) > 160))
//                    && getDistanceToTarget(bot, opp.getXPosition(), opp.getYPosition()) < 20) {
//                if (isPreviousDirectionForward) {
//                    System.out.println("preset back - theta = " + Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) + "dist = " + getDistanceToTarget(bot, opp.getXPosition(), opp.getYPosition()));
//                    presetToBackward = true;
//                } else {
//                    System.out.println("preset for");
//                    presetToForward = true;
//                }
//            }
//        }

        boolean isCurrentDirectionForward;
        double timePeriod;
        long currentTime = System.currentTimeMillis();

        timePeriod = lastTime == 0 ? 0 : currentTime - lastTime;
        lastTime = currentTime;

        //get angle to ball
        double angleToBall = getTargetTheta(bot, ballX, ballY);
        double actualAngleError;

        if ((!presetToForward && Math.abs(angleToBall) > 90) || presetToBackward) {
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
//        if (isCurrentDirectionForward == isPreviousDirectionForward) {
//            errorsList.add(actualAngleError * timePeriod/1000.0);
//        } else {
//            errorsList.clear();
//        }
        isPreviousDirectionForward = isCurrentDirectionForward;
//        bot.angularVelocity += errorsList.getTotal() * parameters.get("ki");

        //charge ball into goal
        double range = 10;
        if (isCharging) {
            range = 30;
        }
        if (getDistanceToTarget(bot, ballX, ballY) < range && Math.abs(actualAngleError) < Math.PI/10 /* radians*/) {
            bot.linearVelocity = isCurrentDirectionForward ? 1 : -1;
            if (ballX > 110) {
                double angleToGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees
                if (Math.abs(angleToGoal) > 45) {
                    if (angleToGoal > 0 && isCurrentDirectionForward || angleToGoal < 0 && !isCurrentDirectionForward) {
                        bot.angularVelocity = 30;
                    } else {
                        bot.angularVelocity = -30;
                    }
                }
            }
//            if (bot.getXPosition() > 140) {
//                bot.angularVelocity = 30;
//            }
            isCharging = true;
        } else {
            isCharging = false;
        }

    }


    private double getDistanceToTarget(Robot r, double targetX, double targetY) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }

    private double angleDifferenceFromGoal(double x, double y, double theta) {
        double targetTheta = Math.atan2(y - 90, 220 - x);
        double difference = targetTheta - Math.toRadians(theta);
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        return Math.toDegrees(difference);
    }

}
