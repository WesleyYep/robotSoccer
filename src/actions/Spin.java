package actions;

import strategy.Action;

/**
 * Created by Wesley on 11/07/2015.
 */
public class Spin extends Action {

    {
        parameters.put("spin", 0); //1 for clockwise, -1 for anticlockwise
    }


    @Override
    public void execute() {
        int spin = parameters.get("spin");
        bot.linearVelocity = 0;
        bot.angularVelocity = spin == 1 ? 30 : -30;
    }

}
