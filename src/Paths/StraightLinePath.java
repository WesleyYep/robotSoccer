package Paths;

import bot.Robot;

/**
 * Created by Wesley on 30/01/2015.
 */
public class StraightLinePath extends Path {

    public StraightLinePath(Robot r, int startX, int startY, int endX, int endY) {
        startPointX = startX;
        startPointY = startY;
        endPointX = endX;
        endPointY = endY;
        robot = r;
    }

    @Override
    public boolean hasReachedTarget() {
        if ((int)robot.getXPosition() == endPointX && (int)robot.getYPosition() == endPointY) {
            System.out.println("Has reached target");
            return true;
        } else {
            return false;
        }
        //return ((int)robot.getXPosition() == endPointX && (int)robot.getYPosition() == y);
    }

    @Override
    public void setPoints() {
        pointsXAlongPath.clear();
        pointsYAlongPath.clear();
        index = 0;
        int distanceX = endPointX - startPointX;
        int distanceY = endPointY - startPointY;
        int numPoints = 10;
        for (int i = 1; i <= numPoints; i++) {
            pointsXAlongPath.add(startPointX + (1/numPoints)*distanceX);
            pointsYAlongPath.add(startPointY + (1/numPoints)*distanceY);
        }
    }
}
