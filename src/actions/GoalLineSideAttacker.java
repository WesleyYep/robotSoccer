package actions;

import data.Coordinate;
import strategy.Action;

/**
 * Created by chan743 on 15/07/2015.
 */
public class GoalLineSideAttacker extends Action {

    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    private int state = 0; //state = 0 means going to spot

    {
        parameters.put("side", 0);
    }

    @Override
    public void execute() {
        int y;
        int facing;
        int side = parameters.get("side");

        if (side == TOP) {
            y = 15;
            facing = 180;
        } else {
            y = 165;
            facing = 0;
        }

        switch (state) {
            case 0: //moving to spot
                MoveAndTurn.moveAndTurn(bot, 212, y, 212, facing);
                if (MoveAndTurn.getDistanceToTarget(bot, 212, y) < 5 && bot.angularVelocity == 0) {
                    state = 1;
                }
                break;
            case 1: //waiting at spot
                if (ballX > 210) {
                    if (ballY > bot.getYPosition() && side == TOP) {
                        MoveToSpot.move(bot, new Coordinate(212, 90), 0.5, false);
                    } else if (ballY < bot.getYPosition() && side == BOTTOM) {
                        MoveToSpot.move(bot, new Coordinate(212,90), 0.5, false);
                    }
                } else /*if (MoveAndTurn.getDistanceToTarget(bot, 210, y) > 5 || bot.angularVelocity != 0) */{
                    state = 0;
                }
                break;
        }

    }

}
