package controllers;

import game.Tick;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import ui.WebcamDisplayPanel;
import ui.WebcamDisplayPanel.ViewState;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <p>Controls the Webcam and WebcamDisplayPanel instance.
 * <p>{@link ui.WebcamDisplayPanel}</p>
 * <p>{@link ui.RobotSoccerMain}</p>
 * @author Chang Kon, Wesley, John
 *
 */

public class WebcamController {

	private WebcamDisplayPanel webcamDisplayPanel;
	protected BufferedImage image;					// image grabbed from webcam (if any)
	private Grabby grabby;							// handles webcam grabbing
    private VideoCapture grabber;
    private int cameraNumber = 0;
    private Mat webcamImageMat;
    private Tick gameTick;

    public WebcamController(WebcamDisplayPanel webcamDisplayPanel, Tick tick) {
		this.webcamDisplayPanel = webcamDisplayPanel;
        grabby = new Grabby();
        gameTick = tick;
    }

	/**
	 * <p>Initially tries to connect to PS3 webcam. If not found, connects to usb webcam.</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 */

	public void connect() {
		// Spawn a separate thread to handle grabbing.
		// Set up webcam. DeviceNumber.
        grabber = new VideoCapture(cameraNumber);
        grabby = new Grabby();
		grabby.execute();
	}

	/**
	 * <p>Connects to an IP Camera</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 * @param url
	 */

	public void connect(String url) {
		grabber = new VideoCapture(url);
		grabby = new Grabby();
		grabby.execute();
	}

	/**
	 * <p>Disconnect the webcam</p>
	 */

	public void disconnect() {
		if (grabby != null && !grabby.isCancelled()) {
			grabby.cancel(true);
		} else {
			System.err.println("Could not disconnect webcam. Could be not connected in the first place or is already cancelled");
		}
	}

	/**
	 * <p>Returns the current webcam image</p>
	 * @return image from webcam.
	 */

	public WebcamDisplayPanel getWebcamDisplayPanel() {
		if (webcamDisplayPanel != null)
			return webcamDisplayPanel;
		else {
			return null;
		}
	}

	/**
	 * <p>Retrieve the webcam mat. If the webcam is not running, it will return null.</p>
	 * @return webcam mat
	 */
	
    public Mat getImageFromWebcam() {
    	if (webcamImageMat != null) {
          return webcamImageMat;
    	} else {
    		return null;
    	}
    }
    
    /**
     * <p>Retrieve the HSV webcam mat. If the webcam is not running, it will return null</p>
     * @return hsv image mat
     */
    
    public Mat getHSVImageFromWebcam() {
    	if (webcamImageMat == null) {
    		return null;
    	}
    	
    	Mat hsvMat = new Mat(webcamImageMat.size(), CvType.CV_8UC3);
    	
    	// Full range HSV. Range 0-255.
    	Imgproc.cvtColor(webcamImageMat, hsvMat, Imgproc.COLOR_BGR2HSV_FULL);
    	
    	return hsvMat;
    }
    
    /**
     * <p>Returns the image dimensions of the webcam image. If webcam image is not present, returns null.</p>
     * @return image dimensions. Null if not present
     */
    
	public Dimension getWebcamResolution() {
		return webcamImageMat == null ? null : new Dimension(webcamImageMat.rows(), webcamImageMat.cols());
	}
	
	/**
	 * <p>Returns the webcam status. This method returns the webcam display panel status.</p>
	 * <p><strong>UNCONNECTED</strong></p>
	 * <p><strong>CONNECTED</strong></p>
	 * <p><strong>ERROR</strong></p>
	 * @return Webcam Status
	 */
	
	public ViewState getWebcamStatus() {
		return webcamDisplayPanel.getViewState();
	}


    /**
	 * Handles grabbing an image from the webcam (following JavaCV examples)
	 * storing it in image, and telling the canvas to repaint itself.
	 */
	private class Grabby extends SwingWorker<Void, Void> {
		protected Void doInBackground() throws Exception {

			System.out.println("Initializing camera");
            grabber.open(0);

            webcamImageMat = new Mat();

            while (!isCancelled() && grabber.isOpened()) {
                grabber.read(webcamImageMat);

                if (webcamImageMat == null) {
                	cancel(true);
                } else {
					webcamDisplayPanel.update(webcamImageMat);
					gameTick.run();
				}
            }

            return null;
		}

		@Override
		protected void done() {
			// All done; clean up
			grabber.release();
			webcamImageMat = null;
			webcamDisplayPanel.update(webcamImageMat);
		}
	}

}
