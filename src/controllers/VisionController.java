package controllers;

import org.opencv.core.Point;
import ui.Field;

import javax.media.jai.PerspectiveTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class VisionController {
	
	private Point2D topRight;
	private Point2D topLeft;
	
	private Point2D bottomRight;
	private Point2D bottomLeft;
	
	public static double mapLeft = 121;
	public static double mapRight = 517;
	public static double mapTop = 48;
	public static double mapBot = 372;
	private static double rotationAngle = 0;
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
    public static int[] processingArea = new int[640*480];
    private int fieldFacing = 0; // (0 is nonflipped - 0 turns, 1 is nonflipped - 1 ACW turn, 2 is nonflipped - 1 CW turn
                                //   3 is flipped - 0 turns, 4 is flipped - 1 ACW turn, 5 is flipped - 1 CW turn

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


    public static Point FlatToScreen(Point p) {
        double x = p.x;
        double y = p.y;

        if (t!=null) {
            Point2D selectedPoint = new Point2D.Double();
            t.transform(new Point2D.Double(x,y), selectedPoint);
            return new Point(selectedPoint.getX(), selectedPoint.getY());
        } else {
            return null;
        }
    }

    public static Point ScreenToGround(Point p) {
        double x = p.x;
        double y = p.y;

        x -= 121;
        y -= 48;

        x /= 180.00;
        y /= 180.00;

        return new Point(x,y);
    }


    public static Point actualPosToimagePos(Point p) {
        double x = p.x;
        double y = p.y;


        if (tInverse != null ) {
            x = x * ((mapRight - mapLeft) / (double) Field.OUTER_BOUNDARY_WIDTH) + mapLeft;
            y = y * ((mapBot - mapTop) / (double) Field.OUTER_BOUNDARY_HEIGHT) + mapTop;

            Point2D selectedPoint = new Point2D.Double();
            tInverse.transform(new Point2D.Double(x,y), selectedPoint);

            return new Point(selectedPoint.getX(), selectedPoint.getY());

         } else {
            return null;
        }

    }

    public static double imageThetaToActualTheta (double theta) {
        double result = theta + rotationAngle;

        if (result > Math.PI) result -= 2*Math.PI;
        if (result < -Math.PI) result += 2*Math.PI;

        return result;
    }

	
	public void rotatePointAntiClockwise() {
		Point2D tempBottomLeft = bottomLeft;
		Point2D tempTopLeft = topLeft;
		
		Point2D tempTopRight = topRight;
		Point2D tempBottomRight = bottomRight;
        rotationAngle += Math.PI/2;
        if (rotationAngle > Math.PI) rotationAngle -= 2*Math.PI;
		
		bottomLeft = tempBottomRight;
		topLeft = tempBottomLeft;
		
		topRight = tempTopLeft;
		bottomRight = tempTopRight;
        //swap if needed
        if (fieldFacing == 1 || fieldFacing == 4) {
            swapGoals();
        }
        //for fieldFacing:
        // (0 is nonflipped - 0 turns, 1 is nonflipped - 1 ACW turn, 2 is nonflipped - 1 CW turn
        //   3 is flipped - 0 turns, 4 is flipped - 1 ACW turn, 5 is flipped - 1 CW turn
        if (fieldFacing == 0 || fieldFacing == 3) {
            fieldFacing++;
        } else if (fieldFacing == 1 || fieldFacing == 5) {
            fieldFacing = 3;
        } else if (fieldFacing == 2 || fieldFacing == 4) {
            fieldFacing = 0;
        }
        //swap if needed
	}
	
	public void rotatePointClockwise() {
        rotationAngle -= Math.PI/2;
        if (rotationAngle < -Math.PI) rotationAngle += 2*Math.PI;
		Point2D tempBottomLeft = bottomLeft;
		Point2D tempTopLeft = topLeft;
		
		Point2D tempTopRight = topRight;
		Point2D tempBottomRight = bottomRight;
		
		bottomLeft = tempTopLeft;
		topLeft = tempTopRight;
		
		topRight = tempBottomRight;
		bottomRight = tempBottomLeft;
        //swap if needed
        if (fieldFacing == 2 || fieldFacing == 5) {
            swapGoals();
        }
        //for fieldFacing:
        // (0 is nonflipped - 0 turns, 1 is nonflipped - 1 ACW turn, 2 is nonflipped - 1 CW turn
        //   3 is flipped - 0 turns, 4 is flipped - 1 ACW turn, 5 is flipped - 1 CW turn
        if (fieldFacing == 0 || fieldFacing == 3) {
            fieldFacing += 2;
        } else if (fieldFacing == 1 || fieldFacing == 5) {
            fieldFacing = 0;
        } else if (fieldFacing == 2 || fieldFacing == 4) {
            fieldFacing = 3;
        }
	}

    private void swapGoals() {
        //temps
        Point2D a = rightGoalTopLeft;
        Point2D b = rightGoalTopRight;
        Point2D c = rightGoalBottomLeft;
        Point2D d = rightGoalBottomRight;

        rightGoalTopLeft = leftGoalBottomRight;
        rightGoalTopRight = leftGoalBottomLeft;
        rightGoalBottomLeft = leftGoalTopRight;
        rightGoalBottomRight = leftGoalTopLeft;

        leftGoalTopLeft = d;
        leftGoalTopRight = c;
        leftGoalBottomLeft = b;
        leftGoalBottomRight = a;
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

    public boolean  pointInPoly(int nvert, double[] vertx, double[] verty, double testx, double testy) {

        int i,j = 0;
        boolean c = false;

        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( ((verty[i]>testy) != (verty[j]>testy)) &&
                    (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
                c = !c;
        }

        return c;
    }

    public void updateProcessingArea() {
        int vert = 12;

        double[] x = {topLeft.getX(),topRight.getX(),rightGoalTopLeft.getX(),rightGoalTopRight.getX(),rightGoalBottomRight.getX()
                        ,rightGoalBottomLeft.getX(),bottomRight.getX(),bottomLeft.getX(),leftGoalBottomRight.getX(),leftGoalBottomLeft.getX()
                        ,leftGoalTopLeft.getX(),leftGoalTopRight.getX()};

        double[] y = {topLeft.getY(),topRight.getY(),rightGoalTopLeft.getY(),rightGoalTopRight.getY(),rightGoalBottomRight.getY()
                ,rightGoalBottomLeft.getY(),bottomRight.getY(),bottomLeft.getY(),leftGoalBottomRight.getY(),leftGoalBottomLeft.getY()
                ,leftGoalTopLeft.getY(),leftGoalTopRight.getY()};
    }

}
