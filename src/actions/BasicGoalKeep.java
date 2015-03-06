package actions;

import strategy.Action;
import ui.Field;
import Paths.StraightLinePath;
import bot.Robot;

public class BasicGoalKeep extends Action {
	
	private boolean onGoalKeepLine = false;
	private boolean keepRotate = false;
	private boolean goStraight = false;
	private boolean inPosition = false;
	
	private double goalKeepLine = 10;
	private double error = 3;

   

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        if (r.getXPosition()  <= (goalKeepLine+error) && r.getXPosition() >= (goalKeepLine-error)) {
        	onGoalKeepLine = true;
        	r.linearVelocity = 0;
       	 	r.angularVelocity = 0;
        }
        else {
        	onGoalKeepLine = false;
        }
        
        
        
         if (onGoalKeepLine == true) {
        	 if ( (r.getTheta() <= (90+error) && r.getTheta() >= (90-error)) || (r.getTheta() <=(-90+error) &&r.getTheta() >= (-90-error))) { 
        		 r.angularVelocity = 0;
        		// System.out.println("here");
        		
        		 
        		if ((r.getYPosition() <= this.ballY+error && r.getYPosition() >= this.ballY-error) || 
        				r.getYPosition() < Field.OUTER_BOUNDARY_HEIGHT/2-Field.GOAL_AREA_HEIGHT/2 ||
        				r.getYPosition() > Field.OUTER_BOUNDARY_HEIGHT/2+Field.GOAL_AREA_HEIGHT/2) {
        			System.out.println(r.getYPosition() + " " + this.ballY);
        			r.linearVelocity = 0;
        		}
        		else {
        			if (r.getYPosition() < this.ballY) {
        				r.linearVelocity = 0.1;
        			}
        			else {
        				r.linearVelocity = -0.1;
        			}
        			
        			if (r.getTheta() > 0) {
        				r.linearVelocity *= -1;
        			}
        		}
        		  
        	 }
        	 else {
        		 r.angularVelocity = (Math.PI/180)*10;
        	 }
         }
        
        
     
        
        if (onGoalKeepLine == false) {
        	path = new StraightLinePath(r, (int)r.getXPosition(), (int)r.getYPosition(), (int)goalKeepLine, Field.OUTER_BOUNDARY_HEIGHT/2);
        	path.setPoints();
        	
        	double theta = Math.atan2(Field.OUTER_BOUNDARY_HEIGHT/2-r.getYPosition(), goalKeepLine - r.getXPosition());        	
        	double sign;

        	
        	//double difference = Math.toDegrees(theta*-1) - r.getTheta();
        	double difference;
        	double diff1;
        	double diff2;
        	if ( Math.toDegrees(theta*-1) > 0 && r.getTheta() <= 0) {
        		diff1 = Math.toDegrees(theta*-1) + Math.abs(r.getTheta());
        		diff2 = -1*(180-Math.toDegrees(theta*-1)) + Math.abs(-180-r.getTheta());
        		
        		if (diff1 <= diff2) {
        			difference = diff1;
        		}
        		else {
        			difference = diff2;
        		}
        	}
        	else if ( Math.toDegrees(theta*-1) <= 0 && r.getTheta() > 0) {
        		diff1 = -1*Math.abs(Math.toDegrees(theta*-1)) + r.getTheta();
        		diff2 = Math.abs(-180-Math.toDegrees(theta*-1)) + (180-r.getTheta());
        		
        		if (diff1 <= diff2) {
        			difference = diff1;
        		}
        		else {
        			difference = diff2;
        		}
        		System.out.println(diff1 + " " + diff2);
        	}
        	else {
        		difference = Math.toDegrees(theta*-1) - r.getTheta();
        	}
        	
        	
        	
        	if ( Math.abs(difference) > 135) {
        		r.angularVelocity = 2*Math.PI;
        	}
        	else if ( Math.abs(difference) > 90) { 
        		r.angularVelocity = Math.PI;
        	}
        	else if ( Math.abs(difference) > 45) {
        		r.angularVelocity = Math.PI/2;
        	}
        	else if (Math.abs(difference) > 20) {
        		r.angularVelocity = Math.PI/4;
        	}
        	else if (Math.abs(difference) > 10) {
        		r.angularVelocity = Math.PI/8;
        	}
        	else if (Math.abs(difference) > 1) {
        		r.angularVelocity = Math.abs(difference)/50;
        	}
        	
        	//System.out.println(difference + " " +  r.getTheta() + " "  + Math.toDegrees(theta*-1) + " " + r.getYPosition());
        	//System.out.println(difference + " " + (r.getTheta() - Math.toDegrees(theta*-1)));
        	//System.out.println(Math.abs(difference) + " " + difference + " " + r.getTheta() + " " + Math.toDegrees(theta*-1));
 
        	
        	if (r.getXPosition()  <= 20 && r.getXPosition() >= 50) {
        		r.linearVelocity = 0.1;
        	}
        	else if (r.getXPosition()  <= 50 && r.getXPosition() >= 100) {
        		r.linearVelocity = 0.2;
        	}
        	else {
        		r.linearVelocity = 0.3;
        	}
      
        }
    }



	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Basic Goal Keeper";
	}
}
