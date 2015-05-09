package actions;

import bot.Robot;
import data.Coordinate;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class MoveToSpot extends Action{
    public static final double ERROR_MARGIN = 5;

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        Coordinate spot = new Coordinate(10,18);  //<-------- EDIT THIS TO CHANGE SPOT
        //change to left/right side depending on where ball is
        if (ballY > 90) {
            //          System.out.println(r.getYPosition());
            spot.y = 162;
        } else {
            spot.y = 18;
        }
        move(r, spot);
    }

    public static void move(Robot r, Coordinate spot) {
        if (Math.abs(r.getXPosition() - spot.x) < ERROR_MARGIN && Math.abs(r.getYPosition() - spot.y) < ERROR_MARGIN ) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
            return;
        }

        double targetTheta = Math.atan2(r.getYPosition() - spot.y, spot.x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        targetTheta = Math.toDegrees(difference);
        double targetDist = Math.sqrt(Math.pow((spot.x-r.getXPosition()),2) + Math.pow((spot.y-r.getYPosition()),2));


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

        r.linearVelocity = linear*2;
        r.angularVelocity = angular*1;
    }

//    public static void move(Robot r, Coordinate spot) {
//        double ballTheta = Math.atan2(r.getYPosition() - spot.y, spot.x - r.getXPosition());
//        double difference = ballTheta - Math.toRadians(r.getTheta());
//        double distance = Math.sqrt(squared(r.getXPosition()-spot.x) + squared(r.getYPosition()-spot.y));
//
//        if (distance < 5) {
//            r.linearVelocity = 0;
//            r.angularVelocity = 0;
//            return;
//        }
//
//        //some hack to make the difference -Pi < theta < Pi
//        if (difference > Math.PI) {
//            difference -= (2 * Math.PI);
//        } else if (difference < -Math.PI) {
//            difference += (2 * Math.PI);
//        }
//
//        if (Math.abs(difference) >= TurnToFaceBall.ERROR_MARGIN) {
//            if (difference > 0) {
//                r.angularVelocity = 2*Math.PI;
//            } else {
//                r.angularVelocity = -2*Math.PI;
//            }
//            r.linearVelocity = 0;
//        } else if (Math.abs(difference) >= TurnToFaceBall.ERROR_MARGIN /2) {
//            if (difference > 0) {
//                r.angularVelocity = Math.PI/2;
//            } else {
//                r.angularVelocity = -Math.PI/2;
//            }
//            r.linearVelocity = 0;
//        } else if (Math.abs(difference) >= TurnToFaceBall.ERROR_MARGIN /4) {
//            if (difference > 0) {
//                r.angularVelocity = Math.PI/4;
//            } else {
//                r.angularVelocity = -Math.PI/4;
//            }
//            r.linearVelocity = 0;
//        } else {
//            r.linearVelocity = distance/100;
//            r.angularVelocity = difference / (distance/100);
//        }
//    }

    protected static double squared (double x) {
        return x * x;
    }
}
