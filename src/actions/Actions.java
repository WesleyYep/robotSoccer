package actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Actions {

      private static List<String> actions = new ArrayList<String>() {{
    	  add(MoveToFacing.class.getSimpleName());
          add(testSelfMade.class.getSimpleName());
          add(ChaseBallWithObstacle.class.getSimpleName());
          add(testSelfMadeObstacle.class.getSimpleName());
          add(BasicGoalKeep.class.getSimpleName());
          add(Wait.class.getSimpleName());
          add(StrikerTest.class.getSimpleName());
          add(MoveToSpot.class.getSimpleName());
          add(MoveAndTurn.class.getSimpleName());
          add(Circle.class.getSimpleName());
          add(BlockOpponentClosestToBall.class.getSimpleName());
          add(PenaltyStraight.class.getSimpleName());
          add(PenaltySpin.class.getSimpleName());
          add(GoalLineSideDefender.class.getSimpleName());
          add(GoalLineSideAttacker.class.getSimpleName());
          add(PIDMoveToBall.class.getSimpleName());
          add(PIDMoveToSpot.class.getSimpleName());
          add(PIDGoalKeeper.class.getSimpleName());
          add(BasicDefender.class.getSimpleName());
          add(AdvancedGoalKeep.class.getSimpleName());
          add(ChaseBall2.class.getSimpleName());
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
    public static List<String> getActions() {
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
