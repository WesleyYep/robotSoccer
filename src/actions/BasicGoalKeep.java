package actions;

import bot.Robot;
import data.Coordinate;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import ui.Field;

import java.util.ArrayList;
import java.util.List;

public class BasicGoalKeep extends Action {

	private double error = 3.75;
	//	private double goalLine = 214;
	private boolean fixPosition = false;
	private double lastBallX = 0;
	private double lastBallY = 0;
	private double lastBallX2 = 0;
	private double lastBallY2 = 0;
	private List<Double> xPositionQueue = new ArrayList<Double>();
	//non-static initialiser block
	{
		parameters.put("goalLine", 5);
		parameters.put("topPoint", 70);
		parameters.put("bottomPoint", 110);
		//parameters.put("error", 2.5);
	}

	@Override
	public void execute() {
		Robot r = bot;
		double goalLine = parameters.get("goalLine");
		//double error = parameters.get("error");
		int topPoint = parameters.get("topPoint");
		int bottomPoint = parameters.get("bottomPoint");
		//double goalLine = 213;
		//System.out.println(goalLine);



		double targetTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
		double difference = targetTheta - Math.toRadians(r.getTheta());
		//some hack to make the difference -Pi < theta < Pi
		if (difference > Math.PI) {
			difference -= (2 * Math.PI);
		} else if (difference < -Math.PI) {
			difference += (2 * Math.PI);
		}
		difference = Math.toDegrees(difference);
		targetTheta = difference;

		//finding ball trajectory and resulty
		double yDiff = Math.round(ballY-lastBallY);
		double xDiff = Math.round(ballX-lastBallX);
		double constant;

		boolean goingHorizontal = false;
		boolean goingVertical = false;
		double trajectoryY = 0;

		if (yDiff == 0) {
			trajectoryY = ballY;
			goingHorizontal = true;
		}

		if (xDiff == 0) {
			trajectoryY = ballY;
			goingVertical = true;
		}

		if (!(goingVertical || goingHorizontal)) {
			constant = ballY - ((yDiff/xDiff)*ballX);
			//trajectoryY = ((yDiff/xDiff)*goalLine) + constant;

			double sumY = ballY + lastBallY + lastBallY2;
			double sumX = ballX + lastBallX + lastBallX2;

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
				trajectoryY = (slope * (goalLine - 3.75)) + yInt;
			}

		}



		boolean goal = false;
		boolean midSection = false;
		double boundary1 = 110, boundary2 = 35;
		if (goalLine > 110) {
			goal = ballX < goalLine + 100;
			midSection = ballX < goalLine + 150;
		}
		else {
			midSection = ballX > goalLine + 50;
			goal =ballX >=100+goalLine;
		}


		double area1Weighting = 0; // > 110
		double area2Weighting = 0; // > 50
		double area3Weighting = 0; // > 0
		double resultY = 0;
		double area1Y = Field.OUTER_BOUNDARY_HEIGHT/2;
		double area2Y = 0;
		double area3Y = 0;

		//working out the weighting for each area to ensure smooth transition for the goal keeper
		//area 1
		if (ballX > 120) {
			area1Weighting = 1;
		} else if (ballX <= 120 && ballX >= 100) {
			area1Weighting = (ballX - 100.0)/(120.0-100.0);
		} else {
			area1Weighting = 0;
		}


		//area 2
		if (ballX > 120) {
			area2Weighting = 0;
		} else if (ballX <= 120 && ballX >= 100) {
			area2Weighting = (ballX-120.0) /(100.0-120.0);
		} else if (ballX < 100 && ballX > 45) {
			area2Weighting = 1;
		} else if (ballX <= 45 && ballX >= 35) {
			area2Weighting = (ballX - 35.0) / (45.0-35.0);
		} else {
			area2Weighting = 0;
		}

		//if ball moving
		if (!goingHorizontal || !goingVertical ) {
			if ((trajectoryY > Field.OUTER_BOUNDARY_HEIGHT/2 && ballY > Field.OUTER_BOUNDARY_HEIGHT/2) || (trajectoryY <= Field.OUTER_BOUNDARY_HEIGHT/2 && ballY <= Field.OUTER_BOUNDARY_HEIGHT/2)) {
				// System.out.println("same side");
				area2Y = 80 + (((100.0-80.0)/(180.0-0.0))*ballY);
			} else {
				// System.out.println("oppo side");
				area2Y = Field.OUTER_BOUNDARY_HEIGHT/2;
			}
		} else {
			//ball idle/going vertical/going horizontal
			area2Y = 80 + (((100.0-80.0)/(180.0-0.0))*ballY);
		}

		//area 3
		if (ballX > 45) {
			area3Weighting = 0;
		} else if (ballX <= 45 && ballX >= 35) {
			area3Weighting = (ballX -45.0) /(35.0-45.0);
		} else {
			area3Weighting = 1;
		}

		boolean direction;
		if (goalLine < 110) {
			direction = xDiff < 0;
		} else {
			direction = xDiff > 0;
		}

		if (direction) {
			//ball going toward the goal
			if (trajectoryY >= topPoint-3 && trajectoryY <= bottomPoint+3) {
					/*
					if (r.getYPosition() >= (trajectoryY - 2) && r.getYPosition() <= (trajectoryY + 2)) {
						area3Y = r.getYPosition();
					} else {
						if (trajectoryY < topPoint) {
							area3Y = topPoint;
;						} else if (trajectoryY > bottomPoint) {
							area3Y = bottomPoint;
						} else {
							area3Y = trajectoryY;
						}
					} */
				area3Y = trajectoryY;
				//System.out.println("toward the goal");
			} else {
				//ball travelling in the same side of board
				if ((trajectoryY > Field.OUTER_BOUNDARY_HEIGHT/2 && ballY > Field.OUTER_BOUNDARY_HEIGHT/2) || (trajectoryY <= Field.OUTER_BOUNDARY_HEIGHT/2 && ballY <= Field.OUTER_BOUNDARY_HEIGHT/2)) {
					// System.out.println("same side");
					if (ballY >= topPoint && ballY <= bottomPoint) {
						area3Y = ballY;
					} else if (ballY < topPoint) {
						area3Y = topPoint;
					} else if (ballY > bottomPoint) {
						area3Y = bottomPoint;
					}
				} else {
					// System.out.println("oppo side");
					area3Y = Field.OUTER_BOUNDARY_HEIGHT / 2;
				}
			}
		} else {
			//away from the goal
			if (ballY >= topPoint && ballY <= bottomPoint) {
				area3Y = ballY;
			} else if (ballY < topPoint) {
				area3Y = topPoint;
			} else if (ballY > bottomPoint) {
				area3Y = bottomPoint;
			}
		}
		resultY = area1Y*area1Weighting + area2Y*area2Weighting + area3Y*area3Weighting;

		//first phase getting the robot to the goal line
		if (r.getXPosition() < goalLine-error || r.getXPosition() >  goalLine+error){
			//System.out.println("getting to the goal");
			double targetPos = 0;
			if (ballY >= topPoint && ballY <= bottomPoint ) {
				targetPos = ballY;
			} else if (ballY < topPoint) {
				targetPos = topPoint;
			} else if (ballY > bottomPoint) {
				targetPos = bottomPoint;
			}
			setVelocityToTarget(goalLine,targetPos, true,false);
			//MoveToSpot.move(r,new Coordinate((int)goalLine,resultY),1,false);
			//code start for getting stuck in the inner goal area
			//finding the angle in the inner goal area so the robot can get out of the inner area
			/*
			 ---           -----
			   |\\      //|
			   | \\    // |
			   |  \\  //  |
			   |___\\//___|
				   ^  ^
				   Angles
			 */
			/*
			double pointY1 = Field.OUTER_BOUNDARY_HEIGHT/2 - Field.INNER_GOAL_AREA_HEIGHT/2 + 3.5;
			double pointY2 = Field.OUTER_BOUNDARY_HEIGHT/2 + Field.INNER_GOAL_AREA_HEIGHT/2 - 3.5;

			double angleLeft = 0;
			double angleRight = 0;

			double roundedXPosition = Math.round(r.getXPosition()*10)/10;
			double roundedYPosition = Math.round(r.getYPosition()*10)/10;

			if (roundedYPosition == pointY1) {
				//System.out.println("angle left is 0");
				angleLeft = 0;
				double adjacent = Math.sqrt(Math.pow((0-(-15)),2) + Math.pow((roundedYPosition-roundedYPosition),2));
				double hypothenus = Math.sqrt(Math.pow((0 - (-15)), 2) + Math.pow((pointY2 - roundedYPosition), 2));

				angleRight = Math.acos(adjacent/hypothenus);
			} else if ( roundedYPosition == pointY2) {
				//System.out.println("angle right is 0");
				angleRight = 0;
				double adjacent = Math.sqrt(Math.pow((roundedXPosition-(-15)),2) + Math.pow((roundedYPosition-roundedYPosition),2));
				double hypothenus = Math.sqrt(Math.pow((0 - (-15)), 2) + Math.pow((pointY1 - roundedYPosition), 2));
				angleLeft = Math.acos(adjacent/hypothenus);
			} else {
				//System.out.println("both calculating");
				double leftAdjacent = Math.sqrt(Math.pow((roundedXPosition-(-15)),2) + Math.pow((roundedYPosition-roundedYPosition),2));
				double leftHypothenus = Math.sqrt(Math.pow((0 - (-15)), 2) + Math.pow((pointY1 - roundedYPosition), 2));
				angleLeft = Math.acos(leftAdjacent/leftHypothenus);

				double rightAdjacent = Math.sqrt(Math.pow((roundedXPosition-(-15)),2) + Math.pow((roundedYPosition-roundedYPosition),2));
				double rightHypothenus = Math.sqrt(Math.pow((0 - (-15)), 2) + Math.pow((pointY2 - roundedYPosition), 2));

				angleRight = Math.acos(rightAdjacent/rightHypothenus);
			}

			angleRight = Math.toDegrees(angleRight)+10;
			angleLeft  = Math.toDegrees(angleLeft)+10;
			//System.out.println("angle right: " + angleRight + " angle left: " + angleLeft + "robot theta: " + r.getTheta() );

			if (r.getXPosition() <= 1 ) {
				//System.out.println("here");
				if (r.getXPosition() > 0) {
					double adjacent =  Math.sqrt(Math.pow((roundedXPosition-(0)),2) + Math.pow((roundedYPosition-roundedYPosition),2));
					double leftHypotenuse =  Math.sqrt(Math.pow((roundedXPosition-(0)),2) + Math.pow((roundedYPosition-pointY1),2));
					double rightHypotenuse =  Math.sqrt(Math.pow((roundedXPosition-(0)),2) + Math.pow((roundedYPosition-pointY2),2));

					double tempLeftAngle = Math.toDegrees(Math.acos(adjacent/leftHypotenuse));
					double tempRightAngle = Math.toDegrees(Math.acos(adjacent/rightHypotenuse));

					if ( r.getTheta() > (55) || r.getTheta() < -1*(55)) {
						r.linearVelocity = 0;
					}
				} else {
					if ( r.getTheta() > angleLeft || r.getTheta() < -1*angleRight ) {
						r.linearVelocity = 0;
					}
				}
			}
				*/
			//code end for getting stuck in the inner goal area
			if (bot.isStuck(new Coordinate(bot.getXPosition(), bot.getYPosition()))) {
				bot.linearVelocity *=-2;
			}
			fixPosition = true;
		}
		//correct it's position
		else if (fixPosition) {
			//System.out.println("fixing position " + fixPosition );
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
		//ball tracking
		else{
			//System.out.println("ball tracking " + fixPosition);
			//working out the trajectory of the ball
			//System.out.println("ballY: " + ballY + " resultY:" + resultY + " area1Y weghting:" + area1Y + " " + area1Weighting
			//		+ " area2Y weghting:" + area2Y + " " + area2Weighting
			//		+ " area3Y weghting:" + area3Y + " " + area3Weighting );
			setVelocityToTarget(goalLine, resultY, true, false);

			/*
			if (goal) {
				setVelocityToTarget(goalLine,Field.OUTER_BOUNDARY_HEIGHT/2, true,false);
			} else if (midSection) {
				setVelocityToTarget(goalLine,getHalfAnglePosition(), true,false);
			} else {
				//going vertical or horizontal
				if (goingVertical || goingHorizontal) {
					if (ballY >= topPoint && ballY <= bottomPoint && r.getXPosition() < ballX) {
						setVelocityToTarget(goalLine, ballY, true, true);
					} else if (ballY < topPoint) {
						setVelocityToTarget(goalLine, topPoint, true, true);
					} else if (ballY > bottomPoint) {
						setVelocityToTarget(goalLine, bottomPoint, true, true);
					}
				//going diagonal
				} else if (!(goingVertical || goingHorizontal)) {


					boolean direction;
					if (goalLine < 110) {
						direction = xDiff < 0;
					} else {
						direction = xDiff > 0;
					}

					if (direction) {
						//ball going toward the goal
						if (trajectoryY >= topPoint && trajectoryY <= bottomPoint) {
							if (r.getYPosition() >= (trajectoryY - 2) && r.getYPosition() <= (trajectoryY + 2)) {
								setVelocityToTarget(goalLine, r.getYPosition(), true, true);
							} else {
								setVelocityToTarget(goalLine, trajectoryY, true, true);
							}
						} else {
							//ball travelling in the same side of board
							if ((trajectoryY > bottomPoint && ballY > bottomPoint) || (trajectoryY <= bottomPoint && ballY <= bottomPoint)) {
								// System.out.println("same side");
								if (ballY >= topPoint && ballY <= bottomPoint && r.getXPosition() < ballX) {
									setVelocityToTarget(goalLine, ballY, true, true);
								} else if (ballY < topPoint) {
									setVelocityToTarget(goalLine, topPoint - 5, true, true);
								} else if (ballY > bottomPoint) {
									setVelocityToTarget(goalLine, bottomPoint + 5, true, true);
								}
							} else {
								// System.out.println("oppo side");
								setVelocityToTarget(goalLine, Field.OUTER_BOUNDARY_HEIGHT / 2, true, true);
							}
						}
					} else {
						//away from the goal
						if (ballY >= topPoint && ballY <= bottomPoint && r.getXPosition() < ballX) {
							setVelocityToTarget(goalLine, ballY, true, true);
						} else if (ballY < topPoint) {
							setVelocityToTarget(goalLine, topPoint, true, true);
						} else if (ballY > bottomPoint) {
							setVelocityToTarget(goalLine, bottomPoint, true, true);
						}
					}
				//stationary
				} else {
					setVelocityToTarget(r.getXPosition(), r.getYPosition(), true, true);
				}
			}
			*/

		}

	//	System.out.println(r.getXPosition());
		lastBallX = ballX;
		lastBallY = ballY;
		lastBallX2 = lastBallX;
		lastBallY2 = lastBallY;
	}

	public void setVelocityToTarget(double x, double y, boolean reverse, boolean onGoalLine) {
		Robot r = bot;
		double targetDist;

		targetDist = Math.sqrt(Math.pow((x-r.getXPosition()),2) + Math.pow((y-r.getYPosition()),2));

		boolean isFacingTop = true;
		boolean isTargetTop = true;
		boolean front  = true;


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
		if (targetTheta > 90 || targetTheta < -90) {
			front = false;
		}

		if (!front && reverse) {
			if (targetTheta < 0) {
				targetTheta = -180 - targetTheta;
			} else if (targetTheta > 0) {
				targetTheta = 180 - targetTheta;
			}
		}

		//if (targetDist <=1.7) {
		//	targetDist = 0;
		//	targetTheta = 0;
		//	fixPosition = true;
		//}
		// targetTheta = Math.round(targetTheta/5)*5;
		/*
		FunctionBlock fb = loadFuzzy("newFuzzy.fcl");

		fb.setVariable("angleError", targetTheta);
		fb.setVariable("distanceError", Math.abs(targetDist));

		// Evaluate
		fb.evaluate();

          //   JFuzzyChart.get().chart(fb);
         //     JOptionPane.showMessageDialog(null, "nwa");

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
		r.angularVelocity = angular*1;  */

		//clear the ball if it about to go in goal
		double goalLine = parameters.get("goalLine");
		if (ballX <= goalLine + 5 && ballX > goalLine - 5 && Math.abs(r.getYPosition()-ballY) > 10) {
			if (ballY > r.getYPosition() && ballY < 110) {
				r.linearVelocity = r.getTheta() < 0 ? 2 : -2;// fast downwards
				return;
			} else if (ballY < r.getYPosition() && ballY > 70) {
				r.linearVelocity = r.getTheta() < 0 ? -2 : 2;// fast upwards
				return;
			}
		}

		FunctionBlock fb = loadFuzzy("fuzzy/goalKeeper.fcl");
		fb.setVariable("targetTheta", targetTheta);
		fb.setVariable("targetDist", targetDist);
		fb.evaluate();
		fb.getVariable("linearVelocity").defuzzify();
		fb.getVariable("angularVelocity").defuzzify();
		r.linearVelocity = fb.getVariable("linearVelocity").getValue();
		//r.linearVelocity = 0.275;
		r.angularVelocity = fb.getVariable("angularVelocity").getValue();

		if (targetDist <= 3.75) {
			r.linearVelocity = 0;
			r.angularVelocity = 0;
			fixPosition = true;
		}


		if (!front &&reverse) {
			r.linearVelocity *= -1;
			r.angularVelocity *= -1;
		}

	}

	private double getHalfAnglePosition() {
		int goalpostOneY = parameters.get("topPoint");
		int goalpostTwoY = parameters.get("bottomPoint");
		int goalLine = parameters.get("goalLine");

		double firstGoalpostTheta = Math.atan2(goalpostOneY - ballY,  (goalLine - ballX));
		double secondGoalpostTheta = Math.atan2(goalpostTwoY - ballY, (goalLine - ballX));
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
//			System.out.println("There is an error in the half angle calculations.");
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