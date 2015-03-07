package actions;

import bot.Robot;
import strategy.Action;

/**
 * Created by Wesley on 21/01/2015.
 */
public class TurnToFaceBall2 extends Action{
    private int time = 0;
    private double lastDifference = 0;
    private double originalDifference = 0;
    @Override
    public String getName() {
        return "Turn to ball (trial2)";
    }

    @Override
    public void execute() {
        Robot r = bots.getRobot(index);

        double ballTheta = Math.atan2(r.getYPosition() - ballY, ballX - r.getXPosition());
        double difference = ballTheta - Math.toRadians(r.getTheta());
        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }



    }

    protected double squared (double x) {
        return x * x;
    }
}
