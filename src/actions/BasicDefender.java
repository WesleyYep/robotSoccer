package actions;

import javax.swing.JOptionPane;

import Paths.Path;
import bot.Robot;
import data.Coordinate;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

import org.opencv.core.Point;

import ui.Field;
import utils.Geometry;

public class BasicDefender extends Defender {

    private double lastBallX = 0;
    private double lastBallY = 0;
    private double lastBallX2 = 0;
    private double lastBallY2 = 0;

    public BasicDefender () {
        super(new Point(50,70), new Point(50,110), null);
    }

    public BasicDefender(Point p1, Point p2, Path path) {
        super(p1, p2, path);

    }

    {
        parameters.put("point 1 x", 35);
        parameters.put("point 1 y", 70);
        parameters.put("point 2 x", 35);
        parameters.put("point 2 y", 110);
    }

    @Override
    public void execute() {
        Robot r = bot;
        setDefendZone(new Point( parameters.get("point 1 x"), parameters.get("point 1 y")),new Point( parameters.get("point 2 x"), parameters.get("point 2 y")));
        Point positionToBe = getPosition();

        double yDiff = Math.round(ballY-lastBallY);
        double xDiff = Math.round(ballX-lastBallX);
        double constant;

        boolean goingHorizontal = false;
        boolean goingVertical = false;
        double interceptY = 0;
        double interceptX = 0;
        Point p1 = defendZone.getFirst();
        Point p2 = defendZone.getSecond();
        //when horizontal ball line
        if (yDiff == 0) {
            goingHorizontal = true;
        }

        if (xDiff == 0) {
            goingVertical = true;  
        }
        if (goingVertical && goingHorizontal) {
        	//System.out.println("staying still");
        }
        
        if (!goingVertical && goingHorizontal) {
        	//System.out.println("horizontal line");
        	//for the defender line
        	//vertical defender line
            if (Math.abs(p1.x-p2.x) == 0)  {
            	interceptY = ballY;
            	interceptX = p1.x;
            }
            //horizontal defender line
            else if (Math.abs(p1.y-p2.y) == 0) {
            	interceptY = p1.y;
            	interceptX  = ballX;
            }
            //other defender line
            else {
            	double gradient = (p1.y-p2.y) /(p1.x-p2.x);
            	double yConst = p1.y - (gradient*p1.x);
            	
            	interceptY = ballY;
            	interceptX = (ballY-yConst) / gradient;
            }
        }
        
        if (goingVertical && !goingHorizontal) {

           // System.out.println("vertical line");

          	//for the defender line
        	//vertical defender line
            if (Math.abs(p1.x-p2.x) == 0)  {
            	interceptY = p1.y;
            	interceptX = ballX;
            }
            //horizontal defender line
            else if (Math.abs(p1.y-p2.y) == 0) {
            	interceptY = p1.x;
            	interceptX  = ballY;
            }
            //other defender line
            else {
            	double gradient = (p1.y-p2.y) /(p1.x-p2.x);
            	double yConst = p1.y - (gradient*p1.x);
            	
            	interceptY = (gradient*ballX) + yConst;
            	interceptX = ballX;
            }
        }

        if (!(goingVertical || goingHorizontal)) {
        	//System.out.println("diagonal line");
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

            
            //trajectoryY = (slope*(goalLine+3.75)) + yInt;

            //	trajectoryY = (slope*(goalLine-3.75)) + yInt;
          	//for the defender line
        	
            if (Math.abs(p1.x-p2.x) == 0)  {
            	interceptY = (slope*(p1.x-3.75)) + yInt;
            	interceptX = p1.x;
            }
            else if (Math.abs(p1.y-p2.y) == 0) {
            	interceptY = p1.y;
            	interceptX = (p1.y-yInt)/slope;
            }
            else {
            	double gradient = (p1.y-p2.y) /(p1.x-p2.x);
            	double yConst = p1.y - (gradient*p1.x);
            	
            	if (gradient != slope) {
            		interceptX = (yInt - yConst) / (gradient- slope);
            		interceptY = (gradient*interceptX) + yConst;
            	}
            }
            

        }
        //System.out.println(interceptY + " " + interceptX);

        //if (!(r.getXPosition() >= positionToBe.x-5 && r.getXPosition() <= positionToBe.x+5 && r.getYPosition() >= positionToBe.y-5
        //		&& r.getYPosition() <= positionToBe.y+5)) {
        //setVelocityToTarget(positionToBe.x, positionToBe.y, true,false);
        if ( ((interceptX <= p1.x & interceptX >= p2.x) || (interceptX >= p1.x & interceptX <= p2.x)) &&
        		((interceptY <= p1.y & interceptY >= p2.y) || (interceptY >= p1.y & interceptY <= p2.y))) {
        	setVelocityToTarget(interceptX, interceptY, true,true);
        	//System.out.println("here");
        }
        else {
        	setVelocityToTarget(positionToBe.x, positionToBe.y, true,false);
        }
        //} else {
        ///	System.out.println("reached");
        //}
        
        lastBallX = ballX;
		lastBallY = ballY;
		lastBallX2 = lastBallX;
		lastBallY2 = lastBallY;

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


        r.linearVelocity = linear*5;
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

    private void checkRobotPosition(double x, double y) {
        Robot r = bot;
        int xError = 10;
        if (r.getXPosition() >= x-xError && r.getXPosition() <= x+xError && r.getYPosition() >= y-10 && r.getYPosition() <= y+10) {
            r.angularVelocity = 0;
            r.linearVelocity = 0;
        }
    }

    private boolean isCloseToWall() {
        Robot r = bot;
        if (r.getYPosition() >= 0 && r.getYPosition() <= 10 ) {
            return true;
        }
        else if (r.getYPosition() >= Field.OUTER_BOUNDARY_HEIGHT-10 && r.getYPosition() <= Field.OUTER_BOUNDARY_HEIGHT) {
            return true;
        }
        else if (r.getXPosition() >= 0 && r.getXPosition() <= 10 ) {
            return true;
        }
        else if (r.getXPosition() >= Field.OUTER_BOUNDARY_WIDTH-10 && r.getXPosition() <= Field.OUTER_BOUNDARY_WIDTH) {
            return true;
        }

        return false;
    }

}