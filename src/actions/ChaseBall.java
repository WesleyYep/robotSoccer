package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import ui.Field;
import utils.Geometry;

/**
 * Created by Wesley on 21/01/2015.
 */
public class ChaseBall extends Action{

    private int goalX = 220;
    private int goalY = 90;

    @Override
    public void execute() {
        setVelocityToTarget(ballX, ballY, true);
    }

    public void setVelocityToTarget(double x, double y, boolean front) {
        Robot r = bots.getRobot(index);
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
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));

        targetTheta = difference;

        String filename = "newFuzzy.fcl";
        FIS fis = FIS.load(filename, true);

        if (fis == null) {
            System.err.println("Can't load file: '" + filename + "'");
            System.exit(1);
        }

        // Get default function block
        FunctionBlock fb = fis.getFunctionBlock(null);
        fb.setVariable("angleError", targetTheta);
        fb.setVariable("distanceError", targetDist);

        // Evaluate
        fb.evaluate();

        // Show output variable's chart
        fb.getVariable("rightWheelVelocity").defuzzify();
        fb.getVariable("leftWheelVelocity").defuzzify();

        double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
        double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());

        double linear =  (right+left)/2;
        double angular = (right-left)*(2/0.135);

        r.linearVelocity = linear*4;
        r.angularVelocity = angular;

        if (targetDist < 20 && Math.abs(targetTheta) < 5) {
            double angle = angleDifferenceFromGoal(r.getXPosition(), r.getYPosition(), r.getTheta());
            if (Math.abs(angle) < Math.PI / 4) {
                System.out.println("dribble! ");
                r.angularVelocity += 5*angle;
            }
        }
        // }
    }

    private double angleDifferenceFromGoal(double x, double y, double theta) {
        double targetTheta = Math.atan2(y - goalY, goalX - x);
        double difference = targetTheta - Math.toRadians(theta);
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        return difference;
    }

    protected double squared (double x) {
        return x * x;
    }
}