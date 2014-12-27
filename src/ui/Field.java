package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import communication.ReceiverListener;
import bot.Robot;

public class Field extends JPanel implements ReceiverListener {
    private Robot[] bots = new Robot[5];
    
    public Field() {
		//draw robots
    	for (int i = 0; i < 5; i++) {
    		bots[i] = new Robot();
    	}  
    }
    
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
		
		//draw robots
    	for (Robot r : bots) {
    		r.draw(g);
    	}    
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 200); // appropriate constants
    }
    
   
    @Override
    public void action(List<Integer> chunks) {
    	for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i) > 9000) {
				bots[4].setY(chunks.get(i) - 9000);
			} else if (chunks.get(i) > 8000) {
				bots[3].setY(chunks.get(i) - 8000);
			} else if (chunks.get(i) > 7000) {
				bots[2].setY(chunks.get(i) - 7000);
			} else if (chunks.get(i) > 6000) {
				bots[1].setY(chunks.get(i) - 6000);
			} else if (chunks.get(i) > 5000) {
				bots[0].setY(chunks.get(i) - 5000);
			} else if (chunks.get(i) > 4000) {
				bots[4].setX(chunks.get(i) - 4000);
			} else if (chunks.get(i) > 3000) {
				bots[3].setX(chunks.get(i) - 3000);
			} else if (chunks.get(i) > 2000) {
				bots[2].setX(chunks.get(i) - 2000);
			} else if (chunks.get(i) > 1000) {
				bots[1].setX(chunks.get(i) - 1000);
			} else {
				bots[0].setX(chunks.get(i));
			}
		}
    	repaint();
    }
    
}