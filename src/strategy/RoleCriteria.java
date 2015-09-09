package strategy;

import java.util.Arrays;
import java.util.List;

import bot.Robot;
import bot.Robots;
import ui.Ball;

/**
 * Criteria used to assign Robots to Roles
 * Created by chan743 on 8/09/2015.
 */
public abstract class RoleCriteria extends Criteria {

    private List<Robot> checkList;

    public RoleCriteria(Robots bots, Ball ball) {
        super(bots, ball);
        checkList = Arrays.asList(bots.getRobots());
    }

    /**
     * Returns list of robots which are used in criteria checking
     * @return
     */
    public List<Robot> getCheckList() {
    	return checkList;
    }
    
    /**
     * Returns robot which meets criteria
     * @return robot
     */
    public abstract Robot isMet();
}
