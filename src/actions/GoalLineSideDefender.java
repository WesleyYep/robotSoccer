package actions;

import data.Coordinate;
import strategy.Action;

/**
 * Created by chan743 on 15/07/2015.
 */
public class GoalLineSideDefender extends Action {

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
            y = 45;
            facing = 0;
        } else {
            y = 135;
            facing = 180;
        }

        switch (state) {
            case 0: //moving to spot
                MoveAndTurn.moveAndTurn(bot, 5, y, 5, facing);
                if (MoveAndTurn.getDistanceToTarget(bot, 5, y) < 5 && bot.angularVelocity == 0) {
                    state = 1;
                } else if(ballX < 15 && ballY > 50 && ballY < 130) {
                    if (side == TOP) {
                        MoveAndTurn.moveAndTurn(bot, 30, 50, 50, 180);
                    } else {
                        MoveAndTurn.moveAndTurn(bot, 30, 130, 130, 180);
                    }
                }
                break;
            case 1: //waiting at spot
                if (ballX < 10) {
                    if (ballY < bot.getYPosition() && side == TOP) {
                        MoveToSpot.move(bot, new Coordinate(0,0), 2, false);
                    } else if (ballY > bot.getYPosition() && side == BOTTOM) {
                        MoveToSpot.move(bot, new Coordinate(0,180), 2, false);
                    }
                } /*else if (15 < ballX && ballX < 55 && (side == TOP && ballY > 50 && ballY < 90) || (side == BOTTOM && ballY < 130 && ballY > 90)) {
                    MoveToSpot.move(bot, new Coordinate(ballX, ballY), 1, false);
                } */else if(ballX < 15 && ballY > 50 && ballY < 130) {
                    if (side == TOP) {
                        MoveAndTurn.moveAndTurn(bot, 30, 50, 50, 180);
                    } else {
                        MoveAndTurn.moveAndTurn(bot, 30, 130, 130, 180);
                    }
                } else if (MoveAndTurn.getDistanceToTarget(bot, 5, y) > 5 || bot.angularVelocity != 0) {
                    state = 0;
                }
                break;
        }

    }

}
