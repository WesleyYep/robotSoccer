package ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import bot.Robot;
import bot.RobotListener;

public class RobotInfoPanel extends JPanel implements RobotListener, FocusListener {

	private Robot robot;
	private JLabel title;
	private JLabel xCoordinate;
	private JLabel yCoordinate;
	private JLabel orientation;

	public RobotInfoPanel(Robot r, int i) {
		robot = r;
		robot.addRobotListener(this);
		robot.addFocusListener(this);
		xCoordinate = new JLabel("x = 0");
		yCoordinate = new JLabel("y = 0");
		orientation = new JLabel("theta = 0");
		title = new JLabel("Robot " + (i+1));

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(title);
		this.add(xCoordinate);
		this.add(yCoordinate);
		this.add(orientation);
		this.setPreferredSize(new Dimension(100, 80));

		// Create border. Initially black.
		Border border = BorderFactory.createLineBorder(Color.black);
		Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		this.setBorder(BorderFactory.createCompoundBorder(border, padding));
	}

	@Override
	public void positionChanged() {
		double x = robot.getXPosition();
		double y = robot.getYPosition();
		double o = robot.getTheta();

		xCoordinate.setText("x= " + (int)x);
		yCoordinate.setText("y= " + (int)y);
		// Show only up to two decimal places.
		orientation.setText("theta= " + String.format("%.2f", o));

		this.repaint();
	}

	@Override
	public void focusChanged() {
		if (robot.isFocused()) {
			this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED), ((CompoundBorder)getBorder()).getInsideBorder()));
		} else {
			this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), ((CompoundBorder)getBorder()).getInsideBorder()));
		}
	}

}
