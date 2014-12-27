package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import bot.Robot;

public class Field extends JPanel {
	
    @Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g); 
		
		g.setColor(Color.black);

		// Draw outer boundary
		g.drawRect(5,5,390,190);
	
		// Draw center line and center circle
		g.drawLine(200,5,200,195);
		g.drawOval(200-50/2,100-50/2,50,50);
	
		// Draw penalty areas
		g.drawRect(5, 50, 50, 100);
		g.drawRect(395-50, 50, 50, 100);


		// Draw corners
		// lower left
		g.drawLine(5,185,15,195);
		// lower right
		g.drawLine(385,195,395,185);
		// upper right
		g.drawLine(5,15,15,5);
		// upper left
		g.drawLine(385,5,395,15);

		// Draw goals
		g.drawRect(5, 75, 15, 50);
		g.drawRect(395-15, 75, 15, 50);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 200); // appropriate constants
    }
    
}