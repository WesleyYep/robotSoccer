package actions;

import controllers.PIDController;
import strategy.Action;

/**
 * Created by chan743 on 8/10/2015.
 */
public class TestPIDMoveToSpot extends Action {

    private PIDController linearPID = new PIDController(1, 1, 1);
    private PIDController angularPID = new PIDController(1, 0, 0);

    private double previousBallX;
    private double previousBallY;
	private double deadZone;
    private double counter = 0;
    // error allowed in updating goal
    private double ballErrorMargin = 3;

    public TestPIDMoveToSpot() {
        // set upper and lower bounds
        linearPID.setMaximumOutput(0.8);
        linearPID.setMinimumOutput(-0.8);
        linearPID.setClip(true);

        // set upper and lower bounds
        angularPID.setMaximumOutput(Math.toRadians(120));
        angularPID.setMinimumOutput(Math.toRadians(-120));
        angularPID.setClip(true);

        // allowable error margin
        deadZone = 0.05;
    }

    @Override
    public void execute() {
        // target ball x y values
        double targetX = ballX;
        double targetY = ballY;

        // information values
        double distanceToTarget = getDistanceToTarget(bot, 110, 90) / 100;
        double distanceToBall = getDistanceToTarget(bot, 110, 90);
        double angleToTarget = getTargetTheta(bot, 110, 90); //degrees
        double angleToBall = getTargetTheta(bot, 110, 90); //degrees

        double difference = Math.abs(Math.abs(110) - Math.abs(110) + Math.abs(90) - Math.abs(90));

        double correctAngle = 0;

        linearPID.setResult(distanceToTarget);
        linearPID.setTotalError(0);

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

//        if (bot.getXPosition() > 110) {
//            bot.linearVelocity = -1 * linResult;
//        } else {
//            bot.linearVelocity = linResult;
//        }

        if (distanceToTarget < deadZone) {
            bot.linearVelocity = 0;
        }
        System.out.println("X position" + bot.getXPosition());
        System.out.println("Distance to target: " + distanceToTarget);

        // update goal
//        if (difference > ballErrorMargin) {
//            angularPID.setResult(Math.toRadians(correctAngle));
//            // clear previous error
//            angularPID.setTotalError(0);
//
//            linearPID.setResult(distanceToTarget);
//            linearPID.setTotalError(0);
//
//            System.out.println("Goal changed");
//        }

        //System.out.println("Distance to ball" + distanceToBall);

        //linearPID
        // update current state
        angularPID.setInput(Math.toRadians(correctAngle));

        // testing purpose
        double result = angularPID.performPID();

        bot.angularVelocity = result;

        //bot.linearVelocity = linResult;
        //System.out.println("Linear velocity : " + bot.linearVelocity);
        // override linear velocity if ball distance is close and is turning
//        if (Math.abs(bot.angularVelocity) > 0.8 && distanceToBall < 30) {
//            bot.linearVelocity = linResult;
//        } else if (distanceToBall > 70 && bot.linearVelocity < 0) {
//            bot.linearVelocity = -1 * linResult;
//        } else if (distanceToBall > 70 && bot.linearVelocity > 0) {
//            bot.linearVelocity = linResult;
//        } else {
//            bot.linearVelocity = linResult;
//        }

        // update previous
        previousBallX = ballX;
        previousBallY = ballY;
        counter++;
    }
}
