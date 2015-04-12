package game;

import bot.Robot;
import bot.Robots;
import communication.Receiver;
import communication.Sender;
import communication.SenderListener;
import ui.Field;
import ui.TestComPanel;
import utils.Geometry;
import vision.KalmanFilter;

import java.util.TimerTask;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Tick extends TimerTask implements SenderListener {
	private Field field;
	private Robots bots;
	private TestComPanel comPanel;
	
	private int count = 0;
	private boolean runStrat = false;
	private Sender sender;
	private double x = 0;
	private long time = 0;
	//private Physics physics;
	
	private int stateSize = 6;
	private int measSize = 4;
	private int contrSize = 0;
	private KalmanFilter kFilter = new KalmanFilter(stateSize,measSize,contrSize,CvType.CV_32F);
	private Mat state;
	private Mat meas;
	
	private boolean firstTime = true;
	
	

	public Tick(Field field, Robots bots, TestComPanel comPanel) {
		this.bots = bots;
		this.field = field;
		this.comPanel = comPanel;
		//physics = new Physics();
		
		meas = new Mat(measSize,1,CvType.CV_32F);
		Core.setIdentity(kFilter.transitionMatrix);
		kFilter.transitionMatrix.put(0, 1, 0.005);
		kFilter.transitionMatrix.put(1, 2, 0.005);
		kFilter.transitionMatrix.put(3, 4, 0.005);
		kFilter.transitionMatrix.put(4, 5, 0.005);
		
		//System.out.println(kFilter.transitionMatrix.dump());
		
		kFilter.measurementMatrix = Mat.zeros(measSize, stateSize,CvType.CV_32F);
		kFilter.measurementMatrix.put(0,0,1);
		kFilter.measurementMatrix.put(1,1,1);
		kFilter.measurementMatrix.put(2,3,1);
		kFilter.measurementMatrix.put(3,4,1);
		
		//System.out.println(kFilter.measurementMatrix.dump());
		
		kFilter.measurementNoiseCov = new Mat(measSize,measSize,CvType.CV_32F);
		Core.multiply(kFilter.measurementNoiseCov, new Scalar(25), kFilter.measurementNoiseCov);
		
		kFilter.processNoiseCov = Mat.zeros(stateSize, stateSize, CvType.CV_32F);
		kFilter.processNoiseCov.put(0, 0, 25);
		kFilter.processNoiseCov.put(1, 1, 10);
		kFilter.processNoiseCov.put(2, 2, 1);
		kFilter.processNoiseCov.put(3, 4, 25);
		kFilter.processNoiseCov.put(4, 5, 10);
		kFilter.processNoiseCov.put(5, 5, 1);
		
		kFilter.errorCovPre = Mat.eye(stateSize, stateSize, CvType.CV_32F);
		Core.multiply(kFilter.measurementNoiseCov, new Scalar(10000), kFilter.measurementNoiseCov); 
		
		
		/*
		state = new Mat(stateSize,1,CvType.CV_32F);
		meas = new Mat(measSize,1,CvType.CV_32F);
		
		Core.setIdentity(kFilter.transitionMatrix);
		
		kFilter.measurementMatrix = Mat.zeros(measSize, stateSize, CvType.CV_32F);
		kFilter.measurementMatrix.put(0, 0, 1.0f);
		kFilter.measurementMatrix.put(1, 1, 1.0f);
		kFilter.measurementMatrix.put(2, 4, 1.0f);
		kFilter.measurementMatrix.put(3, 5, 1.0f);
		
		kFilter.processNoiseCov.put(0, 0, 0.02);
		kFilter.processNoiseCov.put(1, 1, 0.02);
		kFilter.processNoiseCov.put(2, 2, 2.0f);
		kFilter.processNoiseCov.put(3, 4, 1.0f);
		kFilter.processNoiseCov.put(4, 5, 0.02);
		kFilter.processNoiseCov.put(5, 5, 0.02);
		kFilter.transitionMatrix.put(0, 2, 0.005);
		kFilter.transitionMatrix.put(1, 3, 0.005);
		Core.setIdentity(kFilter.measurementNoiseCov, new Scalar(0.1)); */
		
		
		
	}

	public void run() {
		//link to actions class somewhere here, set linearVelocity and angularVelocity of robots.;
		if (!comPanel.isManualControl()) {
			if (runStrat) {
				field.executeStrategy();
			} else {
				bots.stopAllMovement();
			}
		}
		field.repaint();
		
		long start = System.currentTimeMillis();
		Robot r = bots.getRobot(0);
		
		
		if (!firstTime) {
			kFilter.predict(new Mat());
		}
		
		meas.put(0, 0, r.getXPosition());
		meas.put(2,0,r.getYPosition());
		meas.put(1, 0, 10);
		meas.put(3, 0, 0);
		
		if (firstTime) {
			
			kFilter.statePost.put(0, 0, r.getXPosition());
			kFilter.statePost.put(3, 0, r.getYPosition());
			firstTime = false;
		}
		else {
			if (x != r.getXPosition()) {
				kFilter.correct(meas); 
				x = r.getXPosition();
				
			}
			
		}
		//Mat temp = kFilter.predictNextPosition(1.0);
		//System.out.println(temp.dump());
		//System.out.println(r.getXPosition() +  " " + r.getYPosition());
		//System.out.println(kFilter.statePost.dump());
		//System.out.println(r.getXPosition() +  " " + r.getYPosition());
		
			
		/*
			kFilter.predict(new Mat());
			
			meas.put(0, 0, r.getXPosition());
			meas.put(1,0,r.getYPosition());
			meas.put(2, 0, 7);
			meas.put(3, 0, 7);
			
			if (firstTime) {
				kFilter.errorCovPre.put(0, 0, 1);
				kFilter.errorCovPre.put(1, 1, 1);
				kFilter.errorCovPre.put(2, 2, 1);
				kFilter.errorCovPre.put(3, 3, 1);
				kFilter.errorCovPre.put(4, 4, 1);
				kFilter.errorCovPre.put(5, 5, 1);
				
				firstTime = false;
			}
			else {
				kFilter.correct(meas); 
		
		
				//System.out.println(kFilter.statePost.dump());
				//System.out.println(r.getXPosition() +  " " + r.getYPosition());
			}
			
		Mat temp = kFilter.predictNextPosition(1.0);
		System.out.println(temp.dump());
		System.out.println(r.getXPosition() +  " " + r.getYPosition()); */
		
		
		
		/*
		if (x != r.getXPosition()) {
			kFilter.process(r.getXPosition(), r.getYPosition());
			
			x = r.getXPosition();
			kFilter.predict(1);
			System.out.println(count + " Estimated: " + kFilter.getEstimatedX() + " " + kFilter.getEstimatedY() + " " 
			+ kFilter.getLinearVelocity() + " " + r.getXPosition() +  " " + r.getYPosition());
			System.out.println(System.currentTimeMillis() + " Predicted: " + kFilter.getPredX() + " " + kFilter.getPredY()
					+ " " + kFilter.getPredXVelocity());
		}
		
		
		
		//System.out.println(r.getXPosition() + " " + r.getYPosition());
		
		// Find the distance between the kalman filter point and the current ball point.
		double distance = Geometry.euclideanDistance(new org.opencv.core.Point(r.getXPosition(), r.getYPosition()), 
				new org.opencv.core.Point(kFilter.getPredX(), kFilter.getPredY()));
		
		// Find the point that is x distance from point 1 along the vector.
		// TODO needs better way.
		double[] vector = new double[2];
		vector[0] = kFilter.getPredX() - r.getXPosition();
		vector[1] = kFilter.getPredY() - r.getYPosition();
		
		double magnitude = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
		
		double[] normalisedVector = new double[2];
		normalisedVector[0] = vector[0] / magnitude;
		normalisedVector[1] = vector[1] / magnitude;
		
		
		
		
		System.out.println((r.getXPosition() - distance * normalisedVector[0]) + " " 
				+ (r.getYPosition() - distance * normalisedVector[1])
				+ " " + r.getXPosition()
				+ " " + r.getYPosition()); */
		//System.out.println("Time taken: " +  (System.currentTimeMillis()-start));

		if (comPanel.isSimulation()) {
			if (sender != null) {
				sender.sendStuff(createBotCoordinatesMessage());
			}
		} else {
			bots.send();
		}
		
		count++;
		if (count>=200) {
			count = 0;
		}
	}

	private String createBotCoordinatesMessage() {
		StringBuilder outputMessage = new StringBuilder();
		Robot[] botArray = bots.getRobots();
		for (int i = 0; i < Robots.BOTTEAMMEMBERCOUNT; i++) {
            //test adjusting simulation to match actual
			outputMessage.append("lin bot" + i + ": " + botArray[i].linearVelocity/18.52*21.90 + System.lineSeparator());
			outputMessage.append("ang bot" + i + ": " + botArray[i].angularVelocity/34.39*40.03 + System.lineSeparator());
//            outputMessage.append("lin bot" + i + ": " + botArray[i].linearVelocity + System.lineSeparator());
//            outputMessage.append("ang bot" + i + ": " + botArray[i].angularVelocity + System.lineSeparator());
        }
		return outputMessage.toString();
	}


	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public void startGame(boolean start) {
		runStrat = start;
	}

	public void runStrategy(boolean run) {
		runStrat = run;
	}


}
