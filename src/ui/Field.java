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
	     g.drawRect(10,10,400,200);  
	     g.setColor(Color.GREEN);  
	     g.fillRect(10,10,400,200);  
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 200); // appropriate constants
    }
    
//    public void addRobot (Robot bot) {
//    	add(bot);
//    }
}