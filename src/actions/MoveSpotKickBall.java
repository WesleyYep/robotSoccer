package actions;

import data.Coordinate;
import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class MoveSpotKickBall extends Action {

    private boolean isPreviousDirectionForward = true;
    private boolean isCharging = true;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private double behindRange = 5;
    private int kp = 3;
    private int speed = 5; //0.5

    {
        parameters.put("spotX", 160);
        parameters.put("spotY", 50);
    }

    @Override
    public void execute() {

        int spotX = parameters.get("spotX");
        int spotY = parameters.get("spotY");
        //System.out.println(predX + " - " + predY);
        //kick ball into goal
        double m1 = (predY-spotY) / (predX - spotX);
        double c1 = predY - (m1 * predX);
        double x1 = 220;

        double y1 = m1 *x1 + c1;
        if (y1 > 70 && y1 < 110  && ballX > bot.getXPosition()) { //intercept with goal line is in goal

            //reverse if stuck
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

            double targetX = ballX;
            double targetY = ballY;

            //change target if it's a negative situation
            if (bot.getXPosition() - ballX > behindRange) {
                if (Math.abs(ballY - bot.getYPosition()) > 10 || bot.getXPosition() < ballX) { //bot is not inline with ball
                    behindRange = -15;
                    targetX = ballX - 20;
                } else {
                    behindRange = -15;
                    targetX = ballX - 20;
                    if (ballY > 110) {
                        targetY = ballY - 20;
                    } else {
                        targetY = ballY + 20;
                    }
                }
                if (ballX < 20) {
                    if (ballY > 45 && ballY < 135) {
                        bot.linearVelocity = 0;
                        return;//don't go for it!
                    } else {
                        targetX = ballX;
                        targetY = ballY;
                    }
                }
            } else {
                behindRange = 5;
            }

            boolean isCurrentDirectionForward;
            //get angle to ball
            double angleToBall = getTargetTheta(bot, targetX, targetY);
            double actualAngleError;
            double distanceToBall = getDistanceToTarget(bot, targetX, targetY);

            //spin if ball is stuck beside robot on positive side
            if (distanceToBall < 7 && Math.abs((angleToBall-90)%180) < 30 && targetX > bot.getXPosition()) {
                if (angleToBall > 0) {//ie. around 90
                    bot.angularVelocity = 30;
                } else {
                    bot.angularVelocity = -30;
                }
                return;
            }

            if ((!presetToForward && Math.abs(angleToBall) > 90) || presetToBackward) {
                if (angleToBall < 0) {
                    actualAngleError = Math.toRadians(-180 - angleToBall);
                } else {
                    actualAngleError = Math.toRadians(180 - angleToBall);
                }
                bot.angularVelocity = actualAngleError * kp * -1;
                bot.linearVelocity = speed/10.0 * -1;
                isCurrentDirectionForward = false;
            } else {
                actualAngleError =  Math.toRadians(angleToBall);
                bot.angularVelocity = actualAngleError * kp;
                bot.linearVelocity = speed/10.0;
                isCurrentDirectionForward = true;
            }
            isPreviousDirectionForward = isCurrentDirectionForward;

            //kick ball into goal
            double angleToGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees
            if (actualAngleError < 0.5 && ballX > bot.getXPosition()) { //facing ball
                double m = (ballY-bot.getYPosition()) / (ballX - bot.getXPosition());
                double c = ballY - (m * ballX);
                double x = 220;

                double y = m *x + c;
                if (y > 70 && y < 110) { //intercept with goal line is in goal
                    bot.linearVelocity *= 3;
                //    System.out.println("kicking for goal!");
                }
            }

            //charge ball into goal
            double range = 10;
            if (isCharging) {
                range = 30;
            }
            if (distanceToBall < range && Math.abs(actualAngleError) < Math.PI/10 /* radians*/) {
                bot.linearVelocity = isCurrentDirectionForward ? 1 : -1;
                if (targetX > 110) {
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
                isCharging = false;
            }

        } else {
            MoveToSpot.pidMove(bot, spotX, spotY);
            if (bot.linearVelocity == 0) {
                turn();
            }
        }

    }

    private void turn() {
        double targetX = 220;
        double targetY = 90;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }


}

//package actions;
//
//import data.Coordinate;
//import strategy.Action;
//
///**
// * Created by Wesley on 18/07/2015.
// */
//public class MoveSpotKickBall extends Action {
//
//    //    private LimitedQueue errorsList = new LimitedQueue(10);
//    private boolean isPreviousDirectionForward = true;
//    private double kp = 3;
//    private boolean presetToForward = false;  // if true, robot will definitely go forward
//    private boolean presetToBackward = false; //if true, robot will definitely go backwards
//    private int state = 0; //state 0 = moving to spot, spot 1 means kicking
//    private boolean isCharging = false;
//
//    {
//        parameters.put("spotX", 100);
//        parameters.put("spotY", 70);
//        parameters.put("turnSpotX", 110);
//        parameters.put("turnSpotY", 90);
//    }
//
//    @Override
//    public void execute() {
//
//        double targetX, targetY;
//
//        if (state == 0) {
//            targetX = parameters.get("spotX");
//            targetY = parameters.get("spotY");
//        } else {
//            targetX = ballX;
//            targetY = ballY;
//        }
//        double dist = getDistanceToTarget(bot, targetX, targetY);
//
//        if (state == 0 && dist < 5) {
//            state = 1;
//            return;
//        }
//
//        if (bot.isStuck(new Coordinate(bot.getXPosition(), bot.getYPosition()))) {
//            if (!presetToBackward && !presetToForward && dist > 10) {
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
//
//        //check for obstacles
////        for (int i = 0; i < opponentRobots.getRobots().length; i++) {
////            Robot opp = opponentRobots.getRobot(i);
////            if ((isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) < 20)
////                    || (!isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) > 160)
////                    && getDistanceToTarget(bot, opp.getXPosition(), opp.getYPosition()) < 20) {
////                if (isPreviousDirectionForward) {
////                    presetToBackward = true;
////                } else {
////                    presetToForward = true;
////                }
////            }
////        }
//
//        boolean isCurrentDirectionForward;
//
//        //get angle to target
//        double angleToTarget = getTargetTheta(bot, targetX, targetY);
//        double actualAngleError;
//
//        if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
//            if (angleToTarget < 0) {
//                actualAngleError = Math.toRadians(-180 - angleToTarget);
//            } else {
//                actualAngleError = Math.toRadians(180 - angleToTarget);
//            }
//            bot.angularVelocity = actualAngleError * kp * -1;
//            bot.linearVelocity = 0.5 * -1;
//            isCurrentDirectionForward = false;
//        } else {
//            actualAngleError =  Math.toRadians(angleToTarget);
//            bot.angularVelocity = actualAngleError * kp;
//            bot.linearVelocity = 0.5;
//            isCurrentDirectionForward = true;
//        }
//
//        isPreviousDirectionForward = isCurrentDirectionForward;
//
//        if (dist <= 3) {
//            bot.linearVelocity = 0;
//            turn();
//        }else if (dist < 10) {
//            bot.linearVelocity *= dist/20.0;
//        }
//
//        if (state == 1) {
//            //charge ball into goal
//            double range = 10;
//            if (isCharging) {
//                range = 30;
//            }
//            if (dist < range && Math.abs(actualAngleError) < Math.PI/10 /* radians*/) {
//                bot.linearVelocity = isCurrentDirectionForward ? 1 : -1;
//                if (targetX > 110) {
//                    double angleToGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees
//                    if (Math.abs(angleToGoal) > 45) {
//                        if (angleToGoal > 0 && isCurrentDirectionForward || angleToGoal < 0 && !isCurrentDirectionForward) {
//                            bot.angularVelocity = 30;
//                        } else {
//                            bot.angularVelocity = -30;
//                        }
//                    }
//                }
//                isCharging = true;
//            } else {
//                if (isCharging) {
//                    state = 0;
//                }
//                isCharging = false;
//            }
//        }
//
//
//    }
//
//    private void turn() {
//        double targetX = parameters.get("turnSpotX");
//        double targetY = parameters.get("turnSpotY");
//
//        //get angle to target
//        double angleToTarget = getTargetTheta(bot, targetX, targetY);
//        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
//    }
//
//
//
//}
