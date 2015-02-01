package criteria;

import strategy.Criteria;
import bot.Robot;

public class permanent extends Criteria{
	@Override
    public String getName() {
        return "permanent";
    }

    @Override
    public boolean isMet() {
        Robot r = bots.getRobot(index);
        return true;
    }
}
