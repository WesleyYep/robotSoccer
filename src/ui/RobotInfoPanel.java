package ui;

import bot.Robot;
import bot.RobotListener;
import net.miginfocom.swing.MigLayout;
import strategy.CurrentStrategy;
import strategy.Play;
import strategy.Role;
import strategy.StrategyListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class RobotInfoPanel extends JPanel implements RobotListener, FocusListener, StrategyListener {

	private Robot robot;
	private JLabel title;
	private JLabel xCoordinate;
	private JLabel yCoordinate;
	private JLabel orientation;
	private CurrentStrategy currentStrategy;
	private JComboBox manualRolesBox = new JComboBox();
	private boolean flag = false;

	public RobotInfoPanel(Robot r, int i, final CurrentStrategy currentStrategy) {
		robot = r;
		robot.addRobotListener(this);
		robot.addFocusListener(this);
		this.currentStrategy = currentStrategy;
		xCoordinate = new JLabel("x = 0");
		yCoordinate = new JLabel("y = 0");
		orientation = new JLabel("theta = 0");
		title = new JLabel("Robot " + (i+1));

		this.setLayout(new MigLayout(
				"wrap 1, ins 0", // layout
				"[min:100:max]", // column
				"" //row
		));
		this.add(title, "span");
		this.add(xCoordinate, "span");
		this.add(yCoordinate, "span");
		this.add(orientation, "span");
		this.add(manualRolesBox, "span, growx, pushx");
		//this.setPreferredSize(new Dimension(100, 100));

		// Create border. Initially black.
		Border border = BorderFactory.createLineBorder(Color.black);
		Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		this.setBorder(BorderFactory.createCompoundBorder(border, padding));

		manualRolesBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED && !flag) {
					Role role = (Role) e.getItem();
					if (currentStrategy != null && currentStrategy.getSetPlay() != null) {
						currentStrategy.getSetPlay().addRole(robot.getId(), role);
					}
		//			System.out.println("fire!" + currentStrategy.getSetPlay().toString());
				}
			}
		});

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

	@Override
	public void strategyChanged() {
		flag = true;
		//System.out.println("flag true");
        manualRolesBox.removeAllItems();
		for (int j = 0; j < currentStrategy.getRoles().size(); j++) {
            manualRolesBox.addItem(currentStrategy.getRoles().get(j));
		}
		flag = false;
		//System.out.println("flag false");
	}

	@Override
	public void setPlayChanged(Play setPlay) {
		flag = true;
	//	System.out.println("flag true");
	//	System.out.println(setPlay);
		Role[] roles = setPlay.getRoles();
		PlaysPanel.setSelectedValue(manualRolesBox, roles[robot.getId()]);
		flag = false;
	//	System.out.println("flag false");
	}

}
