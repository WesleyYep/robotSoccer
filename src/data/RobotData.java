package data;

import org.opencv.core.Point;

import utils.Image;
import utils.PairPoint;

public class RobotData {
	private Point[] teamRectPoint;
	private Point teamCenterPoint;
	private PairPoint shortPair;
	private PairPoint longPair;
	private PairPoint greenPatch1 = null;
	private PairPoint greenPatch2 = null;
	private double thresholdDistance;
	
	private final static double THRESHOLDANGLE = 20;
    
    private double theta;
	
	public RobotData(Point[] teamRectPoint, Point teamCenterPoint) {
		this.teamRectPoint = teamRectPoint;
		this.teamCenterPoint = teamCenterPoint;
		assignPairPoints();
		assignThresholdDistance();
	}
	
	private void assignPairPoints() {
		Point referencePoint = teamRectPoint[0];
		
		// find the distance between this reference point and every other point.
		double d1 = Image.euclideanDistance(referencePoint, teamRectPoint[1]);
		double d2 = Image.euclideanDistance(referencePoint, teamRectPoint[2]);
		double d3 = Image.euclideanDistance(referencePoint, teamRectPoint[3]);
		
		// find the angle between this reference point and every other point.
		double a1 = Image.angleBetweenTwoPoints(referencePoint, teamRectPoint[1]);
		double a2 = Image.angleBetweenTwoPoints(referencePoint, teamRectPoint[2]);
		double a3 = Image.angleBetweenTwoPoints(referencePoint, teamRectPoint[3]);
		
		// find the short pair.
		double shortPair = Math.min(d1, Math.min(d2, d3));
		
		if (shortPair == d1) {
			this.shortPair = new PairPoint(referencePoint, teamRectPoint[1], d1, a1);
		} else if (shortPair == d2) {
			this.shortPair = new PairPoint(referencePoint, teamRectPoint[2], d2, a2);
		} else {
			this.shortPair = new PairPoint(referencePoint, teamRectPoint[3], d3, a3);
		}
		
		double longPair = Math.max(Math.min(d1,d2), Math.min(Math.max(d1,d2), d3));
		
		if (longPair == d1) {
			this.longPair = new PairPoint(referencePoint, teamRectPoint[1], d1, a1);
		} else if (longPair == d2) {
			this.longPair = new PairPoint(referencePoint, teamRectPoint[2], d2, a2);
		} else {
			this.longPair = new PairPoint(referencePoint, teamRectPoint[3], d3, a3);
		}
	}
	
	private void assignThresholdDistance() {
		double endX = shortPair.getSecond().x + shortPair.getEuclideanDistance() * Math.cos(Math.toRadians(shortPair.getTheta()));
		double endY = shortPair.getSecond().y + shortPair.getEuclideanDistance() * Math.sin(Math.toRadians(shortPair.getTheta()));
		Point endPoint = new Point(endX, endY);
		thresholdDistance = Image.euclideanDistance(teamCenterPoint, endPoint);
	}
	
	public void addGreenPatch(Point greenCenterPoint) {
		
		double distance = Image.euclideanDistance(teamCenterPoint, greenCenterPoint);
		
		if (distance > thresholdDistance) {
			return;
		}
		
		if (greenPatch1 == null) {
			greenPatch1 = new PairPoint(teamCenterPoint, greenCenterPoint, distance, Image.angleBetweenTwoPoints(teamCenterPoint, greenCenterPoint));
		} else if (greenPatch2 == null) {
			greenPatch2 = new PairPoint(teamCenterPoint, greenCenterPoint, distance, Image.angleBetweenTwoPoints(teamCenterPoint, greenCenterPoint));
		} else { //long patch should override green patch
			if (!isLongPatch(greenPatch1) && isLongPatch(new PairPoint(teamCenterPoint, greenCenterPoint, distance, Image.angleBetweenTwoPoints(teamCenterPoint, greenCenterPoint)))) {
				greenPatch1 = new PairPoint(teamCenterPoint, greenCenterPoint, distance, Image.angleBetweenTwoPoints(teamCenterPoint, greenCenterPoint));
			} else if (!isLongPatch(greenPatch2) && isLongPatch(new PairPoint(teamCenterPoint, greenCenterPoint, distance, Image.angleBetweenTwoPoints(teamCenterPoint, greenCenterPoint)))) {
				greenPatch2 = new PairPoint(teamCenterPoint, greenCenterPoint, distance, Image.angleBetweenTwoPoints(teamCenterPoint, greenCenterPoint));
			}
		}
		
	}
	
	public boolean isLongPatch(PairPoint greenPatch) {
		
		double longPairTheta = longPair.getTheta();
		
		double differenceTheta = Math.abs(longPairTheta - Image.angleBetweenTwoPoints(teamCenterPoint, greenPatch.getSecond()));

		if (!((differenceTheta % 90) > THRESHOLDANGLE && (differenceTheta % 90) < 90 - THRESHOLDANGLE)) {
			return true;
		}

		return false;
	}
	
	public PairPoint getShortPair() {
		return shortPair;
	}
	
	public PairPoint getLongPair() {
		return longPair;
	}
	
	public Point getTeamCenterPoint() {
		return teamCenterPoint;
	}
	
	/**
	 * <p>Returns robot identification. 1-5. Returns -1 if not identifiable.</p>
	 * @return
	 */
	
	public int robotIdentification() {
		int robotNum = -1;
		
        if (greenPatch1 == null && greenPatch2 == null) {
            return -1;
        }

        boolean isLongPatchPresent = false;
        boolean isRobotNumThree = false;
        Point shortMidPoint;

		if (greenPatch1 != null && greenPatch2 != null) {
			// Must be either 5, 4, 3
			if (!isLongPatch(greenPatch1) && !isLongPatch(greenPatch2)) {
				isRobotNumThree = true;
                shortMidPoint = greenPatch1.getSecond(); //any green patch will work
			} else {
                // Must be either 5, 4
                isLongPatchPresent = true;
                if (isLongPatch(greenPatch1)) {
                    shortMidPoint = greenPatch2.getSecond();
                } else {
                    shortMidPoint = greenPatch1.getSecond();
                }
			}
			
		} else {
            // Must be either 2, 1
            shortMidPoint = greenPatch1.getSecond();
        }

        Point robotMidPoint = getTeamCenterPoint();
        double robotOrientation = getLongPair().getTheta();
        double normAngle  = Math.toDegrees(Math.atan2(shortMidPoint.y - robotMidPoint.y, shortMidPoint.x - robotMidPoint.x));
        double shortAngleToRobot = normAngle - robotOrientation;

        shortAngleToRobot = clip(shortAngleToRobot);
        int quadrant = getQuadrant(shortAngleToRobot);

        if (isRobotNumThree) {
            robotNum = 3;
            if (quadrant == 0 || quadrant == 3) {
                theta = robotOrientation + 180;
            } else {
                theta = robotOrientation;
            }
        } else if (!isLongPatchPresent) {
            if (quadrant == 0) {
                robotNum = 2;
                theta = robotOrientation + 180;
            } else if (quadrant == 1) {
                robotNum = 1;
                theta = robotOrientation;
            } else if (quadrant == 2) {
                robotNum = 2;
                theta = robotOrientation;
            } else if (quadrant == 3) {
                robotNum = 1;
                theta = robotOrientation + 180;
            }
        } else {
            if (quadrant == 0) {
                robotNum = 4;
                theta = robotOrientation + 180;
            } else if (quadrant == 1) {
                robotNum = 5;
                theta = robotOrientation;
            } else if (quadrant == 2) {
                robotNum = 4;
                theta = robotOrientation;
            } else if (quadrant == 3) {
                robotNum = 5;
                theta = robotOrientation + 180;
            }
        }

        return robotNum;
	}
	
	/**
	 * <p>Return the given angle between -180 to 180 range.</p>
	 * @param angle
	 * @return clipped angle
	 */
	
	public static double clip(double angle) {
		if (angle > 180) {
			return angle -= 360;
		} else if (angle < -180) {
			return angle += 360;
		} else {
			return angle;
		}
	}
	
	public static int getQuadrant(double angle) {
		for (int i = 0; i < 4; i++) {
			if (-180+90*i < angle && angle < -180+90*(i+1)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public double getTheta() {
        return Math.toRadians(-theta);
    }
	
}
