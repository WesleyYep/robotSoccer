package actions;

import strategy.Action;
import strategy.GameState;

/**
 * Created by Wesley on 11/07/2015.
 */
public class KickOffSurround extends Action {

    {
        parameters.put("speed", 3);
    }

    @Override
    public void execute() {



        int speed = parameters.get("speed");

        if (speed > 100) {
            speed = -(speed - 100);
        }
        if (Math.abs(bot.getTheta()) < 90) {
            bot.linearVelocity = speed / 10.0;
        } else {
            bot.linearVelocity = speed / 30.0;
        }
        bot.angularVelocity = 0;

        if (System.currentTimeMillis() - GameState.getInstance().getLastStartedTime() > 2500) {
            if (Math.abs(bot.getTheta()) < 90) {
                bot.linearVelocity = 1;
            } else {
                bot.linearVelocity = -0.5;
            }
        }

//        double angleDifferenceFromGoal = angleDifferenceFromGoal(bot.getXPosition(), bot.getYPosition(), bot.getTheta()); //degrees
//
//        if (GameState.getInstance().isGoingOn("kickOffSurroundKicking") && !me) {
//            bot.linearVelocity = -2;
//        }
//
//        if (Math.abs(angleDifferenceFromGoal) < 10) {
//            bot.linearVelocity = 2;
//            me = true;
//            GameState.getInstance().addToWhatsGoingOn("kickOffSurroundKicking");
//        }
    }

}
