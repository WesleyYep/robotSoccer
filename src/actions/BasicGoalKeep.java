package actions;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import strategy.Action;
import ui.Field;
import Paths.StraightLinePath;
import bot.Robot;

public class BasicGoalKeep extends Action {
   
	private double error = 5;
	private double goalLine = 6;
	
    @Override
    public void execute() {
    	Robot r = bots.getRobot(index);
    	
    	if (r.getXPosition() < goalLine-error || r.getXPosition() >  goalLine+error) {
    		setVelocityToTarget(goalLine,Field.OUTER_BOUNDARY_HEIGHT/2, true);
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
   // 		System.out.println(ballY);
    		if (ballY >= 70 && ballY <= 110 ) {
    			setVelocityToTarget(goalLine,ballY, reverseTheta);
    		}
    		else if (ballY < 70) {
    			setVelocityToTarget(goalLine,70,reverseTheta);
    		}
    		else if (ballY > 110) {
    			setVelocityToTarget(goalLine,110,reverseTheta);
    		}
    		
    	}
    	
    	
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
 //		System.out.println(r.angularVelocity);
 		r.linearVelocity= ((targetDist-2)/10)*0.05+0.15;
 		
 		if (isCloseToWall()) {
 			r.linearVelocity = 0.3;
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
