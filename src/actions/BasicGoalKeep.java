package actions;

import javax.swing.JOptionPane;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import strategy.Action;
import ui.Field;
import bot.Robot;

public class BasicGoalKeep extends Action {
   
	private double error = 2.5;
	private double goalLine = 6;
	private boolean fixPosition = false;
	private double lastBallX = 0;
	private double lastBallY = 0;
	
    @Override
    public void execute() {
    	Robot r = bots.getRobot(index);
    	
    	if (r.getXPosition() < goalLine-error || r.getXPosition() >  goalLine+error) {
    		int targetPos = 0;
    		if (ballY >= 70 && ballY <= 110 ) {
    			targetPos = (int) ballY;
    		}
    		else if (ballY < 70) {
    			targetPos = 70;
    		}
    		else if (ballY > 110) {
    			targetPos = 110;
    		}
    		setVelocityToTarget(goalLine,targetPos, true,false);
    		fixPosition = true;
    	}
    	else if (fixPosition) {;
    		if ( ( r.getTheta() > 90+5 && r.getTheta() <= 180)|| (r.getTheta() <= 0 && r.getTheta() > -90+5)) {
        		System.out.println("turning negative: " + r.getTheta() );
        		r.angularVelocity = -Math.PI/4;
        	}
        	else if ( (r.getTheta() < 90-5 && r.getTheta() >= 0) || (r.getTheta() < -90-5 && r.getTheta() >= -180)) {
        		r.angularVelocity = Math.PI/4;
        		r.linearVelocity = 0;
        	}
        	else {
        		fixPosition = false;
        	}
    	}
    	else{
    		boolean isFacingTop = true;
    		boolean isBallTop = true;
    		
    		boolean reverseTheta = true;
    		
    		if (r.getTheta() < 0) {
    			isFacingTop = false;
    		}
    		
    		if (predY > r.getYPosition()) {
    			isBallTop = false;
    		}
    		 
    		if (isBallTop != isFacingTop) {
    			reverseTheta = false;
    		}
    //		System.out.println(ballY);
    //		System.out.println("front: " + reverseTheta);
    		
    		double yDiff = Math.round(ballY-lastBallY);
    		double xDiff = Math.round(ballX-lastBallX);
    		double constant;  		
    		
    	
    		if (predY >= 70 && predY <= 110 ) {
    			setVelocityToTarget(goalLine,predY, reverseTheta,true);

    		}
    		else if (predY < 70) {
    			setVelocityToTarget(goalLine,70,reverseTheta,true);
    		}
    		else if (predY > 110) {
    			setVelocityToTarget(goalLine,110,reverseTheta,true);
    		} 
    		
    	}
    	lastBallX = ballX;
    	lastBallY = ballY;
    	
    }
    
    public void setVelocityToTarget(double x, double y, boolean front, boolean onGoalLine) {
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
        
        if (!front) {
        	if (targetTheta < 0) {
        		targetTheta = -180 - targetTheta;
        	}
        	else if (targetTheta > 0) {
        		targetTheta = 180 - targetTheta;
        	}
        }   
            
        	 String filename = "newFuzzy.fcl";
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
             if (targetDist <=2.5) {
            	 targetDist = 0;
            	 targetTheta = 0;
             }
            // targetTheta = Math.round(targetTheta/5)*5;
             
             fb.setVariable("angleError", targetTheta);
             fb.setVariable("distanceError", Math.abs(targetDist));
       //      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " " 
       //      		+ r.getYPosition() + " targetDist " + targetDist);
             // Evaluate
             fb.evaluate();
             /*
             JFuzzyChart.get().chart(fb);
              JOptionPane.showMessageDialog(null, "nwa"); */
       
             // Show output variable's chart
             fb.getVariable("rightWheelVelocity").defuzzify();
             fb.getVariable("leftWheelVelocity").defuzzify();
           //  JFuzzyChart.get().chart(fb.getVariable("leftWheelVelocity"), fb.getVariable("leftWheelVelocity").getDefuzzifier(), true);
          //   JFuzzyChart.get().chart(fb.getVariable("rightWheelVelocity"), fb.getVariable("rightWheelVelocity").getDefuzzifier(), true);
             double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
             double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());
         //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());
             double linear =  (right+left)/2;
             double angular = (right-left)*(2/0.135);
        //    System.out.println("right :" + right + "left " + left);

            r.linearVelocity = linear*2; 
             r.angularVelocity = angular*1;
             
             if (!front) {
            	 r.linearVelocity *= -1;
            	 r.angularVelocity *= -1;
             }
            System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta
          		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);
             
             
        //     System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
             //r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	
        	
       // }
    }

}
