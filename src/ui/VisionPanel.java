package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import controllers.VisionController;
import controllers.WebcamController;

public class VisionPanel extends JPanel implements WebcamDisplayPanelListener{
	
	
	private WebcamController webcamController;
	private VisionController visionController;
	
	private JButton boardButton;
	
	private JLabel mousePoint;
	
	private BoardDialog dialog;
	private BufferedImage webcamImage;
	
	
	public VisionPanel(WebcamController wc, VisionController vc) {
		webcamController = wc;
		visionController = vc;
		
		boardButton = new JButton("Set board area");
		
		dialog = new BoardDialog((JFrame) SwingUtilities.getWindowAncestor(this), visionController);
		dialog.setVisible(false);
		
		boardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				webcamImage = webcamController.getImageFromWebcam();
				if (webcamImage != null) {
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
		
		add(mousePoint,"wrap");
		add(boardButton);
	}
	
	
	public void updateMousePoint(int x, int y, BufferedImage image) {
		mousePoint.setText("Mouse at x:" + x + " y: " + y);
		this.repaint();
	}
	
	
	public boolean isSelectedTab() {
		
		JTabbedPane parent  = (JTabbedPane) this.getParent();
		
		if (parent.getSelectedComponent().equals(this)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void viewStateChanged() {
		
	}

}
