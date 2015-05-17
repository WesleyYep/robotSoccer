package actions;

import javax.swing.JOptionPane;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import strategy.Action;
import bot.Robot;

public class testSelfMade extends Action {
   
	private double error = 2.5;
	
	
    @Override
    public void execute() {
    	setVelocityToTarget(110,90,false,false);
    }
    
    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
        Robot r = bots.getRobot(index);
        double targetDist;
        
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
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));
        
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
       //      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " " 
       //      		+ r.getYPosition() + " targetDist " + targetDist);
             // Evaluate
             fb.evaluate();
             /*
             JFuzzyChart.get().chart(fb);
              JOptionPane.showMessageDialog(null, "nwa"); */
       
             // Show output variable's chart
             fb.getVariable("linearVelocity").defuzzify();
             fb.getVariable("angularVelocity").defuzzify();
           //  JFuzzyChart.get().chart(fb.getVariable("leftWheelVelocity"), fb.getVariable("leftWheelVelocity").getDefuzzifier(), true);
          //   JFuzzyChart.get().chart(fb.getVariable("rightWheelVelocity"), fb.getVariable("rightWheelVelocity").getDefuzzifier(), true);
             double linear  = fb.getVariable("linearVelocity").getValue();
             double angular = fb.getVariable("angularVelocity").getValue();
         //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());
  
        //    System.out.println("right :" + right + "left " + left);

             r.linearVelocity = linear*1; 
             r.angularVelocity = angular*-1;
             System.out.println(linear + " " + angular);
             
             if (!front &&reverse) {
            	 r.linearVelocity *= -1;
            	 r.angularVelocity *= -1;
             }
             if (targetDist <=2.5) {
            	 r.linearVelocity = 0;
            	 r.angularVelocity = 0;
             }
      //      System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta 
        //  		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);
             
        //     System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
             //r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	
        	
       // }
    }
}
