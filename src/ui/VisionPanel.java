package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.media.jai.PerspectiveTransform;
import javax.swing.JButton;
import javax.swing.JLabel;
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
		
		add(mousePoint,"wrap");
		add(boardButton);
	}
	
	
	public void updateMousePoint(int x, int y, BufferedImage image) {
		mousePoint.setText("Mouse at x:" + x + " y: " + y);
		
		if (topLeft != null && topRight !=null) {
			if (bottomLeft != null && bottomRight != null) {	
				System.out.println("");
				//positive x axis as 0
				createTransformMatrix();
				System.out.println(imagePosToActualPos(x,y));
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

		/*
		System.out.println("mapping: " + mapLeft + " " + mapRight + " " + mapTop + " " + mapBot);
		System.out.println(topLeft);
		System.out.println(topRight);
		System.out.println(bottomRight);
		System.out.println(bottomLeft);
		*/
		try {
			t = tInverse.createInverse();
			//System.out.println(t.toString());
	//		System.out.println(tInverse.toString());
		} catch (NoninvertibleTransformException e) {

			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	
	public Point2D imagePosToActualPos (double x, double y) {
		if (t != null ) {
			Point2D selectedPoint = new Point2D.Double();
			t.transform(new Point2D.Double(x,y), selectedPoint);
			double actualX = (selectedPoint.getX() - mapLeft) / ((mapRight-mapLeft)/(double)Field.OUTER_BOUNDARY_WIDTH);
			double actualY = (selectedPoint.getY() - mapTop) / ((mapBot-mapTop)/(double)Field.OUTER_BOUNDARY_HEIGHT);
			
			return new Point2D.Double(actualX,actualY);
		}
		else {
			return null;
		}
		
	}
	
	
	

	@Override
	public void viewStateChanged() {
		
	}

}
