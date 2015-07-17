package actions;

import bot.Robot;
import data.Coordinate;
import strategy.Action;

/**
 * Created by chan743 on 15/07/2015.
 */
public class GoalLineSideDefender extends Action {

//    private static final int TOP = 0;
//    private static final int BOTTOM = 1;
//    private int state = 0; //state = 0 means going to spot
    private int targetX = 0;
    private int targetY = 0;
    private double oldDistanceToTarget = 0;
    private int countTimesThatSeemStuck = 0;

    {
        parameters.put("startingX", 10);
        parameters.put("startingY", 45);
    }

    @Override
    public void execute() {
        Robot r = bot;

        //check if robot is stuck
        double newTargetDistance = getDistanceToTarget(r);
        //  System.out.println(Math.abs(oldDistanceToTarget - newTargetDistance));
        if (Math.abs(oldDistanceToTarget - newTargetDistance) < 0.4) {
            countTimesThatSeemStuck++;
        } else if (r.linearVelocity >= 0){
            countTimesThatSeemStuck = 0;
        }
        if (countTimesThatSeemStuck > 20) {
            countTimesThatSeemStuck = 0;
            return;
        } else if (countTimesThatSeemStuck > 10) {
            r.linearVelocity = -0.5;
            r.angularVelocity = 10;
            countTimesThatSeemStuck++;
            return;
        }

        //move and turn
        if (Math.abs(r.getXPosition() - parameters.get("startingX")) < 10 && Math.abs(r.getYPosition() - parameters.get("startingY")) < 10 ) { //already at centre, now turn to goal
            TurnTo.turn(r, new Coordinate(220, 90), 1);
            double targetTheta = getTargetTheta(r, 220, 90);
            r.linearVelocity = 0;
            if (Math.abs(targetTheta) < 5) {
                r.angularVelocity = 0;
            }
            countTimesThatSeemStuck = 0;
        }
        else {
            targetX = parameters.get("startingX");
            targetY = parameters.get("startingY");
            MoveToSpot.move(r, new Coordinate(targetX, targetY), 1, true);
            oldDistanceToTarget = getDistanceToTarget(r);
        }

        if (getDistanceToTarget(r) < 10) {
            r.angularVelocity = 30;
            r.linearVelocity = 0;
            return;
        }

//        int y;
//        int facing;
//        int side = parameters.get("side");
//
//        if (side == TOP) {
//            y = 45;
//            facing = 0;
//        } else {
//            y = 135;
//            facing = 180;
//        }
//
//        switch (state) {
//            case 0: //moving to spot
//                MoveAndTurn.moveAndTurn(bot, 5, y, 5, facing);
//                if (MoveAndTurn.getDistanceToTarget(bot, 5, y) < 5 && bot.angularVelocity == 0) {
//                    state = 1;
//                } else if(ballX < 15 && ballY > 50 && ballY < 130) {
//                    if (side == TOP) {
//                        MoveAndTurn.moveAndTurn(bot, 30, 50, 50, 180);
//                    } else {
//                        MoveAndTurn.moveAndTurn(bot, 30, 130, 130, 180);
//                    }
//                }
//                break;
//            case 1: //waiting at spot
//                if (ballX < 10) {
//                    if (ballY < bot.getYPosition() && side == TOP) {
//                        MoveToSpot.move(bot, new Coordinate(0,0), 2, false);
//                    } else if (ballY > bot.getYPosition() && side == BOTTOM) {
//                        MoveToSpot.move(bot, new Coordinate(0,180), 2, false);
//                    }
//                } /*else if (15 < ballX && ballX < 55 && (side == TOP && ballY > 50 && ballY < 90) || (side == BOTTOM && ballY < 130 && ballY > 90)) {
//                    MoveToSpot.move(bot, new Coordinate(ballX, ballY), 1, false);
//                } */else if(ballX < 15 && ballY > 50 && ballY < 130) {
//                    if (side == TOP) {
//                        MoveAndTurn.moveAndTurn(bot, 30, 50, 50, 180);
//                    } else {
//                        MoveAndTurn.moveAndTurn(bot, 30, 130, 130, 180);
//                    }
//                } else if (MoveAndTurn.getDistanceToTarget(bot, 5, y) > 5 || bot.angularVelocity != 0) {
//                    state = 0;
//                }
//                break;
//        }

    }

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

    private double getTargetTheta(Robot r, double x, double y) {
        double targetTheta = Math.atan2(r.getYPosition() - y, x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        return Math.toDegrees(difference);
    }

}
