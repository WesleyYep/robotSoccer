package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class DrawAreaGlassPanel extends JPanel implements MouseListener, MouseMotionListener{

	private Point startPoint;
	private Point endPoint;

	private boolean isMouseDrag;
	private int mouseClicked;

	private SituationPanel sPanel;
	private Field field;

	public DrawAreaGlassPanel (Field panel, SituationPanel panel2) {
		this.setOpaque(false);
		this.setPreferredSize(panel.getPreferredSize());

		this.setBounds(0,0,panel.getPreferredSize().width, getPreferredSize().height);
		field = panel;
		sPanel = panel2;

		isMouseDrag = false;
		addMouseListener(this);
		addMouseMotionListener(this);
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (isMouseDrag) {
			// drawRect does not take negative values hence values need to be calculated so it doesn't fill the rectangle.
			// Rectangle co-ordinates.
			int x, y, w, h;

			x = Math.min(startPoint.x, endPoint.x);
			y = Math.min(startPoint.y, endPoint.y);

			w = Math.abs(endPoint.x - startPoint.x);
			h = Math.abs(endPoint.y - startPoint.y);

			g.setColor(Color.RED);
			g.drawRect(x, y, w, h);
		}

	}


	@Override
	public void mouseDragged(MouseEvent e) {
		isMouseDrag = true;
		endPoint = e.getPoint();
		if (mouseClicked == MouseEvent.BUTTON1) {
			field.repaint();
		}
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));		
	}


	@Override
	public void mouseClicked(MouseEvent e) {

	}


	@Override
	public void mouseEntered(MouseEvent e) {

	}


	@Override
	public void mouseExited(MouseEvent e) {

	}


	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
		mouseClicked = e.getButton();
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		endPoint = e.getPoint();
		isMouseDrag = false;

		if (e.getButton() == MouseEvent.BUTTON1) {
			Rectangle area = new Rectangle(startPoint);
			area.add(endPoint);		
			sPanel.addSituations(area);
		}
		this.setVisible(false);
	}
}
