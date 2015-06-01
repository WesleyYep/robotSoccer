package controllers;

import ui.WebcamDisplayPanel.ViewState;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;


public class WindowController implements WindowListener, KeyListener, KeyEventDispatcher {
	
	private WebcamController webcamController;
	private HashMap<Integer,Boolean> keyMapping = new HashMap<Integer,Boolean>();
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

	@Override
	public void keyPressed(KeyEvent arg0) {
		System.out.println(arg0.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		
		 if (e.getID() == KeyEvent.KEY_PRESSED) {
             //System.out.println(e.getKeyCode());
             keyMapping.put(e.getKeyCode(), true);
             if (keyMapping.get(17) && keyMapping.get(83)) {
            	 
             }
         } else if (e.getID() == KeyEvent.KEY_RELEASED) {
        	 keyMapping.put(e.getKeyCode(), false);
         }
		return false;
	}

}
