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
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
	
	private JButton boardButton;
	
	private JLabel mousePoint;
	
	private JButton buttonSelected = null;
	private String originalButtonText = null;
	
	private boolean isDrawImage = false;
	
	private BoardDialog dialog;
	private BufferedImage webcamImage;
	
	public VisionPanel(WebcamController wc) {
		
		topRight = new Point(200,100);
		topLeft = new Point(100,100);
		bottomLeft = new Point(100,200);
		bottomRight = new Point(200,200);
		
		webcamController = wc;

		
		boardButton = new JButton("Set board area");
		
		dialog = new BoardDialog((JFrame) SwingUtilities.getWindowAncestor(this), topLeft,topRight,bottomLeft,bottomRight);
		dialog.setVisible(false);
		
		boardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				webcamImage = webcamController.getImageFromWebcam();
				if (webcamImage != null) {
					System.out.println(webcamImage.getWidth() +" " + webcamImage.getHeight());
					if (webcamImage != null) {
						dialog.setBoardImage(webcamImage);
					}
					dialog.repaint();
					dialog.setVisible(!dialog.isVisible());
				}
			}
					
		});
		
		
		mousePoint = new JLabel("Mouse at x: 0 y: 0");
		
		this.setLayout(new MigLayout());
		
		add(boardButton);
	}
	
	
	public void updateMousePoint(int x, int y, BufferedImage image) {
		mousePoint.setText("Mouse at x:" + x + " y: " + y);
		
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
				

				Graphics2D g2d = image.createGraphics();
				
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
