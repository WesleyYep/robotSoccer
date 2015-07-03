package game;

import bot.Robot;
import bot.Robots;
import communication.Sender;
import communication.SenderListener;
import controllers.FieldController;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import ui.RobotSoccerMain;
import vision.KalmanFilter;

import java.util.TimerTask;

public class Tick implements SenderListener {
	private FieldController fieldController;
	private Robots bots;
	private RobotSoccerMain main;
	private int count = 0;
	private boolean runStrat = false;
    private boolean runSetPlay = false;
    private boolean runManualMovement = false;
	private boolean lastSend = false;
    private Sender sender;
	private double x = 0;
	private long time = 0;
	//private Physics physics;

	private int stateSize = 6;
	private int measSize = 4;
	private int contrSize = 0;
	private int number = 0;
	private KalmanFilter kFilter = new KalmanFilter(stateSize,measSize,contrSize,CvType.CV_32F);
	private Mat state;
	private Mat meas;

	private boolean firstTime = true;
    public static double PREDICT_TIME = 0.5;

    public Tick(RobotSoccerMain main) {
		this.main = main;
		this.fieldController = main.getFieldController();
		this.bots = fieldController.getRobots();
		//physics = new Physics();

		/*
		meas = new Mat(measSize,1,CvType.CV_32F);
		Core.setIdentity(kFilter.transitionMatrix);
		kFilter.transitionMatrix.put(0, 1, 0.005);
		kFilter.transitionMatrix.put(1, 2, 0.005);
		kFilter.transitionMatrix.put(3, 4, 0.005);
		kFilter.transitionMatrix.put(4, 5, 0.005);

		//System.out.println(kFilter.transitionMatrix.dump());

		kFilter.measurementMatrix = Mat.zeros(measSize, stateSize,CvType.CV_32F);
		kFilter.measurementMatrix.put(0,0,1);
		kFilter.measurementMatrix.put(1,3,1);
		//kFilter.measurementMatrix.put(2,3,1);
		//kFilter.measurementMatrix.put(3,4,1);

		System.out.println(kFilter.measurementMatrix.dump());

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
		Core.multiply(kFilter.measurementNoiseCov, new Scalar(10000), kFilter.measurementNoiseCov); */



		state = new Mat(stateSize,1,CvType.CV_32F);
		meas = new Mat(measSize,1,CvType.CV_32F);

		Core.setIdentity(kFilter.transitionMatrix);
		kFilter.transitionMatrix.put(0, 2, 0.005);
		kFilter.transitionMatrix.put(1, 3, 0.005);

		kFilter.measurementMatrix = Mat.zeros(measSize, stateSize, CvType.CV_32F);
		kFilter.measurementMatrix.put(0, 0, 1.0f);
		kFilter.measurementMatrix.put(1, 1, 1.0f);
		kFilter.measurementMatrix.put(2, 4, 1.0f);
		kFilter.measurementMatrix.put(3, 5, 1.0f);

		kFilter.processNoiseCov.put(0, 0, 0.02);
		kFilter.processNoiseCov.put(1, 1, 0.02);
		kFilter.processNoiseCov.put(2, 2, 0.02);
		kFilter.processNoiseCov.put(3, 4, 0.02);
		kFilter.processNoiseCov.put(4, 5, 0.02);
		kFilter.processNoiseCov.put(5, 5, 0.02);

		Core.setIdentity(kFilter.measurementNoiseCov, new Scalar(0.1));
	}

	public void run() {
        //link to actions class somewhere here, set linearVelocity and angularVelocity of robots.;
		if (!main.isManualControl()) {
			if (runStrat) {
				fieldController.executeStrategy();
			} else if (runSetPlay) {
				fieldController.executeSetPlay();
            } else if (runManualMovement) {
                fieldController.executeManualControl();
            }else {
				bots.stopAllMovement();
			}
		} else {
		//	Robot r = bots.getRobot(0);
		//	System.out.println("r.x r.y: " + r.getXPosition() + " " + r.getYPosition() + " lin ang " + r.linearVelocity + " " + r.angularVelocity + " " + System.currentTimeMillis() );
		}
		fieldController.redrawArea();

		/*
		if (!firstTime) {
			kFilter.predict(new Mat());
		}

		meas.put(0, 0, field.getBallX());
		meas.put(1,0,field.getBallY());
		//meas.put(1, 0, 10);
		//meas.put(3, 0, 0);

		if (firstTime) {

			kFilter.statePost.put(0, 0, field.getBallX());
			kFilter.statePost.put(3, 0,field.getBallY());
			firstTime = false;
		}
		else {
			kFilter.correct(meas);
			x = r.getXPosition();

		}
		Mat temp = kFilter.predictNextPosition(1.0);
		//System.out.println(temp.dump());
		//System.out.println(r.getXPosition() +  " " + r.getYPosition());
		System.out.println(kFilter.statePost.dump());
		//System.out.println(r.getXPosition() +  " " + r.getYPosition());
		System.out.println(field.getBallX() + " " + field.getBallY());
		field.setPredPoint(temp.get(0, 0)[0],temp.get(3, 0)[0]);
		*/
			double dT = (System.currentTimeMillis()-time)/1000.0;
			//30-35ms
			//System.out.println(dT + " " + System.currentTimeMillis() + " " + time);
			time = System.currentTimeMillis();
			kFilter.transitionMatrix.put(0, 2, dT);
			kFilter.transitionMatrix.put(1, 3, dT);

			kFilter.predict(new Mat());

			meas.put(0, 0, fieldController.getBallX());
			meas.put(1,0,fieldController.getBallY());
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

		Mat temp = kFilter.predictNextPosition(PREDICT_TIME);
		//System.out.println(temp.dump());
		//System.out.println(r.getXPosition() +  " " + r.getYPosition());
			//System.out.println(field.getBallX() + " " + field.getBallY());
			fieldController.setPredPoint(temp.get(0, 0)[0],temp.get(1, 0)[0]);
		//	System.out.println(temp.get(0, 0)[0] + " " + temp.get(1, 0)[0]);
		temp.release();	
		if (main.isSimulation()) {
			if (sender != null) {
				sender.sendStuff(createBotCoordinatesMessage());
			}
		} else {
			//if (runStrat || comPanel.isManualControl()) {
				bots.send();
						/*
				//System.out.println("sending");
				lastSend = true;
			} else if (lastSend)  {
				Robot r = bots.getRobot(0);
				System.out.println("r.x r.y: " + r.getXPosition() + " " + r.getYPosition() + " lin ang " + r.linearVelocity + " " + r.angularVelocity + " " + System.currentTimeMillis() );
				bots.send();
				//System.out.println("sending");
				lastSend = false;
				number = 1;
			}

			if (number >= 1 && number < 11) {
				number++;
				Robot r = bots.getRobot(0);
				System.out.println("r.x r.y: " + r.getXPosition() + " " + r.getYPosition() + " lin ang " + r.linearVelocity + " " + r.angularVelocity + " " + System.currentTimeMillis() );
			} */

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

    public void runSetPlay (boolean run) {
        runSetPlay = run;
        if (run) {
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (runSetPlay) { //if set play is already stopped, don't transition into normal play
                        runSetPlay = false;
                        runStrat = true;
                    }
                }
            }, 10000);
        }
    }

	public void runStrategy(boolean run) {
		runStrat = run;
	}


    public void runManualMovement(boolean b) {
        runManualMovement = b;
    }
}
