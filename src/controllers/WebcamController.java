package controllers;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import com.github.sarxos.webcam.WebcamResolution;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import ui.WebcamDisplayPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

import static org.bytedeco.javacpp.opencv_core.cvFlip;

/**
 * <p>Controls the Webcam and WebcamDisplayPanel instance.
 * <strong>Note:</strong> In this class, uses two methods, Webcam.setDriver and Webcam.resetDriver 
 * which are <strong>not thread-safe</strong>. It changes a volatile field.<p>
 * <p>{@link ui.WebcamDisplayPanel}</p>
 * <p>{@link ui.RobotSoccerMain}</p>
 * @author Chang Kon, Wesley, John
 *
 */

public class WebcamController {

//	private Webcam webcam;
	private WebcamDisplayPanel webcamDisplayPanel;
	private final static String IPWEBCAMDEVICENAME = "BLAZE";
	
	//javaCV stuff
    protected double scale = 1.0;					// to downsize the image (for speed), set this to a fraction < 1
    protected boolean mirror = true;				// make true in order to mirror left<->right so your left hand is on the left side of the image
    protected int width, height;					// the size of the grabbed images (scaled if so specified)
    protected BufferedImage image;					// image grabbed from webcam (if any)
    protected opencv_core.IplImage img;
    private Grabby grabby;							// handles webcam grabbing
    private JLabel imgLabel = new JLabel();
    private JButton captureBtn = new JButton("Capture");
    private FrameGrabber grabber;					// JavaCV
	
	public WebcamController(WebcamDisplayPanel webcamDisplayPanel) {
	//	webcam = null;
		this.webcamDisplayPanel = webcamDisplayPanel;

        // Set up webcam
          grabber = new OpenCVFrameGrabber(0); //change to ip address if using ip camera
    //      grabber = new OpenCVFrameGrabber("http://10.0.0.2:8080/video");
    //      grabber.setFormat("mjpeg");

        // Repeated attempts following discussion on javacv forum, fall 2013 (might be fixed internally in future versions)
        final int MAX_ATTEMPTS = 60;
        int attempt = 0;
        while (attempt < MAX_ATTEMPTS) {
            attempt++;
            try {
                grabber.start();
                break;
            }
            catch (Exception e) { }
        }
        if (attempt == MAX_ATTEMPTS) {
            System.err.println("Failed after "+attempt+" attempts");
            return;
        }

	}
	
	/**
	 * <p>Connects to a default webcam. <strong>Examples:</strong> USB connected or built in.</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 */
	
	public void connect() {

        // Spawn a separate thread to handle grabbing.
        grabby = new Grabby();
        grabby.execute();
//
//		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
//
//			@Override
//			protected Boolean doInBackground() throws Exception {
//				// Retrieve the webcam.
//				webcam = Webcam.getDefault();
//				webcam.setViewSize(WebcamResolution.VGA.getSize());
//				webcam.open();
//
//
//				return true;
//			}
//
//			@Override
//			protected void done() {
//				try {
//					get();
//					webcamDisplayPanel.update(webcam);
//				} catch (ExecutionException | InterruptedException e) {
//					webcamDisplayPanel.update((Webcam)null);
//				}
//			}
//
//		};
//
//		worker.execute();
	}

    public BufferedImage getImageFromWebcam() {
        return img.getBufferedImage();
//    	if (webcam != null) {
//    		return webcam.getImage();
//    	}
//    	else {
//    		return null;
//    	}
    }

	
	/**
	 * <p>Connects to a IP network camera. After connection attempt, it updates webcamDisplayPanel</p>
	 * @param url
	 */
	
//	public void connect(final String url) {
//
//		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
//
//			@Override
//			protected Boolean doInBackground() throws Exception {
//				// Not thread safe. Updating the driver is not thread safe. Driver field is volatile.
//				Webcam.setDriver(new IpCamDriver());
//
//				// IpCamMode.PUSH - stream Motion JPEG in real time and serve newest image on-demand.
//				IpCamDeviceRegistry.register(IPWEBCAMDEVICENAME, url, IpCamMode.PUSH);
//
//				webcam = Webcam.getDefault();
//                webcam.setViewSize(WebcamResolution.VGA.getSize());
//				webcam.open();
//                return true;
//			}
//
//			@Override
//			protected void done() {
//				try {
//
//					get();
//					webcamDisplayPanel.update(webcam);
//				} catch (ExecutionException | InterruptedException e) {
//					webcamDisplayPanel.update((Webcam)null);
//					// Reset driver.
//					Webcam.resetDriver();
//					IpCamDeviceRegistry.unregisterAll();
//				}
//			}
//
//		};
//        worker.execute();
//	}
	
	/**
	 * <p>Disconnects webcam and updates webcamDisplayPanel</p>
	 */
	
//	public void disconnect() {
//
//		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
//
//			@Override
//			protected Void doInBackground() throws Exception {
//
//				webcam.close();
//
//				IpCamDeviceRegistry.unregisterAll();
//
//				return null;
//			}
//
//			@Override
//			protected void done() {
//				// Update webcam display panel. Disconnect webcam.
//				webcamDisplayPanel.update(webcam);
//				// Not thread safe.
//				Webcam.resetDriver();
//			}
//
//		};
//
//		worker.execute();
//
//	}
//
	
    public void setPainter(WebcamPanel.Painter painter) {
    //	webcamDisplayPanel.getRSWebcamPanel().setPainter(painter);
    }
    
//    public Webcam getWebcam() {
//    	return webcam;
//    }
    
    public WebcamDisplayPanel getWebcamDisplayPanel() {
    	return webcamDisplayPanel;
    }



    /**
     * Handles grabbing an image from the webcam (following JavaCV examples)
     * storing it in image, and telling the canvas to repaint itself.
     */
    private class Grabby extends SwingWorker<Void, Void> {
        protected Void doInBackground() throws Exception {
            System.out.println("Initializing camera");
            while (!isCancelled()) {
                //insert grabbed video from to IplImage img
                img = grabber.grab();

                if (img != null) {
                    //Flip image horizontally
                    cvFlip(img, img, 1);
                    //Show video frame in canvas
                    webcamDisplayPanel.update(img);

                }
            }
            // All done; clean up
            grabber.stop();
            grabber.release();
            grabber = null;
            return null;
        }
    }

}
