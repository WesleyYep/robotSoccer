package actions;

import bot.Robot;
import data.Coordinate;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import strategy.GameState;

/**
 * Uses fuzzy for chase ball action
 */
public class DefenderStriker extends Action {

    private boolean isCharging = false;
    private boolean front = true;
    private double kp = 3;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards
    private int count = 0;
    private double lastYGoal = 0;
    private boolean isPreviousDirectionForward = false;
    {
        parameters.put("goalLine", 110);
        parameters.put("topPoint", 60);
        parameters.put("bottomPoint", 120);
    }

    @Override
    public void execute() {
        if (count == 0 && ballInLineWithGoal()) {
            count = 50;
        }

        if (count > 0) {
//            double targetX = ballX;
//            double targetY = ballY;
//
//            boolean isCurrentDirectionForward;
//            //get angle to ball
//            double actualAngleError;
//            double distanceToTarget = getDistanceToTarget(bot, targetX, targetY);
//            double distanceToBall = getDistanceToTarget(bot, ballX, ballY);
//            double angleToTarget = getTargetTheta(bot, targetX, targetY); //degrees
//            double angleToBall = getTargetTheta(bot, ballX, ballY); //degrees
//
//            if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
//                if (angleToTarget < 0) {
//                    actualAngleError = Math.toRadians(-180 - angleToTarget);
//                } else {
//                    actualAngleError = Math.toRadians(180 - angleToTarget);
//                }
//                bot.angularVelocity = actualAngleError * 5 * -1;
//                bot.linearVelocity = 8/10.0 * -1;
//                isCurrentDirectionForward = false;
//            } else {
//                actualAngleError =  Math.toRadians(angleToTarget);
//                bot.angularVelocity = actualAngleError * 5;
//                bot.linearVelocity = 8/10.0;
//                isCurrentDirectionForward = true;
//            }
//            isPreviousDirectionForward = isCurrentDirectionForward;
//            System.out.println("angular " + bot.angularVelocity);
//            System.out.println("linear " + bot.linearVelocity);
            setVelocityToTarget(ballX, ballY, true, false);
        } else {
            moveToDefencePosition();
        }

        if (count > 0) {
            count--;
        }
    }

    private boolean ballInLineWithGoal() {
        //draw a line between expected robot position and ball
        double robotX = parameters.get("goalLine");
        double robotY = ballY;

        double m = (ballY - robotY) / (ballX - robotX);
        double c = ballY - (m * ballX);

        double y = m * 220 + c;

//        if (y > 70 && y < 110 && ballX > robotX) {
//            lastYGoal = y;
//            return true;
//        }

        double differenceFromLast = y - lastYGoal;
        double difference = y + differenceFromLast * 10;

        if (difference < 100 && difference > 80 && ballX > parameters.get("goalLine")) {
      //      System.out.println("difference: " + difference);
            lastYGoal = y;
            return true;
        }
      //  System.out.println("failed difference: " + difference);

        lastYGoal = y;
        return false;
    }

    private void moveToDefencePosition() {
        double targetX = parameters.get("goalLine");
        double targetY = getYPositionForGoalKeeper();

        if (ballX < parameters.get("goalLine")) {
            targetY = 90;
        }

        double dist = getDistanceToTarget(bot, targetX, targetY);

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        double actualAngleError;

        double speed = 0.5;
        if (Math.abs(bot.getTheta()) < 95 && Math.abs(bot.getTheta()) > 85) {
            //    System.out.println("on goal line");
            speed = 0.8;
        }

        if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
            bot.angularVelocity = actualAngleError * kp * -1;
            bot.linearVelocity = Math.abs(angleToTarget) > 160 ? speed * -1 : 0;
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = Math.abs(angleToTarget) < 20 ? speed : 0;
        }

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 20 && Math.abs(bot.getTheta()) > 10) {
            //  }else if (dist < 10 && Math.abs(bot.getTheta()) > 10) {
            //bot.linearVelocity *= dist/20.0;
            if (bot.linearVelocity > 0) {
                bot.linearVelocity = dist > 15 ? 0.4 : dist > 10 ? 0.3 : dist > 5 ? 0.2 : 0.1;
            } else if (bot.linearVelocity < 0) {
                bot.linearVelocity = dist > 15 ? -0.4 : dist > 10 ? -0.3 : dist > 5 ? -0.2 : -0.1;
            }
        }

        double angleToBall = getTargetTheta(bot, ballX, ballY); //degrees
        //clear the ball
        int goalLine = parameters.get("goalLine");
        if (ballX <= goalLine + 5 && ballX > goalLine - 5) {
            if (ballY > bot.getYPosition() && ballY - bot.getYPosition() < 35 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToBall) < 5 || Math.abs(angleToBall) > 175 )) {
                MoveToSpot.move(bot, new Coordinate(goalLine, 175), 1, false);
                return;
            } else {
                if (ballY < bot.getYPosition() && bot.getYPosition() - ballY < 35 && Math.abs(bot.getXPosition() - goalLine) < 5 &&(Math.abs(angleToBall) < 5 || Math.abs(angleToBall) > 175 )) {
                    MoveToSpot.move(bot, new Coordinate(goalLine, 5), 1, false);
                    return;
                }
            }
        }
    }

    private double getYPositionForGoalKeeper() {
        //just use ballY
        double minY = parameters.get("topPoint");
        double maxY = parameters.get("bottomPoint");

        return ballY < maxY && ballY > minY ? ballY : ballY > maxY ? maxY : minY;
    }

    private void turn() {
        double targetX = 220;
        double targetY = 90;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }

    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
        Robot r = bot;

        if (GameState.getInstance().isGoingOn("waitingStrikerKicking")) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
            return;
        }

//
//        //see if robot is not in positive situation
//        if (ballX < r.getXPosition()) {
//            int yPos;
//            if (ballY > 90) {
//                yPos = (int)(60*Math.random()) + 120;
//            } else {
//                yPos = (int)(60*Math.random());
//            }
//            oldDistanceToTarget = newTargetDistance;
//            MoveToSpot.move(r, new Coordinate(30, yPos), 1);
//            return;
//        }
//
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
//
//        //dribble ball towards goal if we are in a dribbling position
//        double ballDist = Math.sqrt(Math.pow((ballX-r.getXPosition()),2) + Math.pow((ballY-r.getYPosition()),2));
//        double angleToGoal = angleDifferenceFromGoal(r.getXPosition(), r.getYPosition(), r.getTheta());
//        if (ballDist <= 7) {
//            if (angleToGoal > Math.PI / 18) {
//                // System.out.println("fast_right");
//                r.angularVelocity = 12;
//                r.linearVelocity = 0.7;
//            } else if (angleToGoal < -(Math.PI / 18)) {
//                //  System.out.println("fast_left");
//                r.linearVelocity = 0.7;
//                r.angularVelocity = -12;
//            } else {
//                r.linearVelocity = 2;
//            }
//            return;
//        }
//        //charge ball if we are in a kicking position
//        if (Math.abs(targetTheta) < 20 && targetDist < 50) {//degrees
//            r.linearVelocity = 2;
//            r.angularVelocity = 0;
//            return;
//        }
//

    	if (targetTheta > 90 || targetTheta < -90) {
			front = false;
		} else {
            front = true;
        }

        //reverse if stuck
        if (bot.isStuck(new Coordinate(bot.getXPosition(), bot.getYPosition()))) {
            if (front) {
                front = false;
            } else {
                front = true;
            }
        }

        //System.out.println("front is " + front);
        if (!front && reverse) {
			if (targetTheta < 0) {
				targetTheta = -180 - targetTheta;
			} else if (targetTheta > 0) {
				targetTheta = 180 - targetTheta;
			}
		}

        FunctionBlock fb = loadFuzzy("fuzzy/selfMade.fcl");

        fb.setVariable("targetTheta", targetTheta);
        fb.setVariable("targetDist", Math.abs(targetDist));
        fb.setVariable("direction", r.getTheta());
        fb.setVariable("xPos", r.getXPosition());
        fb.setVariable("yPos", r.getYPosition());
        //      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " "
        //      		+ r.getYPosition() + " targetDist " + targetDist);
        // Evaluate
        fb.evaluate();

        //     JFuzzyChart.get().chart(fb);


        // Show output variable's chart
        fb.getVariable("linearVelocity").defuzzify();
        fb.getVariable("angularVelocity").defuzzify();
        //    JFuzzyChart.get().chart(fb.getVariable("linearVelocity"), fb.getVariable("linearVelocity").getDefuzzifier(), true);
        //     JFuzzyChart.get().chart(fb.getVariable("angularVelocity"), fb.getVariable("angularVelocity").getDefuzzifier(), true);
        //    JOptionPane.showMessageDialog(null, "nwa");
        double linear  = fb.getVariable("linearVelocity").getValue();
        double angular = fb.getVariable("angularVelocity").getValue();
        //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());

        //    System.out.println("right :" + right + "left " + left);

        r.linearVelocity = linear;
        r.angularVelocity = angular*-1;

//            System.out.println("linear: " + linear + " angular:" + angular*-1
//            					+ " x: " + r.getXPosition() + " y: " + r.getYPosition()
//            					+ " r theta: " + r.getTheta() + " t theta: " + targetTheta
//            					+ " t dist" + targetDist + " time: " + System.currentTimeMillis());

        if (!front &&reverse) {
            r.linearVelocity *= -1;
            r.angularVelocity *= -1;
        }
        if (targetDist <=2.5) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        }

       // oldDistanceToTarget = newTargetDistance;

//          System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta
//        		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);

        //    System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
//             r.linearVelocity = 0;
//            r.angularVelocity = 0;
//

        // }

        double targetX = ballX;
        double targetY = ballY;

        double actualAngleError;
        double distanceToTarget = getDistanceToTarget(bot, targetX, targetY);
        double angleToTarget = getTargetTheta(bot, targetX, targetY); //degrees

        if ((Math.abs(angleToTarget) > 90)) {
            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
        }


        //charge ball into goal
        double range = 10;
        if (isCharging) {
            range = 30;
        }
        if (distanceToTarget < range && Math.abs(actualAngleError) < Math.PI/10 /* radians*/) {
            bot.linearVelocity = front ? 1 : -1;
            if (targetX > 110) {
                double angleToGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees
             //   System.out.println("front is " + front + "     abs(angleTogoal) is " + Math.abs(angleToGoal));
                if ((front && Math.abs(angleToGoal) > 45) || (!front && Math.abs(angleToGoal)  < 135)) {
                    if (angleToGoal > 0) {
                        bot.angularVelocity = front ? 30 : -30;
                    } else {
                        bot.angularVelocity = front ? -30 : 30;
                    }
                }
            }
            isCharging = true;
        } else {
            isCharging = false;
        }

      return;
    }

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(110 - r.getXPosition()) + squared(90 - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}