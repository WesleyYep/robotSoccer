package actions;

import controllers.PIDController;
import strategy.Action;

public class TestPIDMoveToBall extends Action {

	private PIDController linearPID = new PIDController(1, 1, 1);
	private PIDController angularPID = new PIDController(1, 1, 1);
	
	private double previousAngleToBall;
//	private double 
	
	@Override
	public void execute() {
		// target ball x y values
        double targetX = ballX;
        double targetY = ballY;
        
        // information values
        double distanceToTarget = getDistanceToTarget(bot, targetX, targetY);
        double distanceToBall = getDistanceToTarget(bot, ballX, ballY);
        double angleToTarget = getTargetTheta(bot, targetX, targetY); //degrees
        double angleToBall = getTargetTheta(bot, ballX, ballY); //degrees
        
        // update goal
        if (previousAngleToBall != angleToBall) {
        	angularPID.setResult(angleToBall);
        }
        
        //linearPID
        // update current state
        angularPID.setInput(Math.toRadians(angleToBall));
        
        // testing purpose
        bot.linearVelocity = 1;
        bot.angularVelocity = angularPID.getResult();
        
        // update previous
        previousAngleToBall = angleToBall;
	}

}
