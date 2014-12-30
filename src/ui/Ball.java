package ui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Ball extends JPanel{
	
	//actual ball diameter is 42.7mm;
	final public static int BALL_DIAMETER = 4;
	
	private int x;
	private int y;
	
	public void setX(int x) {
		this.x = x;
	}

	
	public void setY(int y) {
		this.y = y;
	}
	
	public void draw(Graphics g) {
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
