package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
	
	private boolean isDrawImage = false;
	
	
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
	
	
	public void updateMousePoint(int x, int y, BufferedImage image) {
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
						/*Method 1
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
						
					
						if (leftAbove != null && leftBelow != null && rightAbove != null && rightBelow != null) {
							
						}
						*/
					}	
			}
			
		}
		
		/*Method 1
		if (topY != 0 && leftX != 0) {
			
			double actualX = x-leftX;
			double actualY = y-topY;
			
			actualX = actualX/xScaleFactor;
			actualY = actualY/yScaleFactor;
			
			System.out.println("Actual X:" + actualX + " actual y:" + actualY);
			
		}*/
		
		if (topLeft != null && topRight !=null) {
			if (bottomLeft != null && bottomRight != null) {	
				System.out.println("");
				//positive x axis as 0
				double angleX1 = Math.atan(topRight.getY()-topLeft.getY())/(topRight.getX()-topLeft.getX());
				double angleX2 = Math.atan(bottomRight.getY()-bottomLeft.getY())/(bottomRight.getX()-bottomLeft.getX());
				
				double avgAngleX = (angleX1+angleX2)/2;
				System.out.println("angle x:" + Math.toDegrees(avgAngleX));
			//	System.out.println(angleX1);
			//	System.out.println(angleX2);
				
				//positve y axis as 0
				double angleY1 = Math.atan(topRight.getX()-bottomRight.getX())/(topRight.getY()-bottomRight.getY());
				double angleY2 = Math.atan(topLeft.getX()-bottomLeft.getX())/(topLeft.getY()-bottomLeft.getY());
				
				double avgAngleY = (angleY1+angleY2)/2;
			//	System.out.println(angleY1);
			//	System.out.println(angleY2);
				
				System.out.println("angle y:" + Math.toDegrees(avgAngleY));
				
				double avgAngle = -1*(avgAngleY+ (-1*avgAngleX)) /2;
				//double avgAngle = (-1*(2*Math.PI)/360)*1.5;
				System.out.println("avgAngle:" + Math.toDegrees(avgAngle));
				
				
				double x1 = (topLeft.getX()+topRight.getX())/2;
				double y1 = (topLeft.getY()+topRight.getY())/2;
				
				double x2 = (bottomLeft.getX()+bottomRight.getX())/2;
				double y2 = (bottomLeft.getY()+bottomRight.getY())/2;
				
				double x3 = (topLeft.getX()+bottomLeft.getX())/2;
				double y3 = (topLeft.getY()+bottomLeft.getY())/2;
				
				double x4 = (topRight.getX()+bottomRight.getX())/2;
				double y4 = (topRight.getY()+bottomRight.getY())/2;
				
				double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);  
			    double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
			    double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
				
				AffineTransform t = new AffineTransform();
				t.rotate((2*Math.PI)/90,xi,yi);
				
				
				Point newTopLeft = new Point();
				Point newTopRight = new Point();
				Point newBottomRight = new Point();
				Point newBottomLeft = new Point();
				
				Point selectedPoint = new Point();
				
				Point[] oldPoint = {topLeft,topRight,bottomRight,bottomLeft};
				Point[] newPoint = {newTopLeft,newTopRight,newBottomRight,newBottomLeft};
				
				t.transform(oldPoint, 0, newPoint, 0, 4);
				
				t.transform(new Point(x,y), selectedPoint);
				
				double avgTopLine = (topLeft.getY() + topRight.getY())/2;
				double avgBotLine = (bottomLeft.getY()+bottomRight.getY())/2;
				
				double avgRightLine = (topRight.getX()+bottomRight.getX())/2;
				double avgLeftLine = (topLeft.getX()+bottomLeft.getX())/2;
				
				double yScaleFactor = (avgBotLine - avgTopLine)/180.00;
				double xScaleFactor = (avgRightLine-avgLeftLine)/220.00;
				
				double actualX = (selectedPoint.getX()-avgLeftLine)/xScaleFactor;
				double actualY = (selectedPoint.getY()-avgTopLine)/yScaleFactor;
					
				
				System.out.println(actualX + " " + actualY);
				/*
				double newTopRightY = (-topRight.getX()*Math.sin(avgAngle)) + (topRight.getY()*Math.cos(avgAngle));
				double newTopLeftY= (-topLeft.getX()*Math.sin(avgAngle)) + (topLeft.getY()*Math.cos(avgAngle));
				
				double newBottomRightY = (-bottomRight.getX()*Math.sin(avgAngle)) + (bottomRight.getY()*Math.cos(avgAngle));
				double newBottomLeftY = (-bottomLeft.getX()*Math.sin(avgAngle)) + (bottomLeft.getY()*Math.cos(avgAngle));
				
				double newTopRightX = (topRight.getX()*Math.cos(avgAngle)) + (topRight.getY()*Math.sin(avgAngle));
				double newBottomRightX = (bottomRight.getX()*Math.cos(avgAngle)) + (bottomRight.getY()*Math.sin(avgAngle));
				
				double newBottomLeftX = (bottomLeft.getX()*Math.cos(avgAngle)) + (bottomLeft.getY()*Math.sin(avgAngle));
				double newTopLeftX= (topLeft.getX()*Math.cos(avgAngle)) + (topLeft.getY()*Math.sin(avgAngle));
				

				double newTopRightY = (-topRight.getX()*Math.sin(avgAngle)) + (topRight.getY()*Math.cos(avgAngle));
				double newTopLeftY= (-topLeft.getX()*Math.sin(avgAngle)) + (topLeft.getY()*Math.cos(avgAngle));
				
				double newBottomRightY = (-bottomRight.getX()*Math.sin(avgAngle)) + (bottomRight.getY()*Math.cos(avgAngle));
				double newBottomLeftY = (-bottomLeft.getX()*Math.sin(avgAngle)) + (bottomLeft.getY()*Math.cos(avgAngle));
				
				double newTopRightX = (topRight.getX()*Math.cos(avgAngle)) + (topRight.getY()*Math.sin(avgAngle));
				double newBottomRightX = (bottomRight.getX()*Math.cos(avgAngle)) + (bottomRight.getY()*Math.sin(avgAngle));
				
				double newBottomLeftX = (bottomLeft.getX()*Math.cos(avgAngle)) + (bottomLeft.getY()*Math.sin(avgAngle));
				double newTopLeftX= (topLeft.getX()*Math.cos(avgAngle)) + (topLeft.getY()*Math.sin(avgAngle));
				
				System.out.println("newTopRightX:" + newTopRightX);
				System.out.println("newBottomRightX" + newBottomRightX);
				
				double avgTopYLine = (newTopRightY+newTopLeftY)/2;
				double avgBotYLine = (newBottomRightY+newBottomLeftY)/2;
				double yScaleFactor = (avgBotYLine-avgTopYLine)/180.00;
				
				double avgLeftXLine = (newTopLeftX+newBottomLeftX)/2;
				double avgRightXLine = (newTopRightX+newBottomRightX)/2;
				double xScaleFactor = (avgRightXLine-avgLeftXLine)/220.00;
				
				System.out.println(newTopRightY + " " + newTopLeftY);
				System.out.println(avgTopYLine + " " + avgBotYLine + " " + yScaleFactor);
				System.out.println(avgLeftXLine + " " + avgRightXLine + " " + xScaleFactor);
				
				
				double newX = (x*Math.cos(avgAngle)) + (y*Math.sin(avgAngle));
				double newY = (-x*Math.sin(avgAngle)) + (y*Math.cos(avgAngle));
				
				newX = (newX - avgLeftXLine)/xScaleFactor;
				newY = (newY - avgTopYLine)/yScaleFactor;
				
				System.out.println("actual x:" + newX);
				System.out.println("actual y:" + newY);		
				
				*/
				Graphics2D g2d = image.createGraphics();
				
				/*
				double oldTopLeftY = (avgLeftXLine*Math.sin(avgAngle)) + (avgTopYLine*Math.cos(avgAngle));
				double oldTopRightY = (avgRightXLine*Math.sin(avgAngle)) + (avgTopYLine*Math.cos(avgAngle));
				
				double oldTopLeftX = (avgLeftXLine*Math.cos(avgAngle)) - (avgTopYLine*Math.sin(avgAngle));
				double oldTopRightX = (avgRightXLine*Math.cos(avgAngle)) - (avgTopYLine*Math.sin(avgAngle));
				*/
				g2d.setColor(Color.black);
				
				//axis
				g2d.drawLine(image.getWidth()/2, 0, image.getWidth()/2, image.getHeight());
				g2d.drawLine(0, image.getHeight()/2, image.getWidth(), image.getHeight()/2);
				
				//orginally rectangle
				g2d.drawLine(topLeft.x, topLeft.y, topRight.x, topRight.y);
				g2d.drawLine(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);
				g2d.drawLine(topRight.x,topRight.y,bottomRight.x,bottomRight.y);
				g2d.drawLine(bottomLeft.x,bottomLeft.y,bottomRight.x,bottomRight.y);
				
				g2d.setColor(Color.BLUE);
				//after rotation
				g2d.drawLine(newTopLeft.x,newTopLeft.y,newTopRight.x,newTopRight.y);
				g2d.drawLine(newTopLeft.x,newTopLeft.y,newBottomLeft.x,newBottomLeft.y);
				g2d.drawLine(newTopRight.x,newTopRight.y,newBottomRight.x,newBottomRight.y);
				g2d.drawLine(newBottomLeft.x,newBottomLeft.y,newBottomRight.x,newBottomRight.y);
				
				g2d.setColor(Color.red);
				//avg lines;
				g2d.drawLine((int)Math.round(avgLeftLine),(int) Math.round(avgTopLine),(int) Math.round(avgRightLine),(int) Math.round(avgTopLine));
				g2d.drawLine((int)Math.round(avgLeftLine),(int) Math.round(avgTopLine),(int) Math.round(avgLeftLine),(int) Math.round(avgBotLine));
				g2d.drawLine((int)Math.round(avgRightLine),(int) Math.round(avgTopLine),(int) Math.round(avgRightLine),(int) Math.round(avgBotLine));
				g2d.drawLine((int)Math.round(avgLeftLine),(int) Math.round(avgBotLine),(int) Math.round(avgRightLine),(int) Math.round(avgBotLine));
				
				g2d.setColor(Color.green);
				System.out.println(selectedPoint);
				g2d.fillRect((int)Math.round(selectedPoint.getX()), (int)Math.round(selectedPoint.getY()), 5, 5);
				
				g2d.setColor(Color.MAGENTA);
				g2d.fillRect(x,y,5,5);
				/*
				g2d.drawLine((int)Math.round(newTopLeftX),(int) Math.round(newTopLeftY),(int) Math.round(newTopRightX),(int) Math.round(newTopRightY));
				g2d.drawLine((int)Math.round(newTopLeftX),(int) Math.round(newTopLeftY),(int) Math.round(newBottomLeftX),(int) Math.round(newBottomLeftY));
				g2d.drawLine((int)Math.round(newTopRightX),(int) Math.round(newTopRightY),(int) Math.round(newBottomRightX),(int) Math.round(newBottomRightY));
				g2d.drawLine((int)Math.round(newBottomLeftX),(int) Math.round(newBottomLeftY),(int) Math.round(newBottomRightX),(int) Math.round(newBottomRightY));
				
				g2d.drawLine((int)Math.round(oldTopLeftX), 
						(int)Math.round(oldTopLeftY), 
						(int)Math.round(oldTopRightX), 
						(int)Math.round(oldTopRightY));
				*/
				
				/*
				g2d.drawLine((int)Math.round(avgLeftXLine), 
						(int)Math.round(avgTopYLine), 
						(int)Math.round(avgRightXLine), 
						(int)Math.round(avgTopYLine));
						
				*/
			
				ImageIcon ii = new ImageIcon(image);
				JOptionPane.showMessageDialog(null,ii);
					
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
