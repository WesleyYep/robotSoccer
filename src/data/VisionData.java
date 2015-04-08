package data;


import org.opencv.core.Point;

/**
* Created by Wesley on 10/02/2015.
*/
public class VisionData {

    private Point coordinate;
    private String type;
    private double theta;

    public VisionData(Point c, double theta, String s) {
        coordinate = c;
        type = s;
        this.theta = theta;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public double getTheta() {
        return theta;
    }

    public String getType() {
        return type;
    }
}
