package vision;

import org.opencv.core.Point;

public class Position {

	public boolean valid;
	public int id;
	public Point pixelPos;
	public Point revisionPos;
	public Point realPos;
	public double direction;
	public double pixelDirection;
	
	public Position() {
		pixelPos = new Point();
		revisionPos = new Point();
		realPos = new Point();
	}
}
