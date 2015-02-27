package controllers;

import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.*;

import ui.WebcamDisplayPanel;

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

	private WebcamDisplayPanel webcamDisplayPanel;
	private final static String IPWEBCAMDEVICENAME = "BLAZE";
	
	//javaCV stuff
    protected double scale = 1.0;					// to downsize the image (for speed), set this to a fraction < 1
    protected int width, height;					// the size of the grabbed images (scaled if so specified)
    protected BufferedImage image;					// image grabbed from webcam (if any)
    protected opencv_core.IplImage img;
    private Grabby grabby;							// handles webcam grabbing
    private JLabel imgLabel = new JLabel();
    private JButton captureBtn = new JButton("Capture");
    private FrameGrabber grabber;					// JavaCV
	
	public WebcamController(WebcamDisplayPanel webcamDisplayPanel) {
		this.webcamDisplayPanel = webcamDisplayPanel;

        // Repeated attempts following discussion on javacv forum, fall 2013 (might be fixed internally in future versions)
//        final int MAX_ATTEMPTS = 60;
//        int attempt = 0;
//        while (attempt < MAX_ATTEMPTS) {
//            attempt++;
//            try {
//                grabber.start();
//                break;
//            }
//            catch (Exception e) { }
//        }
//        if (attempt == MAX_ATTEMPTS) {
//            System.err.println("Failed after "+attempt+" attempts");
//            return;
//        }

	}
	
	/**
	 * <p>Connects to a default webcam. <strong>Examples:</strong> USB connected or built in.</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 */
	
	public void connect() {
        // Spawn a separate thread to handle grabbing.
		// Set up webcam. DeviceNumber.
  //      try {
       //     System.load("C:\\javaProjects\\robotSoccer\\lib\\CLEyeMulticam.dll");
      //      grabber = new PS3EyeFrameGrabber(0);
            grabber = new OpenCVFrameGrabber(2);
//        } catch (FrameGrabber.Exception e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
        grabby = new Grabby();
        grabby.execute();
	}

	/**
	 * <p>Connects to an IP Camera</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 * @param url
	 */
	
	public void connect(String url) {
//		try {
//			grabber = new IPCameraFrameGrabber(url);
//			grabby = new Grabby();
//			grabby.execute();
//		} catch (MalformedURLException e) {
//			System.err.println("Could not connect to IP webcam.");
//		}
		
	}
	
	/**
	 * <p>Disconnect the webcam</p>
	 */
	
	public void disconnect() {
		grabby.cancel(true);
	}
	
    public BufferedImage getImageFromWebcam() {
    	if (img != null) {
    		return img.getBufferedImage();
    	}
    	else {
    		return null;
    	}
    }
    
    public WebcamDisplayPanel getWebcamDisplayPanel() {
    	return webcamDisplayPanel;
    }
    
    public IplImage getIplImage () {
    	return img;
    }

    //try this

    /**
     * Handles grabbing an image from the webcam (following JavaCV examples)
     * storing it in image, and telling the canvas to repaint itself.
     */
    private class Grabby extends SwingWorker<Void, IplImage> {
        protected Void doInBackground() throws Exception {
        	
            System.out.println("Initializing camera");
            grabber.start();
            CanvasFrame canvas = null;

            while (!isCancelled()) {
                //insert grabbed video from to IplImage img
                img = grabber.grab();
                if (img == null) {
              //      cancel(true);
                } else {
   //                 cvCvtColor(img, img, CV_RGB2BGR);

//                    if (canvas == null) {
//                        canvas = new CanvasFrame("My Image", 1);
//                    }
//                    canvas.showImage(img);
                    //Flip image horizontally
                    // cvFlip(img, img, 1);
                    //Show video frame in canvas
                 //   IplImage img2 = IplIm
                 //   cvCvtColor(img, img, CV_YUV2RGB_I420);
                    webcamDisplayPanel.update(img);

                }
            }
            
            // All done; clean up
			grabber.stop();
            grabber = null;
            
	        // Notify webcamdisplaypanel.
	        webcamDisplayPanel.update((IplImage)null);
            System.out.println("after");
            return null;
        }

    }

}
