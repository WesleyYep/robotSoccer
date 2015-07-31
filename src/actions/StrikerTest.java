package actions;

import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class StrikerTest extends Action {

    private double kp = 5;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private double lastBallX = 0;
    private double lastBallY = 0;
    private long lastTime = 0;

    {
        parameters.put("targetX", 170);
        parameters.put("targetY", 90);
    }

    @Override
    public void execute() {

        double targetX = parameters.get("targetX");
        double targetY = parameters.get("targetY");
        double angleToGoal = Math.abs(getTargetTheta(bot, 220, targetY));
        double speedMod = 1;

        if (ballX < 50) {
            targetY = ballY < 70 ? 75 : ballY > 110 ? 105 : ballY;
        }

        //check if ball is coming into path
        if (ballComingIntoPath()) {
            if (getDistanceToTarget(bot, targetX, 90) > 5 || (angleToGoal > 10 && angleToGoal < 170)) {
                //try to intercept
                //         System.out.println("not on goaline but trying to intercept");
            } else {
                speedMod = 3;
                targetX = ballX;
                targetY = ballY;
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
            actualAngleError = Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = 0.5;
        }

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        } else if (dist < 10) {
            bot.linearVelocity *= dist / 20.0;
        }
        if (Math.abs(bot.angularVelocity) < 0.2) {
            bot.angularVelocity = 0;
        }
        bot.linearVelocity *= speedMod;

        lastBallX = ballX;
        lastBallY = ballY;

    }

    private boolean ballComingIntoPath() {
        double m = (ballY-lastBallY) / (ballX - lastBallX);
        double c = ballY - (m * ballX);
        double y = parameters.get("targetY");

        //y = mx + c therefore x = (y-c)/m
        double xInt = (y - c)/m;
        double ballDistance = Math.sqrt(squared(ballX-xInt) + squared(ballY-y));
        long currentTime = System.currentTimeMillis();
        double ballSpeed = (Math.sqrt(squared(ballX-lastBallX) + squared(ballY-lastBallY))) / ((currentTime - lastTime)/1000);
        lastTime = currentTime;
        double time = ballDistance / ballSpeed;
        if (time > 1) {
            //System.out.println("time or distance too long! " + time);
            xInt = 0;
        }

        return (xInt > bot.getXPosition());

    }

    private void turn() {
        double targetX = 220;
        double targetY = parameters.get("targetY");

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }



}
