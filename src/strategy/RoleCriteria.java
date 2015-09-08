package strategy;

import bot.Robot;
import bot.Robots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chan743 on 8/09/2015.
 */
public abstract class RoleCriteria extends Criteria {

    public List<Robot> checkList;

    public RoleCriteria() {
        super();
        checkList = Arrays.asList(bots.getRobots());
    }

    /**
     * Returns robot which meets criteria
     * @return robot
     */
    public abstract Robot isMet();
}
