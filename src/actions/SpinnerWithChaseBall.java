//package actions;
//
//import bot.Robot;
//import data.Coordinate;
//import strategy.Action;
//
///**
// * Created by chan743 on 15/07/2015.
// */
//public class SpinnerWithChaseBall extends Action {
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
//        } if (getDistanceToTarget(r) > 20) {
//            spinning = false;
//        }
//
//        if (spinning) {
//            if (ballY < r.getYPosition()) {
//                r.angularVelocity = -12;
//            } else {
//                r.angularVelocity = 12;
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
//    }
//
//    private double getDistanceToTarget(Robot r) {
//        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
//    }
//
//
//}
