package criteria;

import strategy.Criteria;
import strategy.GameState;

/**
 * Created by Wesley on 11/07/2015.
 */
public class IsFirst2Seconds extends Criteria{

    @Override
    public boolean isMet() {
        return System.currentTimeMillis() - GameState.getInstance().getLastStartedTime() < 2000;
    }
}
