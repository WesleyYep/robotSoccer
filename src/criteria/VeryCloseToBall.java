package criteria;

import strategy.Criteria;

/**
 * Created by chan743 on 24/07/2015.
 */
public class VeryCloseToBall extends Criteria {

    private long lastTimeTrue = 0;

    @Override
    public String getName() {
        return "VeryCloseToBall";
    }

    @Override
    public boolean isMet() {
        double distanceToBall = getDistanceToTarget(bot, ballX, ballY);

        if (distanceToBall < 7) {
            lastTimeTrue = System.currentTimeMillis();
            return true;
        } else if (System.currentTimeMillis() - lastTimeTrue < 3000) {
            return true;
        } else {
            return false;
        }
    }
}
