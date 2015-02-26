package controllers;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import ui.WebcamDisplayPanel.ViewState;

public class WindowController implements WindowListener {
	
	private WebcamController webcamController;
	
	public WindowController(WebcamController wc) {
		webcamController = wc;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		ViewState webcamStatus = webcamController.getWebcamStatus();
		if (webcamStatus == ViewState.CONNECTED) {
			webcamController.disconnect();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

}
