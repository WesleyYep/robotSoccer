package controllers;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.IPCameraFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.PS3EyeFrameGrabber;

import ui.WebcamDisplayPanel;
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
		try {
			grabber = new PS3EyeFrameGrabber(0);
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
			System.out.println("Could not find PS3 camera, will not try to connect to usb webcam.");
			grabber = new OpenCVFrameGrabber(0);
		}
		
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

	public BufferedImage getImageFromWebcam() {
		if (img != null) {
			return img.getBufferedImage();
		} else {
			return null;
		}
	}

	public WebcamDisplayPanel getWebcamDisplayPanel() {
		return webcamDisplayPanel;
	}

	/**
	 * <p>Retrieve the current IplImage from the webcam</p>
	 * @return
	 */

	public IplImage getIplImage () {
		return img;
	}

	/**
	 * <p>Returns the webcam status. This method returns the SwingWorker status.</p>
	 * <p><strong>DONE</strong> - SwingWorker is DONE after doInBackground method is finished.</p>
	 * <p><strong>INITIAL</strong> - Initial SwingWorker state.</p>
	 * <p><strong>STARTED</strong> - SwingWorker is STARTED before invoking doInBackground.</p>
	 * @return Webcam Status
	 */
	
	public StateValue getWebcamStatus() {
		return grabby.getState();
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

				if (grabber instanceof PS3EyeFrameGrabber) {
					if (img != null) {
						webcamDisplayPanel.update(img);
					}
				} else {

					if (img == null) {
						cancel(true);
					}

					//Show video frame in canvas
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
