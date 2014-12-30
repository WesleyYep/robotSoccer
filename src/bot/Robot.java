package bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import ui.Field;

public class Robot extends JPanel {
	private int x;
	private int y;
	
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
	     g.fillRect(x,y*Field.SCALE_FACTOR,15,15);  
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(15, 15); // appropriate constants
    }
}
