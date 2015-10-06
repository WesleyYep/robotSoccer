package actions;

import strategy.Action;

/**
 * Created by Wesley on 11/07/2015.
 */
public class PenaltySpinToStraight extends Action {
    //for this method, parameter spin = 0 means spin clockwise, spin = 1 means anticlockwise


    {
        if (!parameters.containsKey("spin")) {
            parameters.put("spin", 0); //default clockwise
        }
    }

    @Override
    public void execute() {
        int mod = 1;
        if (bot.getTheta() > 0) {
            mod = -1;
        }

        if (Math.abs(bot.getTheta()) > 5) {
            bot.linearVelocity = 0.1;
            bot.angularVelocity = 1 * mod;
        } else {
            bot.angularVelocity = 0;
            bot.linearVelocity = 3;
        }



    }

}
