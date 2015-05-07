package strategy;

import vision.KalmanFilter;
import Paths.Path;
import bot.Robots;

/**
 * Created by Wesley on 21/01/2015.
 */
public abstract class Action {
	protected Robots bots;
	protected double ballX;
	protected double ballY;
	protected double predX;
	protected double predY;
	protected int index;
	protected Path path;
//	protected static KalmanFilter kFilter = new KalmanFilter();

	public String getName() {
        return getClass().getSimpleName();
    }

	public void addRobot (Robots bots, int index) {
		this.bots = bots;
		this.index = index;
	}

	public void setBallPosition(double x, double y) {
		this.ballX = x;
		this.ballY = y;
	}

	@Override
	public String toString() {
		return getName();
	}

	public abstract void execute();

	public void setPredBallPosition(double predictedBallX, double predictedBallY) {
		this.predX = predictedBallX;
		this.predY = predictedBallY;
	}

}
