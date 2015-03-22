package criteria;

import bot.Robot;
import strategy.Criteria;

/**
 * Created by Wesley on 21/03/2015.
 */
public class PositiveSituation extends Criteria {
    @Override
    public String getName() {
        return "Positive Situation";
    }

    @Override
    public boolean isMet() {
        Robot r = bots.getRobot(index);
        //simply return true if robot is to the left of ball
        return r.getXPosition() < ballX;
    }
}
