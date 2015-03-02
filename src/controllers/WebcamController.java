package controllers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.SwingWorker;
import org.opencv.core.*;
import org.opencv.highgui.VideoCapture;
import ui.WebcamDisplayPanel;
import ui.WebcamDisplayPanel.ViewState;

/**
 * <p>Controls the Webcam and WebcamDisplayPanel instance.
 * <p>{@link ui.WebcamDisplayPanel}</p>
 * <p>{@link ui.RobotSoccerMain}</p>
 * @author Chang Kon, Wesley, John
 *
 */

public class WebcamController {

	private WebcamDisplayPanel webcamDisplayPanel;
	private final static String IPWEBCAMDEVICENAME = "BLAZE";

	//javaCV stuff
	protected double scale = 1.0;					// to downsize the image (for speed), set this to a fraction < 1
	protected int width, height;					// the size of the grabbed images (scaled if so specified)
	protected BufferedImage image;					// image grabbed from webcam (if any)
	private Grabby grabby;							// handles webcam grabbing
	//private FrameGrabber grabber;					// JavaCV
    private VideoCapture grabber;
    private int cameraNumber = 0;
    private Mat webcamImageMat;

	public WebcamController(WebcamDisplayPanel webcamDisplayPanel) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.webcamDisplayPanel = webcamDisplayPanel;
        grabber = new VideoCapture(cameraNumber);
        grabby = new Grabby();

    }

	/**
	 * <p>Initially tries to connect to PS3 webcam. If not found, connects to usb webcam.</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 */

	public void connect() {
		// Spawn a separate thread to handle grabbing.
		// Set up webcam. DeviceNumber.

        grabby.execute();
	}

	/**
	 * <p>Connects to an IP Camera</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 * @param url
	 */

//	public void connect(String url) {
//		try {
//			grabber = new IPCameraFrameGrabber(url);
//			grabby.execute();
//		} catch (MalformedURLException e) {
//			System.err.println("Could not connect to IP webcam.");
//		}
//	}

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
		return webcamDisplayPanel;
	}

	/**
	 * <p>Retrieve the current IplImage from the webcam</p>
	 * @return
	 */

//	public IplImage getIplImage() {
//		return img;
//	}

	/**
	 * <p>Retrieve the webcam resolution. If the webcam is not running, it will return null.</p>
	 * @return webcam resolution
	 */
	
    public BufferedImage getImageFromWebcam() {
    	if (webcamImageMat != null) {
          return toBufferedImage(webcamImageMat);
    	}
    	else {
    		return null;
    	}
    }

//    private IplImage getBlurredImage(IplImage originalImage) {
//        cvSmooth(originalImage, originalImage, CV_GAUSSIAN, 5, 0, 0, 0);
//        return originalImage;
//    }

    public BufferedImage toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;

        if ( matrix.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
        byte [] b = new byte[bufferSize];

        matrix.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
//
//	public Dimension getWebcamResolution() {
//		return grabber == null ? null : new Dimension(grabber.getImageWidth(), grabber.getImageHeight());
//	}
	
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
                webcamDisplayPanel.update(webcamImageMat);
            }


            // All done; clean up
            grabber.release();
            grabber = null;
            return null;
		}

	}

}
