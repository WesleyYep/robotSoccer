package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class SituationArea extends JPanel implements MouseMotionListener, MouseListener{
	
	final static public int ORIGIN_X = 500;
	final static public int ORIGIN_Y = 100;
	
	final static public int MOVING = 1;
	final static public int RESIZING = 2;
	
	final static public int MOVING_RIGHT = 1;
	final static public int MOVING_LEFT = 2;
	
	final static public int MOUSE_AREA = 5;
	
	private int startingX = 0;
	private int startingY = 0;
	
	private AreaListener listener;
	
	private int draggingMode = 0;
	private int movingDirection = 0;
	
	private int baseWidth;
	private int baseHeight;
	
	private boolean clickTop = false;
	private	boolean clickBottom = false;
	private boolean clickLeft = false;
	private boolean clickRight = false;
	
	private int pivotX = 0;
	private int pivotY = 0;
	
	private boolean active = false;
	
	private JLabel coordLbl;
	private JLabel nameLbl;
	private JLabel sizeLbl;
	
	private String situationName;
	
	public SituationArea (int width, int height) {
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.setSize(width,height);
		this.setBorder(BorderFactory.createLineBorder(Color.RED));
		this.setOpaque(false);
		situationName = "";
				
		nameLbl = new JLabel(situationName);
		coordLbl = new JLabel("X: " + this.getX() + " Y: " + this.getY());
		sizeLbl = new JLabel("W: " + this.getWidth() + " H: " + this.getHeight());
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		
		nameLbl.setAlignmentX(Label.CENTER_ALIGNMENT);
		coordLbl.setAlignmentX(Label.CENTER_ALIGNMENT);
		sizeLbl.setAlignmentX(Label.CENTER_ALIGNMENT);
		
		this.add(nameLbl);
		this.add(coordLbl);
		this.add(sizeLbl);
		
		baseWidth = width;
		baseHeight = height;
		
	}
	
	protected void paintComponent(Graphics g) {
		nameLbl.setText(situationName);
		coordLbl.setText("X: " + (this.getX() - Field.ORIGIN_X)/Field.SCALE_FACTOR + " Y: " + (this.getY() - Field.ORIGIN_Y)/Field.SCALE_FACTOR);
		sizeLbl.setText("W: " + this.getWidth()/Field.SCALE_FACTOR + " H: " + this.getHeight()/Field.SCALE_FACTOR);
		
		super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.setRenderingHint(
	            RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.setComposite(AlphaComposite.getInstance(
	            AlphaComposite.SRC_OVER, 1f));
	        g2d.setColor(Color.BLACK);
	        g2d.drawRect(0, 0, this.getWidth(), this.getHeight());
	}
	
	public void addAreaListener(AreaListener l) {
		listener =l;
	}
	
	public void setActive(boolean bool) {
		active = bool;
	}
	

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (active) {
			int diffX = startingX - arg0.getX();
			int diffY = startingY - arg0.getY();
			
			if (draggingMode == RESIZING) {
				int newWidth = 0;
				int newHeight = 0;
				
				int newX = this.getX();
				int newY = this.getY();
				
				if (clickRight == true) {
					if (arg0.getX() < -1) {
						clickRight = false;
						clickLeft = true;
						baseWidth = 1;
						startingX = arg0.getX();
						newWidth = 0;
						newX = pivotX-this.getWidth();
						pivotX = pivotX-1;
					} else {
						newWidth = ((diffX*-1)+baseWidth)-this.getWidth();
					}
					
				} else if ( clickLeft == true) {
					diffX = startingX - (arg0.getX()+ (baseWidth-this.getWidth()));
					if (diffX < (baseWidth*-1+1)) {
						clickRight = true;
						clickLeft = false;
						baseWidth = this.getWidth();
						startingX = arg0.getX();
						newWidth = 0;				
						newX = pivotX+1;
						pivotX = newX;
					} else {
						newWidth =((diffX)+baseWidth)-this.getWidth();	
						newX = this.getX()-newWidth;
					}	
				}
				
				
				if (clickTop == true) {
					diffY = startingY - (arg0.getY()+ (baseHeight-this.getHeight()));
					if (diffY < baseHeight*-1) {
						clickBottom = true;
						clickTop = false;
						baseHeight = this.getHeight();
						startingY = arg0.getY();
						newHeight = 0;				
						newY = pivotY+1;
						pivotY = newY;
					} else {
						newHeight =((diffY)+baseHeight)-this.getHeight();	
						newY = this.getY()-newHeight;
					}
				} else if (clickBottom == true) {
					if (arg0.getY() < -1) {
						clickBottom = false;
						clickTop = true;
						baseHeight = this.getHeight();
						startingY = arg0.getY();
						newHeight = 0;
						newY = pivotY-this.getHeight();
						pivotY = pivotY-1;
					} else {
					
						newHeight = ((diffY*-1)+baseHeight-this.getHeight());
						
					}
				}
								
				listener.resizeArea( newWidth, newHeight, newX, newY);		
			} else if (draggingMode == MOVING) {
				
				
				listener.moveArea(diffX, diffY);
			}
		}	
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (active) {
			//diagonal
			if ( isMouseLeft(arg0) && isMouseTop(arg0)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			} else if (isMouseRight(arg0) && isMouseBottom(arg0)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			} else if (isMouseTop(arg0) && isMouseRight(arg0)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			} else if (isMouseBottom(arg0) && isMouseLeft(arg0)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			} else if ( isMouseLeft(arg0)) {
				// straight
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (isMouseRight(arg0)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			} else if (isMouseTop(arg0)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else if (isMouseBottom(arg0)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			} else {
				
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (active) {
			startingX = arg0.getX();
			startingY = arg0.getY();
			
			draggingMode = MOVING;
			
			if ( isMouseLeft(arg0))  {
				draggingMode = RESIZING;
				clickLeft = true;
				pivotX = this.getX()+this.getWidth()-1;
			}
			
			if  (isMouseRight(arg0)) {
				draggingMode = RESIZING;
				clickRight = true;
				pivotX= this.getX();
			}
			
			if ( isMouseTop(arg0))  {
				draggingMode = RESIZING;
				clickTop = true;
				pivotY = this.getY()+this.getHeight()-1;
			}
			
			if  (isMouseBottom(arg0)) {
				draggingMode = RESIZING;
				clickBottom = true;
				pivotY = this.getY();
			}	
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if ( draggingMode == RESIZING && active) {
			baseWidth = this.getWidth();
			baseHeight = this.getHeight();
			
			clickTop = false;
			clickBottom = false;
			clickLeft = false;
			clickRight = false;
		}
	}
	
	public void setBorderColor(Color c) {
		this.setBorder(BorderFactory.createLineBorder(c));
	}
	
	public void setName(String s) {
		situationName = s;
		nameLbl.setText(situationName);
		listener.redrawArea();
	}
	
	private boolean isMouseTop(MouseEvent arg0) {
		return arg0.getY() >=0 && arg0.getY() < MOUSE_AREA;
	}
	
	private boolean isMouseBottom(MouseEvent arg0) {
		return arg0.getY() < this.getHeight() && arg0.getY() >= this.getHeight()-MOUSE_AREA;
	}
	
	private boolean isMouseLeft(MouseEvent arg0) {
		return arg0.getX() >=0 && arg0.getX() < MOUSE_AREA;
	}
	
	private boolean isMouseRight(MouseEvent arg0) {
		return arg0.getX() < this.getWidth() && arg0.getX() >= this.getWidth()-MOUSE_AREA;
	}

	public boolean containsPoint(double x, double y) {
//		System.out.println("ball x: " + x);
//		System.out.println("ball y: " + y);
		double areaX = (this.getX() - Field.ORIGIN_X) / Field.SCALE_FACTOR;
		double areaY = (this.getY() - Field.ORIGIN_Y) / Field.SCALE_FACTOR;
		double width = this.getWidth() / Field.SCALE_FACTOR;
		double height = this.getHeight() / Field.SCALE_FACTOR;
//		System.out.println("x: " + getX());
//		System.out.println("y: " + getY());
//		System.out.println("height: " + getHeight());
//		System.out.println("width: " + getWidth());
//		System.out.println((x > areaX && y > areaY && x < areaX + width && y < areaY + height));
		return (x > areaX && y > areaY && x < areaX + width && y < areaY + height);
	}
}
