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
import data.RobotData;

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
    private int greenMinSize;
    
    private int robotMaxSize;
    private int ballMaxSize;
    private int greenMaxSize;
    
    private int suitableLength;
    private Coordinate[] oldRobotPositions = null;
    private int[] oldRobotOrientations = new int[5];
    private int numberOfGroups = 0;
    public List<Point> centerPoint = new ArrayList<Point>();

    public VisionWorker(ColourPanel cp, WebcamController wc, WebcamDisplayPanel wdp) {
        webcamController = wc;
        colourPanel = cp;
        webcamDisplayPanel = wdp;
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
            try {
                long startTime = System.currentTimeMillis();

                double[] rgbBallMin = utils.ColorSpace.YUVToRGB(ballSP.getLowerBoundForY(), ballSP.getLowerBoundForU(), ballSP.getLowerBoundForV());
                double[] rgbBallMax = utils.ColorSpace.YUVToRGB(ballSP.getUpperBoundForY(), ballSP.getUpperBoundForU(), ballSP.getUpperBoundForV());
                double[] rgbTeamMin = utils.ColorSpace.YUVToRGB(teamSp.getLowerBoundForY(), teamSp.getLowerBoundForU(), teamSp.getLowerBoundForV());
                double[] rgbTeamMax = utils.ColorSpace.YUVToRGB(teamSp.getUpperBoundForY(), teamSp.getUpperBoundForU(), teamSp.getUpperBoundForV());
                double[] rgbGreenMin = utils.ColorSpace.YUVToRGB(greenSp.getLowerBoundForY(), greenSp.getLowerBoundForU(), greenSp.getLowerBoundForV());
                double[] rgbGreenMax = utils.ColorSpace.YUVToRGB(greenSp.getUpperBoundForY(), greenSp.getUpperBoundForU(), greenSp.getUpperBoundForV());

                ballMin = new Scalar(rgbBallMin[0], rgbBallMin[1], rgbBallMin[2]);
                ballMax = new Scalar(rgbBallMax[0], rgbBallMax[1], rgbBallMax[2]);
                teamMin = new Scalar(rgbTeamMin[0], rgbTeamMin[1], rgbTeamMin[2]);
                teamMax = new Scalar(rgbTeamMax[0], rgbTeamMax[1], rgbTeamMax[2]);
                greenMin = new Scalar(rgbGreenMin[0], rgbGreenMin[1], rgbGreenMin[2]);
                greenMax = new Scalar(rgbGreenMax[0], rgbGreenMax[1], rgbGreenMax[2]);
                List<MatOfPoint> ballContours = new ArrayList<MatOfPoint>();
                List<MatOfPoint> teamContours = new ArrayList<MatOfPoint>();
                List<MatOfPoint> greenContours = new ArrayList<MatOfPoint>();
                ballMinSize = colourPanel.getBallSizeMinimum();
                robotMinSize = colourPanel.getRobotSizeMinimum();
                greenMinSize = colourPanel.getGreenSizeMinimum();
                
                
                robotMaxSize = colourPanel.getRobotSizeMaximum();
                ballMaxSize = colourPanel.getBallSizeMaximum();
                greenMaxSize = colourPanel.getGreenSizeMaximum();

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
                    double area = Imgproc.contourArea(ballContours.get(i));
                    if (areaThreshold < area && area < ballMaxSize) {
                        Moments m = Imgproc.moments(ballContours.get(i));
                        ballX = (int) (m.get_m10() / m.get_m00());
                        ballY = (int) (m.get_m01() / m.get_m00());
                        Imgproc.drawContours(webcamImageMat, ballContours, i, new Scalar(255, 255, 255));
                        //centerPoint.add(new Point(ballX, ballY));
                        publish(new VisionData(new Coordinate(ballX, ballY), 0, "ball"));
                    }
                }

                // Team
                Core.inRange(webcamImageMat, teamMin, teamMax, teamBinary);
                Imgproc.erode(teamBinary, teamBinary, erodeKernel);
                Imgproc.dilate(teamBinary, teamBinary, dilateKernel);
                Imgproc.findContours(teamBinary, teamContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                RobotData[] data = new RobotData[5];
                int numRobots = 0;
                int teamX = 0, teamY = 0;
                for (int i = 0; i < teamContours.size(); i++) {
                    double areaThreshold = robotMinSize;
                    double area = Imgproc.contourArea(teamContours.get(i));
                    if (areaThreshold < area && area < robotMaxSize) {
                        Moments m = Imgproc.moments(teamContours.get(i));
                        teamX = (int) (m.get_m10() / m.get_m00());
                        teamY = (int) (m.get_m01() / m.get_m00());
                        Imgproc.drawContours(webcamImageMat, teamContours, i, new Scalar(0, 255, 128));
                        MatOfPoint2f teamContour2f = new MatOfPoint2f();
                        teamContours.get(i).convertTo(teamContour2f, CvType.CV_32FC2);
                        RotatedRect patch = Imgproc.minAreaRect(teamContour2f);
                        org.opencv.core.Point[] p = new org.opencv.core.Point[4];
                        patch.points(p); 
                        
                        data[numRobots] = new RobotData(p, new org.opencv.core.Point(teamX, teamY));
                        
                        //double ratio = data[numRobots].getLongPair().getEuclideanDistance()/data[numRobots].getShortPair().getEuclideanDistance();
                       // if (ratio < 2.5) { 
                        	data[numRobots] = null;
                       // } else {
                        numRobots++;
                       // }
                        for (int k = 0; k < p.length; k++) {
                            Core.line(webcamImageMat, p[k], p[(k + 1) % 4], new Scalar(255, 255, 255));
                        }
                        centerPoint.add(new Point(teamX, teamY));
                        if (numRobots >= 5) {
                            break;
                        }
                    }
                }

                // Green
                Core.inRange(webcamImageMat, greenMin, greenMax, greenBinary);
                Imgproc.erode(greenBinary, greenBinary, erodeKernel);
                Imgproc.dilate(greenBinary, greenBinary, dilateKernel);
                Imgproc.findContours(greenBinary, greenContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                int greenX = 0, greenY = 0;
                for (int i = 0; i < greenContours.size(); i++) {
                    double areaThreshold = greenMinSize;
                    double area = Imgproc.contourArea(greenContours.get(i));
                    if (areaThreshold < area && area < greenMaxSize) {
                        Moments m = Imgproc.moments(greenContours.get(i));
                        greenX = (int) (m.get_m10() / m.get_m00());
                        greenY = (int) (m.get_m01() / m.get_m00());
                        for (int j = 0; j < 5; j++) {
                            if (data[j] != null) {
                                data[j].addGreenPatch(new org.opencv.core.Point(greenX, greenY));
                            }
                        }
                        centerPoint.add(new Point(greenX, greenY));
                        Imgproc.drawContours(webcamImageMat, greenContours, i, new Scalar(180, 105, 255));
                    }
                }
                for (int j = 0; j < 5; j++) {
                    RobotData rd = data[j];
                    if (rd != null) {
                        int robotNum = rd.robotIdentification();

                        if (robotNum > 0) {
                            publish(new VisionData(new Coordinate((int) rd.getTeamCenterPoint().x, (int) rd.getTeamCenterPoint().y), rd.getTheta(), "robot:" + robotNum));
                        }
                    }
                }

                //System.out.println(System.currentTimeMillis() - startTime);
                webcamDisplayPanel.update(webcamImageMat);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
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

}