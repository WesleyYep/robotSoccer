package actions;

import bot.Robot;
import data.Coordinate;
import game.Tick;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import org.opencv.core.Point;
import strategy.GameState;
import ui.Field;
import utils.Geometry;


/**
 * Created by Wesley on 27/02/2015.
 */
public class WaitingStrikerDefender extends Defender {
    private int targetX = 0;
    private int targetY = 0;
    private double oldDistanceToTarget = 0;
    private int countTimesThatSeemStuck = 0;
    private double lastBallX = 0;
    private double lastBallY = 0;
    private double lastBallX2 = 0;
    private double lastBallY2 = 0;
    private double error = 2.5;
    private boolean fixPosition = false;


    public WaitingStrikerDefender () {
        super(new Point(50,70), new Point(50,110), null);
    }

    //non-static initialiser block
    {
        if(!(parameters.containsKey("point 1 x") && parameters.containsKey("point 1 y"))) {
            //don't bother if these already exist
            parameters.put("goalLine", 7);
            parameters.put("topPoint", 70);
            parameters.put("bottomPoint", 110);
        }
    }

    @Override
    public void execute() {
        Robot r = bot;
        double goalLine =  parameters.get("goalLine");

        if (r.getXPosition() > ballX) {
            if (r.getXPosition() < goalLine) {
                r.linearVelocity = 0;
                r.angularVelocity = 0;
                return;
            }
            Coordinate c = new Coordinate(goalLine, 90);
            MoveToSpot.move(r, c, 1, true);
            return;
        }

        //this is number 1 priority
        if (ballComingIntoPath(r)) {
            GameState.getInstance().addToWhatsGoingOn("waitingStrikerKicking");
            return;
        }else {
            GameState.getInstance().removeFromWhatsGoingOn("waitingStrikerKicking");
        }

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

        //defend
        //double error = parameters.get("error");
        int topPoint = parameters.get("topPoint");
        int bottomPoint = parameters.get("bottomPoint");
        //double goalLine = 213;
        //System.out.println(goalLine);

//        double targetTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
//        double difference = targetTheta - Math.toRadians(r.getTheta());
//        //some hack to make the difference -Pi < theta < Pi
//        if (difference > Math.PI) {
//            difference -= (2 * Math.PI);
//        } else if (difference < -Math.PI) {
//            difference += (2 * Math.PI);
//        }
//        difference = Math.toDegrees(difference);
//        targetTheta = difference;

//        //clear the ball
//        if (ballX <= goalLine + 5 && ballX > goalLine - 5) {
//            if (ballY > r.getYPosition() && Math.abs(r.getXPosition() - goalLine) < 5 &&(Math.abs(targetTheta) < 5 || Math.abs(targetTheta) > 175 )) {
//                MoveToSpot.move(r, new Coordinate((int)goalLine, 175), 1.5);
//                return;
//            } else if (ballY < r.getYPosition()) {
//                MoveToSpot.move(r, new Coordinate((int) goalLine, 5), 1.5);
//                return;
//            }
//        }

        //first phase getting the robot to the goal line
        if (r.getXPosition() < goalLine-error || r.getXPosition() >  goalLine+error) {
            int targetPos = 0;
            if (ballY >= topPoint && ballY <= bottomPoint ) {
                targetPos = (int) ballY;
            }
            else if (ballY < topPoint) {
                targetPos = topPoint;
            }
            else if (ballY > bottomPoint) {
                targetPos = bottomPoint;
            }
            setVelocityToTarget(goalLine,targetPos, false,false);
            fixPosition = true;
        }
        //correct it's position
        else if (fixPosition) {
            if ( ( r.getTheta() > 90+5 && r.getTheta() <= 180)|| (r.getTheta() >= 0 && r.getTheta() < 90-5)) {
                TurnTo.turn(r, new Coordinate(r.getXPosition(), 0), 3);
                return;
            }
            else if ( (r.getTheta() < 0-5 && r.getTheta() >= -90+5) || (r.getTheta() < -90-5 && r.getTheta() >= -180)) {
                TurnTo.turn(r, new Coordinate(r.getXPosition(), 180), 3);
                return;
            }
            else {
                fixPosition = false;
            }
        }
        //ball tracking
        else{

            //working out the trajectory of the ball
            double yDiff = Math.round(ballY-lastBallY);
            double xDiff = Math.round(ballX-lastBallX);
            double constant;

            boolean goingHorizontal = false;
            boolean goingVertical = false;
            double trajectoryY = 0;

            if (yDiff == 0) {
                goingHorizontal = true;
            }

            if (xDiff == 0) {
                goingVertical = true;
            }

            if (!(goingVertical || goingHorizontal)) {
                constant = ballY - ((yDiff/xDiff)*ballX);
                //trajectoryY = ((yDiff/xDiff)*goalLine) + constant;

                double sumY = ballY + lastBallY + lastBallY2;
                double sumX = ballX + lastBallX + lastBallX2;

                double sumY2 = (ballY*ballY) + (lastBallY*lastBallY) + (lastBallY2*lastBallY2);
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

            if (goingVertical || goingHorizontal) {
                if (ballY >= topPoint && ballY <= bottomPoint && r.getXPosition() < ballX) {
                    setVelocityToTarget(goalLine,ballY, true,true);
                } else if (ballY < topPoint) {
                    setVelocityToTarget(goalLine,topPoint,true,true);
                } else if (ballY > bottomPoint) {
                    setVelocityToTarget(goalLine,bottomPoint,true,true);
                }
            } else if (!(goingVertical || goingHorizontal)) {

                boolean direction;
                if (goalLine < 110) {
                    direction = xDiff < 0;
                } else {
                    direction = xDiff > 0;
                }

                if (direction) {
                    //ball going toward the goal
                    if (trajectoryY >= topPoint && trajectoryY <=bottomPoint) {
                        if (r.getYPosition()>= (trajectoryY-2) && r.getYPosition() <=(trajectoryY+2)) {
                            setVelocityToTarget(goalLine,r.getYPosition(),true,true);
                        } else {
                            setVelocityToTarget(goalLine,trajectoryY,true,true);
                        }
                    } else {
                        //ball travelling in the same side of board
                        if ( (trajectoryY> bottomPoint && ballY > bottomPoint) || (trajectoryY<= bottomPoint && ballY <= bottomPoint)) {
                            // System.out.println("same side");
                            if (ballY >= topPoint && ballY <= bottomPoint && r.getXPosition() < ballX) {
                                setVelocityToTarget(goalLine,ballY, true,true);
                            } else if (ballY < topPoint) {
                                setVelocityToTarget(goalLine,topPoint,true,true);
                            } else if (ballY > bottomPoint) {
                                setVelocityToTarget(goalLine,bottomPoint,true,true);
                            }
                        } else {
                            // System.out.println("oppo side");
                            setVelocityToTarget(goalLine,Field.OUTER_BOUNDARY_HEIGHT/2,true,true);
                        }
                    }
                } else {
                    if (ballY >= topPoint && ballY <= bottomPoint && r.getXPosition() < ballX) {
                        setVelocityToTarget(goalLine,ballY, true,true);
                    } else if (ballY < topPoint) {
                        setVelocityToTarget(goalLine,topPoint,true,true);
                    } else if (ballY > bottomPoint) {
                        setVelocityToTarget(goalLine,bottomPoint,true,true);
                    }
                }

            } else {
                setVelocityToTarget(r.getXPosition(),r.getYPosition(),true,true);
            }


        }
        lastBallX = ballX;
        lastBallY = ballY;
        lastBallX2 = lastBallX;
        lastBallY2 = lastBallY;
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

    private boolean ballComingIntoPath(Robot r) {
//return true if ball is directly in front (or behind) of robot
        double angleToBall = Math.abs(getTargetTheta(r, ballX, ballY));
        double angleToGoal = Math.abs(getTargetTheta(r, 220, 90));
        boolean isFacingGoal = angleToGoal < 10 || Math.abs(getTargetTheta(r, 220, 90)) > 170;

        if (isFacingGoal) {
            if (angleToBall < 10) {
                r.linearVelocity = 3;
                r.angularVelocity = 0;
                return true;
            } else if (r.getXPosition() < ballX && angleToBall > 170) {
                r.linearVelocity = -3;
                r.angularVelocity = 0;
                return true;
            }
        }

        //get an equation in the form y = mx + c of the path of ball
        double m = (predY-ballY) / (predX - ballX);
        double c = ballY - (m * ballX);
        //get an equation of the line for robot angle
        double mR = 0 - Math.tan(Math.toRadians(angleToGoal));
        double cR = r.getYPosition() - (mR * r.getXPosition());

        //check if line crosses the line y = mRx + cR (old was y = robotY) and 0 < x < r.X]
        // mx + c = mRx + cR
        // mx - mRx = cR - c
        //x(m - mR) = cR - c
        //x = (cR - c) / (m - mR)
        //use this to get coordinates of intersection point
        double xInt = (cR - c) / (m - mR);
        double yInt = m * xInt + c;
        //double x = (r.getYPosition() - c) / m;
        if (xInt < 220 && xInt > r.getXPosition()) {
            //find distance of intersection point from current ball position
            double ballDistance = Math.sqrt(squared(ballX-xInt) + squared(ballY-yInt));
            //find speed of ball
            double ballSpeed = (Math.sqrt(squared(predX-ballX) + squared(predY-ballY))) / Tick.PREDICT_TIME;
            //find time taken for ball to reach intersection point
            double time = ballDistance / ballSpeed;
            //only go if the time is under 3 seconds
            if (time < 1.5) {
                //get distance of robot to spot
                if (!isFacingGoal) {
                    TurnTo.turn(r, new Coordinate(220, 90), 3);
                    return true;
                }
                double robotDistance = Math.sqrt(squared(r.getXPosition()-xInt) + squared(r.getYPosition()-yInt));
                r.linearVelocity = squared((robotDistance/time)/100);
                r.angularVelocity = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    protected Point getPosition() {
        Point p1 = defendZone.getFirst();
        //Point p1 = new Point( parameters.get("point 1 x"), parameters.get("point 1 y"));
        //Point p2 = new Point( parameters.get("point 2 x"), parameters.get("point 2 y"));
        Point p2 = defendZone.getSecond();
        Point p3 = new Point(ballX, ballY);

        double[] angles = Geometry.anglesInTriangle(p1, p2, p3);

        if (angles[0] > Math.PI / 2) {
            return p1;
        } else if (angles[1] > Math.PI / 2) {
            return p2;
        } else {
            // either p1p3p2 is > 90 or all angles less than 90.

            // using either p1 or p2, in this case p1. find the adjacent side length using cosine.
            double distance = Math.cos(angles[0]) * Geometry.euclideanDistance(p1, p3);

            // Find the point that is x distance from point 1 along the vector.
            // TODO needs better way.
            double[] vector = new double[2];
            vector[0] = p2.x - p1.x;
            vector[1] = p2.y - p1.y;

            double magnitude = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));

            double[] normalisedVector = new double[2];
            normalisedVector[0] = vector[0] / magnitude;
            normalisedVector[1] = vector[1] / magnitude;

            return new Point(p1.x + distance * normalisedVector[0], p1.y + distance * normalisedVector[1]);

        }
    }


    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
        Robot r = bot;
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

        //clear the ball
        double ballTargetTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
        double ballDifference = ballTargetTheta - Math.toRadians(r.getTheta());
//       System.out.println("initial targetTheta: " + targetTheta + " initial difference " + difference + " current Theta "
        //     		+ Math.toRadians(r.getTheta()));
        //some hack to make the difference -Pi < theta < Pi
        if (ballDifference > Math.PI) {
            ballDifference -= (2 * Math.PI);
        } else if (ballDifference < -Math.PI) {
            ballDifference += (2 * Math.PI);
        }
        ballDifference = Math.toDegrees(ballDifference);
        ballTargetTheta = ballDifference;
        /*
        if ((Math.abs(ballTargetTheta) < 5 && Math.abs(r.getTheta()) < 90) || (Math.abs(ballTargetTheta) > 175 && Math.abs(r.getTheta()) > 90)) {
            MoveToSpot.move(r, new Coordinate((int)ballX, (int)ballY), 1.5);
            return;
        } */

        boolean isFacingTop = true;
        boolean isTargetTop = true;
        boolean front  = true;
        /*
        if (r.getTheta() < 0) {
            isFacingTop = false;
        }

        if (y > r.getYPosition()) {
            isTargetTop = false;
        }

        if (isTargetTop != isFacingTop) {
            front = false;
        } */

        if (targetTheta > 90 || targetTheta < -90) {
            front = false;
        }

        if (!front && reverse) {
            if (targetTheta < 0) {
                targetTheta = -180 - targetTheta;
            }
            else if (targetTheta > 0) {
                targetTheta = 180 - targetTheta;
            }
        }

        String filename = "newFuzzy.fcl";
        FIS fis = FIS.load(filename, true);

        if (fis == null) {
            System.err.println("Can't load file: '" + filename + "'");
            System.exit(1);
        }

        // Get default function block
        FunctionBlock fb = fis.getFunctionBlock(null);

             /*
             if (onGoalLine) {
            	targetTheta = 0;
             }
             */
        //if (targetDist <= 3.75) targetDist = 0;
        if (targetDist <=3.75) {
            targetDist = 0;
            targetTheta = 0;
        }
        // targetTheta = Math.round(targetTheta/5)*5;

        fb.setVariable("angleError", targetTheta);
        fb.setVariable("distanceError", Math.abs(targetDist));
        //      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " "
        //      		+ r.getYPosition() + " targetDist " + targetDist);
        // Evaluate
        fb.evaluate();
             /*
             JFuzzyChart.get().chart(fb);
              JOptionPane.showMessageDialog(null, "nwa"); */

        // Show output variable's chart
        fb.getVariable("rightWheelVelocity").defuzzify();
        fb.getVariable("leftWheelVelocity").defuzzify();
        //  JFuzzyChart.get().chart(fb.getVariable("leftWheelVelocity"), fb.getVariable("leftWheelVelocity").getDefuzzifier(), true);
        //   JFuzzyChart.get().chart(fb.getVariable("rightWheelVelocity"), fb.getVariable("rightWheelVelocity").getDefuzzifier(), true);
        double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
        double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());
        //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());
        double linear =  (right+left)/2;
        double angular = (right-left)*(2/0.135);
        //    System.out.println("right :" + right + "left " + left);


        r.linearVelocity = linear*2.5;
        r.angularVelocity = angular*1;

        if (!front &&reverse) {
            r.linearVelocity *= -1;
            r.angularVelocity *= -1;
        }
        //      System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta
        //  		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);
        // 	System.out.println("x:" + x + " y: " + y + " r.x: " + r.getXPosition() + " r.y" + r.getYPosition());
        //     System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
        //r.linearVelocity = 0;
//            r.angularVelocity = 0;
//

        // }
    }

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}
