package vision;

import data.RobotData;
import data.VisionData;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import ui.ColourPanel;
import ui.SamplingPanel;
import ui.WebcamDisplayPanel.ViewState;
import ui.WebcamDisplayPanelListener;
import utils.Geometry;
import utils.Image;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 6/02/2015.
 */

public class VisionWorker implements WebcamDisplayPanelListener {

	private Mat dilateKernel, erodeKernel;
	private SamplingPanel ballSP, teamSP, greenSP;

	private boolean isTestingColour = false;

	private List<VisionListener> listeners = new ArrayList<VisionListener>();
	private List<MatOfPoint> ballContours, teamContours, greenContours, opponentContours;

	private ColourPanel colourPanel;

	private int robotMinSize;
	private int ballMinSize;
	private int greenMinSize;
	
	private int robotMaxSize;
    private int ballMaxSize;
    private int greenMaxSize;

    private Scalar ballMin, ballMax, teamMin, teamMax, greenMin, greenMax;

    private List<MatOfPoint> correctBallContour;

	private Point[] oldRobotPositions = {new Point(),new Point(),new Point(),new Point(),new Point()};

	private ViewState webcamDisplayPanelState;
	private List<MatOfPoint> correctGreenContour;
	private List<MatOfPoint> correctTeamContour;
	private KalmanFilter kFilter;
	
	private static final int KERNELSIZE = 3;

	public VisionWorker(ColourPanel cp) {
		colourPanel = cp;

		ballSP = colourPanel.ballSamplingPanel;
		teamSP = colourPanel.teamSamplingPanel;
		greenSP = colourPanel.greenSamplingPanel;
		
		correctBallContour = new ArrayList<MatOfPoint>();
		correctTeamContour = new ArrayList<MatOfPoint>();
		correctGreenContour = new ArrayList<MatOfPoint>();

	}

	@Override
	public void imageUpdated(Mat image) {
		if (webcamDisplayPanelState == ViewState.CONNECTED) {

            Mat webcamImageMat = image;
            // Full range HSV. Range 0-255.
	    	Imgproc.cvtColor(webcamImageMat, webcamImageMat, Imgproc.COLOR_BGR2HSV_FULL);
	    	
			Mat ballBinary, teamBinary, greenBinary, opponentBinary;

			dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(KERNELSIZE, KERNELSIZE));
			erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(KERNELSIZE, KERNELSIZE));
			//kFilter = new KalmanFilter();
			// Get the sampling panel values.
			double[] hsvBallMin = {
					ballSP.getLowerBoundForH(),
					ballSP.getLowerBoundForS(),
					ballSP.getLowerBoundForV()
			};

			double[] hsvBallMax = {
					ballSP.getUpperBoundForH(),
					ballSP.getUpperBoundForS(),
					ballSP.getUpperBoundForV()
			};

			double[] hsvTeamMin = {
					teamSP.getLowerBoundForH(),
					teamSP.getLowerBoundForS(),
					teamSP.getLowerBoundForV()
			};

			double[] hsvTeamMax = {
					teamSP.getUpperBoundForH(),
					teamSP.getUpperBoundForS(),
					teamSP.getUpperBoundForV()
			};

			double[] hsvGreenMin = {
					greenSP.getLowerBoundForH(),
					greenSP.getLowerBoundForS(),
					greenSP.getLowerBoundForV()
			};

			double[] hsvGreenMax = {
					greenSP.getUpperBoundForH(),
					greenSP.getUpperBoundForS(),
					greenSP.getUpperBoundForV()
			};

			// Create the scalar values.
			ballMin = new Scalar(hsvBallMin[0], hsvBallMin[1], hsvBallMin[2]);
			ballMax = new Scalar(hsvBallMax[0], hsvBallMax[1], hsvBallMax[2]);
			teamMin = new Scalar(hsvTeamMin[0], hsvTeamMin[1], hsvTeamMin[2]);
			teamMax = new Scalar(hsvTeamMax[0], hsvTeamMax[1], hsvTeamMax[2]);
			greenMin = new Scalar(hsvGreenMin[0], hsvGreenMin[1], hsvGreenMin[2]);
			greenMax = new Scalar(hsvGreenMax[0], hsvGreenMax[1], hsvGreenMax[2]);

			// Contour points.
			ballContours = new ArrayList<MatOfPoint>();
			teamContours = new ArrayList<MatOfPoint>();
			greenContours = new ArrayList<MatOfPoint>();

			// Get the minimum lengths
			ballMinSize = colourPanel.getBallSizeMinimum();
			robotMinSize = colourPanel.getRobotSizeMinimum();
			greenMinSize = colourPanel.getGreenSizeMinimum();
			
			//Get the maximum length
			ballMaxSize = colourPanel.getBallSizeMaximum();
			robotMaxSize = colourPanel.getRobotSizeMaximum();
			greenMaxSize = colourPanel.getGreenSizeMaximum();

			// Create the binary matrix.
			ballBinary = new Mat(webcamImageMat.size(), CvType.CV_8UC1);
			teamBinary = new Mat(webcamImageMat.size(), CvType.CV_8UC1);
			greenBinary = new Mat(webcamImageMat.size(), CvType.CV_8UC1);

			// Ball
			Core.inRange(webcamImageMat, ballMin, ballMax, ballBinary);
		//	Imgproc.erode(ballBinary, ballBinary, erodeKernel);
		//	Imgproc.dilate(ballBinary, ballBinary, dilateKernel);
			Imgproc.findContours(ballBinary, ballContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

			int ballX = 0, ballY = 0;
			correctBallContour.clear();
			for (int i = 0; i < ballContours.size(); i++) {
				double area = Imgproc.contourArea(ballContours.get(i));
				if (ballMinSize <= area && area <= ballMaxSize) {
					Moments m = Imgproc.moments(ballContours.get(i));
					correctBallContour.add(ballContours.get(i));
					ballX = (int) (m.get_m10() / m.get_m00());
					ballY = (int) (m.get_m01() / m.get_m00());
					
					
					//Imgproc.drawContours(webcamImageMat, ballContours, i, new Scalar(255, 255, 255));
					//centerPoint.add(new Point(ballX, ballY));

					// Ball position update.
					notifyListeners(new VisionData(new Point(ballX, ballY), 0, "ball"));
				}
			}

			// Team
			Core.inRange(webcamImageMat, teamMin, teamMax, teamBinary);
			Imgproc.erode(teamBinary, teamBinary, erodeKernel);
			Imgproc.dilate(teamBinary, teamBinary, dilateKernel);
			Imgproc.findContours(teamBinary, teamContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

			ballBinary.release();
			teamBinary.release();

			// Create robot data.
			RobotData[] data = new RobotData[5];

			int numRobots = 0;
			int teamX = 0, teamY = 0;
			correctTeamContour.clear();
			for (int i = 0; i < teamContours.size(); i++) {
				double area = Imgproc.contourArea(teamContours.get(i));
				if (robotMinSize <= area && area <= robotMaxSize ) {
					Moments m = Imgproc.moments(teamContours.get(i));
					teamX = (int) (m.get_m10() / m.get_m00());
					teamY = (int) (m.get_m01() / m.get_m00());
					correctTeamContour.add(teamContours.get(i));
					//Imgproc.drawContours(webcamImageMat, teamContours, i, new Scalar(0, 255, 128));

					// Get the rotated rect and find the points.
					MatOfPoint2f teamContour2f = new MatOfPoint2f();
					teamContours.get(i).convertTo(teamContour2f, CvType.CV_32FC2);
				//	RotatedRect patch1 = Imgproc.fitEllipse(teamContour2f);
				//	System.out.println("angle ellipse - degrees:" + patch1.angle);
					RotatedRect patch = Imgproc.minAreaRect(teamContour2f);
//					System.out.println("angle rect - degrees:" + patch.angle);
					org.opencv.core.Point[] p = new org.opencv.core.Point[4];
					patch.points(p);

					data[numRobots] = new RobotData(p, new org.opencv.core.Point(teamX, teamY));
//
//					double longPairDist = data[numRobots].getLongPair().getEuclideanDistance();
//					double shortPairDist = data[numRobots].getShortPair().getEuclideanDistance();
					
//					if ((longPairDist/shortPairDist) < 2.5) {
//						data[numRobots] = null;
//					} else {
						numRobots++;
//					}
					/*
				for (int k = 0; k < p.length; k++) {
					Core.line(webcamImageMat, p[k], p[(k + 1) % 4], new Scalar(255, 255, 255));
				}
					 */

					//centerPoint.add(new Point(teamX, teamY));

					if (numRobots >= 5) {
						break;
					}
				}
			}

			// Green
//			Core.inRange(webcamImageMat, greenMin, greenMax, greenBinary);
//			Imgproc.erode(greenBinary, greenBinary, erodeKernel);
//			Imgproc.dilate(greenBinary, greenBinary, dilateKernel);
//			Imgproc.findContours(greenBinary, greenContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//			correctGreenContour.clear();
//			int greenX = 0, greenY = 0;
//			for (int i = 0; i < greenContours.size(); i++) {
//				double area =  Imgproc.contourArea(greenContours.get(i));
//				if (greenMinSize <= area && area <= greenMaxSize) {
//					Moments m = Imgproc.moments(greenContours.get(i));
//					greenX = (int) (m.get_m10() / m.get_m00());
//					greenY = (int) (m.get_m01() / m.get_m00());
//					correctGreenContour.add(greenContours.get(i));
//					for (int j = 0; j < data.length; j++) {
//						if (data[j] != null) {
//							data[j].addGreenPatch(new org.opencv.core.Point(greenX, greenY));
//						}
//					}
//
//					//centerPoint.add(new Point(greenX, greenY));
//					//Imgproc.drawContours(webcamImageMat, greenContours, i, new Scalar(180, 105, 255));
//				}
//			}
			
			int[] robotNumber = new int[5];
			int robotCount = 0;
			// Update robot positions.
			for (RobotData rd : data) {
				if (rd != null) {
					//int robotNum = rd.robotIdentification();
                    int robotNum = identify(rd, webcamImageMat);
					if (robotNum > 0) {
                        Point pos = new Point((int) rd.getTeamCenterPoint().x, (int) rd.getTeamCenterPoint().y);
                        double distance =  Geometry.euclideanDistance(pos, oldRobotPositions[robotNum-1]);

                        if (distance < 15) { //change this if needed //todo
//						System.out.println(pos.x + ", " + pos.y);
						notifyListeners(new VisionData(pos, rd.getTheta(), "robot:" + robotNum));
                        }
                        oldRobotPositions[robotNum-1] = pos;
                    }

				} else {
					robotNumber[robotCount] = -1;
					robotCount++;	
				}
			}

			/*
			int[] robotDuplicate = new int[5];
			boolean isDuplicate = false;
			for (int i = 0; i<robotDuplicate.length; i++) {
				if (robotNumber[i]>=0) {
					robotDuplicate[robotNumber[i]]++;
					if (robotDuplicate[robotNumber[i]] > 2) {
						isDuplicate = true;
						break;
					}
				}
			} */
			
		}
	}

    private int identify(RobotData rd, Mat image) {
		boolean[] areasAreBlack = new boolean[4];
        double startTheta = rd.getLongPair().getTheta(); //in degrees

        for (int i = 0; i < 4; i++) { //loop through each quadrant and check if black pixel
			double dist = 8;//Math.sqrt(2 * Math.pow(rd.getShortPair().getEuclideanDistance(),2));
		//	System.out.println("short pair dist: " + rd.getShortPair().getEuclideanDistance());
		//	System.out.println("dist - " + dist);
			double theta = Math.toRadians(startTheta + i*90 +45); //eg. the theta of the middle of each quadrant
			Point centre = rd.getTeamCenterPoint();
			//get coordinates of the point to check
            int x = (int) (centre.x + dist * Math.cos(theta));
            int y = (int) (centre.y + dist * Math.sin(theta));
		//	System.out.println("x: " + x + " - y: " + y);
		//	System.out.println("width: " + image.size().width + "height: " + image.size().height);  
		//	System.out.println("quadrant: " + i);
			areasAreBlack[i] = isBlack(image.get(y, x));
		}
        int robotNum = 1;
		if (areasAreBlack[0] && areasAreBlack[1] && areasAreBlack[3]) {rd.setTheta(startTheta+180); robotNum = 2;}
        else if (areasAreBlack[1] && areasAreBlack[2] && areasAreBlack[3]) {rd.setTheta(startTheta); robotNum = 2;}
        else if (areasAreBlack[0] && areasAreBlack[2] && areasAreBlack[3]) {rd.setTheta(startTheta+180); robotNum = 1;}
        else if (areasAreBlack[0] && areasAreBlack[1] && areasAreBlack[2]) {rd.setTheta(startTheta); robotNum = 1;}
        else if (areasAreBlack[0] && areasAreBlack[3]) {rd.setTheta(startTheta+180); robotNum = 3;}
        else if (areasAreBlack[1] && areasAreBlack[2]) {rd.setTheta(startTheta); robotNum = 3;}
        else if (areasAreBlack[3]) {rd.setTheta(startTheta+180); robotNum = 4;}
        else if (areasAreBlack[1]) {rd.setTheta(startTheta); robotNum = 4;}
        else if (areasAreBlack[0]) {rd.setTheta(startTheta+180); robotNum = 5;}
        else if (areasAreBlack[2]) {rd.setTheta(startTheta); robotNum = 5;}
	//	System.out.println(robotNum + " detected");
		return robotNum;
    }

    private boolean isBlack(double[] scalar) {
		try {
	//		System.out.println("h: " + scalar[0] + "s: " + scalar[1] + "v: " + scalar[2]);
			return (scalar[2] < 130);
		}
		catch (ArrayIndexOutOfBoundsException | NullPointerException e){
			System.out.println(scalar);
			return false;
		}

    }

    @Override
	public void viewStateChanged(ViewState currentViewState) {
		webcamDisplayPanelState = currentViewState;
	}

	public void notifyListeners(VisionData visionData) {
		for (VisionListener l : listeners) {
			l.receive(visionData);
		}
	}

	public List<MatOfPoint> getBallContours() {
		return correctBallContour;
	}

	public List<MatOfPoint> getTeamContours() {
		return correctTeamContour;
	}

	public List<MatOfPoint> getGreenContours() {
		return correctGreenContour;
	}

	public List<MatOfPoint> getOpponentContours() {
		return opponentContours;
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