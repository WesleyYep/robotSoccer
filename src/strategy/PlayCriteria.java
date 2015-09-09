package strategy;

import bot.Robots;
import ui.Ball;

/**
 * Created by chan743 on 8/09/2015.
 */
public abstract class PlayCriteria extends Criteria {
	
	public PlayCriteria(Robots bots, Ball ball) {
		super(bots, ball);
	}
	
    public abstract boolean isMet();
}
