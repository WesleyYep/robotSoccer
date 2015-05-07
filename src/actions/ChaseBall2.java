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

    private int goalX = 220;
    private int goalY = 90;
    private boolean isShooting = false;
    private int spinKick = 0;

    @Override
    public void execute() {
		setVelocityToTarget(predBallX, predBallY, true);
    }

    public void setVelocityToTarget(double x, double y, boolean front) {
        Robot r = bots.getRobot(index);

        if (spinKick > 0) {
            r.linearVelocity = 0;
            r.angularVelocity = 20;
            spinKick--;
            return;
        }

        double targetDist;
        double targetTheta = Math.atan2(r.getYPosition() - y, x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        targetTheta = Math.toDegrees(difference);
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));

        //System.out.println("Difference: " + difference);
        if (isShooting) {
    		targetTheta = Math.atan2(r.getYPosition() - goalY, goalX - r.getXPosition());
            difference = targetTheta - Math.toRadians(r.getTheta());
            //some hack to make the difference -Pi < theta < Pi
            if (difference > Math.PI) {
                difference -= (2 * Math.PI);
            } else if (difference < -Math.PI) {
                difference += (2 * Math.PI);
            }
            targetTheta = Math.toDegrees(difference);
            targetDist = Math.sqrt(Math.pow((goalX-r.getXPosition()),2) + Math.pow((goalY-r.getYPosition()),2));
    	}
    
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

         if (isShooting) {
        	 r.linearVelocity *= 2;
        	 r.angularVelocity *= 2;
        	 
        	 if (r.getXPosition() > 180) {
                 spinKick = 10;
        		 isShooting = false;
        	 }
        	 
         }else if (targetDist < 20 && Math.abs(targetTheta) < 5) {
            double angle = angleDifferenceFromGoal(r.getXPosition(), r.getYPosition(), r.getTheta());
            if (Math.abs(angle) < Math.PI / 8) {
                System.out.println("kick! ");
                isShooting = true;
            } else if (Math.abs(angle) < Math.PI / 4) {
                System.out.println("dribble! ");
                r.angularVelocity += 2*angle;
            }
        }
        
        
        
//        r.linearVelocity = 0;
 //       r.angularVelocity = 0;

        	

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