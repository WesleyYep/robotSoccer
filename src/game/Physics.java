package game;

import bot.Robot;
import bot.Robots;
import ui.Field;

/**
 * Created by Wesley on 15/01/2015.
 */
public class Physics {

    public void calculatePhysics(Field field, Robots bots) {
        for (Robot r : bots.getRobots()) {
            if (((int)r.getXPosition() ==  (int)field.getBallX()) && ((int)r.getYPosition() == (int)field.getBallY())) {
                System.out.println("kicked!");
                field.kickBall(r.linearVelocity, r.getTheta());
            }
        }
    }

}
