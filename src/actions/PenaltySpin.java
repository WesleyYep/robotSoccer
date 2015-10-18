package actions;

import strategy.Action;

/**
 * Created by Wesley on 11/07/2015.
 */
public class PenaltySpin extends Action {
//used for goalkeeper
    {
        parameters.put("speed", 3);
    }


    @Override
    public void execute() {
        int speed = parameters.get("speed");

        if (speed > 100) {
            speed = -(speed - 100);
        }
        bot.linearVelocity = speed;
        bot.angularVelocity = 0;

        if(bot.getXPosition() > 110) {
            bot.linearVelocity = 0.5;
            bot.angularVelocity = bot.getYPosition() > 90 ? 30 : -30;
        }

    }

}
