package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import strategy.GameState;

public class testSelfMadeCarry extends Action {

    private double error = 2.5;
    private double oldDistanceToTarget = 0;
    private int countTimesThatSeemStuck = 0;
    private boolean fastForward = false;

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        setVelocityToTarget(ballX,ballY,false,false);
    }

    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
        Robot r = bots.getRobot(index);

        if (GameState.getInstance().isGoingOn("waitingStrikerKicking")) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
            return;
        }

        //check if robot is stuck
        double newTargetDistance = getDistanceToTarget(r);

        if (Math.abs(oldDistanceToTarget - newTargetDistance) < 0.5) {
            // System.out.println(oldDistanceToTarget - newTargetDistance + " count - " + countTimesThatSeemStuck);
            countTimesThatSeemStuck++;
        } else if (r.linearVelocity >= 0){
            countTimesThatSeemStuck = 0;
        }
        if (countTimesThatSeemStuck > 70) {
            r.linearVelocity = -5;
            countTimesThatSeemStuck = 0;
            return;
        } else if (countTimesThatSeemStuck > 50) {
//            System.out.println("stuck!");
            r.linearVelocity = -0.5;
            r.angularVelocity = 5;
            countTimesThatSeemStuck++;
            return;
        }

        double targetDist;

        double targetTheta = Math.atan2(r.getYPosition() - y, x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());
//       System.out.println("initial targetTheta: " + targetTheta + " initial difference " + difference + " current Theta " 
        //     		+ Math.toRadians(r.getTheta()));
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        difference = Math.toDegrees(difference);
        targetTheta = difference;
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));

        boolean isFacingTop = true;
        boolean isTargetTop = true;
        boolean front  = true;
        if (r.getTheta() < 0) {
            isFacingTop = false;
        }

        if (y > r.getYPosition()) {
            isTargetTop = false;
        }

        if (isTargetTop != isFacingTop) {
            front = false;
        }


        if (!front && reverse) {
            if (targetTheta < 0) {
                targetTheta = -180 - targetTheta;
            }
            else if (targetTheta > 0) {
                targetTheta = 180 - targetTheta;
            }
        }

        FunctionBlock fb = loadFuzzy("selfMade.fcl");

        fb.setVariable("targetTheta", targetTheta);
        fb.setVariable("targetDist", Math.abs(targetDist));
        fb.setVariable("direction", r.getTheta());
        fb.setVariable("xPos", r.getXPosition());
        fb.setVariable("yPos", r.getYPosition());
        //      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " "
        //      		+ r.getYPosition() + " targetDist " + targetDist);
        // Evaluate
        fb.evaluate();

        //     JFuzzyChart.get().chart(fb);


        // Show output variable's chart
        fb.getVariable("linearVelocity").defuzzify();
        fb.getVariable("angularVelocity").defuzzify();
        //    JFuzzyChart.get().chart(fb.getVariable("linearVelocity"), fb.getVariable("linearVelocity").getDefuzzifier(), true);
        //     JFuzzyChart.get().chart(fb.getVariable("angularVelocity"), fb.getVariable("angularVelocity").getDefuzzifier(), true);
        //    JOptionPane.showMessageDialog(null, "nwa"); 
        double linear  = fb.getVariable("linearVelocity").getValue();
        double angular = fb.getVariable("angularVelocity").getValue();
        //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());

        //    System.out.println("right :" + right + "left " + left);

        r.linearVelocity = linear;
        r.angularVelocity = angular*-1;

        double angleToGoal = angleDifferenceFromGoal(r.getXPosition(), r.getYPosition(), r.getTheta());

//        if (Math.abs(targetTheta) < 5) {
//            //System.out.println("dribble! ");
//            if (Math.abs(angleToGoal) < Math.PI / 8  && targetDist < 80) { //radians
//                r.angularVelocity += angleToGoal;
//                r.linearVelocity*=3;
//                return;
//            }
//            else if (targetDist < 10) {
//                r.angularVelocity += angleToGoal;
//            }
//        } else if (targetDist <= 7) {
        if (targetDist <= 7) {
            r.linearVelocity = 2;
        }



//            System.out.println("linear: " + linear + " angular:" + angular*-1
//            					+ " x: " + r.getXPosition() + " y: " + r.getYPosition()
//            					+ " r theta: " + r.getTheta() + " t theta: " + targetTheta
//            					+ " t dist" + targetDist + " time: " + System.currentTimeMillis());

        if (!front &&reverse) {
            r.linearVelocity *= -1;
            r.angularVelocity *= -1;
        }
        if (targetDist <=2.5) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        }

        oldDistanceToTarget = getDistanceToTarget(r);

        //  System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta 
        //		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);

        //    System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
//             r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	

        // }
    }

    private double angleDifferenceFromGoal(double x, double y, double theta) {
        double targetTheta = Math.atan2(y - 90, 220 - x);
        double difference = targetTheta - Math.toRadians(theta);
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        return difference;
    }

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(110 - r.getXPosition()) + squared(90 - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}