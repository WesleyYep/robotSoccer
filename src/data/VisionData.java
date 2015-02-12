package data;


/**
 * Created by Wesley on 10/02/2015.
 */
public class VisionData {
    private Coordinate coordinate;
    private String type;
    private double theta;

    public VisionData(Coordinate c, double theta, String s) {
        coordinate = c;
        type = s;
        this.theta = theta;
    }

    public  Coordinate getCoordinate() {
        return coordinate;
    }

    public double getTheta() {
        return theta;
    }

    public String getType() {
        return type;
    }
}
