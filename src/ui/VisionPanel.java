package ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import controllers.WebcamController;

public class VisionPanel extends JPanel implements WebcamDisplayPanelListener{
	
	private Point topRight;
	private Point topLeft;
	private Point topMiddle;
	
	private Point bottomRight;
	private Point bottomLeft;
	private Point bottomMiddle;
	
	private Point right;
	private Point left;
	
	private WebcamController webcamController;
	
	private JButton topRightButton;
	private JButton topLeftButton;
	private JButton topMiddleButton;
	
	private JButton bottomLeftButton;
	private JButton bottomMiddleButton;
	private JButton bottomRightButton;
	
	private JButton leftButton;
	private JButton rightButton;

	
	private JLabel mousePoint;
	
	private JButton buttonSelected = null;
	private String originalButtonText = null;
	
	public VisionPanel(WebcamController wc) {
		webcamController = wc;
		
		topRightButton = new JButton("Top Right");
		topLeftButton = new JButton("Top Left");
		topMiddleButton = new JButton("Top Middle");
		
		bottomLeftButton = new JButton("Bottom Left");
		bottomMiddleButton = new JButton("Bottom Middle");
		bottomRightButton = new JButton("Bottom Right");
		
		leftButton = new JButton("Left");
		rightButton = new JButton("Right");
		
		
		topRightButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (topRightButton.getText().equals("Top Right")) {
					topRightButton.setText("Selecting");
					buttonSelected = topRightButton;
					originalButtonText = "Top Right";
				}
				else {
					topRightButton.setText("Top Right");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});
		
		topMiddleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (topMiddleButton.getText().equals("Top Middle")) {
					topMiddleButton.setText("Selecting");
					buttonSelected = topMiddleButton;
					originalButtonText = "Top Middle";
				}
				else {
					topMiddleButton.setText("Top Middle");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});

		topLeftButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (topLeftButton.getText().equals("Top Left")) {
					topLeftButton.setText("Selecting");
					buttonSelected = topLeftButton;
					originalButtonText = "Top Left";
				}
				else {
					topLeftButton.setText("Top Left");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});

		bottomRightButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (bottomRightButton.getText().equals("Bottom Right")) {
					bottomRightButton.setText("Selecting");
					buttonSelected = bottomRightButton;
					originalButtonText = "Bottom Right";
				}
				else {
					bottomRightButton.setText("Bottom Right");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});

		bottomMiddleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (bottomMiddleButton.getText().equals("Bottom Middle")) {
					bottomMiddleButton.setText("Selecting");
					buttonSelected = bottomMiddleButton;
					originalButtonText = "Bottom Middle";
				}
				else {
					bottomMiddleButton.setText("Bottom Middle");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});

		bottomLeftButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (bottomLeftButton.getText().equals("Bottom Left")) {
					bottomLeftButton.setText("Selecting");
					buttonSelected = bottomLeftButton;
					originalButtonText = "Bottom Left";
				}
				else {
					bottomLeftButton.setText("Bottom Left");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});
	
		rightButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (rightButton.getText().equals("Right")) {
					rightButton.setText("Selecting");
					buttonSelected = rightButton;
					originalButtonText = "Right";
				}
				else {
					rightButton.setText("Right");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});

		leftButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (leftButton.getText().equals("Left")) {
					leftButton.setText("Selecting");
					buttonSelected = leftButton;
					originalButtonText = "Left";
				}
				else {
					leftButton.setText("Left");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});
		
		mousePoint = new JLabel("Mouse at x: 0 y: 0");
		
		this.setLayout(new MigLayout());
		
		
		add(mousePoint, "wrap");
		add(topLeftButton);
		add(topMiddleButton);
		add(topRightButton,"wrap");
		
		add(leftButton);
		add(rightButton,"wrap");
		
		add(bottomLeftButton);
		add(bottomMiddleButton);
		add(bottomRightButton);
	}
	
	
	
	public void updateMousePoint(int x, int y) {
		mousePoint.setText("Mouse at x:" + x + " y: " + y);
		
		if (buttonSelected != null) {
			if (buttonSelected.equals(topRightButton)) {
				topRight = new Point(x,y);
			}
			else if (buttonSelected.equals(topMiddleButton)){
				topMiddle = new Point(x,y);
			}
			else if (buttonSelected.equals(topLeftButton)){
				topLeft = new Point(x,y);
			}
			else if (buttonSelected.equals(bottomRightButton)) {
				bottomRight = new Point(x,y);
			}
			else if (buttonSelected.equals(bottomMiddleButton)){
				bottomMiddle = new Point(x,y);
			}
			else if (buttonSelected.equals(bottomLeftButton)){
				bottomLeft = new Point(x,y);
			}
			else if (buttonSelected.equals(leftButton)){
				left = new Point(x,y);
			}
			else if (buttonSelected.equals(rightButton)){
				right = new Point(x,y);		
			}
			
			buttonSelected.setText(originalButtonText);
			buttonSelected = null;
			originalButtonText = null;
		}
		System.out.println(" ");
		System.out.println(topLeft + " " + topMiddle + " " + topRight);
		System.out.println(left + " " + right);
		System.out.println(bottomLeft + " " + bottomMiddle + " " + bottomRight);
		
		if (topLeft != null && topMiddle !=null && topRight !=null) {
			if (left != null && right != null) {
				if (bottomLeft != null && bottomMiddle != null && bottomRight != null) {
					
					
				}
			}
		}
		
		
		this.repaint();
	}
	
	
	public boolean isSelectedTab() {
		
		JTabbedPane parent  = (JTabbedPane) this.getParent();
		
		if (parent.getSelectedComponent().equals(this)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
	
	

	@Override
	public void viewStateChanged() {
		
	}
}
