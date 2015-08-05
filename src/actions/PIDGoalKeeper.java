package actions;

import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class PIDGoalKeeper extends Action {

    private double kp = 5;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private double lastBallX = 0;
    private double lastBallY = 0;
    private long lastTime = 0;

    {
        parameters.put("goalLine", 5);
        parameters.put("topPoint", 70);
        parameters.put("bottomPoint", 110);
    }

    @Override
    public void execute() {

        double targetX = parameters.get("goalLine");
        double targetY = 90;
        double angleToTop = Math.abs(getTargetTheta(bot, targetX, 0));
        double goalLine = parameters.get("goalLine");

        if (ballX < parameters.get("goalLine")+5) {
            targetY = ballY < 70 ? 75 : ballY > 110 ? 105 : ballY;
        }

        //check if ball is coming into path
        //clear the ball
        if (ballX <= goalLine + 5 && ballX > goalLine - 5 && ballY > 50 && ballY < 130) {
            targetX = ballX;
            targetY = ballY;
        } else if (ballComingIntoPath()) {
            if (getDistanceToTarget(bot, targetX, 90) > 5 || (angleToTop > 10 && angleToTop < 170)) {
                //try to intercept
       //         System.out.println("not on goaline but trying to intercept");
            } else {
                lastBallX = ballX;
                lastBallY = ballY;
                return;
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
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = 0.5;
        }

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 10) {
            bot.linearVelocity *= dist/20.0;
        }
        if (Math.abs(bot.angularVelocity) < 0.2) {
            bot.angularVelocity = 0;
        }

        lastBallX = ballX;
        lastBallY = ballY;


    }

    private boolean ballComingIntoPath() {
        //get an equation in the form y = mx + c of the path of ball
        double m = (ballY-lastBallY) / (ballX - lastBallX);
        double c = ballY - (m * ballX);
        double x = parameters.get("goalLine");

        //check if line crosses the line y = mRx + cR (old was y = robotY) and 0 < x < r.X]
        // mx + c = y
        //use this to get coordinates of intersection point
        double yInt = m*x + c;
        double time = 0;

        if (ballX - lastBallX >= 0) {
            yInt = 0;
        } else {
            double ballDistance = Math.sqrt(squared(ballX-x) + squared(ballY-yInt));
            long currentTime = System.currentTimeMillis();
            double ballSpeed = (Math.sqrt(squared(ballX-lastBallX) + squared(ballY-lastBallY))) / ((currentTime - lastTime)/1000);
            lastTime = currentTime;
            time = ballDistance / ballSpeed;
            if (time > 1) {
                //System.out.println("time or distance too long! " + time);
                yInt = 0;
            }
        }
        if (65 < yInt && yInt < 115) {
            if (ballX < parameters.get("goalLine")+25 && (yInt > 90 && ballY < 90 || yInt < 90 && ballY > 90)) {
                return false;
            }
           // System.out.println("going to hit goal! " + yInt);
            //move forward or back to intercept
            double distanceFromRobotToIntercept = bot.getYPosition() - yInt;
            bot.linearVelocity = (distanceFromRobotToIntercept / 5)/ (time+1);
            bot.angularVelocity = 0;
            return true;
        } else {
            return false;
        }
    }


    private void turn() {
        double targetX = parameters.get("goalLine");
        double targetY = 0;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }



}
