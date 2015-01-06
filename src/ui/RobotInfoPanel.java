package ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import bot.Robot;
import bot.RobotListener;

public class RobotInfoPanel extends JPanel implements RobotListener {
	
	
	private Robot robot;
	private JLabel title;
	private JLabel xCoordinate;
	private JLabel yCoordinate;
	private JLabel orientation;
	private int robotNumber;
	
	public RobotInfoPanel(Robot r, int i) {
		robot = r;
		robot.addRobotListener(this);
		xCoordinate = new JLabel("x = 0");
		yCoordinate = new JLabel("y = 0");
		orientation = new JLabel("theta = 0");
		robotNumber = i;
		title = new JLabel("Robot " + (i+1));
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(title);
		this.add(xCoordinate);
		this.add(yCoordinate);
		this.add(orientation);
		this.setPreferredSize(new Dimension(100, 80));
		
		// Create border.
		Border border = BorderFactory.createLineBorder(Color.black);
		Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		this.setBorder(BorderFactory.createCompoundBorder(border, padding));
	}

	@Override
	public void positionChanged() {
		int x = robot.getXPosition();
		int y = robot.getYPosition();
		double o = robot.getTheta();
		
		
		
		xCoordinate.setText("x= " + x);
		yCoordinate.setText("y= " + y);
		
		// Show only up to two decimal places.
		orientation.setText("theta= " + String.format("%.2f", o));
		System.out.println(this.getHeight());
		this.repaint();

	}

}
