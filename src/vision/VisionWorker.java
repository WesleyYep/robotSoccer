package vision;

import controllers.VisionController;
import controllers.WebcamController;
import data.Coordinate;
import data.VisionData;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import ui.ColourPanel;
import ui.SamplingPanel;
import ui.WebcamDisplayPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
* Created by Wesley on 6/02/2015.
*/
public class VisionWorker extends SwingWorker<Void, VisionData> {
    private Scalar ballMin;
    private Scalar ballMax;
    private Scalar teamMin;
    private Scalar teamMax;
    private Scalar greenMin;
    private Scalar greenMax;
    private boolean isTestingColour = false;
    private List<VisionListener> listeners = new ArrayList<VisionListener>();
    private List<Coordinate> alreadyProcessed = new ArrayList<Coordinate>();
    private WebcamController webcamController;
    private ColourPanel colourPanel;
    private WebcamDisplayPanel webcamDisplayPanel;
    private int robotMinSize;
    private int ballMinSize;
    private int suitableLength;
    private Coordinate[] oldRobotPositions = null;
    private int[] oldRobotOrientations = new int[5];
    private int numberOfGroups = 0;
    public List<Point> centerPoint = new ArrayList<Point>();

    public VisionWorker(ColourPanel cp, WebcamController wc, WebcamDisplayPanel wdp) {
        webcamController = wc;
        colourPanel = cp;
        webcamDisplayPanel = wdp;
        robotMinSize = cp.getRobotSizeMinimum();
        suitableLength = cp.getRobotDimension(); //change this if it doesn't work
    }

    @Override
    protected Void doInBackground() throws Exception {
        Mat ballBinary, teamBinary, greenBinary;
        Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Mat erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        SamplingPanel ballSP = colourPanel.ballSamplingPanel;
        SamplingPanel teamSp = colourPanel.teamSamplingPanel;
        SamplingPanel greenSp = colourPanel.greenSamplingPanel;


        while (!isCancelled()) {
            long startTime = System.currentTimeMillis();

            double[] rgbBallMin = utils.ColorSpace.YUVToRGB(ballSP.getLowerBoundForY(), ballSP.getLowerBoundForU(), ballSP.getLowerBoundForV());
            double[] rgbBallMax = utils.ColorSpace.YUVToRGB(ballSP.getUpperBoundForY(), ballSP.getUpperBoundForU(), ballSP.getUpperBoundForV());
            double[] rgbTeamMin= utils.ColorSpace.YUVToRGB(teamSp.getLowerBoundForY(), teamSp.getLowerBoundForU(), teamSp.getLowerBoundForV());
            double[] rgbTeamMax = utils.ColorSpace.YUVToRGB(teamSp.getUpperBoundForY(), teamSp.getUpperBoundForU(), teamSp.getUpperBoundForV());
            double[] rgbGreenMin = utils.ColorSpace.YUVToRGB(greenSp.getLowerBoundForY(), greenSp.getLowerBoundForU(), greenSp.getLowerBoundForV());
            double[] rgbGreenMax = utils.ColorSpace.YUVToRGB(greenSp.getUpperBoundForY(), greenSp.getUpperBoundForU(), greenSp.getUpperBoundForV());

            ballMin = new Scalar(rgbBallMin[0], rgbBallMin[1], rgbBallMin[2]);
            ballMax = new Scalar(rgbBallMax[0], rgbBallMax[1], rgbBallMax[2]);
            teamMin = new Scalar(rgbTeamMin[0], rgbTeamMin[1], rgbTeamMin[2]);
            teamMax = new Scalar(rgbTeamMax[0], rgbBallMin[1], rgbTeamMax[2]);
            greenMin = new Scalar(rgbGreenMin[0], rgbGreenMin[1], rgbGreenMin[2]);
            greenMax = new Scalar(rgbGreenMax[0], rgbGreenMax[1], rgbGreenMax[2]);
            List<MatOfPoint> ballContours = new ArrayList<MatOfPoint>();
            List<MatOfPoint> teamContours = new ArrayList<MatOfPoint>();
            List<MatOfPoint> greenContours = new ArrayList<MatOfPoint>();
            ballMinSize = colourPanel.getBallSizeMinimum();
            Mat webcamImageMat = webcamController.getImageFromWebcam();
            ballBinary = new Mat(webcamImageMat.size(), CvType.CV_8UC1);
            teamBinary = new Mat(webcamImageMat.size(), CvType.CV_8UC1);
            greenBinary = new Mat(webcamImageMat.size(), CvType.CV_8UC1);

            // Ball
           Core.inRange(webcamImageMat, ballMin, ballMax, ballBinary);

            Imgproc.erode(ballBinary, ballBinary, erodeKernel);
            Imgproc.dilate(ballBinary, ballBinary, dilateKernel);
            Imgproc.findContours(ballBinary, ballContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            int ballX = 0, ballY = 0;
            for (int i = 0; i < ballContours.size(); i++) {
                double areaThreshold = ballMinSize;
                if (areaThreshold < Imgproc.contourArea(ballContours.get(i))) {
                    Moments m = Imgproc.moments(ballContours.get(i));
                    ballX = (int) (m.get_m10() / m.get_m00());
                    ballY = (int) (m.get_m01() / m.get_m00());
                    Imgproc.drawContours(webcamImageMat, ballContours, i, new Scalar(255, 255, 255));
                    //centerPoint.add(new Point(ballX, ballY));
                    publish(new VisionData(new Coordinate(ballX, ballY), 0, "ball"));
                }
            }
    //        System.out.println(System.currentTimeMillis() - startTime);
            webcamDisplayPanel.update(webcamImageMat);
        }

//        isTestingColour = true;
//        SamplingPanel ballSP = colourPanel.ballSamplingPanel;
//        SamplingPanel teamSp = colourPanel.teamSamplingPanel;
//        SamplingPanel greenSp = colourPanel.greenSamplingPanel;
//
//        ballMin = new int []{ ballSP.getLowerBoundForY(), ballSP.getLowerBoundForU(), ballSP.getLowerBoundForV() };
//        ballMax = new int []{ ballSP.getUpperBoundForY(), ballSP.getUpperBoundForU(), ballSP.getUpperBoundForV() };
//        teamMin = new int []{ teamSp.getLowerBoundForY(), teamSp.getLowerBoundForU(), teamSp.getLowerBoundForV() };
//        teamMax = new int []{ teamSp.getUpperBoundForY(), teamSp.getUpperBoundForU(), teamSp.getUpperBoundForV() };
//        greenMin = new int []{ greenSp.getLowerBoundForY(), greenSp.getLowerBoundForU(), greenSp.getLowerBoundForV() };
//        greenMax = new int []{ greenSp.getUpperBoundForY(), greenSp.getUpperBoundForU(), greenSp.getUpperBoundForV() };
//
//        while (!isCancelled()) try {
//            long startTime = System.currentTimeMillis();
//            BufferedImage image = webcamController.getImageFromWebcam();
//
//            int imageHeight = image.getHeight();
//            int imageWidth = image.getWidth();
//            numberOfGroups = 0;
//
//            boolean ballFound  = false;
//
//            //prioritize looking at previous locations to find robots
//            if (oldRobotPositions != null) {
//
//                for (int k = 0; k < 5; k++) {
//                    Coordinate oldRobotPos = oldRobotPositions[k];
//                    if (oldRobotPos != null) {
//                        locateRobot(oldRobotPos.x, oldRobotPos.y, image, k);
//                    }
//                }
//                oldRobotPositions = null;
//
//            }
//
//            //loop through every 10th row
//            for (int i = 0; i < imageHeight; i += 10) {
//                //loop through every 10th column from right to left
//                for (int j = imageWidth - 1; j >= 0; j -= 10) {
//
//                    if (numberOfGroups >= 5) {
//                        break;
//                    }
//
//                    Color color = new Color(image.getRGB(j, i));
//                    int r = color.getRed();
//                    int g = color.getGreen();
//                    int b = color.getBlue();
//
//                    int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
//                    int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
//                    int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;
//
//                    //ball detection
//                    if (!ballFound && isBall(y, u, v)) {
//                        Queue<Coordinate> queue = new LinkedList<Coordinate>();
//                        List<Coordinate> group = new ArrayList<Coordinate>();
//                        queue.add(new Coordinate(j,i));
//                        while (!queue.isEmpty()) {
//                            Coordinate c = queue.poll();
//                            int w = c.x;
//                            int e = c.x;
//
//                            while (isPixelInBallColourRange(image, w - 1, c.y) && !group.contains(new Coordinate(w - 1, c.y))) {
//                                w--;
//                            }
//                            while (isPixelInBallColourRange(image, e + 1, c.y) && !group.contains(new Coordinate(e + 1, c.y))) {
//                                e++;
//                            }
//
//                            for (int k = w; k < e + 1; k++) {
//                                group.add(new Coordinate(k, c.y));
//                                alreadyProcessed.add(new Coordinate(k, c.y));
//                                if (isPixelInBallColourRange(image, k, c.y - 1) && !group.contains(new Coordinate(k, c.y - 1))) {
//                                    queue.add(new Coordinate(k, c.y - 1));
//                                } if (isPixelInBallColourRange(image, k, c.y + 1) && !group.contains(new Coordinate(k, c.y + 1))) {
//                                    queue.add(new Coordinate(k, c.y + 1));
//                                }
//                            }
//                        }
//                        if (group.size() < ballMinSize) {
//                            //            System.out.println("group was too small");
//                        } else {
//                            ballFound = true;
//                            //first get center
//                            int N = group.size();
//                            int xSum = 0;
//                            int ySum = 0;
//
//                            for (Coordinate c: group) {
//                                xSum += c.x;
//                                ySum += c.y;
//                            }
//                            Coordinate centre = new Coordinate(xSum/N, ySum/N);
//                           //send ball
//                            publish(new VisionData(new Coordinate(centre.x, centre.y), 0, "ball"));
//                        }
//                    }
//
//                    //robot detection if needed
//                     if (numberOfGroups < 5) {
//                        oldRobotPositions = new Coordinate[5];
//                        if (isTeam(y, u, v) && !alreadyProcessed.contains(new Coordinate(j, i))) {
//                            locateRobot(j, i, image, -1);
//                        }
//                     }
//                }
//                if (numberOfGroups >= 5 && ballFound) {
//                    break;
//                }
//            }
//            System.out.println("number of robot groups: " + numberOfGroups);
//            numberOfGroups = 0;
//
//              alreadyProcessed.clear();
//            System.out.println("Time: " + (System.currentTimeMillis() - startTime));
//
//        } catch (Exception e) {
//            System.out.println("wtf");
//            e.printStackTrace();
//        }

        return null;
    }

//    public void locateRobot(int j, int i, BufferedImage image, int oldRobotNum) {
//        Queue<Coordinate> queue = new LinkedList<Coordinate>();
//        List<Coordinate> group = new ArrayList<Coordinate>();
//
//        queue.add(new Coordinate(j, i));
//        while (!queue.isEmpty()) {
//            Coordinate c = queue.poll();
//            int w = c.x;
//            int e = c.x;
//
//            while (isPixelInTeamColourRange(image, w - 1, c.y) && !group.contains(new Coordinate(w - 1, c.y))) {
//                w--;
//            }
//            while (isPixelInTeamColourRange(image, e + 1, c.y) && !group.contains(new Coordinate(e + 1, c.y))) {
//                e++;
//            }
//            //    System.out.println("w: " + c.x + ", e: " + c.y);
//
//            for (int k = w; k < e + 1; k++) {
//                group.add(new Coordinate(k, c.y));
//                alreadyProcessed.add(new Coordinate(k, c.y));
//                if (isPixelInTeamColourRange(image, k, c.y - 1) && !group.contains(new Coordinate(k, c.y - 1))) {
//                    queue.add(new Coordinate(k, c.y - 1));
//                }
//                if (isPixelInTeamColourRange(image, k, c.y + 1) && !group.contains(new Coordinate(k, c.y + 1))) {
//                    queue.add(new Coordinate(k, c.y + 1));
//                }
//            }
//        }
//        if (group.size() < robotMinSize) {
//            //            System.out.println("group was too small");
//        } else {
//            numberOfGroups++;
//            //identify robot
//            //first get center
//            double N = group.size();
//            double xSum = 0;
//            double ySum = 0;
//
//            for (Coordinate c : group) {
//                xSum += c.x;
//                ySum += c.y;
//            }
//            Coordinate centre = new Coordinate((int) (xSum / N), (int) (ySum / N));
//
//            //now get variance
//            int xVarSum = 0;
//            int yVarSum = 0;
//            int topRightQuadrant = 0;
//            int topLeftQuadrant = 0;
//            //use this to get angle
//            for (Coordinate c : group) {
//                xVarSum += squared(c.x - centre.x);
//                yVarSum += squared(c.y - centre.y);
//                if (c.y < centre.y) {
//                    if (c.x > centre.x) {
//                        topRightQuadrant++;
//                    } else {
//                        topLeftQuadrant++;
//                    }
//                }
//            }
//            double xVar = xVarSum / N;
//            double yVar = yVarSum / N;
//            double theta = Math.atan2(yVar, xVar); //original estimated theta
//
//            if (topLeftQuadrant > topRightQuadrant) {
//                theta = Math.PI - theta;
//            }
//            //  System.out.println("theta: " + Math.toDegrees(theta));
//            boolean greenQuadrants[] = new boolean[4];
//            int robotNum = 0;
//            int orientation = 1; //1 for normal, 0 for opposite
//
//            for (int t = 0; t < 4; t++) {
//                double testTheta = theta + t * Math.PI / 2 + Math.PI / 4;
//                int testX = centre.x + (int) (suitableLength * Math.cos(testTheta));
//                int testY = centre.y - (int) (suitableLength * Math.sin(testTheta));
//                //       System.out.println("test theta: " + testTheta);
//                greenQuadrants[t] = isPixelInGreenColourRange(image, testX, testY, 2); //this gives 3 tries to detect green
//            }
//            if (greenQuadrants[0]) {
//                if (greenQuadrants[3]) {
//                    if (greenQuadrants[1]) {
//                        robotNum = 4;
//                    } else if (greenQuadrants[2]) {
//                        robotNum = 5;
//                    } else {
//                        robotNum = 3;
//                    }
//                } else if (greenQuadrants[1]) {
//                    robotNum = 5;
//                    theta = theta - Math.PI;
//                    orientation = 0;
//                } else {
//                    robotNum = 1;
//                }
//            } else if (greenQuadrants[1]) {
//                if (greenQuadrants[2]) {
//                    if (greenQuadrants[3]) {
//                        robotNum = 4;
//                        theta = theta - Math.PI;
//                        orientation = 0;
//                    } else {
//                        robotNum = 3;
//                        theta = theta - Math.PI;
//                        orientation = 0;
//                    }
//                } else {
//                    robotNum = 2;
//                    theta = theta - Math.PI;
//                    orientation = 0;
//                }
//            } else if (greenQuadrants[2]) {
//                robotNum = 1;
//                theta = theta - Math.PI;
//                orientation = 0;
//            } else if (greenQuadrants[3]) {
//                robotNum = 2;
//            } else {
//                if (oldRobotNum >= 0) {
//                    robotNum = oldRobotNum;
//                    if (oldRobotOrientations[oldRobotNum] == 0) {
//                        theta = theta - Math.PI;
//                        orientation = 0;
//                    }
//                } else {
//                    System.out.println("could not identify");
//                }
//            }
//            //                      System.out.println("1st: " + greenQuadrants[0] + ", 2nd: " + greenQuadrants[1] + ", 3rd: " + greenQuadrants[2] + ", 4th: " + greenQuadrants[3]);
//            //                      System.out.println(robotNum + (theta > 0 ? " up":" down"));
//
//            if (robotNum > 0) {
//                publish(new VisionData(new Coordinate(centre.x, centre.y), theta, "robot:" + robotNum));
//                oldRobotPositions[robotNum-1] = new Coordinate(centre.x, centre.y);
//                oldRobotOrientations[robotNum-1] = orientation;
//            }
//        }
//    }

    @Override
    public void process(List<VisionData> chunks) {
        for (VisionData v : chunks) {
            for (VisionListener l : listeners) {
                l.receive(v);
            }
        }
    }

    public void setCancelled() {
        isTestingColour = false;
    }

    public boolean isTestingColor() {
        return isTestingColour;
    }

    public void addListener(VisionListener listener) {
        listeners.add(listener);
    }

    protected int squared (int x) {
        return x * x;
    }
//
//    private boolean isPixelInBallColourRange(BufferedImage image, int xPos, int yPos) {
//        try {
//            Color color = new Color(image.getRGB(xPos, yPos));
//            int r = color.getRed();
//            int g = color.getGreen();
//            int b = color.getBlue();
//
//            int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
//            int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
//            int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;
//
//            return isBall(y, u, v);
//        } catch (ArrayIndexOutOfBoundsException ex) {
//        //    System.out.println("pixel out of bounds!");
//            return false;
//        }
//    }

//     private boolean isPixelInTeamColourRange(BufferedImage image, int xPos, int yPos) {
//         try {
//             Color color = new Color(image.getRGB(xPos, yPos));
//             int r = color.getRed();
//             int g = color.getGreen();
//             int b = color.getBlue();
//
//             int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
//             int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
//             int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;
//
//             return isTeam(y, u, v);
//         } catch (ArrayIndexOutOfBoundsException ex) {
//     //        System.out.println("pixel out of bounds!");
//             return false;
//         }
//     }
//
//    private boolean isPixelInGreenColourRange(BufferedImage image, int xPos, int yPos, int tryNum) {
//        try {
//            Color color = new Color(image.getRGB(xPos, yPos));
//            int r = color.getRed();
//            int g = color.getGreen();
//            int b = color.getBlue();
//
//            int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
//            int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
//            int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;
//
//            if (isGreen(y, u, v)) {
//                return true;
//            } else if (tryNum == 0) {
//                return false;
//            } else {
//                return isPixelInGreenColourRange(image, xPos - 2, yPos - 2, tryNum - 1);
//            }
//        } catch (ArrayIndexOutOfBoundsException ex) {
//     //       System.out.println("pixel out of bounds!");
//            return false;
//        }
//    }

//    private boolean isBall(int y, int u, int v) {
//
//        return (y > ballMin[0] && y < ballMax[0] &&
//                u > ballMin[1] && u < ballMax[1] &&
//                v > ballMin[2] && v < ballMax[2]);
//
//    	/*
//
//    	return ( ((LookupTable.YTable[y] >> LookupTable.BALL_BIT_POS) & 1) &&
//    			LookupTable.UTable[u] == LookupTable.BALL_COLOUR &&
//    			LookupTable.VTable[v] == LookupTable.BALL_COLOUR);
//    			*/
//    }
//
//    private boolean isTeam(int y, int u, int v) {
//
//        return (y > teamMin[0] && y < teamMax[0] &&
//                u > teamMin[1] && u < teamMax[1] &&
//                v > teamMin[2] && v < teamMax[2]);
//
//	}
//
//	private boolean isGreen(int y, int u, int v) {
//
//
//		return (y > greenMin[0] && y < greenMax[0] &&
//				u > greenMin[1] && u < greenMax[1] &&
//				v > greenMin[2] && v < greenMax[2]);
//
//
//		/*
//    	return (LookupTable.YTable[y] == LookupTable.GREEN_COLOUR &&
//    			LookupTable.UTable[u] == LookupTable.GREEN_COLOUR &&
//    			LookupTable.VTable[v] == LookupTable.GREEN_COLOUR);  */
//
//	}


}
