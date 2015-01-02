package bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import ui.Field;

public class Robot extends JPanel {
	private int x;
	private int y;
	private int theta;
	
	//still need to measure the actual size, just using 7cm for now
	final public static int ROBOT_WIDTH = 7;
	final public static int ROBOT_HEIGHT = 7;
	
	public void setX (int x) {
		this.x = x;
	}
	
	public void setY (int y) {
		this.y = y;
	}
	
	public void setTheta (int theta) {
		this.theta = theta;
	}
	
	public int getXPostion() {
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
		
		g.rotate(Math.toRadians(360-theta), xPos + width/2, yPos + height/2);
	    g.fillRect(xPos, yPos, width, height);	
	  
		g.rotate(Math.toRadians(-(360-theta)), xPos + width/2, yPos + height/2);
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
    
}
