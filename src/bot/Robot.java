package bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Robot extends JPanel {
	private int x;
	private int y;
	
	public Robot (int x, int y) {
		this.x = x;
		this.y = y;
	}
    @Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);  
	     g.drawRect(x,y,40,20);  
	     g.setColor(Color.BLACK);  
	     g.fillRect(x,y,40,20);  
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(40, 20); // appropriate constants
    }
}
