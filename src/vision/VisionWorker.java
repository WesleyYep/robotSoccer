//package vision;
//
//import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
//import static org.bytedeco.javacpp.opencv_core.cvGetSize;
//import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
//import static org.bytedeco.javacpp.opencv_core.cvScalar;
//import controllers.VisionController;
//import controllers.WebcamController;
//import data.Coordinate;
//import data.VisionData;
//import ui.ColourPanel;
//import ui.SamplingPanel;
//
//import javax.swing.*;
//
//import org.bytedeco.javacpp.opencv_core.CvScalar;
//import org.bytedeco.javacpp.opencv_core.IplImage;
//
//import java.awt.*;
//import java.awt.geom.Point2D;
//import java.awt.image.BufferedImage;
//import java.util.*;
//import java.util.List;
//
///**
// * Created by Wesley on 6/02/2015.
// */
//public class VisionWorker extends SwingWorker<Void, VisionData> {
//    private WebcamController webcamController;
//    private VisionController visionController;
//    private ColourPanel colourPanel;
//    private int[] ballMin = new int[3];
//    private int[] ballMax = new int[3];
//    private int[] teamMin = new int[3];
//    private int[] teamMax = new int[3];
//    private int[] greenMin = new int[3];
//    private int[] greenMax = new int[3];
//    private List<VisionListener> listeners = new ArrayList<VisionListener>();
//    private List<Coordinate> alreadyProcessed = new ArrayList<Coordinate>();
//    private boolean isTestingColour = false;
//
//    public VisionWorker(WebcamController wc, ColourPanel colourPanel, VisionController vc) {
//        this.webcamController = wc;
//        this.colourPanel = colourPanel;
//        this.visionController = vc;
//    }
//
//    @Override
//    protected Void doInBackground() throws Exception {
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
//            int imageHeight = image.getHeight();
//            int imageWidth = image.getWidth();
//
//            boolean previous = false;
//            int rowWidth = 0;
//            int highestRowWidth = 0;
//            int ballX = 0;
//            int ballY = 0;
//
//            int robotMinSize = colourPanel.getRobotSizeMinimum();
//            int ballMinSize = colourPanel.getBallSizeMinimum();
//            int suitableLength = colourPanel.getRobotDimension(); //change this if it doesn't work
//            boolean ballFound  = false;
//
//            //loop through every 10th row
//            for (int i = 0; i < imageHeight; i += 10) {
//                //loop through every 10th column from right to left
//                for (int j = imageWidth - 1; j >= 0; j -= 10) {
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
//                            //send ball
//                            publish(new VisionData(new Coordinate(centre.x, centre.y), 0, "ball"));
//                        }
//                    }
//
//                    //robot detection
//                    if (isTeam(y, u, v) && !alreadyProcessed.contains(new Coordinate(j, i))) {
//                        Queue<Coordinate> queue = new LinkedList<Coordinate>();
//                        List<Coordinate> group = new ArrayList<Coordinate>();
//
//                        queue.add(new Coordinate(j,i));
//                        while (!queue.isEmpty()) {
//                            Coordinate c = queue.poll();
//                            int w = c.x;
//                            int e = c.x;
//
//                            while (isPixelInTeamColourRange(image, w - 1, c.y) && !group.contains(new Coordinate(w - 1, c.y))) {
//                                w--;
//                            }
//                            while (isPixelInTeamColourRange(image, e + 1, c.y) && !group.contains(new Coordinate(e + 1, c.y))) {
//                                e++;
//                            }
//                            //    System.out.println("w: " + c.x + ", e: " + c.y);
//
//                            for (int k = w; k < e + 1; k++) {
//                                group.add(new Coordinate(k, c.y));
//                                alreadyProcessed.add(new Coordinate(k, c.y));
//                                if (isPixelInTeamColourRange(image, k, c.y - 1) && !group.contains(new Coordinate(k, c.y - 1))) {
//                                    queue.add(new Coordinate(k, c.y - 1));
//                                } if (isPixelInTeamColourRange(image, k, c.y + 1) && !group.contains(new Coordinate(k, c.y + 1))) {
//                                    queue.add(new Coordinate(k, c.y + 1));
//                                }
//                            }
//                        }
//                        if (group.size() < robotMinSize) {
//                            //            System.out.println("group was too small");
//                        } else {
//                            //identify robot
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
//
//                            //now get variance
//                            int xVarSum = 0;
//                            int yVarSum = 0;
//                            int topRightQuadrant = 0;
//                            int topLeftQuadrant = 0;
//                            //use this to get angle
//                            for (Coordinate c: group) {
//                                xVarSum += squared(c.x - centre.x);
//                                yVarSum += squared(c.y - centre.y);
//                                if (c.y < centre.y) {
//                                    if (c.x > centre.x) {
//                                        topRightQuadrant++;
//                                    } else {
//                                        topLeftQuadrant++;
//                                    }
//                                }
//                            }
//                            double xVar = xVarSum / N;
//                            double yVar = yVarSum / N;
//                            double theta = Math.atan2(yVar, xVar); //original estimated theta
//
//                            if (topLeftQuadrant > topRightQuadrant) {
//                                theta = Math.PI - theta;
//                            }
//                            //       System.out.println(Math.toDegrees(theta));
//
//                            boolean greenQuadrants[] = new boolean[4];
//                            int robotNum = 0;
//
//                            for (int t = 0; t < 4; t++) {
//                                double testTheta = theta + t * Math.PI/2 + Math.PI/4;
//                                int testX = centre.x + (int)(suitableLength*Math.cos(testTheta));
//                                int testY = centre.y - (int)(suitableLength*Math.sin(testTheta));
//                                //       System.out.println("test theta: " + testTheta);
//                                greenQuadrants[t] = isPixelInGreenColourRange(image, testX, testY, 2); //this gives 3 tries to detect green
//                            }
//                            if (greenQuadrants[0]) {
//                                if (greenQuadrants[3]) {
//                                    if (greenQuadrants[1]) {
//                                        robotNum = 4;
//                                    } else if (greenQuadrants[2]) {
//                                        robotNum = 5;
//                                    } else {
//                                        robotNum = 3;
//                                    }
//                                } else if (greenQuadrants[1]) {
//                                    robotNum = 5;
//                                    theta = theta - Math.PI;
//                                } else {
//                                    robotNum = 1;
//                                }
//                            } else if (greenQuadrants[1]) {
//                                if (greenQuadrants[2]) {
//                                    if (greenQuadrants[3]) {
//                                        robotNum = 4;
//                                        theta = theta - Math.PI;
//                                    } else {
//                                        robotNum = 3;
//                                        theta = theta - Math.PI;
//                                    }
//                                } else {
//                                    robotNum = 2;
//                                    theta = theta - Math.PI;
//                                }
//                            } else if (greenQuadrants[2]) {
//                                robotNum = 1;
//                                theta = theta - Math.PI;
//                            } else if (greenQuadrants[3]) {
//                                robotNum = 2;
//                            }
//                            //                      System.out.println("1st: " + greenQuadrants[0] + ", 2nd: " + greenQuadrants[1] + ", 3rd: " + greenQuadrants[2] + ", 4th: " + greenQuadrants[3]);
//                            //                      System.out.println(robotNum + (theta > 0 ? " up":" down"));
//
//                            if (robotNum > 0) {
//                                publish(new VisionData(new Coordinate(centre.x, centre.y), theta, "robot:" + robotNum));
//                            }
//                        }
//                    }
//                }
//            }
//
//            alreadyProcessed.clear();
//            System.out.println("Time: " + (System.currentTimeMillis() - startTime));
//
//        } catch (Exception e) {
//            System.out.println("wtf");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//
//    @Override
//    public void process(List<VisionData> chunks) {
//        for (VisionData v : chunks) {
//            for (VisionListener l : listeners) {
//                l.receive(v);
//            }
//        }
//    }
//
//    public void addListener(VisionListener listener) {
//        listeners.add(listener);
//    }
//    public void setCancelled() {
//        isTestingColour = false;
//    }
//
//    public boolean isTestingColor() {
//        return isTestingColour;
//    }
//    protected int squared (int x) {
//        return x * x;
//    }
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
//            //    System.out.println("pixel out of bounds!");
//            return false;
//        }
//    }
//
//    private boolean isPixelInTeamColourRange(BufferedImage image, int xPos, int yPos) {
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
//            return isTeam(y, u, v);
//        } catch (ArrayIndexOutOfBoundsException ex) {
//            //        System.out.println("pixel out of bounds!");
//            return false;
//        }
//    }
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
//            //       System.out.println("pixel out of bounds!");
//            return false;
//        }
//    }
//
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
//         /*
//    	return (LookupTable.YTable[y] == LookupTable.TEAM_COLOUR &&
//    			LookupTable.UTable[u] == LookupTable.TEAM_COLOUR &&
//    			LookupTable.VTable[v] == LookupTable.TEAM_COLOUR); */
//
//    }
//
//    private boolean isGreen(int y, int u, int v) {
//
//
//        return (y > greenMin[0] && y < greenMax[0] &&
//                u > greenMin[1] && u < greenMax[1] &&
//                v > greenMin[2] && v < greenMax[2]);
//
//
//        /*
//    	return (LookupTable.YTable[y] == LookupTable.GREEN_COLOUR &&
//    			LookupTable.UTable[u] == LookupTable.GREEN_COLOUR &&
//    			LookupTable.VTable[v] == LookupTable.GREEN_COLOUR);  */
//
//    }
//
//
//}

package vision;

import controllers.VisionController;
import controllers.WebcamController;
import data.Coordinate;
import data.VisionData;
import ui.ColourPanel;
import ui.SamplingPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
* Created by Wesley on 6/02/2015.
*/
public class VisionWorker extends SwingWorker<Void, VisionData> {
    private WebcamController webcamController;
    private VisionController visionController;
    private ColourPanel colourPanel;
    private int[] ballMin = new int[3];
    private int[] ballMax = new int[3];
    private int[] teamMin = new int[3];
    private int[] teamMax = new int[3];
    private int[] greenMin = new int[3];
    private int[] greenMax = new int[3];
    private boolean isTestingColour = false;
    private List<VisionListener> listeners = new ArrayList<VisionListener>();
    private List<Coordinate> alreadyProcessed = new ArrayList<Coordinate>();
    private int robotMinSize;
    private int ballMinSize;
    private int suitableLength;
    private Coordinate[] oldRobotPositions = null;
    private int[] oldRobotOrientations = new int[5];
    private int numberOfGroups = 0;

    public VisionWorker(WebcamController wc, ColourPanel colourPanel, VisionController vc) {
        this.webcamController = wc;
        this.colourPanel = colourPanel;
        this.visionController = vc;
        robotMinSize = colourPanel.getRobotSizeMinimum();
        ballMinSize = colourPanel.getBallSizeMinimum();
        suitableLength = colourPanel.getRobotDimension(); //change this if it doesn't work
    }

    @Override
    protected Void doInBackground() throws Exception {
        isTestingColour = true;
        SamplingPanel ballSP = colourPanel.ballSamplingPanel;
        SamplingPanel teamSp = colourPanel.teamSamplingPanel;
        SamplingPanel greenSp = colourPanel.greenSamplingPanel;

        ballMin = new int []{ ballSP.getLowerBoundForY(), ballSP.getLowerBoundForU(), ballSP.getLowerBoundForV() };
        ballMax = new int []{ ballSP.getUpperBoundForY(), ballSP.getUpperBoundForU(), ballSP.getUpperBoundForV() };
        teamMin = new int []{ teamSp.getLowerBoundForY(), teamSp.getLowerBoundForU(), teamSp.getLowerBoundForV() };
        teamMax = new int []{ teamSp.getUpperBoundForY(), teamSp.getUpperBoundForU(), teamSp.getUpperBoundForV() };
        greenMin = new int []{ greenSp.getLowerBoundForY(), greenSp.getLowerBoundForU(), greenSp.getLowerBoundForV() };
        greenMax = new int []{ greenSp.getUpperBoundForY(), greenSp.getUpperBoundForU(), greenSp.getUpperBoundForV() };

        while (!isCancelled()) try {

            long startTime = System.currentTimeMillis();
            BufferedImage image = webcamController.getImageFromWebcam();

            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();
            numberOfGroups = 0;

            boolean ballFound  = false;

            //prioritize looking at previous locations to find robots
            if (oldRobotPositions != null) {

                for (int k = 0; k < 5; k++) {
                    Coordinate oldRobotPos = oldRobotPositions[k];
                    if (oldRobotPos != null) {
                        locateRobot(oldRobotPos.x, oldRobotPos.y, image, k);
                    }
                }
                oldRobotPositions = null;

            }

            //loop through every 10th row
            for (int i = 0; i < imageHeight; i += 10) {
                //loop through every 10th column from right to left
                for (int j = imageWidth - 1; j >= 0; j -= 10) {

                    if (numberOfGroups >= 5) {
                        break;
                    }

                    Color color = new Color(image.getRGB(j, i));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();

                    int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
                    int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
                    int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;

                    //ball detection
                    if (!ballFound && isBall(y, u, v)) {
                        Queue<Coordinate> queue = new LinkedList<Coordinate>();
                        List<Coordinate> group = new ArrayList<Coordinate>();
                        queue.add(new Coordinate(j,i));
                        while (!queue.isEmpty()) {
                            Coordinate c = queue.poll();
                            int w = c.x;
                            int e = c.x;

                            while (isPixelInBallColourRange(image, w - 1, c.y) && !group.contains(new Coordinate(w - 1, c.y))) {
                                w--;
                            }
                            while (isPixelInBallColourRange(image, e + 1, c.y) && !group.contains(new Coordinate(e + 1, c.y))) {
                                e++;
                            }

                            for (int k = w; k < e + 1; k++) {
                                group.add(new Coordinate(k, c.y));
                                alreadyProcessed.add(new Coordinate(k, c.y));
                                if (isPixelInBallColourRange(image, k, c.y - 1) && !group.contains(new Coordinate(k, c.y - 1))) {
                                    queue.add(new Coordinate(k, c.y - 1));
                                } if (isPixelInBallColourRange(image, k, c.y + 1) && !group.contains(new Coordinate(k, c.y + 1))) {
                                    queue.add(new Coordinate(k, c.y + 1));
                                }
                            }
                        }
                        if (group.size() < ballMinSize) {
                            //            System.out.println("group was too small");
                        } else {
                            ballFound = true;
                            //first get center
                            int N = group.size();
                            int xSum = 0;
                            int ySum = 0;

                            for (Coordinate c: group) {
                                xSum += c.x;
                                ySum += c.y;
                            }
                            Coordinate centre = new Coordinate(xSum/N, ySum/N);
                           //send ball
                            publish(new VisionData(new Coordinate(centre.x, centre.y), 0, "ball"));
                        }
                    }

                    //robot detection if needed
                     if (numberOfGroups < 5) {
                        oldRobotPositions = new Coordinate[5];
                        if (isTeam(y, u, v) && !alreadyProcessed.contains(new Coordinate(j, i))) {
                            locateRobot(j, i, image, -1);
                        }
                     }
                }
                if (numberOfGroups >= 5 && ballFound) {
                    break;
                }
            }
            System.out.println("number of robot groups: " + numberOfGroups);
            numberOfGroups = 0;

              alreadyProcessed.clear();

            System.out.println("Time: " + (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            System.out.println("wtf");
            e.printStackTrace();
        }

        return null;
    }

    public void locateRobot(int j, int i, BufferedImage image, int oldRobotNum) {
        Queue<Coordinate> queue = new LinkedList<Coordinate>();
        List<Coordinate> group = new ArrayList<Coordinate>();

        queue.add(new Coordinate(j, i));
        while (!queue.isEmpty()) {
            Coordinate c = queue.poll();
            int w = c.x;
            int e = c.x;

            while (isPixelInTeamColourRange(image, w - 1, c.y) && !group.contains(new Coordinate(w - 1, c.y))) {
                w--;
            }
            while (isPixelInTeamColourRange(image, e + 1, c.y) && !group.contains(new Coordinate(e + 1, c.y))) {
                e++;
            }
            //    System.out.println("w: " + c.x + ", e: " + c.y);

            for (int k = w; k < e + 1; k++) {
                group.add(new Coordinate(k, c.y));
                alreadyProcessed.add(new Coordinate(k, c.y));
                if (isPixelInTeamColourRange(image, k, c.y - 1) && !group.contains(new Coordinate(k, c.y - 1))) {
                    queue.add(new Coordinate(k, c.y - 1));
                }
                if (isPixelInTeamColourRange(image, k, c.y + 1) && !group.contains(new Coordinate(k, c.y + 1))) {
                    queue.add(new Coordinate(k, c.y + 1));
                }
            }
        }
        if (group.size() < robotMinSize) {
            //            System.out.println("group was too small");
        } else {
            numberOfGroups++;
            //identify robot
            //first get center
            double N = group.size();
            double xSum = 0;
            double ySum = 0;

            for (Coordinate c : group) {
                xSum += c.x;
                ySum += c.y;
            }
            Coordinate centre = new Coordinate((int) (xSum / N), (int) (ySum / N));

            //now get variance
            int xVarSum = 0;
            int yVarSum = 0;
            int topRightQuadrant = 0;
            int topLeftQuadrant = 0;
            //use this to get angle
            for (Coordinate c : group) {
                xVarSum += squared(c.x - centre.x);
                yVarSum += squared(c.y - centre.y);
                if (c.y < centre.y) {
                    if (c.x > centre.x) {
                        topRightQuadrant++;
                    } else {
                        topLeftQuadrant++;
                    }
                }
            }
            double xVar = xVarSum / N;
            double yVar = yVarSum / N;
            double theta = Math.atan2(yVar, xVar); //original estimated theta

            if (topLeftQuadrant > topRightQuadrant) {
                theta = Math.PI - theta;
            }
            //  System.out.println("theta: " + Math.toDegrees(theta));
            boolean greenQuadrants[] = new boolean[4];
            int robotNum = 0;
            int orientation = 1; //1 for normal, 0 for opposite

            for (int t = 0; t < 4; t++) {
                double testTheta = theta + t * Math.PI / 2 + Math.PI / 4;
                int testX = centre.x + (int) (suitableLength * Math.cos(testTheta));
                int testY = centre.y - (int) (suitableLength * Math.sin(testTheta));
                //       System.out.println("test theta: " + testTheta);
                greenQuadrants[t] = isPixelInGreenColourRange(image, testX, testY, 2); //this gives 3 tries to detect green
            }
            if (greenQuadrants[0]) {
                if (greenQuadrants[3]) {
                    if (greenQuadrants[1]) {
                        robotNum = 4;
                    } else if (greenQuadrants[2]) {
                        robotNum = 5;
                    } else {
                        robotNum = 3;
                    }
                } else if (greenQuadrants[1]) {
                    robotNum = 5;
                    theta = theta - Math.PI;
                    orientation = 0;
                } else {
                    robotNum = 1;
                }
            } else if (greenQuadrants[1]) {
                if (greenQuadrants[2]) {
                    if (greenQuadrants[3]) {
                        robotNum = 4;
                        theta = theta - Math.PI;
                        orientation = 0;
                    } else {
                        robotNum = 3;
                        theta = theta - Math.PI;
                        orientation = 0;
                    }
                } else {
                    robotNum = 2;
                    theta = theta - Math.PI;
                    orientation = 0;
                }
            } else if (greenQuadrants[2]) {
                robotNum = 1;
                theta = theta - Math.PI;
                orientation = 0;
            } else if (greenQuadrants[3]) {
                robotNum = 2;
            } else {
                if (oldRobotNum >= 0) {
                    robotNum = oldRobotNum;
                    if (oldRobotOrientations[oldRobotNum] == 0) {
                        theta = theta - Math.PI;
                        orientation = 0;
                    }
                } else {
                    System.out.println("could not identify");
                }
            }
            //                      System.out.println("1st: " + greenQuadrants[0] + ", 2nd: " + greenQuadrants[1] + ", 3rd: " + greenQuadrants[2] + ", 4th: " + greenQuadrants[3]);
            //                      System.out.println(robotNum + (theta > 0 ? " up":" down"));

            if (robotNum > 0) {
                publish(new VisionData(new Coordinate(centre.x, centre.y), theta, "robot:" + robotNum));
                oldRobotPositions[robotNum-1] = new Coordinate(centre.x, centre.y);
                oldRobotOrientations[robotNum-1] = orientation;
            }
        }
    }

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

    private boolean isPixelInBallColourRange(BufferedImage image, int xPos, int yPos) {
        try {
            Color color = new Color(image.getRGB(xPos, yPos));
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
            int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
            int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;

            return isBall(y, u, v);
        } catch (ArrayIndexOutOfBoundsException ex) {
        //    System.out.println("pixel out of bounds!");
            return false;
        }
    }

     private boolean isPixelInTeamColourRange(BufferedImage image, int xPos, int yPos) {
         try {
             Color color = new Color(image.getRGB(xPos, yPos));
             int r = color.getRed();
             int g = color.getGreen();
             int b = color.getBlue();

             int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
             int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
             int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;

             return isTeam(y, u, v);
         } catch (ArrayIndexOutOfBoundsException ex) {
     //        System.out.println("pixel out of bounds!");
             return false;
         }
     }

    private boolean isPixelInGreenColourRange(BufferedImage image, int xPos, int yPos, int tryNum) {
        try {
            Color color = new Color(image.getRGB(xPos, yPos));
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
            int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
            int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;

            if (isGreen(y, u, v)) {
                return true;
            } else if (tryNum == 0) {
                return false;
            } else {
                return isPixelInGreenColourRange(image, xPos - 2, yPos - 2, tryNum - 1);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
     //       System.out.println("pixel out of bounds!");
            return false;
        }
    }

    private boolean isBall(int y, int u, int v) {

        return (y > ballMin[0] && y < ballMax[0] &&
                u > ballMin[1] && u < ballMax[1] &&
                v > ballMin[2] && v < ballMax[2]);

    	/*

    	return ( ((LookupTable.YTable[y] >> LookupTable.BALL_BIT_POS) & 1) &&
    			LookupTable.UTable[u] == LookupTable.BALL_COLOUR &&
    			LookupTable.VTable[v] == LookupTable.BALL_COLOUR);
    			*/
        
    }

    private boolean isTeam(int y, int u, int v) {

        return (y > teamMin[0] && y < teamMax[0] &&
                u > teamMin[1] && u < teamMax[1] &&
                v > teamMin[2] && v < teamMax[2]);

	}

	private boolean isGreen(int y, int u, int v) {


		return (y > greenMin[0] && y < greenMax[0] &&
				u > greenMin[1] && u < greenMax[1] &&
				v > greenMin[2] && v < greenMax[2]);


		/*
    	return (LookupTable.YTable[y] == LookupTable.GREEN_COLOUR &&
    			LookupTable.UTable[u] == LookupTable.GREEN_COLOUR &&
    			LookupTable.VTable[v] == LookupTable.GREEN_COLOUR);  */

	}


}
