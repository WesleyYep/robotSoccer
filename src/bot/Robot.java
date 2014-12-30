package bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import ui.Field;

public class Robot extends JPanel {
	private int x;
	private int y;
	
	
	//still need to measure the actual size, just using 7cm for now
	final public static int ROBOT_WIDTH = 7;
	final public static int ROBOT_HEIGHT = 7;
	
	public void setX (int x) {
		this.x = x;
	}
	
	public void setY (int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	public void draw(Graphics g) {
	     g.fillRect(x*Field.SCALE_FACTOR+Field.ORIGIN_X-(ROBOT_WIDTH*Field.SCALE_FACTOR/2),
	    		    y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(ROBOT_WIDTH*Field.SCALE_FACTOR/2),
	    		    ROBOT_WIDTH*Field.SCALE_FACTOR,
	    		    ROBOT_HEIGHT*Field.SCALE_FACTOR);  
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ROBOT_WIDTH*Field.SCALE_FACTOR, ROBOT_WIDTH*Field.SCALE_FACTOR); // appropriate constants
    }
}
