package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public class BoardAreaGlassPanel extends JPanel implements MouseListener, MouseMotionListener {

	private Point2D topLeft;
	private Point2D topRight;
	private Point2D bottomRight;
	private Point2D bottomLeft;
	
	final public static int NONE = 0;
	final public static int TOP_LEFT = 1;
	final public static int TOP_RIGHT = 2;
	final public static int BOT_LEFT = 3;
	final public static int BOT_RIGHT = 4;
	
	private int pointMoving = NONE;
	
	
	public BoardAreaGlassPanel(Point2D topLeft2, Point2D topRight2, Point2D botLeft, Point2D botRight) {
		topLeft = topLeft2;
		topRight = topRight2;
		bottomLeft = botLeft;
		bottomRight = botRight;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		
		g.setColor(Color.black);
		
		g.drawLine((int)Math.round(topLeft.getX())
				, (int)Math.round(topLeft.getY())
				, (int)Math.round(topRight.getX())
				, (int)Math.round(topRight.getY()));
		g.drawLine((int)Math.round(topLeft.getX())
				,(int)Math.round(topLeft.getY())
				,(int)Math.round(bottomLeft.getX())
				,(int)Math.round(bottomLeft.getY()));
		g.drawLine((int)Math.round(topRight.getX())
				,(int)Math.round(topRight.getY())
				,(int)Math.round(bottomRight.getX())
				,(int)Math.round(bottomRight.getY()));
		g.drawLine((int)Math.round(bottomLeft.getX())
				,(int)Math.round(bottomLeft.getY())
				,(int)Math.round(bottomRight.getX())
				,(int)Math.round(bottomRight.getY()));
		
		g.drawString("Top Left", (int)Math.round(topLeft.getX())-1, (int)Math.round(topLeft.getY())-1);
		g.drawString("BottomRight", (int)Math.round(bottomRight.getX())-1,(int)Math.round(bottomRight.getY())-1);
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mousePressed(MouseEvent e) {
		if (isTopLeft(e)) {
			pointMoving = TOP_LEFT;
		}
		else if (isTopRight(e)) {
			pointMoving = TOP_RIGHT;
		}
		else if (isBotRight(e)) {
			pointMoving = BOT_RIGHT;
			
		}
		else if (isBotLeft(e)) {
			pointMoving = BOT_LEFT;
		}
		else {
			pointMoving = NONE;
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		pointMoving = NONE;
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		if (pointMoving == TOP_LEFT) {
			topLeft.setLocation(e.getPoint());
		}
		else if (pointMoving == TOP_RIGHT) {
			topRight.setLocation(e.getPoint());
		}
		else if (pointMoving == BOT_LEFT) {
			bottomLeft.setLocation(e.getPoint());
		}
		else if (pointMoving == BOT_RIGHT) {
			bottomRight.setLocation(e.getPoint());
		}
		this.repaint();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		if (isTopLeft(e) || isTopRight(e) || isBotLeft(e) || isBotRight(e)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
		else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	private boolean isTopLeft(MouseEvent e) {
		return (e.getX() < (topLeft.getX()+5) && e.getX() > (topLeft.getX()-5) && e.getY() < (topLeft.getY()+5) && e.getY() > (topLeft.getY()-5));
	}
	
	private boolean isTopRight(MouseEvent e) {
		return (e.getX() < (topRight.getX()+5) && e.getX() > (topRight.getX()-5) && e.getY() < (topRight.getY()+5) && e.getY() > (topRight.getY()-5));
	}
	
	private boolean isBotLeft(MouseEvent e) {
		return (e.getX() < (bottomLeft.getX()+5) && e.getX() > (bottomLeft.getX()-5) && e.getY() < (bottomLeft.getY()+5) && e.getY() > (bottomLeft.getY()-5));
	}
	
	private boolean isBotRight(MouseEvent e) {
		return (e.getX() < (bottomRight.getX()+5) && e.getX() > (bottomRight.getX()-5) && e.getY() < (bottomRight.getY()+5) && e.getY() > (bottomRight.getY()-5));
	}
	
	
	
}
