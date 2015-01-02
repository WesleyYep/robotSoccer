package bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import ui.Field;

public class Robot extends JPanel {
	private int x;
	private int y;
	private ArrayList<RobotListener> listeners = new ArrayList<RobotListener>();
	
	//still need to measure the actual size, just using 7cm for now
	final public static int ROBOT_WIDTH =8;
	final public static int ROBOT_HEIGHT = 8;
	
	public void setX (int x) {
		for (RobotListener l : listeners) {
			l.positionChanged();
		}
		this.x = x;
	}
	
	public void setY (int y) {
		for (RobotListener l : listeners) {
			l.positionChanged();
		}
		this.y = y;
	}
	
	public int getXPosition() {
		return x;
	}
	
	public int getYPosition() {
		return y;
	}
	public void draw(Graphics g) {
	     g.fillRect(x*Field.SCALE_FACTOR+Field.ORIGIN_X-(ROBOT_WIDTH*Field.SCALE_FACTOR/2),
	    		    y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(ROBOT_WIDTH*Field.SCALE_FACTOR/2),
	    		    ROBOT_WIDTH*Field.SCALE_FACTOR,
	    		    ROBOT_HEIGHT*Field.SCALE_FACTOR);  
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
}
