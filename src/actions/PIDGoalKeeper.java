package actions;

import data.Coordinate;
import strategy.Action;
import ui.Field;

/**
 * Created by Wesley on 18/07/2015.
 */
public class PIDGoalKeeper extends Action {

    private long lastTime = 0;
//    private LimitedQueue errorsList = new LimitedQueue(10);
    private boolean isPreviousDirectionForward = true;
    private double kp = 3;
    private double ki = 0;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private double lastBallX = 0;
    private double lastBallY = 0;
    private double lastBallX2 = 0;
    private double lastBallY2 = 0;

    {
        parameters.put("goalLine", 5);
        parameters.put("topPoint", 70);
        parameters.put("bottomPoint", 110);
    }

    @Override
    public void execute() {

        double targetX = parameters.get("goalLine");
        double targetY = getYPositionForGoalKeeper();
        double dist = getDistanceToTarget(bot, targetX, targetY);
        double goalLine = parameters.get("goalLine");

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

        //check for obstacles
//        for (int i = 0; i < opponentRobots.getRobots().length; i++) {
//            Robot opp = opponentRobots.getRobot(i);
//            if ((isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) < 20)
//                    || (!isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) > 160)
//                    && getDistanceToTarget(bot, opp.getXPosition(), opp.getYPosition()) < 20) {
//                if (isPreviousDirectionForward) {
//                    presetToBackward = true;
//                } else {
//                    presetToForward = true;
//                }
//            }
//        }

        boolean isCurrentDirectionForward;
        double timePeriod;
        long currentTime = System.currentTimeMillis();

        timePeriod = lastTime == 0 ? 0 : currentTime - lastTime;
        lastTime = currentTime;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        double actualAngleError;

        //clear the ball
        if (ballX <= goalLine + 5 && ballX > goalLine - 5) {
            //System.out.println(targetTheta);
            if (ballY > bot.getYPosition() && ballY - bot.getYPosition() < 15 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToTarget) < 5 || Math.abs(angleToTarget) > 175 )) {
                MoveToSpot.move(bot, new Coordinate((int)goalLine, 175), 2, false);
                return;
            } else {
                if (ballY < bot.getYPosition() && bot.getYPosition() - ballY < 15 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToTarget) < 5 || Math.abs(angleToTarget) > 175 )) {
                    MoveToSpot.move(bot, new Coordinate((int)goalLine, 5), 2, false);
                    return;
                }
            }
        }

        if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
            bot.angularVelocity = actualAngleError * kp * -1;
            bot.linearVelocity = 0.5 * -1;
            isCurrentDirectionForward = false;
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = 0.5;
            isCurrentDirectionForward = true;
        }
//        if (isCurrentDirectionForward == isPreviousDirectionForward) {
//            errorsList.add(actualAngleError * timePeriod/1000.0);
//        } else {
//            errorsList.clear();
//        }
        isPreviousDirectionForward = isCurrentDirectionForward;
//        bot.angularVelocity += errorsList.getTotal() * ki;

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 10 && Math.abs(bot.getTheta()) > 10) {
            bot.linearVelocity *= dist/10.0;
        }


    }

    private double getYPositionForGoalKeeper() {

        double area1Weighting; // > 110
        double area2Weighting; // > 50
        double area3Weighting; // > 0
        double area1Y = Field.OUTER_BOUNDARY_HEIGHT/2;
        double area2Y;

        int topPoint = parameters.get("topPoint");
        int bottomPoint = parameters.get("bottomPoint");
        double goalLine = parameters.get("goalLine");

        double yDiff = Math.round(ballY-lastBallY);
        double xDiff = Math.round(ballX-lastBallX);

        boolean goingHorizontal = false;
        boolean goingVertical = false;
        double trajectoryY = 0;
        double area3Y = 0;

        if (yDiff == 0) {
            goingHorizontal = true;
        }

        if (xDiff == 0) {
            goingVertical = true;
        }

        if (!(goingVertical || goingHorizontal)) {
            double sumY = ballY + lastBallY + lastBallY2;
            double sumX = ballX + lastBallX + lastBallX2;

            double sumX2 = (ballX*ballX) + (lastBallX*lastBallX) + (lastBallX2*lastBallX2);
            double sumXY = (ballX*ballY) + (lastBallX*lastBallY) + (lastBallY2*lastBallX2);

            double xMean = sumX/3;
            double yMean = sumY/3;

            double slope = (sumXY - sumX * yMean) / (sumX2 - sumX * xMean);

            double yInt = yMean - slope* xMean;


            if (goalLine < 110 ) {
                trajectoryY = (slope*(goalLine+3.75)) + yInt;
            }
            else {
                trajectoryY = (slope*(goalLine-3.75)) + yInt;
            }

        }

        //working out the weighting for each area to ensure smooth transition for the goal keeper
        //area 1
        if (ballX > 120) {
            area1Weighting = 1;
        } else if (ballX <= 120 && ballX >= 100) {
            area1Weighting = (ballX - 100.0)/(120.0-100.0);
        } else {
            area1Weighting = 0;
        }


        //area 2
        if (ballX > 120) {
            area2Weighting = 0;
        } else if (ballX <= 120 && ballX >= 100) {
            area2Weighting = (ballX-120.0) /(100.0-120.0);
        } else if (ballX < 100 && ballX > 45) {
            area2Weighting = 1;
        } else if (ballX <= 45 && ballX >= 35) {
            area2Weighting = (ballX - 35.0) / (45.0-35.0);
        } else {
            area2Weighting = 0;
        }

        area2Y = 80 + (((100.0-80.0)/(180.0-0.0))*ballY);

        //area 3
        if (ballX > 45) {
            area3Weighting = 0;
        } else if (ballX <= 45 && ballX >= 35) {
            area3Weighting = (ballX -45.0) /(35.0-45.0);
        } else {
            area3Weighting = 1;
        }

        if (ballY >= topPoint-20 && ballY <= bottomPoint+20) {
            if (trajectoryY >= topPoint && trajectoryY <= bottomPoint) {
                area3Y = trajectoryY;
            } else {
                if (ballY >= topPoint && ballY <= bottomPoint) {
                    area3Y = ballY;
                } else if (ballY < topPoint) {
                    area3Y = topPoint;
                } else if (ballY > bottomPoint) {
                    area3Y = bottomPoint;
                }
            }
        }
        else {
            area3Y = topPoint + ((bottomPoint - topPoint) / (176.0 - 3.0)) * ballY;
        }

        return area1Y*area1Weighting + area2Y*area2Weighting + area3Y*area3Weighting;
    }

    private void turn() {
        double targetX = parameters.get("goalLine");
        double targetY = 0;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }



}
