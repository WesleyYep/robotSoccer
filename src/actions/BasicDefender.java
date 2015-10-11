package actions;

import Paths.Path;
import bot.Robot;
import controllers.PIDController;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import org.opencv.core.Point;
import ui.Field;
import utils.Geometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;

public class BasicDefender extends Defender {

    private PIDController linearPID = new PIDController(1, 1, 1);
    private PIDController angularPID = new PIDController(1, 0, 0);

    private double previousDefX;
    private double previousDefY;

    private double deadZone;

    public BasicDefender () {
        this(new Point(50, 70), new Point(50, 110), null);
    }

    public BasicDefender(Point p1, Point p2, Path path) {
        super(p1, p2, path);

        // set upper and lower bounds
        linearPID.setMaximumOutput(0.65);
        linearPID.setMinimumOutput(0.65);
        linearPID.setClip(true);

        angularPID.setMinimumOutput(Math.toRadians(-120));
        angularPID.setMaximumOutput(Math.toRadians(120));

        // allowable error margin
        deadZone = 0.05;
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
        setDefendZone(new Point(parameters.get("point 1 x"), parameters.get("point 1 y")), new Point(parameters.get("point 2 x"), parameters.get("point 2 y")));
        Point positionToBe = getPosition();

        // information values
        // distance to target in metres
        double distanceToTarget = getDistanceToTarget(bot, positionToBe.x, positionToBe.y) / 100;
        // angle to target in degrees
        double angleToTarget = getTargetTheta(bot, positionToBe.x, positionToBe.y);

        // difference between previous
        //double difference = Math.abs(Math.abs(previousDefY) - Math.abs(positionToBe.y) + Math.abs(previousDefX) - Math.abs(positionToBe.y));

        // Check if distance to target is small
        if (distanceToTarget < deadZone) {
            bot.linearVelocity = 0;

            // set angle to robot

            Point p1 = getDefendZone().getFirst();
            Point p2 = getDefendZone().getSecond();

            double distanceToP1 = getDistanceToTarget(bot, p1.x, p1.y) / 100;
            double distanceToP2 = getDistanceToTarget(bot, p2.x, p2.y) / 100;

            if (distanceToP1 > distanceToP2) {
                angleToTarget = getTargetTheta(bot, p1.x, p1.y);
            } else {
                angleToTarget = getTargetTheta(bot, p2.x, p2.y);
            }

            double correctAngle = 0;

            if (angleToTarget > 90) {
                // quadrant a

                correctAngle = 180 - angleToTarget;
                // ball is behind
            } else if (angleToTarget < 0 && angleToTarget > -90) {
                // quadrant d
                correctAngle = Math.abs(angleToTarget);
            } else if (angleToTarget > 0 && angleToTarget < 90) {
                // quadrant b
                correctAngle = angleToTarget * -1;
            } else {
                correctAngle = -180 - angleToTarget;
            }

            angularPID.setResult(Math.toRadians(correctAngle));
            angularPID.setTotalError(0);
            angularPID.setInput(Math.toRadians(correctAngle));
            double angleResult = angularPID.performPID();
            bot.angularVelocity = angleResult;
            System.out.println(bot.angularVelocity);
            System.out.println("Angle to target: " + angleToTarget);
            return;
        }

        // update goal
        linearPID.setResult(distanceToTarget);
        linearPID.setTotalError(0);

        // find linear velocity to target
        linearPID.setInput(distanceToTarget);
        double linResult = linearPID.performPID();

        double correctAngle = 0;

        //  a                   | b
        //                      |
        // 180                  |                       0
        // ----------------------------------------------
        // -180                 |                       0
        //                      |
        //  c                   | d

        if (angleToTarget > 90) {
            // quadrant a

            correctAngle = 180 - angleToTarget;
            // ball is behind
            bot.linearVelocity = -1 * linResult;
        } else if (angleToTarget < 0 && angleToTarget > -90) {
            // quadrant d
            correctAngle = Math.abs(angleToTarget);
            bot.linearVelocity = linResult;
        } else if (angleToTarget > 0 && angleToTarget < 90) {
            // quadrant b
            correctAngle = angleToTarget * -1;
            bot.linearVelocity = linResult;
        } else {
            bot.linearVelocity = -1 * linResult;
            correctAngle = -180 - angleToTarget;
        }

        angularPID.setResult(Math.toRadians(correctAngle));
        angularPID.setTotalError(0);
        angularPID.setInput(Math.toRadians(correctAngle));
        double angleResult = angularPID.performPID();

        bot.angularVelocity = angleResult;

        // override linear velocity if ball distance is close and is turning
        if (Math.abs(bot.angularVelocity) > 0.5 && distanceToTarget < 0.3) {
            bot.linearVelocity = 0.1;
        } else if (distanceToTarget < 0.5) {
            bot.linearVelocity *= 0.5;
        }

        // update previous details
        previousDefX = positionToBe.x;
        previousDefY = positionToBe.y;
    }

    @Override
    protected Point getPosition() {
        Point p1 = defendZone.getFirst();
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


//    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
//        Robot r = bot;
//        double targetDist;
//
//        double targetTheta = Math.atan2(r.getYPosition() - y, x - r.getXPosition());
//        double difference = targetTheta - Math.toRadians(r.getTheta());
////       System.out.println("initial targetTheta: " + targetTheta + " initial difference " + difference + " current Theta "
//        //     		+ Math.toRadians(r.getTheta()));
//        //some hack to make the difference -Pi < theta < Pi
//        if (difference > Math.PI) {
//            difference -= (2 * Math.PI);
//        } else if (difference < -Math.PI) {
//            difference += (2 * Math.PI);
//        }
//        difference = Math.toDegrees(difference);
//        targetTheta = difference;
//        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));
//
//        //clear the ball
//        double ballTargetTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
//        double ballDifference = ballTargetTheta - Math.toRadians(r.getTheta());
////       System.out.println("initial targetTheta: " + targetTheta + " initial difference " + difference + " current Theta "
//        //     		+ Math.toRadians(r.getTheta()));
//        //some hack to make the difference -Pi < theta < Pi
//        if (ballDifference > Math.PI) {
//            ballDifference -= (2 * Math.PI);
//        } else if (ballDifference < -Math.PI) {
//            ballDifference += (2 * Math.PI);
//        }
//        ballDifference = Math.toDegrees(ballDifference);
//        ballTargetTheta = ballDifference;
//        /*
//        if ((Math.abs(ballTargetTheta) < 5 && Math.abs(r.getTheta()) < 90) || (Math.abs(ballTargetTheta) > 175 && Math.abs(r.getTheta()) > 90)) {
//            MoveToSpot.move(r, new Coordinate((int)ballX, (int)ballY), 1.5);
//            return;
//        } */
//
//        boolean isFacingTop = true;
//        boolean isTargetTop = true;
//        boolean front  = true;
//        /*
//        if (r.getTheta() < 0) {
//            isFacingTop = false;
//        }
//
//        if (y > r.getYPosition()) {
//            isTargetTop = false;
//        }
//
//        if (isTargetTop != isFacingTop) {
//            front = false;
//        } */
//
//        if (targetTheta > 90 || targetTheta < -90) {
//        	front = false;
//        }
//
//        if (!front && reverse) {
//            if (targetTheta < 0) {
//                targetTheta = -180 - targetTheta;
//            }
//            else if (targetTheta > 0) {
//                targetTheta = 180 - targetTheta;
//            }
//        }
//
//        /*
//        FunctionBlock fb = loadFuzzy("fuzzy/newFuzzy.fcl");
//        //if (targetDist <= 3.75) targetDist = 0;
//        if (targetDist <=3.75) {
//            targetDist = 0;
//            targetTheta = 0;
//        }
//        // targetTheta = Math.round(targetTheta/5)*5;
//
//        fb.setVariable("angleError", targetTheta);
//        fb.setVariable("distanceError", Math.abs(targetDist));
//        //      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " "
//        //      		+ r.getYPosition() + " targetDist " + targetDist);
//        // Evaluate
//        fb.evaluate();
//        // Show output variable's chart
//        fb.getVariable("rightWheelVelocity").defuzzify();
//        fb.getVariable("leftWheelVelocity").defuzzify();
//        //  JFuzzyChart.get().chart(fb.getVariable("leftWheelVelocity"), fb.getVariable("leftWheelVelocity").getDefuzzifier(), true);
//        //   JFuzzyChart.get().chart(fb.getVariable("rightWheelVelocity"), fb.getVariable("rightWheelVelocity").getDefuzzifier(), true);
//        double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
//        double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());
//        //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());
//        double linear =  (right+left)/2;
//        double angular = (right-left)*(2/0.135);
//        //    System.out.println("right :" + right + "left " + left)
//        r.linearVelocity = linear*2.5;
//        r.angularVelocity = angular*1; */
//
//
//        FunctionBlock fb = loadFuzzy("fuzzy/goalKeeper.fcl");
//        fb.setVariable("targetTheta", targetTheta);
//        fb.setVariable("targetDist", targetDist);
//        fb.evaluate();
//        fb.getVariable("linearVelocity").defuzzify();
//        fb.getVariable("angularVelocity").defuzzify();
//        r.linearVelocity = fb.getVariable("linearVelocity").getValue();
//        r.angularVelocity = fb.getVariable("angularVelocity").getValue();
//
//        if (targetDist <= 3.75) {
//            r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        }
//
//        if (!front &&reverse) {
//            r.linearVelocity *= -1;
//            r.angularVelocity *= -1;
//        }
//        //      System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta
//        //  		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);
//       // 	System.out.println("x:" + x + " y: " + y + " r.x: " + r.getXPosition() + " r.y" + r.getYPosition());
//        //     System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
//        //r.linearVelocity = 0;
////            r.angularVelocity = 0;
////
//
//        // }
//    }

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