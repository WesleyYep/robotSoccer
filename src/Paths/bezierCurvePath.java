package Paths;

import bot.Robot;

/**
 * Created by Wesley on 27/02/2015.
 */
public class bezierCurvePath extends Path {
    //control points
    private int P0X;
    private int P1X;
    private int P2X;
    private int P3X;
    private int P0Y;
    private int P1Y;
    private int P2Y;
    private int P3Y;
    private double numPoints = 200;

    public bezierCurvePath(Robot r, int startX, int startY) {
        startPointX = startX;
        startPointY = startY;
        endPointX = startPointX + 150;
        endPointY = startY;
        robot = r;
        //set control points
        P0X = startX;
        P0Y = startY;
        P1X = startPointX + 50;
        P1Y = startPointY - 50;
        P2X = startPointX + 100;
        P2Y = startPointY + 50;
        P3X = endPointX;
        P3Y = endPointY;
        setPoints();
    }

    @Override
    public boolean hasReachedTarget() {
        if (numPoints - index <= 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setPoints() {

        pointsXAlongPath.clear();
        pointsYAlongPath.clear();
        index = 0;

        for (int i = 0; i < numPoints; i++) {
            double u = i / numPoints;

            double x = P0X*Math.pow(1-u, 3) + 3*P1X*u*Math.pow(1-u, 2) + 3*P2X*Math.pow(u, 2)*(1-u) + P3X * Math.pow(u, 3);
            double y = P0Y*Math.pow(1-u, 3) + 3*P1Y*u*Math.pow(1-u, 2) + 3*P2Y*Math.pow(u, 2)*(1-u) + P3Y * Math.pow(u, 3);

            pointsXAlongPath.add(x);
            pointsYAlongPath.add(y);
        }

    }
}
