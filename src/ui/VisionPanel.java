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
	
	private Point rightAbove;
	private Point rightBelow;
	private Point leftAbove;
	private Point leftBelow;
	
	private WebcamController webcamController;
	
	private JButton topRightButton;
	private JButton topLeftButton;
	private JButton topMiddleButton;
	
	private JButton bottomLeftButton;
	private JButton bottomMiddleButton;
	private JButton bottomRightButton;
	
	private JButton leftAboveButton;
	private JButton leftButton;
	private JButton leftBelowButton;
	
	private JButton rightAboveButton;
	private JButton rightButton;
	private JButton rightBelowButton;

	
	private JLabel mousePoint;
	
	private JButton buttonSelected = null;
	private String originalButtonText = null;
	
	//pixel/cm
	private double xScaleFactor = 0;
	private double yScaleFactor = 0;
	
	private int topY;
	private int BottomY;
	private int centreX;
	
	private int leftX;
	private int centreY;
	private int rightX;
	
	public VisionPanel(WebcamController wc) {
		webcamController = wc;
		
		topRightButton = new JButton("Top Right");
		topLeftButton = new JButton("Top Left");
		topMiddleButton = new JButton("Top Middle");
		
		bottomLeftButton = new JButton("Bottom Left");
		bottomMiddleButton = new JButton("Bottom Middle");
		bottomRightButton = new JButton("Bottom Right");
		
		leftAboveButton = new JButton("Left Above");
		leftButton = new JButton("Left");
		leftBelowButton = new JButton("Left Below");
		
		rightAboveButton = new JButton("Right Above");
		rightButton = new JButton("Right");
		rightBelowButton = new JButton("Right Below");
		
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
		
		rightAboveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (rightAboveButton.getText().equals("Right Above")) {
					rightAboveButton.setText("Selecting");
					buttonSelected = rightAboveButton;
					originalButtonText = "Right Above";
				}
				else {
					rightAboveButton.setText("Right Above");
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
		
		rightBelowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (rightBelowButton.getText().equals("Right Below")) {
					rightBelowButton.setText("Selecting");
					buttonSelected = rightBelowButton;
					originalButtonText = "Right Below";
				}
				else {
					rightBelowButton.setText("Right Below");
					buttonSelected = null;
					originalButtonText = null;
				}
			}
			
		});
		
		leftAboveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (leftAboveButton.getText().equals("Left Above")) {
					leftAboveButton.setText("Selecting");
					buttonSelected = leftAboveButton;
					originalButtonText = "Left Above";
				}
				else {
					leftAboveButton.setText("Left Above");
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
		
		leftBelowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (leftBelowButton.getText().equals("Left Below")) {
					leftBelowButton.setText("Selecting");
					buttonSelected = leftBelowButton;
					originalButtonText = "Left Below";
				}
				else {
					leftBelowButton.setText("Left Below");
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
		
		add(leftAboveButton);
		add(leftButton);
		add(leftBelowButton, "wrap");
		
		add(rightAboveButton);
		add(rightButton);
		add(rightBelowButton,"wrap");
		
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
			else if (buttonSelected.equals(leftAboveButton)){
				leftAbove = new Point(x,y);
			}
			else if (buttonSelected.equals(rightAboveButton)){
				rightAbove = new Point(x,y);		
			}
			else if (buttonSelected.equals(leftBelowButton)){
				leftBelow = new Point(x,y);
			}
			else if (buttonSelected.equals(rightBelowButton)){
				rightBelow = new Point(x,y);		
			}
			
			buttonSelected.setText(originalButtonText);
			buttonSelected = null;
			originalButtonText = null;
			
			System.out.println(" ");
			System.out.println(topLeft + " " + topMiddle + " " + topRight);
			System.out.println(leftAbove+ " " + left +" " + leftBelow);
			System.out.println(rightAbove+ " " + right +" " + rightBelow);
			System.out.println(bottomLeft + " " + bottomMiddle + " " + bottomRight);
			
			if (topLeft != null && topMiddle !=null && topRight !=null) {
				if (bottomLeft != null && bottomMiddle != null && bottomRight != null) {
						
						topY = Math.round((topLeft.y+topMiddle.y+topRight.y)/3);
						BottomY = Math.round((bottomLeft.y+bottomMiddle.y+bottomRight.y)/3);
						//centreX = Math.round((left.x + right.x)/2);
						
						leftX = Math.round((topLeft.x+bottomLeft.x)/2);
						rightX = Math.round((topRight.x+bottomRight.x)/2);
						//centreY = Math.round((topMiddle.y+bottomMiddle.y)/2);
						
						xScaleFactor = (rightX-leftX)/220.00;
						
						yScaleFactor = (BottomY-topY)/180.00;
						
						System.out.println("topY: " +  topY + " BottomY: " + BottomY);
						System.out.println("leftX: " + leftX + " rightX: " + rightX);
						System.out.println("xscale: " + xScaleFactor + " yscale: " + yScaleFactor);
					}	
			}
		}
		
		
		if (topY != 0 && leftX != 0) {
			double actualX = x-leftX;
			double actualY = y-topY;
			
			actualX = actualX/xScaleFactor;
			actualY = actualY/yScaleFactor;
			
			System.out.println("Actual X:" + actualX + " actual y:" + actualY);
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
