package vision;

import data.Coordinate;

/**
* Created by Wesley on 7/02/2015.
*/
public class PixelGroup {

    //this class represents matching colour pixels along one row of a buffered image

    public Coordinate mostLeftCorner;
    public Coordinate mostRightCorner;
    public Coordinate mostTopCorner;
    public Coordinate mostBottomCorner;

    public PixelGroup (int x, int y) {
        mostLeftCorner = new Coordinate(x, y);
        mostRightCorner = new Coordinate(x, y);
        mostTopCorner = new Coordinate(x, y);
        mostBottomCorner = new Coordinate(x, y);
    }

    public int getSize() {
        return (mostBottomCorner.y - mostTopCorner.y) + (mostRightCorner.x - mostLeftCorner.x);
    }
}
