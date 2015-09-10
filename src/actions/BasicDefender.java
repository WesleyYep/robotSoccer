package actions;

import Paths.Path;
import bot.Robot;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import org.opencv.core.Point;
import ui.Ball;
import ui.Field;
import utils.Geometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;

public class BasicDefender extends Defender {

    private double lastBallX = 0;
    private double lastBallY = 0;
    private double lastBallX2 = 0;
    private double lastBallY2 = 0;

    public BasicDefender () {
        super(new Point(50,70), new Point(50,110));
    }

    public BasicDefender(Point p1, Point p2) {
        super(p1, p2);
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
        setDefendZone(new Point( 30, 50),new Point( 100, 50));
        Point positionToBe = getPosition();

        double yDiff = Math.round(ball.getYPosition()-lastBallY);
        double xDiff = Math.round(ball.getXPosition()-lastBallX);
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
            	interceptY = ball.getYPosition();
            	interceptX = p1.x;
            }
            //horizontal defender line
            else if (Math.abs(p1.y-p2.y) == 0) {
            	interceptY = p1.y;
            	interceptX  = ball.getXPosition();
            }
            //other defender line
            else {
            	double gradient = (p1.y-p2.y) /(p1.x-p2.x);
            	double yConst = p1.y - (gradient*p1.x);

            	interceptY = ball.getYPosition();
            	interceptX = (ball.getYPosition()-yConst) / gradient;
            }
        }

        if (goingVertical && !goingHorizontal) {

           // System.out.println("vertical line");

          	//for the defender line
        	//vertical defender line
            if (Math.abs(p1.x-p2.x) == 0)  {
            	interceptY = p1.y;
            	interceptX = ball.getXPosition();
            }
            //horizontal defender line
            else if (Math.abs(p1.y-p2.y) == 0) {
            	interceptY = p1.x;
            	interceptX  = ball.getYPosition();
            }
            //other defender line
            else {
            	double gradient = (p1.y-p2.y) /(p1.x-p2.x);
            	double yConst = p1.y - (gradient*p1.x);

            	interceptY = (gradient*ball.getXPosition()) + yConst;
            	interceptX = ball.getXPosition();
            }
        }

        if (!(goingVertical || goingHorizontal)) {
        	//System.out.println("diagonal line");
            constant = ball.getYPosition() - ((yDiff/xDiff)*ball.getXPosition());
            //trajectoryY = ((yDiff/xDiff)*goalLine) + constant;

            double sumY = ball.getYPosition() + lastBallY + lastBallY2;
            double sumX = ball.getXPosition() + lastBallX + lastBallX2;

            double sumY2 = (ball.getYPosition()*ball.getYPosition()) + (lastBallY*lastBallY) + (lastBallY2*lastBallY2);
            double sumX2 = (ball.getXPosition()*ball.getXPosition()) + (lastBallX*lastBallX) + (lastBallX2*lastBallX2);

            double sumXY = (ball.getXPosition()*ball.getYPosition()) + (lastBallX*lastBallY) + (lastBallY2*lastBallX2);

            double xMean = sumX/3;
            double yMean = sumY/3;

            double slope = (sumXY - sumX * yMean) / (sumX2 - sumX * xMean);

            double yInt = yMean - slope* xMean;

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

        if ( ((interceptX <= p1.x & interceptX >= p2.x) || (interceptX >= p1.x & interceptX <= p2.x)) &&
        		((interceptY <= p1.y & interceptY >= p2.y) || (interceptY >= p1.y & interceptY <= p2.y))) {
        	setVelocityToTarget(interceptX, interceptY, true,true);
        	//System.out.println("here");
        }
        else {
        	setVelocityToTarget(positionToBe.x, positionToBe.y, true,false);
        }


        lastBallX = ball.getXPosition();
        lastBallY = ball.getYPosition();
		lastBallX2 = lastBallX;
		lastBallY2 = lastBallY;
    }

    @Override
    protected Point getPosition() {
        //Point p1 = defendZone.getFirst();
        Point p1 = new Point( parameters.get("point 1 x"), parameters.get("point 1 y"));
        Point p2 = new Point( parameters.get("point 2 x"), parameters.get("point 2 y"));
        //Point p2 = defendZone.getSecond();
        Point p3 = new Point(ball.getXPosition(), ball.getYPosition());

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
        double ballTargetTheta = Math.atan2(r.getYPosition() - ball.getYPosition(), ball.getXPosition() - r.getXPosition());
        double ballDifference = ballTargetTheta - Math.toRadians(r.getTheta());

        if (ballDifference > Math.PI) {
            ballDifference -= (2 * Math.PI);
        } else if (ballDifference < -Math.PI) {
            ballDifference += (2 * Math.PI);
        }
        ballDifference = Math.toDegrees(ballDifference);
        ballTargetTheta = ballDifference;

        boolean isFacingTop = true;
        boolean isTargetTop = true;
        boolean front  = true;
        
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

        FunctionBlock fb = loadFuzzy("fuzzy/goalKeeper.fcl");
        fb.setVariable("targetTheta", targetTheta);
        fb.setVariable("targetDist", targetDist);
        fb.evaluate();
        fb.getVariable("linearVelocity").defuzzify();
        fb.getVariable("angularVelocity").defuzzify();
        r.linearVelocity = fb.getVariable("linearVelocity").getValue();
        r.angularVelocity = fb.getVariable("angularVelocity").getValue();

        if (targetDist <= 3.75) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        }

        if (!front &&reverse) {
            r.linearVelocity *= -1;
            r.angularVelocity *= -1;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D)g.create();

        g2.setBackground(Color.RED);

        // Convert parameter values to correct values to be shown on field.
        int x1 = Field.fieldXValueToGUIValue(parameters.get("point 1 x"));
        int y1 = Field.fieldYValueToGUIValue(parameters.get("point 1 y"));

        int x2 = Field.fieldXValueToGUIValue(parameters.get("point 2 x"));
        int y2 = Field.fieldYValueToGUIValue(parameters.get("point 2 y"));

        // Draw string and how to change it
        g2.drawString("Left Click", x1, y1);
        g2.drawString("Right Click", x2, y2);
        g2.drawLine(x1, y1, x2, y2);

        g2.dispose();
    }

    @Override
    public void react(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Transform to actual coordinates for the field
        x = Field.GUIXValueToFieldValue(x);
        y = Field.GUIYValueToFieldValue(y);

        if (SwingUtilities.isLeftMouseButton(e)) {
            parameters.put("point 1 x", x);
            parameters.put("point 1 y", y);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            parameters.put("point 2 x", x);
            parameters.put("point 2 y", y);
        }
    }
}