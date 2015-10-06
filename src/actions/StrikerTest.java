package actions;

import strategy.Action;
import strategy.GameState;

/**
 * Created by Wesley on 18/07/2015.
 */
public class StrikerTest extends Action {

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
                if (time > 0 || (ballX > bot.getXPosition() && Math.abs(ballY - bot.getYPosition()) < 5)) {
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
                }
            }
            state = 0;
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
        double m = (ballY-lastBallY) / (ballX - lastBallX);
        double c = ballY - (m * ballX);
        double y = parameters.get("targetY");

        //y = mx + c therefore x = (y-c)/m
        double xInt = (y - c)/m;
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
        if (xInt > bot.getXPosition() && ((lastBallY > ballY && lastBallY > y) || (lastBallY < ballY && lastBallY < y))) {
       //     System.out.println("ball dist: " + ballDistance + "    ballSpeed: " + ballSpeed);
            return time;
        } else {
            return -1;
        }

    }

    private double turn() {
        double targetX = 220;
        double targetY = parameters.get("targetY");

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
        return angleToTarget;
    }



}
