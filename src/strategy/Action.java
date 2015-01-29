package strategy;

import bot.Robots;

/**
 * Created by Wesley on 21/01/2015.
 */
public abstract class Action {
    protected Robots bots;
    private double ballX;
    private double ballY;
    protected int index;

    public abstract String getName();

    public void addRobot (Robots bots, int index) {
        this.bots = bots;
        this.index = index;
    }

    public void setBallPosition(double x, double y) {
        this.ballX = x;
        this.ballY = y;
    }

    @Override
    public String toString() {
        return getName();
    }

    public abstract void execute();

//    @Override
//    public boolean equals(Object action) {
//        return (((Action)action).toString().equals(toString()));
//    }
}
