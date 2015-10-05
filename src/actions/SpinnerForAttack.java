package actions;

import data.Coordinate;
import strategy.Action;
import ui.Field;

/**
 * Created by Wesley on 18/07/2015.
 */
public class SpinnerForAttack extends Action {

    private boolean isPreviousDirectionForward = true;
    private double kp = 3;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards


    {
        parameters.put("firstX", 130);
        parameters.put("firstY", 30);
        parameters.put("secondX", 180);
        parameters.put("secondY", 60);
    }

    @Override
    public void execute() {

        if (Math.abs(getTargetTheta(bot, ballX, ballY)) < 10) {
            bot.linearVelocity = 1;
            bot.angularVelocity = 0;
            return;
        }

        //firstX should always be lower than secondX
        double firstX = parameters.get("firstX");
        double firstY = parameters.get("firstY");
        double secondX = parameters.get("secondX");
        double secondY = parameters.get("secondY");


        double m = (secondY-firstY) / (secondX-firstX);
        double expectedTheta = -Math.atan(m);
       // System.out.println("expectecTheta: " + expectedTheta);
        double c = firstY - (m * firstX);
        double x = ballX-5;

        double targetX = x > firstX && x < secondX ? x : x < firstX ? firstX : secondX;
        double y = m * targetX + c;

        double targetY = y;


        double dist = getDistanceToTarget(bot, targetX, targetY);

        boolean isCurrentDirectionForward;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        double actualAngleError;

        double speed = 0.5;
//        if (Math.abs(bot.getTheta()) < expectedTheta + 10 && Math.abs(bot.getTheta()) > expectedTheta - 10) {
//            //    System.out.println("on goal line");
//            speed = 0.8;
//        }

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

//        //clear the ball
//        int goalLine = parameters.get("goalLine");
//        if (ballX <= goalLine + 5 && ballX > goalLine - 5) {
//            if (ballY > bot.getYPosition() && ballY - bot.getYPosition() < 35 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToBall) < 5 || Math.abs(angleToBall) > 175 )) {
//                MoveToSpot.move(bot, new Coordinate(goalLine, 175), 2, false);
//                return;
//            } else {
//                if (ballY < bot.getYPosition() && bot.getYPosition() - ballY < 35 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToBall) < 5 || Math.abs(angleToBall) > 175 )) {
//                    MoveToSpot.move(bot, new Coordinate(goalLine, 5), 2, false);
//                    return;
//                }
//            }
//        }

    }
//
//    private double getYPositionForGoalKeeper() {
//        //just use ballY
//        double minY = parameters.get("topPoint");
//        double maxY = parameters.get("bottomPoint");
//
//        return ballY < maxY && ballY > minY ? ballY : ballY > maxY ? maxY : minY;
//    }

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
//import bot.Robot;
//import data.Coordinate;
//import strategy.Action;
//
///**
// * Created by chan743 on 15/07/2015.
// */
//public class SpinnerForAttack extends Action {
//
////    private static final int TOP = 0;
////    private static final int BOTTOM = 1;
////    private int state = 0; //state = 0 means going to spot
//    private int targetX = 0;
//    private int targetY = 0;
//    private double oldDistanceToTarget = 0;
//    private int countTimesThatSeemStuck = 0;
//    private boolean spinning = true;
//
//    {
//        parameters.put("startingX", 10);
//        parameters.put("startingY", 45);
//    }
//
//    @Override
//    public void execute() {
//        Robot r = bot;
//        targetX = parameters.get("startingX");
//        targetY = parameters.get("startingY");
//
//        //check if robot is stuck
////        double newTargetDistance = getDistanceToTarget(r);
////        //  System.out.println(Math.abs(oldDistanceToTarget - newTargetDistance));
////        if (Math.abs(oldDistanceToTarget - newTargetDistance) < 0.4) {
////            countTimesThatSeemStuck++;
////        } else if (r.linearVelocity >= 0){
////            countTimesThatSeemStuck = 0;
////        }
////        if (countTimesThatSeemStuck > 20) {
////            countTimesThatSeemStuck = 0;
////            return;
////        } else if (countTimesThatSeemStuck > 10) {
////            r.linearVelocity = -0.5;
////            r.angularVelocity = 10;
////            countTimesThatSeemStuck++;
////            return;
////        }
//        if (getDistanceToTarget(r) < 10) {
//            spinning = true;
//        } if (getDistanceToTarget(r) > 15) {
//            spinning = false;
//        }
//
//        if (spinning) {
//            if (ballY < r.getYPosition()) {
//                r.angularVelocity = 12;
//            } else {
//                r.angularVelocity = -12;
//            }
//            r.linearVelocity = 0;
//            return;
//        }
//
//        //move and turn
//        if (Math.abs(r.getXPosition() - parameters.get("startingX")) < 10 && Math.abs(r.getYPosition() - parameters.get("startingY")) < 10 ) { //already at centre, now turn to goal
//            TurnTo.turn(r, new Coordinate(220, 90), 1);
//            double targetTheta = getTargetTheta(r, 220, 90);
//            r.linearVelocity = 0;
//            if (Math.abs(targetTheta) < 5) {
//                r.angularVelocity = 0;
//            }
//            countTimesThatSeemStuck = 0;
//        }
//        else {
//            MoveToSpot.pidMove(r, targetX, targetY);
//            oldDistanceToTarget = getDistanceToTarget(r);
//        }
//
//
//
////        int y;
////        int facing;
////        int side = parameters.get("side");
////
////        if (side == TOP) {
////            y = 45;
////            facing = 0;
////        } else {
////            y = 135;
////            facing = 180;
////        }
////
////        switch (state) {
////            case 0: //moving to spot
////                MoveAndTurn.moveAndTurn(bot, 5, y, 5, facing);
////                if (MoveAndTurn.getDistanceToTarget(bot, 5, y) < 5 && bot.angularVelocity == 0) {
////                    state = 1;
////                } else if(ballX < 15 && ballY > 50 && ballY < 130) {
////                    if (side == TOP) {
////                        MoveAndTurn.moveAndTurn(bot, 30, 50, 50, 180);
////                    } else {
////                        MoveAndTurn.moveAndTurn(bot, 30, 130, 130, 180);
////                    }
////                }
////                break;
////            case 1: //waiting at spot
////                if (ballX < 10) {
////                    if (ballY < bot.getYPosition() && side == TOP) {
////                        MoveToSpot.move(bot, new Coordinate(0,0), 2, false);
////                    } else if (ballY > bot.getYPosition() && side == BOTTOM) {
////                        MoveToSpot.move(bot, new Coordinate(0,180), 2, false);
////                    }
////                } /*else if (15 < ballX && ballX < 55 && (side == TOP && ballY > 50 && ballY < 90) || (side == BOTTOM && ballY < 130 && ballY > 90)) {
////                    MoveToSpot.move(bot, new Coordinate(ballX, ballY), 1, false);
////                } */else if(ballX < 15 && ballY > 50 && ballY < 130) {
////                    if (side == TOP) {
////                        MoveAndTurn.moveAndTurn(bot, 30, 50, 50, 180);
////                    } else {
////                        MoveAndTurn.moveAndTurn(bot, 30, 130, 130, 180);
////                    }
////                } else if (MoveAndTurn.getDistanceToTarget(bot, 5, y) > 5 || bot.angularVelocity != 0) {
////                    state = 0;
////                }
////                break;
////        }
//
//    }
//
//    private double getDistanceToTarget(Robot r) {
//        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
//    }
//
//
//}
