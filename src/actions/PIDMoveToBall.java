package actions;

import data.Coordinate;
import strategy.Action;
import strategy.GameState;

/**
 * Created by Wesley on 18/07/2015.
 *
 *
 * The main action that is used to move to the ball and kick it towards the goal
 * The robot will reverse if it finds that it is a negative situation and is about to kick the ball the wrong way
 * Also, the action deals with the robot getting stuck, in which case it should move backwards for a second
 *
 * Action parameters:
 * speed - the linear velocity that the PID should move at (scaled up by factor of 10)
 * kp - the proportional constant (increasing this will make it turn to the target faster)
 * ki - integral constant (unused)
 *
 */
public class PIDMoveToBall extends Action {

    private boolean isPreviousDirectionForward = true;
    private boolean isCharging = true;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private double behindRange = 5;
    private int state = 0;
    private int state2 = 0;

    {
        parameters.put("speed", 8);
        parameters.put("kp", 5); //0.5
        parameters.put("ki", 0);  //0.1
    }

    @Override
    public void execute() {

        //if the waiting striker is kicking, then wait for it
        if (GameState.getInstance().isGoingOn("waitingStrikerKicking")) {
            bot.linearVelocity = 0;
            bot.angularVelocity = 0;
            return;
        }

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

//        if (state2 == 1) {
//        	if (bot.getXPosition() > ballX - 30) {
//            //   System.out.println("negative!");
//               if (ballY < 90) {
//               	state2 = 1;
//               	MoveToSpot.move(bot, new Coordinate(0,0), 0.8, false);
//               } else {
//               	state2 = 1;
//               	MoveToSpot.move(bot, new Coordinate(0,180), 0.8, false);
//               }
////               bot.linearVelocity = isPreviousDirectionForward ? 0 : 0;
////               bot.angularVelocity = 0;
//               return;
//           } else {
//           	state2 = 0;
//           }
//        } 
//        
        double targetX = ballX;
        double targetY = ballY;

        //find pre-target spot
//        double pretargetX = predX - 40;
//        double pretargetY = predY + ((90 - targetY)/(220 - targetX))*(pretargetX - targetX);
//
//        pretargetX = pretargetX < 5 ? 5 : pretargetX > 215 ? 215 : pretargetX; //set limits
//        pretargetY = pretargetY < 5 ? 5 : pretargetY > 175 ? 175 : pretargetY; //set limits
//
//        if (state == 0) {
//            if (getDistanceToTarget(bot, pretargetX, pretargetY) < 5 || ballX > 210) { //try to push ball if its close to goal
//     //           System.out.println("reached pretarget");
//                state = 1; //going to actual target
//            } else {
//                targetX = pretargetX;
//                targetY = pretargetY;
//            }
//        }
//        if (state == 1 && bot.getXPosition() > ballX) {
//    //        System.out.println("reached target");
//            state = 0;
//        }


        boolean isCurrentDirectionForward;
        //get angle to ball
        double actualAngleError;
        double distanceToTarget = getDistanceToTarget(bot, targetX, targetY);
        double distanceToBall = getDistanceToTarget(bot, ballX, ballY);
        double angleToTarget = getTargetTheta(bot, targetX, targetY); //degrees
        double angleToBall = getTargetTheta(bot, ballX, ballY); //degrees

//        //stop if it's a negative situation
//     //   System.out.println(bot.getXPosition() + " " + ballX + " " + distanceToTarget + " " + angleToBall);
//        if (bot.getXPosition() > ballX/* && distanceToBall < 20 && (Math.abs(angleToBall) < 10 || Math.abs(angleToBall) > 170)*/) {
//         //   System.out.println("negative!");
//            if (ballY < 90) {
//            	state2 = 1;
//            	MoveToSpot.move(bot, new Coordinate(0,0), 0.8, false);
//            } else {
//            	state2 = 1;
//            	MoveToSpot.move(bot, new Coordinate(0,180), 0.8, false);
//            }
////            bot.linearVelocity = isPreviousDirectionForward ? 0 : 0;
////            bot.angularVelocity = 0;
//            return;
//        }

//        //spin if ball is stuck beside robot on positive side
//        if (distanceToBall < 7 && Math.abs((angleToBall-90)%180) < 30 && ballX > bot.getXPosition()) {
//            if (angleToBall > 0) {//ie. around 90
//                bot.angularVelocity = 30;
//            } else {
//                bot.angularVelocity = -30;
//            }
//            return;
//        }

        if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
            bot.angularVelocity = actualAngleError * parameters.get("kp") * -1;
            bot.linearVelocity = parameters.get("speed")/10.0 * -1;
            isCurrentDirectionForward = false;
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * parameters.get("kp");
            bot.linearVelocity = parameters.get("speed")/10.0;
            isCurrentDirectionForward = true;
        }
        isPreviousDirectionForward = isCurrentDirectionForward;

//        if (state == 0) {
//            return; //don't want to do any charging if still in pretarget state
//        }
//
//        //charge ball into goal
//        double range = 10;
//        if (isCharging) {
//            range = 30;
//        }
//        if (distanceToTarget < range && Math.abs(actualAngleError) < Math.PI/10 /* radians*/) {
//            bot.linearVelocity = isCurrentDirectionForward ? 1 : -1;
//            if (targetX > 110) {
//                double angleToGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees
//                if (Math.abs(angleToGoal) > 45) {
//                    if (angleToGoal > 0 && isCurrentDirectionForward || angleToGoal < 0 && !isCurrentDirectionForward) {
//                        bot.angularVelocity = 30;
//                    } else {
//                        bot.angularVelocity = -30;
//                    }
//                }
//            }
//            isCharging = true;
//        } else {
//            isCharging = false;
//        }
//
    }


}
