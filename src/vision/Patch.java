package vision;

import org.opencv.core.Point;

import java.util.ArrayList;

/**
 * Created by chan743 on 31/07/2015.
 */
public class Patch {



    private ArrayList<Point> pixels = new ArrayList<Point>();
    private Point center;
    private Point revisionCenter;
    private Point realCenter;

    private boolean found;
    private int related_id;

    public Patch () {

        center = new Point(0,0);
        revisionCenter = new Point(0,0);
        realCenter = new Point(0,0);

        found = false;
        related_id = -1;
    }

    public ArrayList<Point> getPixels() {
        return pixels;
    }

    public Point getCenter() {
        return center;
    }

    public Point getRevisionCenter() {
        return revisionCenter;
    }

    public Point getRealCenter() {
        return realCenter;
    }

    public boolean isFound() {
        return found;
    }

    public int getRelated_id() {
        return related_id;
    }

    public void setPixels(ArrayList<Point> pixels) {
        this.pixels = pixels;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setRevisionCenter(Point revisionCenter) {
        this.revisionCenter = revisionCenter;
    }

    public void setRealCenter(Point realCenter) {
        this.realCenter = realCenter;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public void setRelated_id(int related_id) {
        this.related_id = related_id;
    }
}
