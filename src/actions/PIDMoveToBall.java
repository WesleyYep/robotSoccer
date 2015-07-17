package actions;

import strategy.Action;

/**
 * Created by Wesley on 18/07/2015.
 */
public class PIDMoveToBall extends Action {

    @Override
    public void execute() {

        //get angle to ball
        double angleToBall = getTargetTheta(bot, ballX, ballY);
        bot.angularVelocity = Math.toRadians(angleToBall) * 3;
        bot.linearVelocity = 0.5;

    }


}
