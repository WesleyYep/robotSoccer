package strategy;

import bot.Robots;
import ui.Ball;

/**
 * Criteria which is used to determine strategy decisions
 * Created by Wesley on 21/01/2015.
 */
public abstract class Criteria {
	
	private Robots bots;
	private Ball ball;
	
	public Criteria(Robots bots, Ball ball) {
		this.bots = bots;
		this.ball = ball;
	}
	
	public final Robots getRobots() {
		return bots;
	}
	
	public final void setRobots(Robots bots) {
		this.bots = bots;
	}
	
	public final Ball getBall() {
		return ball;
	}
	
	public final void setBall(Ball ball) {
		this.ball = ball;
	}
	
	/**
	 * Returns the name of the Criteria
	 * @return Criteria Name
	 */
    public abstract String getName();
}
