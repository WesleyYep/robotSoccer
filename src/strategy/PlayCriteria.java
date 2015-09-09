package strategy;

import bot.Robots;
import ui.Ball;

/**
 * Criteria used to determine which play to be used in Situation
 * Created by chan743 on 8/09/2015.
 */
public abstract class PlayCriteria extends Criteria {
	
	public PlayCriteria(Robots bots, Ball ball) {
		super(bots, ball);
	}
	
	/**
	 * Returns if play criteria is met
	 * @return
	 */
	public abstract boolean isMet();
}
