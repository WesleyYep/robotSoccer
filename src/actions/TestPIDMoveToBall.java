package actions;

import controllers.PIDController;
import strategy.Action;

public class TestPIDMoveToBall extends Action {

	private PIDController linearPID = new PIDController(1, 1, 1);
	private PIDController angularPID = new PIDController(3, 0, 0);
	
	private double previousBallX;
    private double previousBallY;
//	private double 

    // error allowed in updating goal
    private double ballErrorMargin = 3;

    public TestPIDMoveToBall() {
        // set upper and lower bounds
        angularPID.setMaximumOutput(Math.toRadians(120));
        angularPID.setMinimumOutput(Math.toRadians(-120));
        angularPID.setClip(true);
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

        double correctAngle = 0;

        //  a                   | b
        //                      |
        // 180                  |                       0
        // ----------------------------------------------
        // -180                 |                       0
        //                      |
        //  c                   | d

        if (angleToBall > 90) {
            // quadrant a

            correctAngle = 180 - angleToBall;
            // ball is behind
            bot.linearVelocity = -0.8;
        } else if (angleToBall < 0 && angleToBall > -90) {
            // quadrant d
            correctAngle = Math.abs(angleToBall);
            bot.linearVelocity = 0.8;
        } else if (angleToBall > 0 && angleToBall < 90) {
            // quadrant b
            correctAngle = angleToBall * -1;
            bot.linearVelocity = 0.8;
        } else {
            bot.linearVelocity = -0.8;
            correctAngle = -180 - angleToBall;
        }

        // update goal
        if (difference > ballErrorMargin) {
            angularPID.setResult(Math.toRadians(correctAngle));
            // clear previous error
            angularPID.setTotalError(0);
            System.out.println("Goal changed");
        }

        //linearPID
        // update current state
        angularPID.setInput(Math.toRadians(correctAngle));

        System.out.println("Angle: " + correctAngle);
        // testing purpose
        double result = angularPID.performPID();

        bot.angularVelocity = result;

        // override linear velocity if ball distance is close and is turning
        if (Math.abs(bot.angularVelocity) > 1 && distanceToBall < 20) {
            bot.linearVelocity = 0.1;
        } else if (distanceToBall > 70 && bot.linearVelocity < 0) {
            bot.linearVelocity = -1.3;
        } else if (distanceToBall > 70 && bot.linearVelocity > 0) {
            bot.linearVelocity = 1.3;
        }

        // update previous
        previousBallX = ballX;
        previousBallY = ballY;
	}

}
