package ui;

import data.Coordinate;
import data.RobotSoccerObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Ball extends RobotSoccerObject {

	//actual ball diameter is 42.7mm;
	final public static int BALL_DIAMETER = 4;

	private double theta;
	private double linearVelocity;
	private ArrayList<Integer> pastX = new ArrayList<Integer>();
	private ArrayList<Integer> pastY = new ArrayList<Integer>();
	
	private ArrayList<FocusListener> fListeners = new ArrayList<FocusListener>();
	private boolean focused;

	public Ball() {
		super(new Coordinate(70, 70));
	}

	public double getXPosition() {
		return c.x;
	}

	public double getYPosition() {
		return c.y;
	}

	public void setLinearVelocity( double linearVelocity) {
		this.linearVelocity = linearVelocity;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public void move() {
		c.x = (int)(c.x + 10 * linearVelocity * Math.cos(Math.toRadians(theta)));
		c.y = (int)(c.y - 10 * linearVelocity * Math.sin(Math.toRadians(theta)));
	}

	public void bounce() {
		theta = 90 - theta;
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

	public void draw(Graphics2D g) {

		if (focused) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}

		g.fillOval(
				(int)c.x*Field.SCALE_FACTOR+Field.ORIGIN_X-(BALL_DIAMETER*Field.SCALE_FACTOR/2),
				(int)c.y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(BALL_DIAMETER*Field.SCALE_FACTOR/2),
				BALL_DIAMETER*Field.SCALE_FACTOR,
				BALL_DIAMETER*Field.SCALE_FACTOR
				);  
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(BALL_DIAMETER*Field.SCALE_FACTOR, BALL_DIAMETER*Field.SCALE_FACTOR); // appropriate constants
	}
}
