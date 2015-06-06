package bot;

import data.Coordinate;
import data.RobotSoccerObject;
import ui.Field;
import ui.FocusListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public abstract class Robot extends RobotSoccerObject {
	private ArrayList<RobotListener> rListeners = new ArrayList<RobotListener>();
	private ArrayList<FocusListener> fListeners = new ArrayList<FocusListener>();
	private double theta;
	private int id;
	private boolean focused;
	public double linearVelocity;
	public double angularVelocity;
	
	/*
	 * 
	 * theta
	 * 
	 * 		 	^ 90
	 * 			|
	 * 	 180<=== ===> 0
	 * 			|
	 * 			v -90
	 */
	
	//still need to measure the actual size, just using 7cm for now
	final public static int ROBOT_WIDTH = 8;
	final public static int ROBOT_HEIGHT = 8;
	
	public Robot (Coordinate c, double theta, int id) {
		super(c);
		setTheta(theta);
		setId(id);
	}
	
	public void setX (double x) {
		super.setX(x);
		notifyRobotListeners();
	}
	
	public void setY (double y) {
		super.setY(y);
		notifyRobotListeners();
	}
	
	public void setTheta (double theta) {
		this.theta = theta;
		notifyRobotListeners();
	}
	
	public double getTheta() {
		return theta;
	}
	
	public double getXPosition() {
		return c.x;
	}
	
	public double getYPosition() {
		return c.y;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setFocus(boolean focused) {
		this.focused = focused;
		notifyFocusListeners();
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	/**
	 * Renders the robot onto the field.
	 * @param g
	 */

	public void draw(Graphics2D g) {
		int xPos = (int) (c.x*Field.SCALE_FACTOR+Field.ORIGIN_X-(ROBOT_WIDTH*Field.SCALE_FACTOR/2));
		int yPos = (int) (c.y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(ROBOT_WIDTH*Field.SCALE_FACTOR/2));
		int width = ROBOT_WIDTH*Field.SCALE_FACTOR;
		int height = ROBOT_HEIGHT*Field.SCALE_FACTOR;

		// Convert id of robot to string value. Add 1 because id starts from 0 but on display, it should start from 1.
		String id = String.valueOf(this.id + 1);
		
		// Get a copy of the current graphics object. Hence can manipulate the copy without affecting the original.
		g = (Graphics2D)g.create();
		
		// Set the color for the robot.
		// Assuming id is never less than 0, if id is between 0-4, black, 5-9 gray.
		if (this.id < 5) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(Color.GRAY);
		}
		
		// Concatenates current graphics object affinetransform with a translated rotation transform.
		g.rotate(Math.toRadians(360-theta), xPos + width/2, yPos + height/2);
		
		// Renders above transformation.
		g.fillRect(xPos, yPos, width, height);
		
		// Text rotate 90 deg. Not too sure.
		g.rotate(Math.toRadians(90), xPos + width/2, yPos + height/2);
		
		// Number colour is white. Red if the robot is selected.
		if (focused) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.WHITE);
		}
		
		FontMetrics fm = g.getFontMetrics();
		int idX = (width - fm.stringWidth(id)) / 2;
		int idY = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);
		
		g.drawString(id, xPos + idX, yPos + idY);

		// Free up resources.
		g.dispose();
    }
	
	
	public void addRobotListener(RobotListener l) {
		rListeners.add(l);
	}
	
	public void removeRobotListener(RobotListener l) {
		rListeners.remove(l);
	}
    
	public void addFocusListener(FocusListener l) {
		fListeners.add(l);
	}
	
	public void removeFocusListener(FocusListener l) {
		fListeners.remove(l);
	}
	
	/**
	 * Notify all robotlisteners. Call positionChanged. <br />
	 * {@link ui.RobotInfoPanel}
	 */
	
	private void notifyRobotListeners() {
		for (RobotListener l : rListeners) {
			l.positionChanged();
		}
	}
	
	/**
	 * Notify all focuslisteners. Call focusChanged. <br />
	 * {@link ui.RobotInfoPanel}
	 */
	
	private void notifyFocusListeners() {
		for (FocusListener l : fListeners) {
			l.focusChanged();
		}
	}
	
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ROBOT_WIDTH*Field.SCALE_FACTOR, ROBOT_WIDTH*Field.SCALE_FACTOR); // appropriate constants
    }
    
    public abstract void moveLinear();
    
    public abstract void moveAngular();

}
