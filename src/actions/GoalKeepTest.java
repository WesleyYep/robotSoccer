package actions;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import strategy.Action;
import ui.Field;
import bot.Robot;

/**
 * This class is used to test the half-angle goalkeeper formula.
 */

public class GoalKeepTest extends Action {

    private double error = 5;
    private double goalLine = 6;

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        if (r.getXPosition() < goalLine-error || r.getXPosition() >  goalLine+error) {
            setVelocityToTarget(goalLine,Field.OUTER_BOUNDARY_HEIGHT/2, true, false);
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

            //set target Y position
            double targetYPosition;

            if (ballX > 110) { //gk should just be in middle
                targetYPosition = 90;
            } else if (ballY > 130 || ballY < 50 || ballX > 55) { //gk should be half angle position
                targetYPosition = getHalfAnglePosition();
            } else { //ball is very close to goal, so gk should be directly in front
                targetYPosition = ballY;
            }

            if (targetYPosition > r.getYPosition()) {
                isBallTop = false;
            }

            if (isBallTop != isFacingTop) {
                reverseTheta = false;
            }
            // 		System.out.println(ballY);
            if (targetYPosition >= 70 && targetYPosition <= 110 ) {
                setVelocityToTarget(goalLine,targetYPosition, reverseTheta, true);
            }
            else if (targetYPosition < 70) {
                setVelocityToTarget(goalLine,70,reverseTheta, true);
            }
            else if (targetYPosition > 110) {
                setVelocityToTarget(goalLine,110,reverseTheta, true);
            }

        }


    }

    /**
     * This method gets the half angle position between the ball and the two goalposts
     */
    private double getHalfAnglePosition() {
        int goalpostOneY = 70;
        int goalpostTwoY = 110;

        double firstGoalpostTheta = Math.atan2(goalpostOneY - ballY, 0 - ballX);
        double secondGoalpostTheta = Math.atan2(goalpostTwoY - ballY, 0 - ballX);
        double averageTheta = Math.PI;

        if ((firstGoalpostTheta >= 0 && secondGoalpostTheta >= 0) || (firstGoalpostTheta <= 0 && secondGoalpostTheta <= 0)) {
            averageTheta = (firstGoalpostTheta + secondGoalpostTheta)/2.0; //same signs, so just get average
        } else if (firstGoalpostTheta < 0) { //should always be the case if first predicate is not true
            firstGoalpostTheta += 2*Math.PI;
            averageTheta = (firstGoalpostTheta + secondGoalpostTheta)/2.0; //same signs, so just get average
            if (averageTheta > Math.PI) {
                averageTheta -= 2*Math.PI;
            }
        } else {
            System.out.println("There is an error in the half angle calculations.");
        }

//        if (ballY < 90) {
//            return ballY + ballX * Math.tan(averageTheta);
 //       } else {
        System.out.println(averageTheta);
        System.out.println(ballY - ballX * Math.tan(averageTheta));
            return ballY - ballX * Math.tan(averageTheta);
  //      }
    }

    public void setVelocityToTarget(double x, double y, boolean front, boolean onGoalLine) {
        Robot r = bots.getRobot(index);

        if (onGoalLine) {
            r.angularVelocity = 0;
            if (Math.abs(r.getYPosition() - y) > 5) {
                r.linearVelocity = 0.3;
            } else {
                r.linearVelocity = 0.1;
            }
        } else {
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
            fb.setVariable("obstacleTheta", Math.PI);
            fb.setVariable("obstacleDist", 10);
            fb.setVariable("targetTheta", Math.toRadians(targetTheta));
            fb.setVariable("targetDist", targetDist);

            // Evaluate
            fb.evaluate();

            // Show output variable's chart
            fb.getVariable("angSpeedError").defuzzify();

            r.angularVelocity = fb.getVariable("angSpeedError").getValue()*0.5;
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

    /**
     * This method is used to check if robot is outside of the goal box.
     */
    private void checkRobotPosition(double x, double y) {
        Robot r = bots.getRobot(index);
        int xError = 10;
        int yError = 2;
        if (r.getXPosition() >= x-xError && r.getXPosition() <= x+xError && (r.getYPosition() >= y-yError) && r.getYPosition() <= y+yError) {
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
        return "GoalKeeperTest";
    }
}
