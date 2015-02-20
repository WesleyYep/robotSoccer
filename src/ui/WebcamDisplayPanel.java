package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;

import controllers.VisionController;
import static org.bytedeco.javacpp.opencv_core.*;

/**
 * <p>Displays the webcam on the JPanel.</p>
 * <p>{@link ui.RobotSoccerMain}</p>
 * <p>{@link controllers.WebcamController}</p>
 * @author Chang Kon, Wesley, John
 *
 */

@SuppressWarnings("serial")
public class WebcamDisplayPanel extends JPanel {

    private final Color DETECTDISPLAYCOLOR = Color.CYAN;
	private ViewState currentViewState;
	private JLabel webcamImageLabel = new JLabel();
    private ArrayList<WebcamDisplayPanelListener> wdpListeners;
    private SamplingPanel samplingPanel;
    private boolean isFiltering = false;
//    static CvScalar min = cvScalar(0, 0, 0, 0);//BGR-A
//    static CvScalar max = cvScalar(0, 0, 0, 0);//BGR-A

	public WebcamDisplayPanel() {
		super();
		
		// Initially not connected to anything.
		currentViewState = ViewState.UNCONNECTED;
		wdpListeners = new ArrayList<WebcamDisplayPanelListener>();
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);

        webcamImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (WebcamDisplayPanelListener listener : wdpListeners) {
                    if (listener instanceof ColourPanel) {
                        ColourPanel cp = (ColourPanel) listener;
                        if (cp.getIsSampling()) {
                            cp.takeSample(e.getX(), e.getY());
                        }
                    } else if (listener instanceof VisionPanel) {
                        VisionPanel panel = (VisionPanel) listener;
                        if (panel.isSelectedTab()) {
             //               panel.updateMousePoint(e.getX(), e.getY(), img.getBufferedImage());
                        	System.out.println(VisionController.imagePosToActualPos(e.getX(), e.getY()));
                        }
                    }
                }
            }
        });
	}
	
	/**
	 * <p>Receives webcam and updates view.</p>
	 * <p>Notifies all listeners of view state change</p>
	 * <p>{@link ui.WebcamDisplayPanelListener}</p>
	 * <p>{@link controllers.WebcamController}</p>
	 * @param webcam
	 */
	
	public void update(final IplImage img) {

		if (img == null) {
			
			/* 
			 * This assumes that you cannot have a connection fail if you're already connected hence you are disconnecting.
			 * If you are unconnected and you get a null image, connection has failed.
			 */
			
			if (currentViewState == ViewState.UNCONNECTED) {
				currentViewState = ViewState.connectionFail();
			} else if (currentViewState == ViewState.CONNECTED) {
				removeAll();
				currentViewState = ViewState.disconnect();
			}
			
		} else {
			currentViewState = ViewState.connectionSuccess();
            final BufferedImage image = img.getBufferedImage();
            
            if (isFiltering) {
                for (int j = 0; j < image.getHeight(); j++) {
                    for (int i = 0; i < image.getWidth(); i++) {
                        Color color = new Color(image.getRGB(i, j));

                        int r = color.getRed();
                        int g = color.getGreen();
                        int b = color.getBlue();

                        // http://en.wikipedia.org/wiki/YUV#Full_swing_for_BT.601
                        int y = ((76 * r + 150 * g +  29 * b + 128) >> 8);
                        int u = ((-43 * r -  84 * g + 127 * b + 128) >> 8) + 128;
                        int v = ((127 * r -  106 * g -  21 * b + 128) >> 8) + 128;

                        if (isDetected(y, u, v)) {
                            image.setRGB(i, j, DETECTDISPLAYCOLOR.getRGB());
                        }
                    }
                }
//                IplImage imgThreshold = cvCreateImage(cvGetSize(img), 8, 1);
//                cvInRangeS(img, min, max, imgThreshold);
//                image = imgThreshold.getBufferedImage();
            }

            /*
             * This method is not being called EDT thread so to update the GUI use invokeLater.
             */
            SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// Update the image.
					webcamImageLabel.setIcon(new ImageIcon(image));
					
					if (webcamImageLabel.getParent() == null) {
						add(webcamImageLabel, BorderLayout.CENTER);
					}
				}
            	
            });

		}
		
		notifyWebcamDisplayPanelListeners();
		// Thread safe call.
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

    public boolean isDetected(int y, int u, int v) {
        if (!((y >= samplingPanel.getLowerBoundForY()) && (y <= samplingPanel.getUpperBoundForY()))) {
            return false;
        }

        if (!((u >= samplingPanel.getLowerBoundForU()) && (u <= samplingPanel.getUpperBoundForU()))) {
            return false;
        }

        if (!((v >= samplingPanel.getLowerBoundForV()) && (v <= samplingPanel.getUpperBoundForV()))) {
            return false;
        }

        return true;
    }

    public void setIsFiltering(boolean bool) {
        isFiltering = bool;
    }

    public void setSamplingPanel(SamplingPanel sp) {
        this.samplingPanel = sp;

    }

//    public void setMaxandMinForFilter() {
//        int cMin = samplingPanel.getLowerBoundForY() - 16;
//        int cMax = samplingPanel.getUpperBoundForY() - 16;
//        int dMin = samplingPanel.getLowerBoundForU() - 128;
//        int dMax = samplingPanel.getUpperBoundForU() - 128;
//        int eMin = samplingPanel.getLowerBoundForV() - 128;
//        int eMax = samplingPanel.getUpperBoundForV() - 128;
//
//        int rMin = (( 298 * cMin + 409 * eMin + 128) >> 8);
//        int rMax = (( 298 * cMax + 409 * eMax + 128) >> 8);
//        int gMin = (( 298 * cMin - 100 * dMin - 208 * eMin + 128) >> 8);
//        int gMax = (( 298 * cMax - 100 * dMax - 208 * eMax + 128) >> 8);
//        int bMin = (( 298 * cMin + 516 * dMin + 128) >> 8);
//        int bMax = (( 298 * cMax + 516 * dMax + 128) >> 8);
//
//        min = cvScalar(bMin, gMin, rMin, 0);  //BGR-Alpha
//        max = cvScalar(bMax, gMax, rMax, 0); //BGR-Alpha
//    }

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
