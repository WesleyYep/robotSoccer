package data;


/**
 * Created by Wesley on 10/02/2015.
 */
public class VisionData {
    private Coordinate coordinate;
    private String type;

    public VisionData (Coordinate c, String s) {
        coordinate = c;
        type = s;
    }

    public  Coordinate getCoordinate() {
        return coordinate;
    }

    public String getType() {
        return type;
    }
}
