package actions;

import bot.Robot;
import data.Coordinate;
import strategy.Action;


/**
 * Created by Wesley on 27/02/2015.
 */
public class StrikerTest extends Action {
    private boolean atCentre = false;

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        if (atCentre) { //already at centre, now turn to goal
            TurnTo.turn(r, new Coordinate(220, 90));
            if (r.angularVelocity < 0.1) {
                System.out.println("reached set up position!");
            }
        } else {
            MoveToSpot.move(r, new Coordinate(110, 90));
            if (r.linearVelocity == 0 && r.angularVelocity == 0) {
                atCentre = true;
            }
        }
    }
}
