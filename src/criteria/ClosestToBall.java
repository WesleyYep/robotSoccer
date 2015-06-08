package criteria;

import bot.Robot;
import bot.Robots;
import strategy.Criteria;

/**
 * Created by Wesley on 21/01/2015.
 */
public class ClosestToBall extends Criteria {
    @Override
    public String getName() {
        return "Closest to Ball";
    }

    /**
     * <p>Checks if the robot is the closest to the ball</p>
     */

    @Override
    public boolean isMet() {
        Robot r = bots.getRobot(index);
        double x = r.getXPosition();
        double y = r.getYPosition();
        double distance = Math.sqrt(squared(x-ballX) + squared(y-ballY));

        for (int i = 0; i < Robots.BOTTEAMMEMBERCOUNT; i++) {
            Robot r2 = bots.getRobot(i);

            if (i == index || !r2.criteriaName.equals(getName())) {
                continue;
            } else {
                double x2 = r2.getXPosition();
                double y2 = r2.getYPosition();

                double distanceOther = Math.sqrt(squared(x2-ballX) + squared(y2-ballY));
                if (distanceOther < distance) {
                    return false;
                }
            }
        }
        return true;
    }
}
