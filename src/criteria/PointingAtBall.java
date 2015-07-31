package criteria;

import actions.TurnTo;
import bot.Robot;
import strategy.Criteria;

/**
 * Created by Wesley on 7/03/2015.
 */
public class PointingAtBall extends Criteria {

    @Override
    public boolean isMet() {
        Robot r = bot;
        double ballTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
        double difference = ballTheta - Math.toRadians(r.getTheta());

        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }

        if (Math.abs(difference) <= TurnTo.ERROR_MARGIN) {
            return true;
        } else {
            return false;
        }

    }
}
