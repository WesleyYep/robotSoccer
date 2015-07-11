package criteria;

import bot.Robot;
import strategy.Criteria;

public class Permanent extends Criteria {
    @Override
    public String getName() {
        return "permanent";
    }

	/*
	 * Incomplete?
	 */

    @Override
    public boolean isMet() {
        return true;
    }
}
