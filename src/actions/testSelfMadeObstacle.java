package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;

public class testSelfMadeObstacle extends Action {

    private double error = 2.5;
    private double highestTheta = -1000;
    private double lowestTheta = 1000;

    //non-static initialiser block
    {
        parameters.put("fixed point1 x", 180);
        parameters.put("fixed point1 y", 90);
        //parameters.put("error", 2.5);
    }
    @Override
    public void execute() {
    	//System.out.println("obs");
        int x = parameters.get("fixed point1 x");
        int y = parameters.get("fixed point1 y");
        setVelocityToTarget(x,y,true,false);
    }

    public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
        Robot r = bot;


        double obstacleTheta = 180, obstacleDist = 220, obstacleX = 0, obstacleY = 0;

        for (int i=0; i<5; i++) {
        	if (!teamRobots.getRobot(i).equals(r)) {
        		Robot obs = teamRobots.getRobot(i);
        		double tempTheta = Math.atan2(r.getYPosition() - obs.getYPosition(), obs.getXPosition() - r.getXPosition());
                double tempDifference = tempTheta - Math.toRadians(r.getTheta());
                if (tempDifference > Math.PI) {
                	tempDifference -= (2 * Math.PI);
                } else if (tempDifference < -Math.PI) {
                	tempDifference += (2 * Math.PI);
                }
                tempDifference = Math.toDegrees(tempDifference);
                tempTheta = tempDifference;
                
                if (Math.abs(tempTheta) < 50) {
                	double tempDist = Math.sqrt(Math.pow((obs.getXPosition()-r.getXPosition()),2) + Math.pow((obs.getYPosition()-r.getYPosition()),2));
                	if (tempDist < obstacleDist) {
                		obstacleDist = tempDist;
                		obstacleTheta = tempTheta;
                        obstacleY = obs.getYPosition();
                        obstacleX = obs.getXPosition();
                	}
                }
        	}
        }

        for (int i=0; i<5; i++) {
            Robot obs = opponentRobots.getRobot(i);
            double tempTheta = Math.atan2(r.getYPosition() - obs.getYPosition(), obs.getXPosition() - r.getXPosition());
            double tempDifference = tempTheta - Math.toRadians(r.getTheta());
            if (tempDifference > Math.PI) {
                tempDifference -= (2 * Math.PI);
            } else if (tempDifference < -Math.PI) {
                tempDifference += (2 * Math.PI);
            }
            tempDifference = Math.toDegrees(tempDifference);
            tempTheta = tempDifference;

            if (Math.abs(tempTheta) < 50) {
                double tempDist = Math.sqrt(Math.pow((obs.getXPosition()-r.getXPosition()),2) + Math.pow((obs.getYPosition()-r.getYPosition()),2));
                if (tempDist < obstacleDist) {
                    obstacleDist = tempDist;
                    obstacleTheta = tempTheta;
                    obstacleY = obs.getYPosition();
                    obstacleX = obs.getXPosition();
                }
            }
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

        double tempAngle = r.getTheta();
        
        double distBetweenObsAndTarget = Math.sqrt(Math.pow((x-obstacleX),2) + Math.pow((y-obstacleY),2));
        FunctionBlock fb = loadFuzzy("fuzzy/selfMadeObstacle.fcl");
        fb.setVariable("distBetweenObsAndTarget", distBetweenObsAndTarget);
        fb.setVariable("obstacleDist", obstacleDist);
        fb.setVariable("obstacleTheta", obstacleTheta);
        fb.setVariable("targetTheta", targetTheta);
        fb.setVariable("targetDist", Math.abs(targetDist));
        fb.setVariable("direction", tempAngle);
        fb.setVariable("xPos", r.getXPosition());
        fb.setVariable("yPos", r.getYPosition());
        //      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " "
        //      		+ r.getYPosition() + " targetDist " + targetDist);
        // Evaluate
        fb.evaluate();

        /*
           JFuzzyChart.get().chart(fb);
        // Show output variable's chart
       fb.getVariable("linearVelocity").defuzzify();
       fb.getVariable("angularVelocity").defuzzify();
          JFuzzyChart.get().chart(fb.getVariable("linearVelocity"), fb.getVariable("linearVelocity").getDefuzzifier(), true);
       JFuzzyChart.get().chart(fb.getVariable("angularVelocity"), fb.getVariable("angularVelocity").getDefuzzifier(), true);
        JOptionPane.showMessageDialog(null, "nwa"); */

        double linear  = fb.getVariable("linearVelocity").getValue();
        double angular = fb.getVariable("angularVelocity").getValue();
        //    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());

        //    System.out.println("right :" + right + "left " + left); 

        r.linearVelocity = linear;
        r.angularVelocity = angular*-1;

        if (targetDist <=3.75) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        }

        // }
        return;
    }

    private double getDistanceToTarget(Robot r) {
        return Math.sqrt(squared(110 - r.getXPosition()) + squared(90 - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}