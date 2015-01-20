package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import bot.RobotListener;

public class Ball extends JPanel{
	
	//actual ball diameter is 42.7mm;
	final public static int BALL_DIAMETER = 4;
	
	private double x = 70;
	private double y = 70;
	private double theta;
	private double linearVelocity;
	
	private ArrayList<FocusListener> fListeners = new ArrayList<FocusListener>();
	private boolean focused;
	
	public double getXPosition() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public void setLinearVelocity( double linearVelocity) {
		this.linearVelocity = linearVelocity;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public void move() {
		x = (x + 10 * linearVelocity * Math.cos(Math.toRadians(theta)));
		y = (y - 10 * linearVelocity * Math.sin(Math.toRadians(theta)));
	}

	public void bounce(){
		theta = 90 - theta;
	}
	
	public double getYPosition() {
		return y;
	}
	
	public void setFocus(boolean focused) {
		this.focused = focused;
		notifyFocusListeners();
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	public void addFocusListener(FocusListener l) {
		fListeners.add(l);
	}
	
	public void removeFocusListener(FocusListener l) {
		fListeners.remove(l);
	}
	
	/**
	 * Notify all focuslisteners. Call focusChanged. <br />
	 */
	
	private void notifyFocusListeners() {
		for (FocusListener l : fListeners) {
			l.focusChanged();
		}
	}
	
	public void draw(Graphics g) {
		
		if (focused) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}
		
	    g.fillOval((int)x*Field.SCALE_FACTOR+Field.ORIGIN_X-(BALL_DIAMETER*Field.SCALE_FACTOR/2),
				(int)y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(BALL_DIAMETER*Field.SCALE_FACTOR/2),
	    		    BALL_DIAMETER*Field.SCALE_FACTOR,
	    		    BALL_DIAMETER*Field.SCALE_FACTOR);  
   }
   
   @Override
   public Dimension getPreferredSize() {
       return new Dimension(BALL_DIAMETER*Field.SCALE_FACTOR, BALL_DIAMETER*Field.SCALE_FACTOR); // appropriate constants
   }
}
