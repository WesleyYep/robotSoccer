package actions;

import strategy.Action;

/**
 * Created by Wesley on 11/07/2015.
 */
public class PenaltyFake extends Action {
    //for this method, parameter spin = 0 means spin clockwise, spin = 1 means anticlockwise

    {
        if (!parameters.containsKey("spin")) {
            parameters.put("spin", 0); //default clockwise
        }
    }

    @Override
    public void execute() {

        double angleToGoal = getTargetTheta(bot, 220, 105);

        if (Math.abs(angleToGoal) > 170) {
            bot.linearVelocity = -1;
            bot.angularVelocity = 0;
        } else {
            bot.linearVelocity = -0.2;
            bot.angularVelocity = -2*(Math.PI-Math.toRadians(angleToGoal));
        }

    }

}
