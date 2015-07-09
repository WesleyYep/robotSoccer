package actions;

import bot.Robot;
import data.Coordinate;
import javafx.scene.shape.MoveTo;
import strategy.Action;

/**
 * Created by Wesley on 10/07/2015.
 */
public class BlockOpponentClosestToBall extends Action {
    private double randomY = 0;

    @Override
    public void execute() {
        int opponentClosestToBallIndex = -1;
        double shortestDistance = 10000;
        double dist;

        for (Robot or : opponentRobots.getRobots()) {
            if ((dist = getDistance(or.getXPosition(), or.getYPosition(), ballX, ballY)) <= shortestDistance) {
                if (dist < 80) {
                    shortestDistance = dist;
                    opponentClosestToBallIndex = or.getId();
                }
            }
        }
        if (opponentClosestToBallIndex == -1) {
            //no opponent is close to ball
            if (randomY == 0) {
               randomY = Math.random()*180;
            } else if (bot.getXPosition() > 105) {
                bot.linearVelocity = 0;
                bot.angularVelocity = 0;
                return;
            }
            MoveToSpot.move(bot, new Coordinate(110,randomY), 0.5, true);
        } else {
            Robot opponentRobotClosestToBall = opponentRobots.getRobot(opponentClosestToBallIndex);
            MoveToSpot.move(bot, new Coordinate(opponentRobotClosestToBall.getXPosition(), opponentRobotClosestToBall.getYPosition()), 0.5, false);
        }
    }


    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2, 2));
    }

}
