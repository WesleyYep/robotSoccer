package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.github.sarxos.webcam.Webcam;

/**
 * <p>Displays the webcam on the JPanel.</p>
 * <p>{@link ui.RobotSoccerMain}</p>
 * <p>{@link controllers.WebcamController}</p>
 * @author Chang Kon, Wesley, John
 *
 */

@SuppressWarnings("serial")
public class WebcamDisplayPanel extends JPanel {

	private ViewState currentViewState;
	private RSWebcamPanel webcamPanel;
	private ArrayList<WebcamDisplayPanelListener> wdpListeners;
	
	public WebcamDisplayPanel() {
		super();
		
		// Initially not connected to anything.
		currentViewState = ViewState.UNCONNECTED;
		wdpListeners = new ArrayList<WebcamDisplayPanelListener>();
		webcamPanel = null;
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
	}
	
	/**
	 * <p>Receives webcam and updates view.</p>
	 * <p>If the webcam is null, either it was not found or error occurred.</p>
	 * <p>Notifies all listeners of view state change</p>
	 * <p>{@link ui.WebcamDisplayPanelListener}</p>
	 * <p>{@link controllers.WebcamController}</p>
	 * @param webcam
	 */
	
	public void update(final Webcam webcam) {
		// Gets webcam from controller. If webcam is null, it means webcam was not found.		
		if (webcam == null) {
			currentViewState = ViewState.connectionFail();
		} else if (webcam.isOpen()) {
			currentViewState = ViewState.connectionSuccess();
			webcamPanel = new RSWebcamPanel(webcam);

            webcamPanel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                	//System.out.println(e.getX());
                    for (WebcamDisplayPanelListener listener : wdpListeners) {
                        if (listener instanceof ColourPanel) {
                            ColourPanel cp = (ColourPanel) listener;
                            if (cp.getIsSampling()) {
                                cp.takeSample(e.getX(), e.getY());
                            }
                        }                       
                   }
                }
                @Override
                public void mousePressed(MouseEvent e) { 
                	 for (WebcamDisplayPanelListener listener : wdpListeners) {
	                	 if (listener instanceof VisionPanel) {
	                     	VisionPanel panel = (VisionPanel) listener;
	                     	if (panel.isSelectedTab()) {
	                     		panel.updateMousePoint(e.getX(), e.getY(), webcam.getImage());
	                     		
	                     	}
	                     }
                	 }
                }
                @Override
                public void mouseReleased(MouseEvent e) {  }
                @Override
                public void mouseEntered(MouseEvent e) {  }
                @Override
                public void mouseExited(MouseEvent e) { }
            });
			add(webcamPanel,BorderLayout.CENTER);
		} else {
			currentViewState = ViewState.disconnect();
			removeAll();
		}
		
		notifyWebcamDisplayPanelListeners();
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Get the current state of the displayPanel. Draw text onto screen.
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

	/**
	 * <p>Add instance to be an observer</p>
	 * @param WebcamDisplayPanelListener instance
	 */
	
	public void addWebcamDisplayPanelListener(WebcamDisplayPanelListener l) {
		wdpListeners.add(l);
	}
	
	/**
	 * <p>Remove the instance from observer list</p>
	 * @param WebcamDisplayPanelListener instance
	 */
	
	public void removeWebcamDisplayPanelListener(WebcamDisplayPanelListener l) {
		wdpListeners.remove(l);
	}
	
	/**
	 * <p>Notify all observers of change</p>
	 */
	
	public void notifyWebcamDisplayPanelListeners() {
		for (WebcamDisplayPanelListener l : wdpListeners) {
			l.viewStateChanged();
		}
	}

	/**
	 * <p>Returns the current view state of the WebcamDisplayPanel</p>
	 * @return currentViewState
	 */
	
    public ViewState getViewState() {
        return currentViewState;
    }

    /**
     * <p>Return the webcamPanel for this WebcamDisplayPanel</p>
     * @return
     */
    
    public RSWebcamPanel getRSWebcamPanel() {
    	return webcamPanel;
    }
    
    /**
	 * <p>Defines the <strong>state</strong> of the display.</p>
	 * <p>Each state has a <strong>display message</strong></p>
	 * @author Chang Kon, Wesley, John
	 *
	 */
	
	public enum ViewState {
		
		UNCONNECTED("Software is not connected to a webcam device"),
		CONNECTED("Connection success"),
		ERROR("An error has occurred! Please fix");
		
		private String displayMessage;
		
		private ViewState(String displayMessage) {
			this.displayMessage = displayMessage;
		}
		
		private String getMessage() {
			return displayMessage;
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
