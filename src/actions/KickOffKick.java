package actions;

import strategy.Action;
import strategy.GameState;

/**
 * Created by Wesley on 11/07/2015.
 */
public class KickOffKick extends Action {

    @Override
    public void execute() {
        if ((System.currentTimeMillis()-GameState.getInstance().getLastStartedTime()) < 100) {
            bot.linearVelocity = 0.1;
            bot.angularVelocity = 0;
        } else {
     //       System.out.println((System.currentTimeMillis()-GameState.getInstance().getLastStartedTime()));
            bot.linearVelocity = -3;
            bot.angularVelocity = 0;
        }

    }

}
