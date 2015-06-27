package controllers;

import org.opencv.core.Point;
import ui.Field;

import javax.media.jai.PerspectiveTransform;
import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class VisionController {
	
	private Point2D topRight;
	private Point2D topLeft;
	
	private Point2D bottomRight;
	private Point2D bottomLeft;
	
	private static double mapLeft = 121;
	private static double mapRight = 517;
	private static double mapTop = 48;
	private static double mapBot = 372;
	
	private static PerspectiveTransform t;
	private static PerspectiveTransform tInverse;
    private Point2D leftGoalTopLeft;
    private Point2D rightGoalTopLeft;
    private Point2D rightGoalTopRight;
    private Point2D leftGoalTopRight;
    private Point2D leftGoalBottomRight;
    private Point2D leftGoalBottomLeft;
    private Point2D rightGoalBottomRight;
    private Point2D rightGoalBottomLeft;

    public VisionController() {
		topRight = new Point2D.Double(450,50);
		topLeft = new Point2D.Double(50,50);
		bottomLeft = new Point2D.Double(50,450);
		bottomRight = new Point2D.Double(450,450);

        leftGoalTopLeft = new Point2D.Double(30,230);
        leftGoalTopRight = new Point2D.Double(50,230);
        leftGoalBottomRight = new Point2D.Double(50,270);
        leftGoalBottomLeft = new Point2D.Double(30,270);

        rightGoalTopLeft = new Point2D.Double(450,230);
        rightGoalTopRight = new Point2D.Double(470,230);
        rightGoalBottomRight = new Point2D.Double(470,270);
        rightGoalBottomLeft = new Point2D.Double(450,270);
		this.createTransformMatrix();
	}
	
	public void createTransformMatrix() {
		//x y: point that u want to map to
		//xp yp: orginal points
		tInverse = PerspectiveTransform.getQuadToQuad(
				mapLeft,
				mapTop,
				mapLeft,
				mapBot,
				mapRight,
				mapBot,
				mapRight,
				mapTop,
				topLeft.getX(),
				topLeft.getY(),
				bottomLeft.getX(),
				bottomLeft.getY(),
				bottomRight.getX(),
				bottomRight.getY(),
				topRight.getX(),
				topRight.getY()
				);

		try {
			t = tInverse.createInverse();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public static Point imagePosToActualPos (Point p) {
        double x = p.x;
        double y = p.y;

		if (t != null ) {
			Point2D selectedPoint = new Point2D.Double();
			t.transform(new Point2D.Double(x,y), selectedPoint);
			double actualX = (selectedPoint.getX() - mapLeft) / ((mapRight-mapLeft)/(double)Field.OUTER_BOUNDARY_WIDTH);
			double actualY = (selectedPoint.getY() - mapTop) / ((mapBot-mapTop)/(double)Field.OUTER_BOUNDARY_HEIGHT);
			
			return new Point(actualX,actualY);
		} else {
			return null;
		}
		
	}
	
	public void rotatePointAntiClockwise() {
		Point2D tempBottomLeft = bottomLeft;
		Point2D tempTopLeft = topLeft;
		
		Point2D tempTopRight = topRight;
		Point2D tempBottomRight = bottomRight;
		
		bottomLeft = tempBottomRight;
		topLeft = tempBottomLeft;
		
		topRight = tempTopLeft;
		bottomRight = tempTopRight;
	}
	
	public void rotatePointClockwise() {
		Point2D tempBottomLeft = bottomLeft;
		Point2D tempTopLeft = topLeft;
		
		Point2D tempTopRight = topRight;
		Point2D tempBottomRight = bottomRight;
		
		bottomLeft = tempTopLeft;
		topLeft = tempTopRight;
		
		topRight = tempBottomRight;
		bottomRight = tempBottomLeft;
	}
	

	public Point2D getTopRight() {
		return topRight;
	}


	public void setTopRight(Point2D topRight) {
		this.topRight = topRight;
		this.createTransformMatrix();
	}


	public Point2D getTopLeft() {
		return topLeft;
	}


	public void setTopLeft(Point2D topLeft) {
		this.topLeft = topLeft;
		this.createTransformMatrix();
	}


	public Point2D getBottomRight() {
		return bottomRight;
	}


	public void setBottomRight(Point2D bottomRight) {
		this.bottomRight = bottomRight;
		this.createTransformMatrix();
	}


	public Point2D getBottomLeft() {
		return bottomLeft;
	}


	public void setBottomLeft(Point2D bottomLeft) {
		this.bottomLeft = bottomLeft;
		this.createTransformMatrix();
	}

    public Point2D getLeftGoalTopRight() {
        return leftGoalTopRight;
    }

    public Point2D getLeftGoalTopLeft() {
        return leftGoalTopLeft;
    }

    public Point2D getLeftGoalBottomRight() {
        return leftGoalBottomRight;
    }

    public Point2D getLeftGoalBottomLeft() {
        return leftGoalBottomLeft;
    }

    public Point2D getRightGoalTopLeft() {
        return rightGoalTopLeft;
    }

    public Point2D getRightGoalTopRight() {
        return rightGoalTopRight;
    }

    public Point2D getRightGoalBottomLeft() {
        return rightGoalBottomLeft;
    }

    public Point2D getRightGoalBottomRight() {
        return rightGoalBottomRight;
    }

    public void setLeftGoalTopRight(Point2D p) {
        leftGoalTopRight = p;
    }

    public void setLeftGoalTopLeft(Point2D p) {
        leftGoalTopLeft = p;
    }

    public void setLeftGoalBottomRight(Point2D p) {
        leftGoalBottomRight = p;
    }

    public void setLeftGoalBottomLeft(Point2D p) {
        leftGoalBottomLeft = p;
    }

    public void setRightGoalTopLeft(Point2D p) {
        rightGoalTopLeft = p;
    }

    public void setRightGoalTopRight(Point2D p) {
        rightGoalTopRight = p;
    }

    public void setRightGoalBottomLeft(Point2D p) {
        rightGoalBottomLeft = p;
    }

    public void setRightGoalBottomRight(Point2D p) {
        rightGoalBottomRight = p;
    }
}
