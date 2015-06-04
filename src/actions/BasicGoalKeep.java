package actions;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import ui.Field;
import bot.Robot;

public class BasicGoalKeep extends Action {

	private double error = 2.5;
	//	private double goalLine = 214;
	private boolean fixPosition = false;
	private double lastBallX = 0;
	private double lastBallY = 0;
	private double lastBallX2 = 0;
	private double lastBallY2 = 0;

	//non-static initialiser block
	{
		parameters.put("goalLine", 7);
	}

	@Override
	public void execute() {
		Robot r = bots.getRobot(index);
		double goalLine = parameters.get("goalLine");
		//double goalLine = 213;
		//System.out.println(goalLine);
		if (r.getXPosition() < goalLine-error || r.getXPosition() >  goalLine+error) {
			int targetPos = 0;
			if (ballY >= 70 && ballY <= 110 ) {
				targetPos = (int) ballY;
			}
			else if (ballY < 70) {
				targetPos = 70;
			}
			else if (ballY > 110) {
				targetPos = 110;
			}
			setVelocityToTarget(goalLine,targetPos, false,false);
			fixPosition = true;
		}
		else if (fixPosition) {;
			if ( ( r.getTheta() > 90+5 && r.getTheta() <= 180)|| (r.getTheta() <= 0 && r.getTheta() > -90+5)) {
			//	System.out.println("turning negative: " + r.getTheta() );
				r.angularVelocity = -Math.PI/5;
				if (r.getTheta() > 120 || r.getTheta() > -60) {
					r.angularVelocity = -Math.PI/3;
				}
				r.linearVelocity = 0;
			}
			else if ( (r.getTheta() < 90-5 && r.getTheta() >= 0) || (r.getTheta() < -90-5 && r.getTheta() >= -180)) {
			//	System.out.println("turning positive: " + r.getTheta() );
				r.angularVelocity = Math.PI/5;
				if (r.getTheta() < 60  || r.getTheta() < -120) {
					r.angularVelocity = Math.PI/3;
				}
				r.linearVelocity = 0;
			}
			else {
				fixPosition = false;
			}
		}
		else{
			//		System.out.println(ballY);
			//		System.out.println("front: " + reverseTheta);

			double yDiff = Math.round(ballY-lastBallY);
			double xDiff = Math.round(ballX-lastBallX);
			double constant;

			boolean goingHorizontal = false;
			boolean goingVertical = false;
			double trajectoryY = 0;

			if (yDiff == 0) {
				goingHorizontal = true;
			}

			if (xDiff == 0) {
				goingVertical = true;
			}

			if (!(goingVertical || goingHorizontal)) {
				constant = ballY - ((yDiff/xDiff)*ballX);
				//trajectoryY = ((yDiff/xDiff)*goalLine) + constant;

				double sumY = ballY + lastBallY + lastBallY2;
				double sumX = ballX + lastBallX + lastBallX2;

				double sumY2 = (ballY*ballY) + (lastBallY*lastBallY) + (lastBallY2*lastBallY2);
				double sumX2 = (ballX*ballX) + (lastBallX*lastBallX) + (lastBallX2*lastBallX2);

				double sumXY = (ballX*ballY) + (lastBallX*lastBallY) + (lastBallY2*lastBallX2);

				double xMean = sumX/3;
				double yMean = sumY/3;

				double slope = (sumXY - sumX * yMean) / (sumX2 - sumX * xMean);

				double yInt = yMean - slope* xMean;


				if (goalLine < 110 ) {
					trajectoryY = (slope*(goalLine+3.75)) + yInt;
				}
				else {
					trajectoryY = (slope*(goalLine-3.75)) + yInt;
				}


			}

			boolean goal = false;
			boolean midSection = false;
			if (goalLine > 110) {
				goal = ballX < 110;
				midSection = ballX < 160;
			}
			else {
				midSection = ballX > 60;
				goal =ballX >=110;
			}



			if (goal) {
				setVelocityToTarget(goalLine,Field.OUTER_BOUNDARY_HEIGHT/2, true,false);
			}

			else if (midSection) {
				setVelocityToTarget(goalLine,getHalfAnglePosition(), true,false);
			}
			else {
				if (goingVertical || goingHorizontal) {
					if (ballY >= 70 && ballY <= 110 ) {
						setVelocityToTarget(goalLine,ballY, true,true);
					}
					else if (ballY < 70) {
						setVelocityToTarget(goalLine,70,true,true);
					}
					else if (ballY > 110) {
						setVelocityToTarget(goalLine,110,true,true);
					}
				}
				else if (!(goingVertical || goingHorizontal)) {

					boolean direction;
					if (goalLine < 110) {
						direction = xDiff < 0;
					}
					else {
						direction = xDiff > 0;
					}
					if (direction) {
						//ball going toward the goal
						if (trajectoryY >= 70 && trajectoryY <=110) {
							if (r.getYPosition()>= (trajectoryY-2) && r.getYPosition() <=(trajectoryY+2)) {
								setVelocityToTarget(goalLine,r.getYPosition(),true,true);
							}
							else {
								setVelocityToTarget(goalLine,trajectoryY,true,true);
							}
						}
						else {
							//
							if ( (trajectoryY> 110 && ballY > 110) || (trajectoryY<= 110 && ballY <= 110)) {
								// System.out.println("same side");
								if (ballY >= 70 && ballY <= 110 ) {
									setVelocityToTarget(goalLine,ballY, true,true);
								}
								else if (ballY < 70) {
									setVelocityToTarget(goalLine,70,true,true);
								}
								else if (ballY > 110) {
									setVelocityToTarget(goalLine,110,true,true);
								}
							}
							else {
								// System.out.println("oppo side");
								setVelocityToTarget(goalLine,Field.OUTER_BOUNDARY_HEIGHT/2,true,true);
							}
						}
					}
					else {
						if (ballY >= 70 && ballY <= 110 ) {
							setVelocityToTarget(goalLine,ballY, true,true);
						}
						else if (ballY < 70) {
							setVelocityToTarget(goalLine,70,true,true);
						}
						else if (ballY > 110) {
							setVelocityToTarget(goalLine,110,true,true);
						}
					}


					/*
					boolean direction;
					if (goalLine < 110) {
						direction = xDiff < 0;
					}
					else {
						direction = xDiff > 0;
					}
					double tempBallX = ballX;
					if (ballX < 30) tempBallX = 30;
					double proportion = 1-((tempBallX-30)/(110-30));
					double dist = 0;
					//if (!direction) proportion *= -1;
					if (trajectoryY >= 70 && trajectoryY <= 110 ) {
						dist = 90-((90-trajectoryY)*proportion);
					}
					else if (trajectoryY < 70) {
						dist = 90-((90-70)*proportion);
					}
					else if (trajectoryY > 110) {
						dist = 90-((90-110)*proportion);
					}
					if (!direction) {
						if (ballY >= 70 && ballY <= 110 ) {
							setVelocityToTarget(goalLine,ballY, true,true);
						}
						else if (ballY < 70) {
							setVelocityToTarget(goalLine,70,true,true);
						}
						else if (ballY > 110) {
							setVelocityToTarget(goalLine,110,true,true);
						}
					}
					else {
						setVelocityToTarget(goalLine,dist,true,true);
					} */
				//	System.out.println( "proportion: " + proportion + " dist: " + dist + " trajectoryY: " + trajectoryY + " direction: " + direction);
				}
				else {
					setVelocityToTarget(r.getXPosition(),r.getYPosition(),true,true);
				}
			}
			
			
		
		}
		lastBallX = ballX;
		lastBallY = ballY;
		lastBallX2 = lastBallX;
		lastBallY2 = lastBallY;
	}

	public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
		Robot r = bots.getRobot(index);
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

		String filename = "newFuzzy.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);
      		
             /*
             if (onGoalLine) {
            	targetTheta = 0;
             } 
             */
		//if (targetDist <= 3.75) targetDist = 0;
		if (targetDist <=1.7) {
			targetDist = 0;
			targetTheta = 0;
			fixPosition = true;
		}
		// targetTheta = Math.round(targetTheta/5)*5;

		fb.setVariable("angleError", targetTheta);
		fb.setVariable("distanceError", Math.abs(targetDist));
		//      System.out.println("x y: " + x + " " + y + " r.x r.y " + r.getXPosition() + " "
		//      		+ r.getYPosition() + " targetDist " + targetDist);
		// Evaluate
		fb.evaluate();
             /*
             JFuzzyChart.get().chart(fb);
              JOptionPane.showMessageDialog(null, "nwa"); */

		// Show output variable's chart
		fb.getVariable("rightWheelVelocity").defuzzify();
		fb.getVariable("leftWheelVelocity").defuzzify();
		//  JFuzzyChart.get().chart(fb.getVariable("leftWheelVelocity"), fb.getVariable("leftWheelVelocity").getDefuzzifier(), true);
		//   JFuzzyChart.get().chart(fb.getVariable("rightWheelVelocity"), fb.getVariable("rightWheelVelocity").getDefuzzifier(), true);
		double right  = Math.toRadians(fb.getVariable("rightWheelVelocity").getValue());
		double left = Math.toRadians(fb.getVariable("leftWheelVelocity").getValue());
		//    System.out.println(" raw right :" + fb.getVariable("rightWheelVelocity").getValue() + " raw left " + fb.getVariable("leftWheelVelocity").getValue());
		double linear =  (right+left)/2;
		double angular = (right-left)*(2/0.135);
		//    System.out.println("right :" + right + "left " + left);

		r.linearVelocity = linear*2.5;
		r.angularVelocity = angular*1;

		if (!front &&reverse) {
			r.linearVelocity *= -1;
			r.angularVelocity *= -1;
		}
		//      System.out.println("linear velocity " + r.linearVelocity + " angular velocity" + r.angularVelocity + "angleError: " + targetTheta
		//  		 + " r.angle: " + r.getTheta() + " dist: " + targetDist);
		//	System.out.println("x:" + x + " y: " + y + " r.x: " + r.getXPosition() + " r.y" + r.getYPosition());
		//     System.out.println("linear: " + r.linearVelocity + " y: " + y + " theta: " + targetTheta + " dist: " + targetDist);
		//r.linearVelocity = 0;
//            r.angularVelocity = 0;
//        	

		// }
	}

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
		//     System.out.println(averageTheta);
		//     System.out.println(ballY - ballX * Math.tan(averageTheta));

		//extra;
		double bonus;

		if (ballY < 35) {
			bonus = -5;
		}
		else if (ballY > 145) {
			bonus = 5;
		}
		else {
			bonus = 0;
		}

		return ballY - ballX * Math.tan(averageTheta) + bonus;
		//      }
	}

}