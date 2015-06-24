package actions;

import javax.swing.JOptionPane;

import data.Coordinate;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import strategy.Action;
import bot.Robot;
import strategy.GameState;
import ui.Field;

public class testSelfMadeObstacle extends Action {

    private double error = 2.5;
    @Override
    public void execute() {
    	//System.out.println("obs");
        setVelocityToTarget(180,90,false,false);
    }

    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
        Robot r = bot;
        double obstacleX = 110;
        double obstacleY = 90;

        double targetDist,obstacleDist;
        double targetTheta = Math.atan2(r.getYPosition() - y, x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
//       System.out.println("initial targetTheta: " + targetTheta + " initial difference " + difference + " current Theta " 
        //     		+ Math.toRadians(r.getTheta()));
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        difference = Math.toDegrees(difference);
        targetTheta = difference;
        
        double obstacleTheta = Math.atan2(r.getYPosition() - obstacleY, obstacleX - r.getXPosition());
        double obsDifference = obstacleTheta - Math.toRadians(r.getTheta());
        if (obsDifference > Math.PI) {
        	obsDifference -= (2 * Math.PI);
        } else if (obsDifference < -Math.PI) {
        	obsDifference += (2 * Math.PI);
        }
        obsDifference = Math.toDegrees(obsDifference);
        obstacleTheta = obsDifference;
        
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));
        obstacleDist = Math.sqrt(Math.pow((obstacleX-r.getXPosition()),2) + Math.pow((obstacleY-r.getYPosition()),2));

        boolean isFacingTop = true;
        boolean isTargetTop = true;
        boolean front  = true;
        if (r.getTheta() < 0) {
            isFacingTop = false;
        }

        if (y > r.getYPosition()) {
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

        FunctionBlock fb = loadFuzzy("selfMadeObstacle.fcl");
        fb.setVariable("obstacleDist", obstacleDist);
        fb.setVariable("obstacleTheta", obstacleTheta);
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
     //      JFuzzyChart.get().chart(fb.getVariable("linearVelocity"), fb.getVariable("linearVelocity").getDefuzzifier(), true);
      //       JFuzzyChart.get().chart(fb.getVariable("angularVelocity"), fb.getVariable("angularVelocity").getDefuzzifier(), true);
      //      JOptionPane.showMessageDialog(null, "nwa"); 
        double linear  = fb.getVariable("linearVelocity").getValue();
        double angular = fb.getVariable("angularVelocity").getValue();
        //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());

        //    System.out.println("right :" + right + "left " + left);

        r.linearVelocity = linear;
        r.angularVelocity = angular*-1;
//            System.out.println("linear: " + linear + " angular:" + angular*-1
//            					+ " x: " + r.getXPosition() + " y: " + r.getYPosition()
//            					+ " r theta: " + r.getTheta() + " t theta: " + targetTheta
//            					+ " t dist" + targetDist + " time: " + System.currentTimeMillis());

        if (!front &&reverse) {
            r.linearVelocity *= -1;
            r.angularVelocity *= -1;
        }
        if (targetDist <=3.75) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        }
        System.out.println(targetDist);
//          System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta
//        		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);

        //    System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
//             r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	

        // }
        return;
    }

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(110 - r.getXPosition()) + squared(90 - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}