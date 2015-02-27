package Paths;

import bot.Robot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 30/01/2015.
 */
public abstract class Path {
    protected List<Double> pointsXAlongPath = new ArrayList<Double>();
    protected List<Double> pointsYAlongPath = new ArrayList<Double>();
    protected int index = 0;
    protected int startPointX;
    protected int startPointY;
    protected int endPointX;
    protected int endPointY;
    protected Robot robot;

    public abstract boolean hasReachedTarget();

    public abstract void setPoints();

    public double getX() {
        return pointsXAlongPath.get(index);
    }

    public double getY() {
        return pointsYAlongPath.get(index);
    }

    public double getNextX() {
        return pointsXAlongPath.get(index+1);
    }

    public double getNextY() {
        return pointsYAlongPath.get(index+1);
    }

    public void step() {
        index++;
    }

    protected double squared (double x) {
        return x * x;
    }


}
