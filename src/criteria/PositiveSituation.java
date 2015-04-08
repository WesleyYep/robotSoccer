package criteria;

import actions.TurnToFaceBall;
import bot.Robot;
import strategy.Criteria;

/**
 * Created by Wesley on 21/03/2015.
 */
public class PositiveSituation extends Criteria {
    @Override
    public String getName() {
        return "Positive Situation";
    }

    @Override
    public boolean isMet() {
        Robot r = bots.getRobot(index);
        double ballTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
        double difference = ballTheta - Math.toRadians(r.getTheta());

        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }

        // return true if robot is to the left of ball or is pointing directly at ball
        return ballX - r.getXPosition() > 25 || Math.abs(difference) <= TurnToFaceBall.ERROR_MARGIN;
    }
}
