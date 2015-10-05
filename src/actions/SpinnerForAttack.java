package actions;

import strategy.Action;
import strategy.GameState;

/**
 * Created by Wesley on 18/07/2015.
 */
public class SpinnerForAttack extends Action {

    private double kp = 3;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private double lastBallX = 0;
    private double lastBallY = 0;
    private long lastTime = 0;
    private int state = 0;

    {
        parameters.put("targetX", 170);
        parameters.put("targetY", 90);
    }

    @Override
    public void execute() {

        double targetX = parameters.get("targetX");
        double targetY = parameters.get("targetY");

        if (state == 1) {
            if (bot.getXPosition() > targetX - 10) {
                //check if ball is coming into path
                double time = ballComingIntoPath();

                if (time > 0/* || (ballX > bot.getXPosition() && Math.abs(ballY - bot.getYPosition()) < 5)*/) {
                    bot.linearVelocity = time > 500 ? 0 : time > 300 ? 0.3 : time > 200 ? 0.5 : time > 100 ? 1 : 2;
                    //     System.out.println("time: " + time);
                    if (time < 200) {
                        GameState.getInstance().addToWhatsGoingOn("waitingStrikerKicking");
                    } else {
                        GameState.getInstance().removeFromWhatsGoingOn("waitingStrikerKicking");
                    }
                    bot.angularVelocity = 0;
                    lastBallY = ballY;
                    lastBallX = ballX;
                    return;
                } else {
                    GameState.getInstance().removeFromWhatsGoingOn("waitingStrikerKicking");
                    state = 0;
                }
            }
        }

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
        else if (state == 0) {
            double dist = getDistanceToTarget(bot, targetX, targetY);

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
            } else {
                actualAngleError = Math.toRadians(angleToTarget);
                bot.angularVelocity = actualAngleError * kp;
                bot.linearVelocity = 0.5;
            }
//        if (isCurrentDirectionForward == isPreviousDirectionForward) {
//            errorsList.add(actualAngleError * timePeriod/1000.0);
//        } else {
//            errorsList.clear();
//        }
//        bot.angularVelocity += errorsList.getTotal() * ki;

            if (dist <= 3) {
                bot.linearVelocity = 0;
                if (Math.abs(turn()) < 10) {
                    state = 1;
                }
            } else if (dist < 10) {
                bot.linearVelocity *= dist / 20.0;
            }

            lastBallX = ballX;
            lastBallY = ballY;
        }
    }

    private double ballComingIntoPath() {

        double targetX = parameters.get("targetX");
        double targetY = parameters.get("targetY");
        double goalY = 90;
        double goalX = 220;

        double mLine = (goalY - targetY) / (goalX - targetX);
        double cLine = goalY - (mLine * goalX);

        double m = (ballY-lastBallY) / (ballX - lastBallX);
        double c = ballY - (m * ballX);

    //    System.out.println("yolo: " + Math.abs((mLine * ballX + cLine) - ballY));

        if ((Double.isNaN(m) || Double.isInfinite(m) || Double.isNaN(c) || Double.isInfinite(c))
                && Math.abs((mLine * ballX + cLine) - ballY) > 4) {
            return -1;
        }
        //m * xInt + c = mLine * xInt + cLine
        // (m - mLine)xInt = cLine - c
        // xInt = (cLine - c) / (m - mLine)

        //System.out.println("m: " + m);
        //System.out.println("c: " + c);

        double xInt = (cLine - c) / (m - mLine);
        double y = mLine * xInt + cLine;

//        if (xInt > targetX && xInt < goalX && y > 0 && y < 180) {
  //          System.out.println("xInt: " + xInt);
  //          System.out.println("yInt: " + y);
  //      }

     //   double y = targetY;
        //y = mx + c therefore x = (y-c)/m
        double ballDistance = Math.sqrt(squared(ballX-xInt) + squared(ballY-y));
        long currentTime = System.currentTimeMillis();
        double ballSpeed = (Math.sqrt(squared(ballX-lastBallX) + squared(ballY-lastBallY))) / ((currentTime - lastTime));
        //    System.out.println("current: " + currentTime + "last: " + lastTime);
        lastTime = currentTime;
        double time = ballDistance / ballSpeed;
      /*  if (time > 3) {
            //System.out.println("time or distance too long! " + time);
            xInt = 0;
        }
*/
        if (xInt > bot.getXPosition()/* && ((lastBallY > y && lastBallY > y) || (lastBallY < ballY && lastBallY < y))*/) {
            //     System.out.println("ball dist: " + ballDistance + "    ballSpeed: " + ballSpeed);
            return time;
        } else if (Math.abs((mLine * ballX + cLine) - ballY) <= 4 && ballX > bot.getXPosition()) {
      //      System.out.println("CHARGE");
            return 1;
        } else {
            return -1;
        }

    }

    private double turn() {
        double targetX = 220;
        double targetY = 90;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
        return angleToTarget;
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
