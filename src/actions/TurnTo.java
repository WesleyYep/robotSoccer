package actions;

import bot.Robot;
import data.Coordinate;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class TurnTo extends Action{
    public static final double ERROR_MARGIN = 0.8;


    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        turn(r, new Coordinate((int)ballX, (int)ballY));
    }

    public static void turn(Robot r, Coordinate spot) {
        double targetTheta = Math.atan2(r.getYPosition() - spot.y, spot.x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        targetTheta = Math.toDegrees(difference);
        double targetDist = 5;


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
        //    System.out.println(targetTheta);
        // Evaluate
        fb.evaluate();

        // Show output variable's chart
        fb.getVariable("rightWheelVelocity").defuzzify();
        fb.getVariable("leftWheelVelocity").defuzzify();

        double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
        double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());

        double linear =  (right+left)/2;
        double angular = (right-left)*(2/0.135);

        r.linearVelocity = 0;
        r.angularVelocity = angular*0.5;
    }

    protected double squared (double x) {
        return x * x;
    }
}
