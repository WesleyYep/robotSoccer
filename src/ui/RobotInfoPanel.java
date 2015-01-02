package ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bot.Robot;
import bot.RobotListener;

public class RobotInfoPanel extends JPanel implements RobotListener {
	
	
	private Robot robot;
	private JLabel title;
	private JLabel xCoordinate;
	private JLabel yCoordinate;
	private int robotNumber;
	
	public RobotInfoPanel(Robot r, int i) {
		robot = r;
		robot.addRobotListener(this);
		xCoordinate = new JLabel("x= 0");
		yCoordinate = new JLabel("y= 0");
		robotNumber = i;
		title = new JLabel("Robot " + (i+1));
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(title);
		this.add(xCoordinate);
		this.add(yCoordinate);
		
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	@Override
	public void positionChanged() {
		int x = robot.getXPosition();
		int y = robot.getYPosition();
			
		xCoordinate.setText("x= " + x);
		yCoordinate.setText("y= " + y);
		
		this.repaint();

	}

}
