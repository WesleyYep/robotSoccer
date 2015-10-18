package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import strategy.GameState;

/**
 * This action chases the ball and tries to avoid obstacles
 * For practical purposes, we no longer use this for the game because it is not as useful as directly trying to go to the ball using something like PID
 *
 */
public class ChaseBallWithObstacle extends Action {

    private double error = 2.5;
    private double oldDistanceToTarget = 0;
    private int countTimesThatSeemStuck = 0;
    private boolean fastForward = false;

    @Override
    public void execute() {
        setVelocityToTarget(ballX,ballY,false,false);
    }

    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
        Robot r = bot;


        if (GameState.getInstance().isGoingOn("waitingStrikerKicking")) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
            return;
        }


        //check if robot is stuck
        double newTargetDistance = getDistanceToTarget(r);
       //   System.out.println(Math.abs(oldDistanceToTarget - newTargetDistance));
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


        //see if robot is not in positive situation
        /*
        if (ballX < r.getXPosition()) {
            System.out.println("here");
            int yPos;
            if (ballY > 90) {
                yPos = (int)(60*Math.random()) + 120;
            } else {
                yPos = (int)(60*Math.random());
            }
            MoveToSpot.move(r, new Coordinate(30, yPos), 1, true);
            return;
        } */

        oldDistanceToTarget = newTargetDistance;


        if (ballX < r.getXPosition()) {
            double angle =0;
            double yDiff = ballY-15*Math.sin(Math.toRadians(angle*-1));
            double xDiff = ballX-15*Math.cos(Math.toRadians(angle*-1));
            double robotPosition =  Math.toDegrees(Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition()));

            if (angle > 0) {
                robotPosition = robotPosition + (180-angle);
            }
            else if (angle < 0) {
                robotPosition += (-180-angle);
            }
            else {
                robotPosition += 180;
            }

            if (robotPosition >= 180) {
                robotPosition -= 360;
            }

            if (robotPosition <= -180) {
                robotPosition += 360;
            }
            robotPosition *= -1;

            double tempAngle = 0;
            if (Math.abs(robotPosition) > 160) {
                tempAngle = angle;
            } else {
                if (robotPosition > 0) {
                    tempAngle = angle+ 35;
                } else {
                    tempAngle = angle - 35;
                }
            }

            if (tempAngle >= 180) {
                tempAngle -= 360;
            }

            if (tempAngle <= -180) {
                tempAngle += 360;
            }


            double yTurn  = yDiff-20*Math.sin(Math.toRadians(tempAngle*-1));
            double xTurn  = xDiff-20*Math.cos(Math.toRadians(tempAngle*-1));
            if (Math.abs(robotPosition) > 165) {
                yTurn = yDiff;
                xTurn = xDiff;
            }

            if (xTurn < 3.75) xTurn = 6;
            if (yTurn > 176) yTurn  = 176;
            if (yTurn < 3.75) yTurn = 3.75;

            int yPos;
            if (ballY > 90) {
                yPos = (int)(60*Math.random()) + 120;
            } else {
                yPos = (int)(60*Math.random());
            }
            y = yTurn;
            x = xTurn;
        }

        double targetDist;
        double targetTheta = Math.atan2(r.getYPosition() - y, x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
//       System.out.println("initial targetTheta: " + targetTheta + " initial difference " + difference + " current Theta "
        //     		+ Math.toRadians(r.getTheta()));
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        difference = Math.toDegrees(difference);
        targetTheta = difference;
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));


        //dribble ball towards goal if we are in a dribbling position
        double ballDist = Math.sqrt(Math.pow((ballX-r.getXPosition()),2) + Math.pow((ballY-r.getYPosition()),2));
        double angleToGoal = angleDifferenceFromGoal(r.getXPosition(), r.getYPosition(), r.getTheta());
        if (ballDist <= 7) {
            if (angleToGoal > Math.PI / 18) {
                // System.out.println("fast_right");
                r.angularVelocity = 12;
                r.linearVelocity = 0.7;
            } else if (angleToGoal < -(Math.PI / 18)) {
                //  System.out.println("fast_left");
                r.linearVelocity = 0.7;
                r.angularVelocity = -12;
            } else {
                r.linearVelocity = 2;
            }
            return;
        }


        //charge ball if we are in a kicking position
        if (Math.abs(targetTheta) < 20 && targetDist < 50) {//degrees
        //    System.out.println("kicking");
            r.linearVelocity = 1;
            r.angularVelocity = 0;
            return;
        }



        double obstacleTheta = 180, obstacleDist = 220, obstacleX = 0, obstacleY = 0;
        /*
        for (int i=0; i<5; i++) {
            if (!teamRobots.getRobot(i).equals(r)) {
                Robot obs = teamRobots.getRobot(i);
                double tempTheta = Math.atan2(r.getYPosition() - obs.getYPosition(), obs.getXPosition() - r.getXPosition());
                double tempDifference = tempTheta - Math.toRadians(r.getTheta());
                if (tempDifference > Math.PI) {
                    tempDifference -= (2 * Math.PI);
                } else if (tempDifference < -Math.PI) {
                    tempDifference += (2 * Math.PI);
                }
                tempDifference = Math.toDegrees(tempDifference);
                tempTheta = tempDifference;

                if (Math.abs(tempTheta) < 50) {
                    double tempDist = Math.sqrt(Math.pow((obs.getXPosition()-r.getXPosition()),2) + Math.pow((obs.getYPosition()-r.getYPosition()),2));
                    if (tempDist < obstacleDist) {
                        obstacleDist = tempDist;
                        obstacleTheta = tempTheta;
                        obstacleY = obs.getYPosition();
                        obstacleX = obs.getXPosition();
                    }
                }
            }
        }

        for (int i=0; i<5; i++) {
            Robot obs = opponentRobots.getRobot(i);
            double tempTheta = Math.atan2(r.getYPosition() - obs.getYPosition(), obs.getXPosition() - r.getXPosition());
            double tempDifference = tempTheta - Math.toRadians(r.getTheta());
            if (tempDifference > Math.PI) {
                tempDifference -= (2 * Math.PI);
            } else if (tempDifference < -Math.PI) {
                tempDifference += (2 * Math.PI);
            }
            tempDifference = Math.toDegrees(tempDifference);
            tempTheta = tempDifference;

            if (Math.abs(tempTheta) < 50) {
                double tempDist = Math.sqrt(Math.pow((obs.getXPosition()-r.getXPosition()),2) + Math.pow((obs.getYPosition()-r.getYPosition()),2));
                if (tempDist < obstacleDist) {
                    obstacleDist = tempDist;
                    obstacleTheta = tempTheta;
                    obstacleY = obs.getYPosition();
                    obstacleX = obs.getXPosition();
                }
            }
        } */

        if (ballX < r.getXPosition() && ballX > 35) {
            double tempTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
            double tempDifference = tempTheta - Math.toRadians(r.getTheta());
            if (tempDifference > Math.PI) {
                tempDifference -= (2 * Math.PI);
            } else if (tempDifference < -Math.PI) {
                tempDifference += (2 * Math.PI);
            }
            tempDifference = Math.toDegrees(tempDifference);
            tempTheta = tempDifference;

            if (Math.abs(tempTheta) < 50) {
                double tempDist = Math.sqrt(Math.pow((ballX - r.getXPosition()), 2) + Math.pow((ballY - r.getYPosition()), 2));
                if (tempDist < obstacleDist) {
                    obstacleDist = tempDist;
                    obstacleTheta = tempTheta;
                    obstacleY = ballY;
                    obstacleX = ballX;
                }
            }
        }

        double distBetweenObsAndTarget = Math.sqrt(Math.pow((x-obstacleX),2) + Math.pow((y-obstacleY),2));

        FunctionBlock fb = loadFuzzy("fuzzy/selfMadeObstacleV2.fcl");
        fb.setVariable("distBetweenObsAndTarget", distBetweenObsAndTarget);
        fb.setVariable("obstacleDist", obstacleDist);
        fb.setVariable("obstacleTheta", obstacleTheta);
        fb.setVariable("targetTheta", targetTheta);
        fb.setVariable("targetDist", Math.abs(targetDist));
        fb.setVariable("direction", r.getTheta());
        fb.setVariable("xPos", r.getXPosition());
        fb.setVariable("yPos", r.getYPosition());

        // Evaluate
        fb.evaluate();

        double linear  = fb.getVariable("linearVelocity").getValue();
        double angular = fb.getVariable("angularVelocity").getValue();
        r.linearVelocity = linear;
        r.angularVelocity = angular*-1;

        return;
    }

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(110 - r.getXPosition()) + squared(90 - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}
