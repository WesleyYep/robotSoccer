package actions;

import controllers.PIDController;
import strategy.Action;

public class TestPIDMoveToBall extends Action {

	private PIDController linearPID = new PIDController(1, 1, 1);
	private PIDController angularPID = new PIDController(2, 0, 0);
	
	private double previousBallX;
    private double previousBallY;
//	private double 

    // error allowed in updating goal
    private double ballErrorMargin = 3;

    public TestPIDMoveToBall() {
        // set upper and lower bounds
        angularPID.setMaximumOutput(Math.toRadians(180));
        angularPID.setMinimumOutput(Math.toRadians(-180));
        angularPID.setClip(false);
    }

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

        double difference = Math.abs(Math.abs(previousBallY) - Math.abs(ballY) + Math.abs(previousBallX) - Math.abs(ballX));

        // update goal
        if (difference > ballErrorMargin) {
            angularPID.setResult(Math.toRadians(angleToBall));
            // clear previous error
            angularPID.setTotalError(0);
            System.out.println("Goal changed");
        }

        //linearPID
        // update current state
        angularPID.setInput(Math.toRadians(angleToBall));

        System.out.println("Angle: " + angleToBall);
        // testing purpose
        double result = angularPID.performPID();

        if (Math.abs(result) < 0.7) {
            bot.angularVelocity = 0;
        } else {
            bot.angularVelocity = result;
        }

//        if (result < 0) {
//            bot.linearVelocity = -0.5;
//        } else {
//            bot.linearVelocity = 0.5;
//        }
        bot.linearVelocity = 0.5;
        System.out.println("Angular v: " + bot.angularVelocity);

        // update previous
        previousBallX = ballX;
        previousBallY = ballY;
	}

}
