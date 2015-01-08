package bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import communication.ReceiverListener;

import ui.Field;

public abstract class Robot extends JPanel {
	private double x;
	private double y;
	private ArrayList<RobotListener> listeners = new ArrayList<RobotListener>();
	private double theta;
	public int linearVelocity;
	public int angularVelocity;
	
	/*
	 * Java theta
	 * 
	 * 		 	^ 270
	 * 			|
	 * 	  0	<=== ===> 180
	 * 			|
	 * 			v 90
	 * 
	 * C++ theta
	 * 
	 * 		 	^ 90
	 * 			|
	 * 	 180<=== ===> 0
	 * 			|
	 * 			v -90
	 */
	
	//still need to measure the actual size, just using 7cm for now
	final public static int ROBOT_WIDTH =8;
	final public static int ROBOT_HEIGHT = 8;
	
	public Robot (double x, double y, double theta) {
		setX(x);
		setY(y);
		setTheta(theta);
	}
	
	public void setX (double x) {
		for (RobotListener l : listeners) {
			l.positionChanged();
		}
		this.x = x;
	}
	
	public void setY (double y) {
		for (RobotListener l : listeners) {
			l.positionChanged();
		}
		this.y = y;
	}
	
	public void setTheta (double theta) {
		for (RobotListener l : listeners) {
			l.positionChanged();
		}
		this.theta = theta;
	}
	
	public double getTheta() {
		return this.theta;
	}
	
	public double getXPosition() {
		return x;
	}
	
	public double getYPosition() {
		return y;
	}
	public void draw(Graphics2D g) {
		int xPos = (int) (x*Field.SCALE_FACTOR+Field.ORIGIN_X-(ROBOT_WIDTH*Field.SCALE_FACTOR/2));
		int yPos = (int) (y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(ROBOT_WIDTH*Field.SCALE_FACTOR/2));
		int width = ROBOT_WIDTH*Field.SCALE_FACTOR;
		int height = ROBOT_HEIGHT*Field.SCALE_FACTOR;
		
		g.rotate(Math.toRadians(360-theta), xPos + width/2, yPos + height/2);
	    g.fillRect(xPos, yPos, width, height);	
	  
		g.rotate(Math.toRadians(-(360-theta)), xPos + width/2, yPos + height/2);
    }
	
	
	public void addRobotListener(RobotListener l) {
		listeners.add(l);
	}
	
	public void removeRobotListener(RobotListener l) {
		listeners.remove(l);
	}
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ROBOT_WIDTH*Field.SCALE_FACTOR, ROBOT_WIDTH*Field.SCALE_FACTOR); // appropriate constants
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.rotate(theta);
    	super.paintComponent(g2d);
    }
    
    public abstract void moveLinear();
    
    public abstract void moveAngular();

    
}
