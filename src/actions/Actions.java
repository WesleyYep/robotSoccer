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
            new MoveToSpot(),
            new StrikerTest(),
            new GoalKeepTest()
    	};

    /*
     * Suggestion:
     * private static List<Action> actions = new ArrayList<Action>() {{
     * 		add(new ChaseBall());
     * 		add(new ChaseBall2());
     * 		add(new BasicGoalKeep());
     * 		add(new Wait());
     * 		add(new TurnToFaceBall());
     * 		add(new TurnToFaceBall2());
     * 		add(new GoalKeepTest());
     * }};
     * 
     * public Action getAction(Class<? extends Action> className) {
     * 		for (Action a : actions) {
     * 			if (a.getClass() == className) {
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
