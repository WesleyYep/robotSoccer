package strategy;

import bot.Robot;

/**
 * Created by Wesley on 21/01/2015.
 */
public abstract class Criteria {
	protected Robot bot;
	protected double ballX;
	protected double ballY;
	protected int index;

	public String getName() {
		return getClass().getSimpleName();
	}

	public void addRobot (Robot bot) {
		this.bot = bot;
        bot.criteriaName = getName();
	}

	public void setBallPosition(double x, double y) {
		this.ballX = x;
		this.ballY = y;
	}

	@Override
	public String toString() {
		return getName();
	}

	public abstract boolean isMet();

	protected double squared (double x) {
		return x * x;
	}

	protected double getDistanceToTarget(Robot r, double targetX, double targetY) {
		return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
	}

}
