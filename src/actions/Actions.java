package actions;

import strategy.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Actions {

	// Contains all the actions in an array.
//    private static Action[] actions = new Action[] {
//            null,
//            new ChaseBall(),
//            new ChaseBall2(),
//            new BasicGoalKeep(),
//            new Wait(),
//            new TurnToFaceBall(),
//            new MoveToSpot(),
//            new StrikerTest(),
//            new GoalKeepTest(),
//            new BasicDefender(new org.opencv.core.Point(10, 30), new org.opencv.core.Point(150, 150), null)
//    	};



      private static List<Action> actions = new ArrayList<Action>() {{
        add(new testSelfMade());
        add(new testSelfMadeCarry());
        add(new ReverseChaseBallStriker());
        add(new ChaseBall());
        add(new ChaseBall2());
        add(new BasicGoalKeep());
        add(new Wait());
        add(new TurnTo());
        add(new StrikerTest());
        add(new MoveToSpot());
        add(new MoveAndTurn());
        add(new BasicDefender(new org.opencv.core.Point(10, 30), new org.opencv.core.Point(150, 150), null));
      }};

//      public Action getAction(Class className) {
//      		for (Action a : actions) {
//      			if (a.getClass() == className) {
//      				return a;
//      			}
//      		}
//      		return null;
//     }
//


//    public Action getAction(int index) {
//        return actions[index];
//    }
//
    public static List<Action> getActions() {
        return actions;
    }
//
//    public int getLength() {
//        return actions.size();
//    }
//
//    public Action findAction(String name) {
//        for (Action a : actions) {
//            if (a == null) {
//                continue;
//            }
//            if (a.getName().equals(name)) {
//                return a;
//            }
//        }
//        return null;
//    }
}
