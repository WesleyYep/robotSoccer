package actions;

import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Actions {

	// Contains all the actions in an array.
    private static Action[] actions = new Action[] {
    	null,
    	new ChaseBall(),
    	new ChaseBall2(),
    	new BasicGoalKeep(),
    	new Wait(),
        new TurnToFaceBall(),
        new TurnToFaceBall2(),
        new GoalKeepTest()
    	};

    public Action getAction(int index) {
        return actions[index];
    }

    public int getLength() {
        return actions.length;
    }

    public Action findAction(String name) {
        for (Action a : actions) {
            if (a == null) {
                continue;
            }
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }
}
