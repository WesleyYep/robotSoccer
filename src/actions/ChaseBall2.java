package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import ui.Field;
import utils.Geometry;

/**
 * Created by Wesley on 21/01/2015.
 */
public class ChaseBall2 extends Action{

    private int goalX = 220;
    private int goalY = 90;

    @Override
    public void execute() {
        setVelocityToTarget(predX, predY, true);
    }

    public void setVelocityToTarget(double x, double y, boolean front) {
        Robot r = bots.getRobot(index);
        double targetDist;
        double targetTheta;

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

        if (targetDist < 20 &&  targetTheta < 10) {
            double goalTheta = Math.atan2(r.getYPosition() - goalY, goalX - r.getXPosition());
            double goalDifference = goalTheta - Math.toRadians(r.getTheta());

            r.angularVelocity = 2*goalDifference;// / (goalDist);
            r.linearVelocity = 1;
        } else {

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

            r.angularVelocity = fb.getVariable("angSpeedError").getValue() * 0.5;
            if (r.angularVelocity > 3) {
                r.angularVelocity = 3;
            } else if (r.angularVelocity < -3) {
                r.angularVelocity = -3;
            }
            //r.linearVelocity = targetDist/100.0;
            r.linearVelocity = 1;

            if (isCloseToWall()) {
                if (Math.abs(targetTheta) < 10) {
                    r.linearVelocity = 0.5;
                } else {
                    r.linearVelocity = 0;
                }
            }

            if (!front) {
                r.linearVelocity *= -1;
            }

            if (targetDist < 20 && Math.abs(targetTheta) < 5) {
                double angle = angleDifferenceFromGoal(r.getXPosition(), r.getYPosition(), r.getTheta());
                if (Math.abs(angle) < Math.PI / 4) {
            //       System.out.println("dribble! ");
                    r.angularVelocity += 2*angle;
                }
            }

        }
    }

    private double angleDifferenceFromGoal(double x, double y, double theta) {
        double targetTheta = Math.atan2(y - goalY, goalX - x);
        double difference = targetTheta - Math.toRadians(theta);
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        return difference;
    }

    private boolean isCloseToWall() {
        Robot r = bots.getRobot(index);
        if (r.getYPosition() >= -20 && r.getYPosition() <= 20 ) {
            return true;
        }
        else if (r.getYPosition() >= Field.OUTER_BOUNDARY_HEIGHT-20 && r.getYPosition() <= Field.OUTER_BOUNDARY_HEIGHT+20) {
            return true;
        }
        else if (r.getXPosition() >= -20 && r.getXPosition() <= 20 ) {
            return true;
        }
        else if (r.getXPosition() >= Field.OUTER_BOUNDARY_WIDTH-20 && r.getXPosition() <= Field.OUTER_BOUNDARY_WIDTH+20) {
            return true;
        }

        return false;
    }


    protected double squared (double x) {
        return x * x;
    }
}