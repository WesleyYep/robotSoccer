package actions;

import strategy.Action;

/**
 * Created by Wesley on 11/07/2015.
 */
public class PenaltySpin extends Action {
    //for this method, parameter spin = 0 means spin clockwise, spin = 1 means anticlockwise


    {
        if (!parameters.containsKey("spin")) {
            parameters.put("spin", 0); //default clockwise
        }
    }

    @Override
    public void execute() {
        bot.linearVelocity = 0.5;
        if (parameters.get("spin") == 0) {
            bot.angularVelocity = -7;
        } else {
            bot.angularVelocity = 7;
        }
    }

}
