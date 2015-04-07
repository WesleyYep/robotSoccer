package criteria;

import bot.Robot;
import bot.Robots;
import strategy.Criteria;

/**
 * Created by Wesley on 21/01/2015.
 */
public class IsSpinningFast extends Criteria {

    private int i = 0;

    @Override
    public String getName() {
        return "Is Spinning Fast";
    }

    /**
     * <p>Checks if the robot is spinning out of control</p>
     */

    @Override
    public boolean isMet() {
        Robot r = bots.getRobot(index);

        if (r.linearVelocity > 0.05) {
            i = 0;
            return false; //can't be spinning on the spot if linear velocity is not 0
        } else if (r.angularVelocity < 3) {
            i = 0;
            return false; //can't be spinning out of control unless angular velocity is at least 3 (I think)
        } else if (i < 1000){
            i++;
            return false; //need to wait for 1000 ticks (5 sec) before it should be considered out of control
        } else {
            System.out.println("Robot out of control!");
            return true;
        }

    }
}
