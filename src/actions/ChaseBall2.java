package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import ui.Field;

/**
 * Created by Wesley on 21/01/2015.
 */
public class ChaseBall2 extends Action{

    private double error = 5;

    @Override
    public String getName() {
        return "Chase Ball (smooth)";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        setVelocityToTarget(ballX, ballY, true);
    }

    public void setVelocityToTarget(double x, double y, boolean front) {
        Robot r = bots.getRobot(index);
        double targetDist = 0;
        double targetTheta = 0;

        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));

        targetTheta = Math.atan2(y-r.getYPosition(), x - r.getXPosition());


        double difference;
        double diff1;
        double diff2;
        if ( Math.toDegrees(targetTheta*-1) > 0 && r.getTheta() <= 0) {
            diff1 = Math.toDegrees(targetTheta*-1) + Math.abs(r.getTheta());
            diff2 = -1*(180-Math.toDegrees(targetTheta*-1)) + Math.abs(-180-r.getTheta());

            if (diff1 <= diff2) {
                difference = diff1;
            }
            else {
                difference = diff2;
            }
        }
        else if ( Math.toDegrees(targetTheta*-1) <= 0 && r.getTheta() > 0) {
            diff1 = -1*Math.abs(Math.toDegrees(targetTheta*-1)) + r.getTheta();
            diff2 = Math.abs(-180-Math.toDegrees(targetTheta*-1)) + (180-r.getTheta());

            if (diff1 <= diff2) {
                difference = diff1;
            }
            else {
                difference = diff2;
            }
        }
        else {
            difference = Math.toDegrees(targetTheta*-1) - r.getTheta();
        }

        targetTheta = difference;

        String filename = "tipper.fcl";
        FIS fis = FIS.load(filename, true);

        if (fis == null) {
            System.err.println("Can't load file: '" + filename + "'");
            System.exit(1);
        }

        // Get default function block
        FunctionBlock fb = fis.getFunctionBlock(null);
        //JFuzzyChart.get().chart(fb);
        // Set inputs
        //fb.setVariable("food", 8.5);
        //fb.setVariable("service", 7.5);
        fb.setVariable("obstacleTheta", Math.PI);
        fb.setVariable("obstacleDist", 10);
        fb.setVariable("targetTheta", Math.toRadians(targetTheta));
        fb.setVariable("targetDist", targetDist);

        // Evaluate
        fb.evaluate();

        // Show output variable's chart
        fb.getVariable("angSpeedError").defuzzify();


        // Print ruleSet
        //System.out.println(fb);
// 		System.out.println("theta: " + targetTheta );
// 		System.out.println("dist: " + targetTheta );
// 		System.out.println("ang speed: " + Math.toDegrees(fb.getVariable("angSpeedError").getValue()));
// 		System.out.println("position " + r.getXPosition() + " " + r.getYPosition());

        r.angularVelocity = fb.getVariable("angSpeedError").getValue()*0.5;
        if (r.angularVelocity > 3) {
        	r.angularVelocity = 3;
        } else if (r.angularVelocity < -3) {
        	r.angularVelocity = -3;
        }
   //           System.out.println(r.angularVelocity);
        r.linearVelocity= 1;

        if (isCloseToWall()) {
            if (Math.abs(targetTheta) < 10) {
            	r.linearVelocity = 0.2;
            } else {
            	r.linearVelocity = 0;
            }
        }

        if (front == false) {
            r.linearVelocity*= -1;
        }

        checkRobotPosition(x,y);
    }

    private void checkRobotPosition(double x, double y) {
        Robot r = bots.getRobot(index);
        if (r.getXPosition() >= x-error && r.getXPosition() <= x+error && r.getYPosition() >= y-error && r.getYPosition() <= y+error) {
            r.angularVelocity = 0;
            r.linearVelocity = 0;
        }
    }

    private boolean isCloseToWall() {
        Robot r = bots.getRobot(index);
        if (r.getYPosition() >= -20 && r.getYPosition() <= 20 ) {
            return true;
        }
        else if (r.getYPosition() >= Field.OUTER_BOUNDARY_HEIGHT-20 && r.getYPosition() <= Field.OUTER_BOUNDARY_HEIGHT+20) {
            return true;
        }
        else if (r.getXPosition() >= -20 && r.getXPosition() <= 20 ) {
            return true;
        }
        else if (r.getXPosition() >= Field.OUTER_BOUNDARY_WIDTH-20 && r.getXPosition() <= Field.OUTER_BOUNDARY_WIDTH+20) {
            return true;
        }

        return false;
    }


    protected double squared (double x) {
        return x * x;
    }
}