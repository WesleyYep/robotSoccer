package criteria;

import bot.Robot;
import strategy.Criteria;

/**
 * Created by Wesley on 21/03/2015.
 */
public class NegativeSituation extends Criteria {
    @Override
    public String getName() {
        return "Negative Situation";
    }

    @Override
    public boolean isMet() {
        Robot r = bot;

        // return true if robot is at least 25cm left of ball or is pointing towards and less then 25cm behind
        //System.out.println(r.getTheta());
        return r.getXPosition() - ballX > 5;
    }
}
