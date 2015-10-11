package actions;

import controllers.PIDController;
import strategy.Action;

/**
 * Created by chan743 on 8/10/2015.
 */
public class TestPIDMoveToSpot extends Action {

    private PIDController linearPID = new PIDController(1, 1, 1);
    private PIDController angularPID = new PIDController(1, 0, 3);

    private double previousBallX;
    private double previousBallY;
	private double deadZone;
    private boolean first = false;
    // error allowed in updating goal
    private double ballErrorMargin = 3;

    public TestPIDMoveToSpot() {
        // set upper and lower bounds
        linearPID.setMaximumOutput(0.65);
        linearPID.setMinimumOutput(-0.65);
        linearPID.setClip(true);

        // set upper and lower bounds
        angularPID.setMaximumOutput(Math.toRadians(120));
        angularPID.setMinimumOutput(Math.toRadians(-120));
        angularPID.setClip(true);

        // allowable error margin
        deadZone = 0.1;
    }

    @Override
    public void execute() {
        // target ball x y values
        double targetX = ballX;
        double targetY = ballY;

        // information values
        double distanceToTarget = getDistanceToTarget(bot, ballX, ballY) / 100;
        double distanceToBall = getDistanceToTarget(bot, 110, 90);
        double angleToTarget = getTargetTheta(bot, 110, 90); //degrees
        double angleToBall = getTargetTheta(bot, ballX, ballY); //degrees

        //double difference = Math.abs(Math.abs(110) - Math.abs(110) + Math.abs(90) - Math.abs(90));

        double correctAngle = 0;

        if (!first) {
            linearPID.setResult(distanceToTarget);
            linearPID.setTotalError(0);
            first = true;
        }

        linearPID.setInput(distanceToTarget);
        double linResult = linearPID.performPID();

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
            bot.linearVelocity = linResult;
        } else if (angleToBall < 0 && angleToBall > -90) {
            // quadrant d
            correctAngle = Math.abs(angleToBall);
            bot.linearVelocity = -1 * linResult;
        } else if (angleToBall > 0 && angleToBall < 90) {
            // quadrant b
            correctAngle = angleToBall * -1;
            bot.linearVelocity = -1 * linResult;
        } else {
            bot.linearVelocity = linResult;
            correctAngle = -180 - angleToBall;
        }

        // update goal

        //linearPID
        // update current state
        angularPID.setInput(Math.toRadians(correctAngle));

        // testing purpose
        double result = angularPID.performPID();

        bot.angularVelocity = result;

        if (Math.abs(bot.angularVelocity) > 0.5 && distanceToTarget < 0.3) {
            bot.linearVelocity = 0.1;
        } else if (distanceToTarget < 0.5) {
            bot.linearVelocity *= 0.5;
        }

        System.out.println(distanceToTarget);

        if (distanceToTarget < deadZone) {
            bot.linearVelocity = 0;
        }

        System.out.println("Linear velocity: " + bot.linearVelocity);

        // update previous
        previousBallX = ballX;
        previousBallY = ballY;
    }
}
