package controllers;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

import javax.swing.SwingWorker;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.IPCameraFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import ui.WebcamDisplayPanel;
import ui.WebcamDisplayPanel.ViewState;

import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

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
	protected opencv_core.IplImage img;
	private Grabby grabby;							// handles webcam grabbing
	private FrameGrabber grabber;					// JavaCV

	public WebcamController(WebcamDisplayPanel webcamDisplayPanel) {
		this.webcamDisplayPanel = webcamDisplayPanel;
		grabby = new Grabby();
	}

	/**
	 * <p>Initially tries to connect to PS3 webcam. If not found, connects to usb webcam.</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 */

	public void connect() {
		// Spawn a separate thread to handle grabbing.
		// Set up webcam. DeviceNumber.
//		try {
//			grabber = new PS3EyeFrameGrabber(0);
//		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
//			e.printStackTrace();
//			System.out.println("Could not find PS3 camera, will not try to connect to usb webcam.");
//			grabber = new OpenCVFrameGrabber(0);
//		}
//		
//		grabby.execute();
  //      try {
       //     System.load("C:\\javaProjects\\robotSoccer\\lib\\CLEyeMulticam.dll");
      //      grabber = new PS3EyeFrameGrabber(0);
            grabber = new OpenCVFrameGrabber(0);
//        } catch (FrameGrabber.Exception e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
        grabby.execute();
	}

	/**
	 * <p>Connects to an IP Camera</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 * @param url
	 */

	public void connect(String url) {
		try {
			grabber = new IPCameraFrameGrabber(url);
			grabby.execute();
		} catch (MalformedURLException e) {
			System.err.println("Could not connect to IP webcam.");
		}
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
		return webcamDisplayPanel;
	}

	/**
	 * <p>Retrieve the current IplImage from the webcam</p>
	 * @return
	 */

	public IplImage getIplImage() {
		return img;
	}

	/**
	 * <p>Retrieve the webcam resolution. If the webcam is not running, it will return null.</p>
	 * @return webcam resolution
	 */
	
    public BufferedImage getImageFromWebcam() {
    	if (img != null) {
  //          return getBlurredImage(img).getBufferedImage();
          return img.getBufferedImage();
    	}
    	else {
    		return null;
    	}
    }

    private IplImage getBlurredImage(IplImage originalImage) {
        cvSmooth(originalImage, originalImage, CV_GAUSSIAN, 5, 0, 0, 0);
        return originalImage;
    }

    //try this


	public Dimension getWebcamResolution() {
		return grabber == null ? null : new Dimension(grabber.getImageWidth(), grabber.getImageHeight());
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
	private class Grabby extends SwingWorker<Void, IplImage> {
		protected Void doInBackground() throws Exception {

			System.out.println("Initializing camera");
			grabber.start();

			while (!isCancelled()) {
				//insert grabbed video from to IplImage img
				img = grabber.grab();

				if (img != null) {
					webcamDisplayPanel.update(img);
				}
				
			}

			// All done; clean up
			grabber.stop();
			grabber = null;

			// Notify webcamdisplaypanel.
			webcamDisplayPanel.update((IplImage)null);
			return null;
		}

	}

}
