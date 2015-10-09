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
          add(PenaltyFake.class.getSimpleName());
          add(GoalLineSideDefender.class.getSimpleName());
          add(GoalLineSideAttacker.class.getSimpleName());
          add(PIDMoveToBall.class.getSimpleName());
          add(PIDMoveToSpot.class.getSimpleName());
          add(PIDGoalKeeper.class.getSimpleName());
          add(BasicDefender.class.getSimpleName());
          add(MoveSpotKickBall.class.getSimpleName());
          add(ChaseBall2.class.getSimpleName());
          add(AdvancedGoalKeeper.class.getSimpleName());
          add(AdvancedGoalKeeperStriker.class.getSimpleName());
          add(PIDMoveToBallReverse.class.getSimpleName());
          add(StrikerTestReverse.class.getSimpleName());
          add(KickOffSurround.class.getSimpleName());
          add(VerticalPusher.class.getSimpleName());
          add(DiagonalWaiting.class.getSimpleName());
          add(FreeBall.class.getSimpleName());
          add(PenaltySpinToStraight.class.getSimpleName());
          add(TestTurnFirstGoalKeeper.class.getSimpleName());
          add(FreeBallKicker.class.getSimpleName());
          add(Spin.class.getSimpleName());
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
