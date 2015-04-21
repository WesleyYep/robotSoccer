package actions;

import javax.swing.JOptionPane;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import strategy.Action;
import ui.Field;
import bot.Robot;

public class BasicGoalKeep extends Action {
   
	private double error = 5;
	private double goalLine = 6;
	
    @Override
    public void execute() {
    	Robot r = bots.getRobot(index);
    	
    	if (r.getXPosition() < goalLine-error || r.getXPosition() >  goalLine+error) {
    		setVelocityToTarget(goalLine,Field.OUTER_BOUNDARY_HEIGHT/2, true,false);
    	}
    	else if ( ( r.getTheta() > 90+error && r.getTheta() <= 180)|| (r.getTheta() <= 0 && r.getTheta() > -90+error)) {
    		r.angularVelocity = -Math.PI/9;
    		r.linearVelocity = 0;
    	}
    	else if ( (r.getTheta() < 90-error && r.getTheta() >= 0) || (r.getTheta() < -90-error && r.getTheta() >= -180)) {
    		r.angularVelocity = Math.PI/9;
    		r.linearVelocity = 0;
    	}
    	else{
    		boolean isFacingTop = true;
    		boolean isBallTop = true;
    		
    		boolean reverseTheta = true;
    		
    		if (r.getTheta() < 0) {
    			isFacingTop = false;
    		}
    		
    		if (ballY > r.getYPosition()) {
    			isBallTop = false;
    		}
    		 
    		if (isBallTop != isFacingTop) {
    			reverseTheta = false;
    		}
    //		System.out.println(ballY);
    //		System.out.println("front: " + reverseTheta);
    		if (ballY >= 70 && ballY <= 110 ) {
    			setVelocityToTarget(goalLine,ballY, reverseTheta,true);

    		}
    		else if (ballY < 70) {
    			setVelocityToTarget(goalLine,70,reverseTheta,true);
    		}
    		else if (ballY > 110) {
    			setVelocityToTarget(goalLine,110,reverseTheta,true);
    		}
    		
    	}
    	
    	
    }
    
    /*
    public void setVelocityToTarget(double x, double y, boolean front, boolean onGoalLine) {
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
 		//JOptionPane.showMessageDialog(null, "NWA");
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
 		
 		if (onGoalLine) {
 			r.angularVelocity = 0;
 			r.linearVelocity = 0.3;
 		} else {
	 		r.angularVelocity = fb.getVariable("angSpeedError").getValue()*0.5;
	 //		System.out.println(r.angularVelocity);
	 		r.linearVelocity= ((targetDist-2)/10)*0.05+0.15;
	 		
	 		if (isCloseToWall()) {
	 			r.linearVelocity = 0.3;
	 		}
 		}
 		
 		if (front == false) {
 			r.linearVelocity*= -1;
 		}
	
 		checkRobotPosition(x,y);
    }

	*/
    
    public void setVelocityToTarget(double x, double y, boolean front, boolean onGoalLine) {
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
        //System.out.println("Difference: " + difference);
        
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
            */

            
            
        	 String filename = "newFuzzy.fcl";
             FIS fis = FIS.load(filename, true);

             if (fis == null) {
                 System.err.println("Can't load file: '" + filename + "'");
                 System.exit(1);
             }

             // Get default function block
             FunctionBlock fb = fis.getFunctionBlock(null);
            // System.out.println("orig theta: " + targetTheta);
             if (onGoalLine) {
            	targetTheta = 0;
             } 
             
             fb.setVariable("angleError", targetTheta);
             fb.setVariable("distanceError", Math.abs(targetDist));

             // Evaluate
             fb.evaluate();

             // Show output variable's chart
             fb.getVariable("rightWheelVelocity").defuzzify();
             fb.getVariable("leftWheelVelocity").defuzzify();

             double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
             double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());
             
             double linear =  (right+left)/2;
             double angular = (right-left)*(2/0.135);

            r.linearVelocity = linear*3;
             r.angularVelocity = angular*1;
             if (onGoalLine) {
            	 r.angularVelocity = 0;
            	 if (Math.abs(difference) >= 90) {
            		 r.linearVelocity *= -1;
            	 }
             }
             
             
             
             //System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
             //r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	
        	
       // }
    }
    private void checkRobotPosition(double x, double y) {
    	 Robot r = bots.getRobot(index);
    	 int xError = 10;
    	if (r.getXPosition() >= x-xError && r.getXPosition() <= x+xError && r.getYPosition() >= y-error && r.getYPosition() <= y+error) {
			r.angularVelocity = 0;
			r.linearVelocity = 0;
		}
    }
    
    private boolean isCloseToWall() {
    	 Robot r = bots.getRobot(index);
    	if (r.getYPosition() >= 0 && r.getYPosition() <= 10 ) {
    		return true;
    	}
    	else if (r.getYPosition() >= Field.OUTER_BOUNDARY_HEIGHT-10 && r.getYPosition() <= Field.OUTER_BOUNDARY_HEIGHT) {
    		return true;
    	}
    	else if (r.getXPosition() >= 0 && r.getXPosition() <= 10 ) {
    		return true;
    	}
    	else if (r.getXPosition() >= Field.OUTER_BOUNDARY_WIDTH-10 && r.getXPosition() <= Field.OUTER_BOUNDARY_WIDTH) {
    		return true;
    	}
    	
    	return false;
    }


	@Override
	public String getName() {
		return "Basic Goal Keeper";
	}
}
