package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
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
	
	private Point2D topRight;
	private Point2D topLeft;
	private Point topMiddle;
	
	private Point2D bottomRight;
	private Point2D bottomLeft;
	private Point bottomMiddle;
	
	private WebcamController webcamController;
	
	private JButton boardButton;
	
	private JLabel mousePoint;
	
	private JButton buttonSelected = null;
	private String originalButtonText = null;
	
	private boolean isDrawImage = false;
	
	private BoardDialog dialog;
	private BufferedImage webcamImage;
	
	public VisionPanel(WebcamController wc) {
		
		topRight = new Point2D.Double(200,100);
		topLeft = new Point2D.Double(100,100);
		bottomLeft = new Point2D.Double(100,200);
		bottomRight = new Point2D.Double(200,200);
		
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
				System.out.println(Math.toDegrees(angleX1));
				System.out.println(Math.toDegrees(angleX2));
				
				//positve y axis as 0
				double angleY1 = Math.atan(topRight.getX()-bottomRight.getX())/(topRight.getY()-bottomRight.getY());
				double angleY2 = Math.atan(topLeft.getX()-bottomLeft.getX())/(topLeft.getY()-bottomLeft.getY());
				
				double avgAngleY = (angleY1+angleY2)/2;
				
				
				System.out.println("angle y:" + Math.toDegrees(avgAngleY));
				System.out.println(Math.toDegrees(angleY1));
				System.out.println(Math.toDegrees(angleY2));
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
				t.rotate(avgAngle,xi,yi);
				
				
				Point2D newTopLeft = new Point2D.Double();
				Point2D newTopRight = new Point2D.Double();
				Point2D newBottomRight = new Point2D.Double();
				Point2D newBottomLeft = new Point2D.Double();
				
				Point2D selectedPoint = new Point2D.Double();
				
				Point2D[] oldPoint = {topLeft,topRight,bottomRight,bottomLeft};
				Point2D[] newPoint = {newTopLeft,newTopRight,newBottomRight,newBottomLeft};
				
				t.transform(oldPoint, 0, newPoint, 0, 4);
				
				t.transform(new Point2D.Double(x,y), selectedPoint);

				double avgTopLine = (newTopLeft.getY() + newTopRight.getY())/2;
				double avgBotLine = (newBottomLeft.getY()+newBottomRight.getY())/2;
				
				double avgRightLine = (newTopRight.getX()+newBottomRight.getX())/2;
				double avgLeftLine = (newTopLeft.getX()+newBottomLeft.getX())/2;
				
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
				g2d.drawLine((int)Math.round(topLeft.getX())
						, (int)Math.round(topLeft.getY())
						, (int)Math.round(topRight.getX())
						,(int)Math.round(topRight.getY()));
				
				g2d.drawLine((int)Math.round(topLeft.getX())
						, (int)Math.round(topLeft.getY())
						, (int)Math.round(bottomLeft.getX())
						, (int)Math.round(bottomLeft.getY()));
				
				g2d.drawLine((int)Math.round(topRight.getX())
						,(int)Math.round(topRight.getY())
						,(int)Math.round(bottomRight.getX())
						,(int)Math.round(bottomRight.getY()));
				
				g2d.drawLine((int)Math.round(bottomLeft.getX())
						,(int)Math.round(bottomLeft.getY())
						,(int)Math.round(bottomRight.getX())
						,(int)Math.round(bottomRight.getY()));
				
				g2d.setColor(Color.BLUE);
				//after rotation
				g2d.drawLine((int)Math.round(newTopLeft.getX())
						,(int)Math.round(newTopLeft.getY())
						,(int)Math.round(newTopRight.getX())
						,(int)Math.round(newTopRight.getY()));
				g2d.drawLine((int)Math.round(newTopLeft.getX())
						,(int)Math.round(newTopLeft.getY())
						,(int)Math.round(newBottomLeft.getX())
						,(int)Math.round(newBottomLeft.getY()));
				g2d.drawLine((int)Math.round(newTopRight.getX())
						,(int)Math.round(newTopRight.getY())
						,(int)Math.round(newBottomRight.getX())
						,(int)Math.round(newBottomRight.getY()));
				g2d.drawLine((int)Math.round(newBottomLeft.getX())
						,(int)Math.round(newBottomLeft.getY())
						,(int)Math.round(newBottomRight.getX())
						,(int)Math.round(newBottomRight.getY()));
				
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
