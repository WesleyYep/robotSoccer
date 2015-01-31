package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

public class WebcamDisplayPanel extends JPanel {

	private ViewState currentViewState;
	private WebcamPanel webcamPanel;
	private ArrayList<WebcamDisplayPanelListener> wdpListeners = new ArrayList<WebcamDisplayPanelListener>();
	
	public WebcamDisplayPanel() {
		super();
		
		// Initially not connected to anything.
		currentViewState = ViewState.UNCONNECTED;
		webcamPanel = null;
		
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
	}
	
	/**
	 * Receives webcam and updates view. <br/>
	 * If the webcam is null, either it was not found or error occurred. <br/>
	 * {@link controllers.WebcamController}
	 * @param webcam
	 */
	
	public void update(Webcam webcam) {
		// Gets webcam from controller. If webcam is null, it means webcam was not found.		
		if (webcam == null) {
			currentViewState = ViewState.connectionFail();
		} else if (!webcam.isOpen() && !webcam.getLock().isLocked()) {
			currentViewState = ViewState.connectionQuery();
		} else if (!webcam.isOpen() && webcam.getLock().isLocked()) {
			currentViewState = ViewState.disconnect();
			remove(webcamPanel);
		} else {
			currentViewState = ViewState.connectionSuccess();
			webcamPanel = new WebcamPanel(webcam);
			webcamPanel.setSize(getSize());
			add(webcamPanel, BorderLayout.CENTER);
		}
		
		notifyWebcamDisplayPanelListeners();
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		switch(currentViewState) {
		case CONNECTED:
			break;
		default:
			g.setColor(Color.WHITE);
			
			// Find width and height of the display panel.
			int width = getWidth();
			int height = getHeight();
			
			String displayMessage = currentViewState.getMessage();
			
			FontMetrics fm = g.getFontMetrics();
			int displayMessageX = (width - fm.stringWidth(displayMessage)) / 2;
			int displayMessageY = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);
			
			g.drawString(displayMessage, displayMessageX, displayMessageY);
		}
	}

	public void addWebcamDisplayPanelListener(WebcamDisplayPanelListener l) {
		wdpListeners.add(l);
	}
	
	public void removeWebcamDisplayPanelListener(WebcamDisplayPanelListener l) {
		wdpListeners.remove(l);
	}
	
	public void notifyWebcamDisplayPanelListeners() {
		for (WebcamDisplayPanelListener l : wdpListeners) {
			l.viewStateChanged();
		}
	}
	
	public ViewState getViewState() {
		return currentViewState;
	}
	
	// Nested enum.
	public enum ViewState {
		
		UNCONNECTED("Software is not connected to a webcam device"),
		CONNECTING("Software is trying to connect to webcam"),
		CONNECTED("Connection success"),
		ERROR("An error has occurred! Please fix");
		
		private String displayMessage;
		
		private ViewState(String displayMessage) {
			this.displayMessage = displayMessage;
		}
		
		private String getMessage() {
			return displayMessage;
		}
		
		private static ViewState connectionQuery() {
			return CONNECTING;
		}
		
		private static ViewState connectionSuccess() {
			return CONNECTED;
		}
		
		private static ViewState connectionFail() {
			return ERROR;
		}
		
		private static ViewState disconnect() {
			return UNCONNECTED;
		}
		
	}
	
}
