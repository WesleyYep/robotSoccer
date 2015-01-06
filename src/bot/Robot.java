package bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

import ui.Field;

/**
 * Properties associated to individual robot.
 * @author Chang Kon, Wesley, John
 *
 */

public class Robot extends JPanel {
	private int x;
	private int y;
	private ArrayList<RobotListener> listeners = new ArrayList<RobotListener>();
	private int theta;
	
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
	
	public void setX (int x) {
		this.x = x;
		notifyRobotListeners();
	}
	
	public void setY (int y) {
		this.y = y;
		notifyRobotListeners();
	}
	
	public void setTheta (int theta) {
		this.theta = theta;
		notifyRobotListeners();
	}
	
	public int getTheta() {
		return theta;
	}
	
	public int getXPosition() {
		return x;
	}
	
	public int getYPosition() {
		return y;
	}
	public void draw(Graphics2D g) {
		int xPos = x*Field.SCALE_FACTOR+Field.ORIGIN_X-(ROBOT_WIDTH*Field.SCALE_FACTOR/2);
		int yPos = y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(ROBOT_WIDTH*Field.SCALE_FACTOR/2);
		int width = ROBOT_WIDTH*Field.SCALE_FACTOR;
		int height = ROBOT_HEIGHT*Field.SCALE_FACTOR;
		
		/*
		g.rotate(Math.toRadians(360-theta), xPos + width/2, yPos + height/2);
	    g.fillRect(xPos, yPos, width, height);
		g.rotate(Math.toRadians(-(360-theta)), xPos + width/2, yPos + height/2);
		*/
		
		// Get a copy of the current graphics object. Hence can manipulate the copy without affecting the original.
		g = (Graphics2D)g.create();
		
		// Concatenates current graphics object affinetransform with a translated rotation transform.
		g.rotate(Math.toRadians(360-theta), xPos + width/2, yPos + height/2);
		
		// Renders above transformation.
		g.fillRect(xPos, yPos, width, height);
		
		// Free up resources.
		g.dispose();
    }
	
	
	public void addRobotListener(RobotListener l) {
		listeners.add(l);
	}
	
	public void removeRobotListener(RobotListener l) {
		listeners.remove(l);
	}
    
	/**
	 * Notify all robotlisteners. Call positionChanged. <br />
	 * {@link ui.RobotInfoPanel}
	 */
	
	private void notifyRobotListeners() {
		for (RobotListener l : listeners) {
			l.positionChanged();
		}
	}
	
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ROBOT_WIDTH*Field.SCALE_FACTOR, ROBOT_WIDTH*Field.SCALE_FACTOR); // appropriate constants
    }
    
    // Not sure if this method is being called.
    /*
    @Override
    protected void paintComponent(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.rotate(theta);
    	super.paintComponent(g2d);
    }
    */
}
