package strategy;

import bot.Robots;
import ui.Ball;

/**
 * Created by Wesley on 21/01/2015.
 */
public abstract class Criteria {
    public Robots bots;
    public Ball ball;

	public abstract String getName();

    public Criteria(Robots bots, Ball ball) {
    	this.bots = bots;
    	this.ball = ball;
    }

	@Override
	public String toString() {
		return getName();
	}
}
