package actions;

import strategy.Action;
import strategy.Role;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chang Kon on 24/07/2015.
 */
public class ActionFactory {

    // Hashmap containing actions
    private static final Map<Role, Map<String, Action>> actionMap = new HashMap<Role, Map<String, Action>>();

    /**
     * <p>Used to return action</p>
     * @param role
     * @param simpleName
     * @return
     */
    public static Action getAction(Role role, final String simpleName) {

        Map<String, Action> map = actionMap.get(role);

        if (map == null || map.get(simpleName) == null) {
            if (simpleName.equals(MoveToFacing.class.getSimpleName())) {
                final MoveToFacing moveToFacing = new MoveToFacing();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, moveToFacing); }});
                return moveToFacing;
            } else if (simpleName.equals(testSelfMade.class.getSimpleName())) {
                final testSelfMade selfMade = new testSelfMade();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, selfMade); }});
                return selfMade;
            } else if (simpleName.equals(ChaseBallWithObstacle.class.getSimpleName())) {
                final ChaseBallWithObstacle chaseBallWithObstacle = new ChaseBallWithObstacle();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, chaseBallWithObstacle); }});
                return chaseBallWithObstacle;
            } else if (simpleName.equals(testSelfMadeObstacle.class.getSimpleName())) {
                final testSelfMadeObstacle selfMadeObstacle = new testSelfMadeObstacle();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, selfMadeObstacle); }});
                return selfMadeObstacle;
            } else if (simpleName.equals(BasicGoalKeep.class.getSimpleName())) {
                final BasicGoalKeep basicGoalKeep = new BasicGoalKeep();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, basicGoalKeep); }});
                return basicGoalKeep;
            } else if (simpleName.equals(Wait.class.getSimpleName())) {
                final Wait wait = new Wait();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, wait); }});
                return wait;
            } else if (simpleName.equals(StrikerTest.class.getSimpleName())) {
                final StrikerTest strikerTest = new StrikerTest();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, strikerTest); }});
                return strikerTest;
            } else if (simpleName.equals(MoveToSpot.class.getSimpleName())) {
                final MoveToSpot moveToSpot = new MoveToSpot();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, moveToSpot); }});
                return moveToSpot;
            } else if (simpleName.equals(MoveAndTurn.class.getSimpleName())) {
                final MoveAndTurn moveAndTurn = new MoveAndTurn();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, moveAndTurn); }});
                return moveAndTurn;
            } else if (simpleName.equals(Circle.class.getSimpleName())) {
                final Circle circle = new Circle();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, circle); }});
                return circle;
            } else if (simpleName.equals(BlockOpponentClosestToBall.class.getSimpleName())) {
                final BlockOpponentClosestToBall blockOpponentClosestToBall = new BlockOpponentClosestToBall();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, blockOpponentClosestToBall); }});
                return blockOpponentClosestToBall;
            } else if (simpleName.equals(PenaltyStraight.class.getSimpleName())) {
                final PenaltyStraight penaltyStraight = new PenaltyStraight();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, penaltyStraight); }});
                return penaltyStraight;
            } else if (simpleName.equals(PenaltySpin.class.getSimpleName())) {
                final PenaltySpin penaltySpin = new PenaltySpin();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, penaltySpin); }});
                return penaltySpin;
            } else if (simpleName.equals(GoalLineSideDefender.class.getSimpleName())) {
                final GoalLineSideDefender goalLineSideDefender = new GoalLineSideDefender();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, goalLineSideDefender); }});
                return goalLineSideDefender;
            } else if (simpleName.equals(PIDMoveToBall.class.getSimpleName())) {
                final PIDMoveToBall pidMoveToBall = new PIDMoveToBall();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, pidMoveToBall); }});
                return pidMoveToBall;
            } else if (simpleName.equals(PIDMoveToSpot.class.getSimpleName())) {
                final PIDMoveToSpot pidmoveToSpot = new PIDMoveToSpot();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, pidmoveToSpot); }});
                return pidmoveToSpot;
            } else if (simpleName.equals(PIDGoalKeeper.class.getSimpleName())) {
                final PIDGoalKeeper pidGoalKeeper = new PIDGoalKeeper();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, pidGoalKeeper); }});
                return pidGoalKeeper;
            } else if (simpleName.equals(BasicDefender.class.getSimpleName())) {
                final BasicDefender basicDefender = new BasicDefender();
                actionMap.put(role, new HashMap<String, Action>() {{ put(simpleName, basicDefender); }});
                return basicDefender;
            }
        }

        return map.get(simpleName);
    }
}