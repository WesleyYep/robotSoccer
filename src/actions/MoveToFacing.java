package actions;

import bot.Robot;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import org.opencv.core.Point;
import strategy.Action;

import java.util.ArrayList;

public class MoveToFacing  extends Action {

    private double error = 2.5;
    private boolean reachFirstSpot = false;
    private boolean reachFinalSpot = false;
    private ArrayList<Point> pointList = new ArrayList<Point>();;
    private int currentPointIndex = 0;
    private boolean once = true;
	private boolean stationary= false;
	private double previousX = 0;
	private double previousY = 0;
    private double highestY = 0;
    private double lowestY = 180;
    //non-static initialiser block
    {
        parameters.put("fixed point1 x", 140);
        parameters.put("fixed point1 y", 30);
        parameters.put("direction facing", 0);
        //parameters.put("error", 2.5);
    }
    @Override
    public void execute() {
    	Robot r = bot;
    	//System.out.println("obs");
        int x = parameters.get("fixed point1 x");
        int y = parameters.get("fixed point1 y");
        double angle = parameters.get("direction facing");
        //setVelocityToTarget(140,30, 0);
        
        double yDiff = y-15*Math.sin(Math.toRadians(angle*-1));
        double xDiff = x-15*Math.cos(Math.toRadians(angle*-1));
        
        
        double robotPosition =  Math.toDegrees(Math.atan2(r.getYPosition() - y, x - r.getXPosition()));
        
        if (angle > 0) {
        	robotPosition = robotPosition + (180-angle);
        }
        else if (angle < 0) {
        	robotPosition += (-180-angle);
        }
        else {
        	robotPosition += 180;
        }
        
        if (robotPosition >= 180) {
        	robotPosition -= 360;
        }
        
        if (robotPosition <= -180) {
        	robotPosition += 360;
        } 
        robotPosition *= -1;
        
        double tempAngle = 0;
        if (Math.abs(robotPosition) > 160) {
        	tempAngle = angle;
        } else {
        	if (robotPosition > 0) {
            	tempAngle = angle+ 35;
            } else {
            	tempAngle = angle - 35;
            }
        }
        
        if (tempAngle >= 180) {
        	tempAngle -= 360;
        }
        
        if (tempAngle <= -180) {
        	tempAngle += 360;
        }
        
        
        double yTurn  = yDiff-20*Math.sin(Math.toRadians(tempAngle*-1));
        double xTurn  = xDiff-20*Math.cos(Math.toRadians(tempAngle*-1));
        if (Math.abs(robotPosition) > 165) {
        	yTurn = yDiff;
        	xTurn = xDiff;
        }
        
        //if (once) {
        pointList.clear();
        pointList.add(new Point(xTurn,yTurn));
	    pointList.add(new Point(xDiff,yDiff));
	    pointList.add(new Point(x,y));
	    pointList.add(new Point(330,35));
	      //  once = false;
       // }
        
        Point currentPoint = pointList.get(currentPointIndex);
        double targetDist = Math.sqrt(Math.pow((currentPoint.x-r.getXPosition()),2) + Math.pow((currentPoint.y-r.getYPosition()),2));
        if (stationary && targetDist >= 3.75) {
        	currentPointIndex = 0;
        	stationary = false;
        }
        
        if (targetDist < 5) {
        	currentPointIndex++;
        	if (currentPointIndex == pointList.size()) {
        		//System.out.println("here");
        		currentPointIndex--;
        	}
        	currentPoint = pointList.get(currentPointIndex);
        }
        //System.out.println(currentPointIndex);
        setVelocityToTarget(currentPoint.x,currentPoint.y);
        
        if (r.getXPosition() == previousX && r.getYPosition() == previousY) {
        	stationary = true;
        }
        //System.out.println(stationary + " " + currentPointIndex);
        previousX = r.getXPosition();
        previousY = r.getYPosition();

        r.linearVelocity = 0.5;
        r.angularVelocity = 7;
        //System.out.println(r.linearVelocity + " " + r.angularVelocity);
        if (r.getYPosition() > highestY) highestY = r.getYPosition();
        if (r.getYPosition() < lowestY) lowestY = r.getYPosition();
        System.out.println(highestY + " " + lowestY);
    }

    /*
    public void setVelocityToTarget(double x, double y, double direction) {
        Robot r = bot;

        double targetDist,robotPosition,robotDirection;
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));
        robotDirection = r.getTheta() - direction;
        
        if (robotDirection >= 270) {
        	robotDirection -= 360;
        }
        
        if (robotDirection <= -270) {
        	robotDirection += 369;
        }
        
        
        robotPosition =  Math.toDegrees(Math.atan2(r.getYPosition() - y, x - r.getXPosition()));
        
        if (direction > 0) {
        	robotPosition = robotPosition + (180-direction);
        }
        else if (direction < 0) {
        	robotPosition += (-180-direction);
        }
        else {
        	robotPosition += 180;
        }
        
        if (robotPosition >= 180) {
        	robotPosition -= 360;
        }
        
        if (robotPosition <= -180) {
        	robotPosition += 360;
        } 
        robotPosition *= -1;
        System.out.println(robotDirection +" " +robotPosition);
        
        
        FunctionBlock fb = loadFuzzy("moveToSpotDirection.fcl");

        fb.setVariable("targetDist", Math.abs(targetDist));
        fb.setVariable("robotPosition", robotPosition);
        fb.setVariable("robotDirection", robotDirection*-1);
        // Evaluate
        fb.evaluate();
        
        
           JFuzzyChart.get().chart(fb);
        // Show output variable's chart
       fb.getVariable("linearVelocity").defuzzify();
       fb.getVariable("angularVelocity").defuzzify();
          JFuzzyChart.get().chart(fb.getVariable("linearVelocity"), fb.getVariable("linearVelocity").getDefuzzifier(), true);
       JFuzzyChart.get().chart(fb.getVariable("angularVelocity"), fb.getVariable("angularVelocity").getDefuzzifier(), true);
        JOptionPane.showConfirmDialog(null, "wait");     
        
        
        double linear  = fb.getVariable("linearVelocity").getValue();
        double angular = fb.getVariable("angularVelocity").getValue();


        r.linearVelocity = linear;
        r.angularVelocity = angular;

       // System.out.println(linear + " " + angular*-1);
        
        if (targetDist <=3.75) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        } 
        return;
    } */
    
    public void setVelocityToTarget(double x, double y) {
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


        /*
        if (r.getXPosition() - ballX > 25|| Math.abs(r.getTheta()) > 60) {
            double tempTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
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
        } */

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
        /*
        obstacleTheta = Math.atan2(r.getYPosition() - obstacleY, obstacleX - r.getXPosition());
        double obsDifference = obstacleTheta - Math.toRadians(r.getTheta());
        if (obsDifference > Math.PI) {
        	obsDifference -= (2 * Math.PI);
        } else if (obsDifference < -Math.PI) {
        	obsDifference += (2 * Math.PI);
        }
        obsDifference = Math.toDegrees(obsDifference);
        obstacleTheta = obsDifference;*/
        
        targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));
       // obstacleDist = Math.sqrt(Math.pow((obstacleX-r.getXPosition()),2) + Math.pow((obstacleY-r.getYPosition()),2));
        double tempAngle = r.getTheta();

        /*
        if (tempAngle < -270) {
            tempAngle = 0;
        }*/
        
        double distBetweenObsAndTarget = Math.sqrt(Math.pow((x-obstacleX),2) + Math.pow((y-obstacleY),2));
        FunctionBlock fb = loadFuzzy("selfMadeObstacle.fcl");
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
//            System.out.println("linear: " + linear + " angular:" + angular*-1
//            					+ " x: " + r.getXPosition() + " y: " + r.getYPosition()
//            					+ " r theta: " + r.getTheta() + " t theta: " + targetTheta
//            					+ " t dist" + targetDist + " time: " + System.currentTimeMillis());

        if (targetDist <=3.75) {
            r.linearVelocity = 0;
            r.angularVelocity = 0;
        }
      //   System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta
        //		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);

        //    System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
//             r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	

        // }
        return;
    }
}