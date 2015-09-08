package strategy;

import bot.Robot;
import bot.Robots;
import ui.Ball;

/**
 * Created by Wesley on 21/01/2015.
 */
public abstract class Criteria {
    public Robots bots;
    public Ball ball;

	public abstract String getName();

    public Criteria() {
        bots = Robots.getInstance();
        ball = Ball.getInstance();
    }

	@Override
	public String toString() {
		return getName();
	}
}
