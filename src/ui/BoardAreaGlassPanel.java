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

import controllers.VisionController;

public class BoardAreaGlassPanel extends JPanel implements MouseListener, MouseMotionListener {

	
	final public static int NONE = 0;
	final public static int TOP_LEFT = 1;
	final public static int TOP_RIGHT = 2;
	final public static int BOT_LEFT = 3;
	final public static int BOT_RIGHT = 4;
	
	private int pointMoving = NONE;
	
	private VisionController vc;
	
	public BoardAreaGlassPanel(VisionController vc) {
		this.vc = vc;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		
		g.setColor(Color.black);
		
		g.drawLine((int)Math.round(vc.getTopLeft().getX())
				, (int)Math.round(vc.getTopLeft().getY())
				, (int)Math.round(vc.getTopRight().getX())
				, (int)Math.round(vc.getTopRight().getY()));
		g.drawLine((int)Math.round(vc.getTopLeft().getX())
				,(int)Math.round(vc.getTopLeft().getY())
				,(int)Math.round(vc.getBottomLeft().getX())
				,(int)Math.round(vc.getBottomLeft().getY()));
		g.drawLine((int)Math.round(vc.getTopRight().getX())
				,(int)Math.round(vc.getTopRight().getY())
				,(int)Math.round(vc.getBottomRight().getX())
				,(int)Math.round(vc.getBottomRight().getY()));
		g.drawLine((int)Math.round(vc.getBottomLeft().getX())
				,(int)Math.round(vc.getBottomLeft().getY())
				,(int)Math.round(vc.getBottomRight().getX())
				,(int)Math.round(vc.getBottomRight().getY()));
		
		g.drawString("Top Left", (int)Math.round(vc.getTopLeft().getX())-1, (int)Math.round(vc.getTopLeft().getY())-1);
		g.drawString("BottomRight", (int)Math.round(vc.getBottomRight().getX())-1,(int)Math.round(vc.getBottomRight().getY())-1);
		
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
			vc.setTopLeft(e.getPoint());
		}
		else if (pointMoving == TOP_RIGHT) {
			vc.setTopRight(e.getPoint());
		}
		else if (pointMoving == BOT_LEFT) {
			vc.setBottomLeft(e.getPoint());
		}
		else if (pointMoving == BOT_RIGHT) {
			vc.setBottomRight(e.getPoint());
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
		return (e.getX() < (vc.getTopLeft().getX()+5) && e.getX() > (vc.getTopLeft().getX()-5) && e.getY() < (vc.getTopLeft().getY()+5) && e.getY() > (vc.getTopLeft().getY()-5));
	}
	
	private boolean isTopRight(MouseEvent e) {
		return (e.getX() < (vc.getTopRight().getX()+5) && e.getX() > (vc.getTopRight().getX()-5) && e.getY() < (vc.getTopRight().getY()+5) && e.getY() > (vc.getTopRight().getY()-5));
	}
	
	private boolean isBotLeft(MouseEvent e) {
		return (e.getX() < (vc.getBottomLeft().getX()+5) && e.getX() > (vc.getBottomLeft().getX()-5) && e.getY() < (vc.getBottomLeft().getY()+5) && e.getY() > (vc.getBottomLeft().getY()-5));
	}
	
	private boolean isBotRight(MouseEvent e) {
		return (e.getX() < (vc.getBottomRight().getX()+5) && e.getX() > (vc.getBottomRight().getX()-5) && e.getY() < (vc.getBottomRight().getY()+5) && e.getY() > (vc.getBottomRight().getY()-5));
	}
	
	
	
}
