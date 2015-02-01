package actions;

import strategy.Action;
import ui.Field;
import Paths.StraightLinePath;
import bot.Robot;

public class basicGoalKeep extends Action{
	
	private boolean onGoalKeepLine = false;
	private boolean keepRotate = false;
	private boolean goStraight = false;
	private boolean inPosition = false;
	
	
	private double goalKeepLine = 10;
	
	private double error = 2;
	@Override
    public String getName() {
        return "Basic Goal Keep";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        if (r.getXPosition()  <= (goalKeepLine+error) && r.getXPosition() >= (goalKeepLine-error)) {
        	onGoalKeepLine = true;
        }
        else {
        	onGoalKeepLine = false;
        }
        
         if (onGoalKeepLine == true) {
        	 if (r.getTheta() <= (90+error) && r.getTheta() >= (90-error) || r.getTheta()>=(-90+error) &&r.getTheta() <= (-90-error)) { 
        		 inPosition = true;
        	 }
        	 else {
        		 inPosition = false;
        	 }
         }
        
        
        
        
        if (onGoalKeepLine == false) {
        	path = new StraightLinePath(r, (int)r.getXPosition(), (int)r.getYPosition(),(int)goalKeepLine, Field.OUTER_BOUNDARY_HEIGHT/2);
        	path.setPoints();
        	
        	double theta = Math.atan2(Field.OUTER_BOUNDARY_HEIGHT/2-r.getYPosition(), goalKeepLine - r.getXPosition());        	
        	
        	if (Math.toDegrees(theta*-1) - r.getTheta() > -1 &&  Math.toDegrees(theta*-1) - r.getTheta() < 1) {
        		r.angularVelocity = 0;
        		keepRotate = false;
        		goStraight = true;
        	}
        	else {
        		keepRotate = true;
        	}
        	
        	
        	if (keepRotate){
        		if (Math.toDegrees(theta*-1) - r.getTheta() > 0) {
        			r.angularVelocity = 1;
        			r.linearVelocity = 0;
        		}
        		else {
        			 r.angularVelocity = -1;
        			 r.linearVelocity = 0;
        		}
        		
        	}
        	
        	if (goStraight) {
        		if (r.getXPosition() < goalKeepLine + error && r.getXPosition() > goalKeepLine - error) {
        			r.linearVelocity = 0;
            		r.angularVelocity = 0;
            		goStraight = false;
        		}
        		else if (r.getXPosition() < 50 && r.getXPosition() > 0){
        			r.linearVelocity = 0.25;
	        		r.angularVelocity = 0;
        		}
        		else if (r.getXPosition() < 100 && r.getXPosition() > 0){
        			r.linearVelocity = 0.3;
	        		r.angularVelocity = 0;
        		}
        		else {
	        		r.linearVelocity = 0.5;
	        		r.angularVelocity = 0;
        		}
        	}
        	
        }
        else {
        	path = new StraightLinePath(r, (int)r.getXPosition(), (int)r.getYPosition(),(int)r.getXPosition(), (int)r.getYPosition());
        	path.setPoints();
        	r.linearVelocity = 0;
            r.angularVelocity = 0;
        }
        

    }
}
