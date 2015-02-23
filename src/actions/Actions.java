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

    /*
     * Suggestion:
     * private static ArrayList<Action> actions = new ArrayList<Action> {{
     * 		add(new ChaseBall());
     * 		add(new ChaseBall2());
     * 		add(new BasicGoalKeep());
     * 		add(new Wait());
     * 		add(new TurnToFaceBall());
     * 		add(TurnToFaceBall2());
     * 		add(new GoalKeepTest());
     * }};
     * 
     * public Action getAction(Class<? extends Action> givenClass) {
     * 		for (Action a : actions) {
     * 			if (a.getClass() == givenClass) {
     * 				return a;
     * 			}
     * 		}
     * 		return null;
     * }
     * 
     */
    
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
