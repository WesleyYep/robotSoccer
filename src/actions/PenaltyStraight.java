package actions;

import strategy.Action;

/**
 * Created by Wesley on 11/07/2015.
 */
public class PenaltyStraight extends Action {

    @Override
    public void execute() {
        bot.linearVelocity = 3;
        bot.angularVelocity = 0;
    }

}
