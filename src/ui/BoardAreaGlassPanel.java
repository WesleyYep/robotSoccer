package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class BoardAreaGlassPanel extends JPanel implements MouseListener, MouseMotionListener {

	private Point topLeft;
	private Point topRight;
	private Point bottomRight;
	private Point bottomLeft;
	
	final public static int NONE = 0;
	final public static int TOP_LEFT = 1;
	final public static int TOP_RIGHT = 2;
	final public static int BOT_LEFT = 3;
	final public static int BOT_RIGHT = 4;
	
	private int pointMoving = NONE;
	
	
	public BoardAreaGlassPanel(Point topLeft2, Point topRight2, Point botLeft, Point botRight) {
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
		
		g.drawLine(topLeft.x, topLeft.y, topRight.x, topRight.y);
		g.drawLine(topLeft.x,topLeft.y,bottomLeft.x,bottomLeft.y);
		g.drawLine(topRight.x,topRight.y,bottomRight.x,bottomRight.y);
		g.drawLine(bottomLeft.x,bottomLeft.y,bottomRight.x,bottomRight.y);
		
		g.drawString("Top Left", topLeft.x-1, topLeft.y-1);
		g.drawString("BottomRight", bottomRight.x-1,bottomRight.y-1);
		
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
		return (e.getX() < (topLeft.x+5) && e.getX() > (topLeft.x-5) && e.getY() < (topLeft.y+5) && e.getY() > (topLeft.y-5));
	}
	
	private boolean isTopRight(MouseEvent e) {
		return (e.getX() < (topRight.x+5) && e.getX() > (topRight.x-5) && e.getY() < (topRight.y+5) && e.getY() > (topRight.y-5));
	}
	
	private boolean isBotLeft(MouseEvent e) {
		return (e.getX() < (bottomLeft.x+5) && e.getX() > (bottomLeft.x-5) && e.getY() < (bottomLeft.y+5) && e.getY() > (bottomLeft.y-5));
	}
	
	private boolean isBotRight(MouseEvent e) {
		return (e.getX() < (bottomRight.x+5) && e.getX() > (bottomRight.x-5) && e.getY() < (bottomRight.y+5) && e.getY() > (bottomRight.y-5));
	}
	
	public Point getTopLeft() {
		return topLeft;
	}
	
	public Point getTopRight() {
		return topRight;
	}
	
	public Point getBotLeft() {
		return bottomLeft;
	}
	
	public Point getBotRight() {
		return bottomRight;
	}
	
	
	
}
