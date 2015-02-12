package controllers;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.media.jai.PerspectiveTransform;

import ui.Field;

public class VisionController {
	
	private Point2D topRight;
	private Point2D topLeft;
	
	private Point2D bottomRight;
	private Point2D bottomLeft;
	
	private double mapLeft = 121;
	private double mapRight = 517;
	private double mapTop = 48;
	private double mapBot = 372;
	

	private PerspectiveTransform t;
	private PerspectiveTransform tInverse;
	
	public VisionController() {
		topRight = new Point2D.Double(200,100);
		topLeft = new Point2D.Double(100,100);
		bottomLeft = new Point2D.Double(100,200);
		bottomRight = new Point2D.Double(200,200);
		this.createTransformMatrix();
		
	}
	
	
	public void createTransformMatrix() {
		//x y: point that u want to map to
		//xp yp: orginal points
		tInverse = PerspectiveTransform.getQuadToQuad(mapLeft, mapTop, mapLeft, mapBot, mapRight, mapBot, mapRight, mapTop,
				topLeft.getX(), topLeft.getY(), bottomLeft.getX(),bottomLeft.getY()
				, bottomRight.getX(),bottomRight.getY(), topRight.getX(),topRight.getY());

		/*
		System.out.println("mapping: " + mapLeft + " " + mapRight + " " + mapTop + " " + mapBot);
		System.out.println(topLeft);
		System.out.println(topRight);
		System.out.println(bottomRight);
		System.out.println(bottomLeft);
		*/
		try {
			t = tInverse.createInverse();
			//System.out.println(t.toString());
	//		System.out.println(tInverse.toString());
		} catch (NoninvertibleTransformException e) {

			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public Point2D imagePosToActualPos (double x, double y) {
		if (t != null ) {
			Point2D selectedPoint = new Point2D.Double();
			t.transform(new Point2D.Double(x,y), selectedPoint);
			double actualX = (selectedPoint.getX() - mapLeft) / ((mapRight-mapLeft)/(double)Field.OUTER_BOUNDARY_WIDTH);
			double actualY = (selectedPoint.getY() - mapTop) / ((mapBot-mapTop)/(double)Field.OUTER_BOUNDARY_HEIGHT);
			
			return new Point2D.Double(actualX,actualY);
		}
		else {
			return null;
		}
		
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


	
}
