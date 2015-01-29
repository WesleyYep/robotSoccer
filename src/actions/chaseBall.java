package actions;

import bot.Robot;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class chaseBall extends Action{

    @Override
    public String getName() {
        return "Chase Ball";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);
        r.linearVelocity = 0.5;

    }
}
