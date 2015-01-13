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
	
	private int x;
	private int y;
	
	private ArrayList<FocusListener> fListeners = new ArrayList<FocusListener>();
	private boolean focused;
	
	public int getXPosition() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getYPosition() {
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
		
	    g.fillOval(x*Field.SCALE_FACTOR+Field.ORIGIN_X-(BALL_DIAMETER*Field.SCALE_FACTOR/2),
	    		    y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(BALL_DIAMETER*Field.SCALE_FACTOR/2),
	    		    BALL_DIAMETER*Field.SCALE_FACTOR,
	    		    BALL_DIAMETER*Field.SCALE_FACTOR);  
   }
   
   @Override
   public Dimension getPreferredSize() {
       return new Dimension(BALL_DIAMETER*Field.SCALE_FACTOR, BALL_DIAMETER*Field.SCALE_FACTOR); // appropriate constants
   }
}
