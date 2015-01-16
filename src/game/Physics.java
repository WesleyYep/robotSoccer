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
            if ((Math.abs(r.getXPosition() -  field.getBallX()) < 8) && ((Math.abs(r.getYPosition() -  field.getBallY()) < 8))) {
                System.out.println("kicked!");
                field.kickBall(r.linearVelocity, r.getTheta());
            }
        }
        if (field.getBallX() < 5) {
            System.out.println(Field.ORIGIN_X);
            field.bounceBall();
        }
    }

}
