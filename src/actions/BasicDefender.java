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

    private Polygon defendArea;
    private double defendSize;

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

        defendSize = 0.2;

        updatePolygon();
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
        if (defendArea.contains(ballX, ballY)) {
            FunctionBlock fb = loadFuzzy("fuzzy/selfMade.fcl");
            double targetDist;
            double targetTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
            double difference = targetTheta - Math.toRadians(r.getTheta());
            boolean reverse = true;
            //some hack to make the difference -Pi < theta < Pi
            if (difference > Math.PI) {
                difference -= (2 * Math.PI);
            } else if (difference < -Math.PI) {
                difference += (2 * Math.PI);
            }
            difference = Math.toDegrees(difference);
            targetTheta = difference;
            targetDist = Math.sqrt(Math.pow((ballX-r.getXPosition()),2) + Math.pow((ballY-r.getYPosition()),2));

            boolean isFacingTop = true;
            boolean isTargetTop = true;
            boolean front  = true;
            if (r.getTheta() < 0) {
                isFacingTop = false;
            }

            if (ballY > r.getYPosition()) {
                isTargetTop = false;
            }

            if (isTargetTop != isFacingTop) {
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

            fb.setVariable("targetTheta", targetTheta);
            fb.setVariable("targetDist", Math.abs(targetDist));
            fb.setVariable("direction", r.getTheta());
            fb.setVariable("xPos", r.getXPosition());
            fb.setVariable("yPos", r.getYPosition());

            // Evaluate
            fb.evaluate();


            // Show output variable's chart
            fb.getVariable("linearVelocity").defuzzify();
            fb.getVariable("angularVelocity").defuzzify();
            double linear  = fb.getVariable("linearVelocity").getValue();
            double angular = fb.getVariable("angularVelocity").getValue();

            r.linearVelocity = linear;
            r.angularVelocity = angular*-1;

            if (!front &&reverse) {
                r.linearVelocity *= -1;
                r.angularVelocity *= -1;
            }
            if (targetDist <=2.5) {
                r.linearVelocity = 0;
                r.angularVelocity = 0;
            }

            return;
        }

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
            return;
        }

        // use fuzzy
        MoveToSpot.pidMove(bot, (int)positionToBe.x, (int)positionToBe.y);

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

    public void updatePolygon() {
        Point perp1 = calculate_perp_point(parameters.get("point 1 x"), parameters.get("point 1 y"), parameters.get("point 2 x"), parameters.get("point 2 y"), true, (float)(defendSize * 100));
        Point perp2 = calculate_perp_point(parameters.get("point 1 x"), parameters.get("point 1 y"), parameters.get("point 2 x"), parameters.get("point 2 y"), false, (float)(defendSize * 100));

        int[] xpoints = new int [4];
        xpoints[0] = parameters.get("point 1 x");
        xpoints[1] = parameters.get("point 2 x");
        xpoints[2] = (int)perp2.x;
        xpoints[3] = (int)perp1.x;

        int[] ypoints = new int [4];
        ypoints[0] = parameters.get("point 1 y");
        ypoints[1] = parameters.get("point 2 y");
        ypoints[2] = (int)perp2.y;
        ypoints[3] = (int)perp1.y;

        defendArea = new Polygon(xpoints, ypoints, xpoints.length);
    }

    public Point calculate_perp_point(double startX, double startY, double stopX, double stopY, boolean start, float distance) {
        Point M = new Point();
        if (start) {
            M = new Point(startX, startY);
        } else {
            M = new Point(stopX, stopY);
        }

        Point p = new Point(startX - stopX, startY - stopY);
        Point n = new Point(-p.y, p.x);
        int norm_length = (int) Math.sqrt((n.x * n.x) + (n.y * n.y));
        n.x /= norm_length;
        n.y /= norm_length;
        return new Point(M.x + (distance * n.x), M.y + (distance * n.y));
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

        int x[] = new int[4];
        x[0] = x1;
        x[1] = x2;
        x[2] = Field.fieldXValueToGUIValue(defendArea.xpoints[2]);
        x[3] = Field.fieldXValueToGUIValue(defendArea.xpoints[3]);

        int y[] = new int[4];
        y[0] = y1;
        y[1] = y2;
        y[2] = Field.fieldYValueToGUIValue(defendArea.ypoints[2]);
        y[3] = Field.fieldYValueToGUIValue(defendArea.ypoints[3]);

        g2.drawPolygon (x, y, defendArea.xpoints.length);
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

        updatePolygon();
    }
}