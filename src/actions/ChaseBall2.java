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
public class ChaseBall2 extends Action{

    private int goalX = 220;
    private int goalY = 90;

    @Override
    public String getName() {
        return "Chase Ball (Striker)";
    }

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
//        targetTheta = Math.atan2(y-r.getYPosition(),x-r
//        		.getXPosition());
//        System.out.println(targetTheta);
//        
//        double diff1;
//        double diff2;
//
//        if ( Math.toDegrees(targetTheta*-1) > 0 && r.getTheta() <= 0) {
//            diff1 = Math.toDegrees(targetTheta*-1) + Math.abs(r.getTheta());
//            diff2 = -1*(180-Math.toDegrees(targetTheta*-1)) + Math.abs(-180-r.getTheta());
//
//            if (diff1 <= diff2) {
//                difference = diff1;
//            }
//            else {
//                difference = diff2;
//            }
//        }
//        else if ( Math.toDegrees(targetTheta*-1) <= 0 && r.getTheta() > 0) {
//            diff1 = -1*Math.abs(Math.toDegrees(targetTheta*-1)) + r.getTheta();
//            diff2 = Math.abs(-180-Math.toDegrees(targetTheta*-1)) + (180-r.getTheta());
//
//            if (diff1 <= diff2) {
//                difference = diff1;
//            }
//            else {
//                difference = diff2;
//            }
//        }
//        else {
//            difference = Math.toDegrees(targetTheta*-1) - r.getTheta();
 //       }

        targetTheta = difference;
        System.out.println("Difference: " + difference);
        
        /*
        if (targetDist < 20 &&  targetTheta < 10) {
            double goalTheta = Math.atan2(r.getYPosition() - goalY, goalX - r.getXPosition());
            double goalDifference = goalTheta - Math.toRadians(r.getTheta());

            r.angularVelocity = 2*goalDifference;// / (goalDist);
            r.linearVelocity = 1;
        } else {
        	
        	/*
            String filename = "tipper.fcl";
            FIS fis = FIS.load(filename, true);

            if (fis == null) {
                System.err.println("Can't load file: '" + filename + "'");
                System.exit(1);
            }

            // Get default function block
            FunctionBlock fb = fis.getFunctionBlock(null);
            fb.setVariable("obstacleTheta", Math.PI);
            fb.setVariable("obstacleDist", 10);
            fb.setVariable("targetTheta", Math.toRadians(targetTheta));
            fb.setVariable("targetDist", targetDist);

            // Evaluate
            fb.evaluate();

            // Show output variable's chart
            fb.getVariable("angSpeedError").defuzzify();

            r.angularVelocity = fb.getVariable("angSpeedError").getValue() * 0.5;
            if (r.angularVelocity > 3) {
                r.angularVelocity = 3;
            } else if (r.angularVelocity < -3) {
                r.angularVelocity = -3;
            }
            r.linearVelocity = targetDist/100.0;

            if (isCloseToWall()) {
                if (Math.abs(targetTheta) < 10) {
                    r.linearVelocity = 0.2;
                } else {
                    r.linearVelocity = 0;
                }
            }

            if (front == false) {
                r.linearVelocity *= -1;
            }
            */
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
             
            r.linearVelocity = linear*8;
             r.angularVelocity = angular*0.5;
             
//             r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	
        	
       // }
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