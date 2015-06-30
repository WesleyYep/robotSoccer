package ui;

import controllers.VisionController;
import controllers.WebcamController;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Mat;
import ui.WebcamDisplayPanel.ViewState;
import utils.Image;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class VisionPanel extends JPanel implements WebcamDisplayPanelListener {
	
	
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
				dialog.setVisible(!dialog.isVisible());	
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
	public void viewStateChanged(ViewState currentViewState) {}


	@Override
	public void imageUpdated(Mat image) {
		webcamImage = Image.toBufferedImage(image);
		dialog.setBoardImage(webcamImage);
		dialog.repaint();
	}

}
