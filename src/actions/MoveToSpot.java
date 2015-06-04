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

    //non-static initialiser block
    {
        if(!(parameters.containsKey("startingX"))) {
            //don't bother if these already exist
            parameters.put("startingX", 20);
        }
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        Coordinate spot = new Coordinate(parameters.get("startingX"), 18);  //This method is used only for going back for the chaseBall methods. Use MoveAndTurn action for other uses.
        //change to left/right side depending on where ball is
        move(r, spot, 4, ballY);
    }

    public static void move(Robot r, Coordinate spot, int speed, double ballY) {
        if (ballY != -100) {
            if (ballY > 90) {
                //          System.out.println(r.getYPosition());
                spot.y = 162;
            } else {
                spot.y = 18;
            }
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

        //if directly behind, just hard code
        if (Math.abs(targetTheta) > 175) {
            r.angularVelocity = 0;
            r.linearVelocity = -1;
            return;
        }

        String filename = "selfMade.fcl";
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

        // targetTheta = Math.round(targetTheta/5)*5;

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

//        if (Math.abs(r.getXPosition() - spot.x) < ERROR_MARGIN && Math.abs(r.getYPosition() - spot.y) < ERROR_MARGIN ) {
//            r.linearVelocity = 0;
//            r.angularVelocity = 0;
//            return;
//        }
//
//        double targetTheta = Math.atan2(r.getYPosition() - spot.y, spot.x - r.getXPosition());
//        double difference = targetTheta - Math.toRadians(r.getTheta());
//        //some hack to make the difference -Pi < theta < Pi
//        if (difference > Math.PI) {
//            difference -= (2 * Math.PI);
//        } else if (difference < -Math.PI) {
//            difference += (2 * Math.PI);
//        }
//        targetTheta = Math.toDegrees(difference);
//        double targetDist = Math.sqrt(Math.pow((spot.x-r.getXPosition()),2) + Math.pow((spot.y-r.getYPosition()),2));
//
//        //if directly behind, just hard code
//        if (Math.abs(targetTheta) > 175) {
//            r.angularVelocity = 0;
//            r.linearVelocity = -1;
//            return;
//        }
//
//
//        String filename = "newFuzzy.fcl";
//        FIS fis = FIS.load(filename, true);
//
//        if (fis == null) {
//            System.err.println("Can't load file: '" + filename + "'");
//            System.exit(1);
//        }
//
//        // Get default function block
//        FunctionBlock fb = fis.getFunctionBlock(null);
//        fb.setVariable("angleError", targetTheta);
//        fb.setVariable("distanceError", targetDist);
//        //    System.out.println(targetTheta);
//        // Evaluate
//        fb.evaluate();
//
//        // Show output variable's chart
//        fb.getVariable("rightWheelVelocity").defuzzify();
//        fb.getVariable("leftWheelVelocity").defuzzify();
//
//        double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
//        double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());
//
//        double linear =  (right+left)/2;
//        double angular = (right-left)*(2/0.135);
//
//        r.linearVelocity = linear * speed;
//        r.angularVelocity = angular;



    }
}
