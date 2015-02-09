package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;

import javax.media.jai.PerspectiveTransform;
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
	
	private Point2D bottomRight;
	private Point2D bottomLeft;
	
	private WebcamController webcamController;
	
	private JButton boardButton;
	
	private JLabel mousePoint;
	
	private boolean isDrawImage = false;
	
	private BoardDialog dialog;
	private BufferedImage webcamImage;
	
	
	private double mapLeft = 121;
	private double mapRight = 517;
	private double mapTop = 48;
	private double mapBot = 372;
	
	private PerspectiveTransform t;
	private PerspectiveTransform tInverse;
	
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
		
		
		createTransformMatrix();
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
				createTransformMatrix();
				
				Point2D selectedPoint = new Point2D.Double();
				t.transform(new Point2D.Double(x,y), selectedPoint);
				System.out.println("method 1:");
				System.out.println(selectedPoint);
			//	System.out.println((selectedPoint.getX()-mapLeft)/1.8 + " " + (selectedPoint.getY()-mapTop)/1.8 + "\n");
				double actualX = (selectedPoint.getX() - mapLeft) / ((mapRight-mapLeft)/(double)Field.OUTER_BOUNDARY_WIDTH);
				double actualY = (selectedPoint.getY() - mapTop) / ((mapBot-mapTop)/(double)Field.OUTER_BOUNDARY_HEIGHT);
				System.out.println(actualX + " " + actualY);
				/*
				System.out.println("method 2:");
				double[][] matrix = new double[3][3];
				
				t.getMatrix(matrix);
				
				double actualX = (matrix[0][0]*x + matrix[0][1]*y + matrix[0][2]) / (matrix[2][0]*x + matrix[2][1]*y + 1);
				double actualY = (matrix[1][0]*x + matrix[1][1]*y + matrix[1][2]) / (matrix[2][0]*x + matrix[2][1]*y + 1);
				System.out.println(actualX/1.8 + " " + actualY/1.8 + "\n");
				*/
				
				
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
	
	
	public void createTransformMatrix() {
		//x y: point that u want to map to
		//xp yp: orginal points
		tInverse = PerspectiveTransform.getQuadToQuad(mapLeft, mapTop, mapLeft, mapBot, mapRight, mapBot, mapRight, mapTop,
				topLeft.getX(), topLeft.getY(), bottomLeft.getX(),bottomLeft.getY()
				, bottomRight.getX(),bottomRight.getY(), topRight.getX(),topRight.getY());

		System.out.println("mapping: " + mapLeft + " " + mapRight + " " + mapTop + " " + mapBot);
		System.out.println(topLeft);
		System.out.println(topRight);
		System.out.println(bottomRight);
		System.out.println(bottomLeft);
		try {
			t = tInverse.createInverse();
			System.out.println(t.toString());
	//		System.out.println(tInverse.toString());
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	

	@Override
	public void viewStateChanged() {
		
	}

}
