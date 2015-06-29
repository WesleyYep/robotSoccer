package ui;

import controllers.VisionController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class BoardAreaGlassPanel extends JPanel implements MouseListener, MouseMotionListener {

	
	final public static int NONE = 0;
	final public static int TOP_LEFT = 1;
	final public static int TOP_RIGHT = 2;
	final public static int BOT_LEFT = 3;
	final public static int BOT_RIGHT = 4;
    final public static int LEFT_GOAL_TOP_LEFT = 5;
    final public static int LEFT_GOAL_TOP_RIGHT = 6;
    final public static int LEFT_GOAL_BOTTOM_LEFT = 7;
    final public static int LEFT_GOAL_BOTTOM_RIGHT = 8;
    final public static int RIGHT_GOAL_TOP_LEFT = 9;
    final public static int RIGHT_GOAL_TOP_RIGHT = 10;
    final public static int RIGHT_GOAL_BOTTOM_LEFT = 11;
    final public static int RIGHT_GOAL_BOTTOM_RIGHT = 12;

	private int pointMoving = NONE;
	private int errorMargin = 10;
	private VisionController vc;
	
	public BoardAreaGlassPanel(VisionController vc) {
		this.vc = vc;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		
		g.setColor(Color.red);

		g.drawLine((int)Math.round(vc.getTopLeft().getX())
				, (int)Math.round(vc.getTopLeft().getY())
				, (int)Math.round(vc.getTopRight().getX())
				, (int)Math.round(vc.getTopRight().getY()));
		g.drawLine((int)Math.round(vc.getTopLeft().getX())
				,(int)Math.round(vc.getTopLeft().getY())
                ,(int)Math.round(vc.getLeftGoalTopRight().getX())
                ,(int)Math.round(vc.getLeftGoalTopRight().getY()));
        g.drawLine((int)Math.round(vc.getLeftGoalTopRight().getX())
                ,(int)Math.round(vc.getLeftGoalTopRight().getY())
				,(int)Math.round(vc.getLeftGoalTopLeft().getX())
				,(int)Math.round(vc.getLeftGoalTopLeft().getY()));
		g.drawLine((int)Math.round(vc.getLeftGoalTopLeft().getX())
				,(int)Math.round(vc.getLeftGoalTopLeft().getY())
				,(int)Math.round(vc.getLeftGoalBottomLeft().getX())
				,(int)Math.round(vc.getLeftGoalBottomLeft().getY()));
		g.drawLine((int)Math.round(vc.getLeftGoalBottomLeft().getX())
				,(int)Math.round(vc.getLeftGoalBottomLeft().getY())
				,(int)Math.round(vc.getLeftGoalBottomRight().getX())
				,(int)Math.round(vc.getLeftGoalBottomRight().getY()));
        g.drawLine((int)Math.round(vc.getLeftGoalBottomRight().getX())
                ,(int)Math.round(vc.getLeftGoalBottomRight().getY())
                ,(int)Math.round(vc.getBottomLeft().getX())
                ,(int)Math.round(vc.getBottomLeft().getY()));
        g.drawLine((int)Math.round(vc.getTopRight().getX())
                ,(int)Math.round(vc.getTopRight().getY())
                ,(int)Math.round(vc.getRightGoalTopLeft().getX())
                ,(int)Math.round(vc.getRightGoalTopLeft().getY()));
        g.drawLine((int)Math.round(vc.getRightGoalTopLeft().getX())
                ,(int)Math.round(vc.getRightGoalTopLeft().getY())
                ,(int)Math.round(vc.getRightGoalTopRight().getX())
                ,(int)Math.round(vc.getRightGoalTopRight().getY()));
        g.drawLine((int)Math.round(vc.getRightGoalTopRight().getX())
                ,(int)Math.round(vc.getRightGoalTopRight().getY())
                ,(int)Math.round(vc.getRightGoalBottomRight().getX())
                ,(int)Math.round(vc.getRightGoalBottomRight().getY()));
        g.drawLine((int)Math.round(vc.getRightGoalBottomRight().getX())
                ,(int)Math.round(vc.getRightGoalBottomRight().getY())
                ,(int)Math.round(vc.getRightGoalBottomLeft().getX())
                ,(int)Math.round(vc.getRightGoalBottomLeft().getY()));
        g.drawLine((int)Math.round(vc.getRightGoalBottomLeft().getX())
                ,(int)Math.round(vc.getRightGoalBottomLeft().getY())
                ,(int)Math.round(vc.getBottomRight().getX())
                ,(int)Math.round(vc.getBottomRight().getY()));
        g.drawLine((int)Math.round(vc.getBottomLeft().getX())
                ,(int)Math.round(vc.getBottomLeft().getY())
                ,(int)Math.round(vc.getBottomRight().getX())
                ,(int)Math.round(vc.getBottomRight().getY()));
		
		g.drawString("Top Left", (int)Math.round(vc.getTopLeft().getX())-1, (int)Math.round(vc.getTopLeft().getY())-1);
		g.drawString("Top Right", (int)Math.round(vc.getTopRight().getX())-1, (int)Math.round(vc.getTopRight().getY())-1);
		g.drawString("BottomRight", (int)Math.round(vc.getBottomRight().getX())-1,(int)Math.round(vc.getBottomRight().getY())-1);
		g.drawString("BottomLeft", (int)Math.round(vc.getBottomLeft().getX())-1,(int)Math.round(vc.getBottomLeft().getY())-1);
		
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
		} else if (isTopRight(e)) {
			pointMoving = TOP_RIGHT;
		} else if (isBotRight(e)) {
			pointMoving = BOT_RIGHT;
			
		} else if (isBotLeft(e)) {
			pointMoving = BOT_LEFT;
		} else if (isLeftGoalTopLeft(e)) {
            pointMoving = LEFT_GOAL_TOP_LEFT;
        }else if (isLeftGoalTopRight(e)) {
            pointMoving = LEFT_GOAL_TOP_RIGHT;
        }else if (isLeftGoalBottomLeft(e)) {
            pointMoving = LEFT_GOAL_BOTTOM_LEFT;
        }else if (isLeftGoalBottomRight(e)) {
            pointMoving = LEFT_GOAL_BOTTOM_RIGHT;
        }else if (isRightGoalTopLeft(e)) {
            pointMoving = RIGHT_GOAL_TOP_LEFT;
        }else if (isRightGoalTopRight(e)) {
            pointMoving = RIGHT_GOAL_TOP_RIGHT;
        }else if (isRightGoalBottomLeft(e)) {
            pointMoving = RIGHT_GOAL_BOTTOM_LEFT;
        }else if (isRightGoalBottomRight(e)) {
            pointMoving = RIGHT_GOAL_BOTTOM_RIGHT;
        }else {
			pointMoving = NONE;
			if (e.getButton() == MouseEvent.BUTTON1) {
				vc.rotatePointAntiClockwise();
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				vc.rotatePointClockwise();
			}
			vc.createTransformMatrix();
			this.repaint();
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
		} else if (pointMoving == TOP_RIGHT) {
			vc.setTopRight(e.getPoint());
		} else if (pointMoving == BOT_LEFT) {
			vc.setBottomLeft(e.getPoint());
		} else if (pointMoving == BOT_RIGHT) {
			vc.setBottomRight(e.getPoint());
		} else if (pointMoving == LEFT_GOAL_TOP_LEFT) {
            vc.setLeftGoalTopLeft(e.getPoint());
        } else if (pointMoving == LEFT_GOAL_TOP_RIGHT) {
            vc.setLeftGoalTopRight(e.getPoint());
        } else if (pointMoving == LEFT_GOAL_BOTTOM_LEFT) {
            vc.setLeftGoalBottomLeft(e.getPoint());
        } else if (pointMoving == LEFT_GOAL_BOTTOM_RIGHT) {
            vc.setLeftGoalBottomRight(e.getPoint());
        } else if (pointMoving == RIGHT_GOAL_TOP_LEFT) {
            vc.setRightGoalTopLeft(e.getPoint());
        } else if (pointMoving == RIGHT_GOAL_TOP_RIGHT) {
            vc.setRightGoalTopRight(e.getPoint());
        } else if (pointMoving == RIGHT_GOAL_BOTTOM_LEFT) {
            vc.setRightGoalBottomLeft(e.getPoint());
        } else if (pointMoving == RIGHT_GOAL_BOTTOM_RIGHT) {
            vc.setRightGoalBottomRight(e.getPoint());
        }
		this.repaint();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		if (isTopLeft(e) || isTopRight(e) || isBotLeft(e) || isBotRight(e) || isLeftGoalTopLeft(e) || isLeftGoalTopRight(e) || isLeftGoalBottomLeft(e) || isLeftGoalBottomRight(e)
                || isRightGoalTopLeft(e) || isRightGoalTopRight(e) || isRightGoalBottomLeft(e) || isRightGoalBottomRight(e)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}
	
	private boolean isTopLeft(MouseEvent e) {
		return (e.getX() < (vc.getTopLeft().getX()+errorMargin) && e.getX() > (vc.getTopLeft().getX()-errorMargin) && e.getY() < (vc.getTopLeft().getY()+errorMargin) && e.getY() > (vc.getTopLeft().getY()-errorMargin));
	}
	
	private boolean isTopRight(MouseEvent e) {
		return (e.getX() < (vc.getTopRight().getX()+errorMargin) && e.getX() > (vc.getTopRight().getX()-errorMargin) && e.getY() < (vc.getTopRight().getY()+errorMargin) && e.getY() > (vc.getTopRight().getY()-errorMargin));
	}
	
	private boolean isBotLeft(MouseEvent e) {
		return (e.getX() < (vc.getBottomLeft().getX()+errorMargin) && e.getX() > (vc.getBottomLeft().getX()-errorMargin) && e.getY() < (vc.getBottomLeft().getY()+errorMargin) && e.getY() > (vc.getBottomLeft().getY()-errorMargin));
	}
	
	private boolean isBotRight(MouseEvent e) {
		return (e.getX() < (vc.getBottomRight().getX()+errorMargin) && e.getX() > (vc.getBottomRight().getX()-errorMargin) && e.getY() < (vc.getBottomRight().getY()+errorMargin) && e.getY() > (vc.getBottomRight().getY()-errorMargin));
	}

    private boolean isLeftGoalTopLeft(MouseEvent e) {
        return (e.getX() < (vc.getLeftGoalTopLeft().getX()+errorMargin) && e.getX() > (vc.getLeftGoalTopLeft().getX()-errorMargin) && e.getY() < (vc.getLeftGoalTopLeft().getY()+errorMargin) && e.getY() > (vc.getLeftGoalTopLeft().getY()-errorMargin));
    }

    private boolean isLeftGoalTopRight(MouseEvent e) {
        return (e.getX() < (vc.getLeftGoalTopRight().getX()+errorMargin) && e.getX() > (vc.getLeftGoalTopRight().getX()-errorMargin) && e.getY() < (vc.getLeftGoalTopRight().getY()+errorMargin) && e.getY() > (vc.getLeftGoalTopRight().getY()-errorMargin));
    }

    private boolean isLeftGoalBottomLeft(MouseEvent e) {
        return (e.getX() < (vc.getLeftGoalBottomLeft().getX()+errorMargin) && e.getX() > (vc.getLeftGoalBottomLeft().getX()-errorMargin) && e.getY() < (vc.getLeftGoalBottomLeft().getY()+errorMargin) && e.getY() > (vc.getLeftGoalBottomLeft().getY()-errorMargin));
    }

    private boolean isLeftGoalBottomRight(MouseEvent e) {
        return (e.getX() < (vc.getLeftGoalBottomRight().getX()+errorMargin) && e.getX() > (vc.getLeftGoalBottomRight().getX()-errorMargin) && e.getY() < (vc.getLeftGoalBottomRight().getY()+errorMargin) && e.getY() > (vc.getLeftGoalBottomRight().getY()-errorMargin));
    }

    private boolean isRightGoalTopLeft(MouseEvent e) {
        return (e.getX() < (vc.getRightGoalTopLeft().getX()+errorMargin) && e.getX() > (vc.getRightGoalTopLeft().getX()-errorMargin) && e.getY() < (vc.getRightGoalTopLeft().getY()+errorMargin) && e.getY() > (vc.getRightGoalTopLeft().getY()-errorMargin));
    }

    private boolean isRightGoalTopRight(MouseEvent e) {
        return (e.getX() < (vc.getRightGoalTopRight().getX()+errorMargin) && e.getX() > (vc.getRightGoalTopRight().getX()-errorMargin) && e.getY() < (vc.getRightGoalTopRight().getY()+errorMargin) && e.getY() > (vc.getRightGoalTopRight().getY()-errorMargin));
    }

    private boolean isRightGoalBottomLeft(MouseEvent e) {
        return (e.getX() < (vc.getRightGoalBottomLeft().getX()+errorMargin) && e.getX() > (vc.getRightGoalBottomLeft().getX()-errorMargin) && e.getY() < (vc.getRightGoalBottomLeft().getY()+errorMargin) && e.getY() > (vc.getRightGoalBottomLeft().getY()-errorMargin));
    }

    private boolean isRightGoalBottomRight(MouseEvent e) {
        return (e.getX() < (vc.getRightGoalBottomRight().getX() + errorMargin) && e.getX() > (vc.getRightGoalBottomRight().getX() - errorMargin) && e.getY() < (vc.getRightGoalBottomRight().getY() + errorMargin) && e.getY() > (vc.getRightGoalBottomRight().getY()-errorMargin));
    }


}
