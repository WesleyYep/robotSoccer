package actions;

import Paths.Path;
import bot.Robot;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import org.opencv.core.Point;

import ui.Field;
import utils.Geometry;

public class BasicDefender extends Defender {

	public BasicDefender(Point p1, Point p2, Path path) {
		super(p1, p2, path);
	}

	@Override
	public String getName() {
		return "Basic Defender";
	}

	@Override
	public void execute() {
		Robot r = bots.getRobot(index);
		
		Point positionToBe = getPosition();

		if (!(r.getXPosition() >= positionToBe.x-5 && r.getXPosition() <= positionToBe.x+5 && r.getYPosition() >= positionToBe.y-5 
				&& r.getYPosition() <= positionToBe.y+5)) {
			setVelocityToTarget(positionToBe.x, positionToBe.y, true,false);
		} else {
			System.out.println("reached");
		}
		
	}

	@Override
	protected Point getPosition() {
		Point p1 = defendZone.getFirst();
		Point p2 = defendZone.getSecond();
		Point p3 = new Point(ballX, ballY);
		
		double[] angles = Geometry.anglesInTriangle(p1, p2, p3);

		if (angles[0] > Math.PI / 2) {
			return p1;
		} else if (angles[1] > Math.PI / 2) {
			return p2;
		} else {
			// either p1p3p2 is > 90 or all angles less than 90.
			
			// using either p1 or p2, in this case p1. find the adjacent side length using cosine.
			double distance = Math.cos(angles[0]) * Geometry.euclideanDistance(p1, p3);
			
			// Find the point that is x distance from point 1 along the vector.
			// TODO needs better way.
			double[] vector = new double[2];
			vector[0] = p2.x - p1.x;
			vector[1] = p2.y - p1.y;

			double magnitude = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));

			double[] normalisedVector = new double[2];
			normalisedVector[0] = vector[0] / magnitude;
			normalisedVector[1] = vector[1] / magnitude;

			return new Point(p1.x + distance * normalisedVector[0], p1.y + distance * normalisedVector[1]);
			
		}
	}
	
	
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

    private void checkRobotPosition(double x, double y) {
    	 Robot r = bots.getRobot(index);
    	 int xError = 10;
    	if (r.getXPosition() >= x-xError && r.getXPosition() <= x+xError && r.getYPosition() >= y-10 && r.getYPosition() <= y+10) {
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

}
