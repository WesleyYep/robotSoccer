package ui;

import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.cvCircle;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
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
            IplImage ballThresholdBinary = null;
            if (isFiltering) {
//                for (int j = 0; j < image.getHeight(); j++) {
//                    for (int i = 0; i < image.getWidth(); i++) {
//                        Color color = new Color(image.getRGB(i, j));
//
//                        int r = color.getRed();
//                        int g = color.getGreen();
//                        int b = color.getBlue();
//
//                        // http://en.wikipedia.org/wiki/YUV#Full_swing_for_BT.601
//                        int y = ((76 * r + 150 * g +  29 * b + 128) >> 8);
//                        int u = ((-43 * r -  84 * g + 127 * b + 128) >> 8) + 128;
//                        int v = ((127 * r -  106 * g -  21 * b + 128) >> 8) + 128;
//
//                        if (isDetected(y, u, v)) {
//                            image.setRGB(i, j, DETECTDISPLAYCOLOR.getRGB());
//                        }
//                    }
//                }
                IplImage webcamImage = IplImage.createFrom(image);
                
                // If gaussian blurring required, place here.
                
                // Binary image.
                ballThresholdBinary = cvCreateImage(cvGetSize(webcamImage), 8, 1);
                
                int lr, lg, lb, hr, hg, hb;
                lr = (int)(samplingPanel.getLowerBoundForY() + 2*(samplingPanel.getLowerBoundForV()-128)*(1-0.299));
                lg = (int)(samplingPanel.getLowerBoundForY() - 2*(samplingPanel.getLowerBoundForU()-128)*(1-0.114)*0.114/0.587 - 2*(samplingPanel.getLowerBoundForV()-128)*(1-0.299)*0.299/0.587);
                lb = (int)(samplingPanel.getLowerBoundForY() + 2*(samplingPanel.getLowerBoundForU()-128)*(1-0.114));
                
                hr = (int)(samplingPanel.getUpperBoundForY() + 2*(samplingPanel.getUpperBoundForV()-128)*(1-0.299));
                hg = (int)(samplingPanel.getUpperBoundForY() - 2*(samplingPanel.getUpperBoundForU()-128)*(1-0.114)*0.114/0.587 - 2*(samplingPanel.getUpperBoundForV()-128)*(1-0.299)*0.299/0.587);
                hb = (int)(samplingPanel.getUpperBoundForY() + 2*(samplingPanel.getUpperBoundForU()-128)*(1-0.114));
                
        		CvScalar ballMin = cvScalar(lb, lg, lr, 0); //BGR-A
        	    CvScalar ballMax= cvScalar(hb, hg, hr, 0); //BGR-A
                cvInRangeS(webcamImage, ballMin, ballMax, ballThresholdBinary);
                CvMemStorage mem = CvMemStorage.create();
                
                CvSeq circles = cvHoughCircles( 
                	    ballThresholdBinary, //Input image
                	    mem, //Memory Storage
                	    CV_HOUGH_GRADIENT, //Detection method
                	    1, //Inverse ratio
                	    100, //Minimum distance between the centers of the detected circles
                	    100, //Higher threshold for canny edge detector
                	    100, //Threshold at the center detection stage
                	    15, //min radius
                	    500 //max radius
                	    );
                CvPoint center = null;
                for(int i = 0; i < circles.total(); i++) {
                	CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
//                    CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
                    center = cvPoint((int)circle.x(), (int)circle.y());
                    int radius = Math.round(circle.z());    
                	cvCircle(webcamImage, center, radius, CvScalar.GREEN, 6, CV_AA, 0);  
                }
                
                webcamImageLabel.setIcon(new ImageIcon(ballThresholdBinary.getBufferedImage()));
                System.out.println(center.x() +""+ center.y());
            }

            /*
             * This method is not being called EDT thread so to update the GUI use invokeLater.
             */
            SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// Update the image.
//					webcamImageLabel.setIcon(new ImageIcon(image));
					
					if (isFiltering) {
//						webcamImageLabel.setIcon(new ImageIcon(ballThresholdBinary.getBufferedImage()));
					} else {
						webcamImageLabel.setIcon(new ImageIcon(image));
					}
					
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
