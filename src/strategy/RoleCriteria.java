package strategy;

import bot.Robot;

import java.util.ArrayList;

/**
 * Created by chan743 on 8/09/2015.
 */
public abstract class RoleCriteria extends Criteria {

    // Contains list of robots to ignore in criteria checking
    public ArrayList<Robot> ignoreList;

    public RoleCriteria() {
        super();
        ignoreList = new ArrayList<Robot>();
    }

    /**
     * Add robots to ignore in criteria checking
     * @param r
     */
    public void addRobotToIgnoreList(Robot r) {
        ignoreList.add(r);
    }

    /**
     * Remove robot from ignore list. Adds robot back into criteria checking
     * @param r
     */
    public void removeRobotFromIgnoreList(Robot r) {
        ignoreList.remove(r);
    }

    /**
     * Returns robot which meets criteria
     * @return robot
     */
    public abstract Robot isMet();
}
