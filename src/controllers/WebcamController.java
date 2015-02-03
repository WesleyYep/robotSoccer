package controllers;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.github.sarxos.webcam.WebcamResolution;
import ui.WebcamDisplayPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

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

	private Webcam webcam;
	private WebcamDisplayPanel webcamDisplayPanel;
	
	private final static String IPWEBCAMDEVICENAME = "BLAZE";
	
	public WebcamController(WebcamDisplayPanel webcamDisplayPanel) {
		webcam = null;
		this.webcamDisplayPanel = webcamDisplayPanel;
	}
	
	/**
	 * <p>Connects to a default webcam. <strong>Examples:</strong> USB connected or built in.</p>
	 * <p>After connection attempt, update the webcamDisplayPanel.</p>
	 */
	
	public void connect() {
		
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			
			@Override
			protected Boolean doInBackground() throws Exception {
				// Retrieve the webcam.
				webcam = Webcam.getDefault();
				webcam.setViewSize(WebcamResolution.VGA.getSize());
				webcam.open();

				
				return true;
			}

			@Override
			protected void done() {
				CardLayout layout = (CardLayout)webcamDisplayPanel.getParent().getLayout();
	    		layout.next(webcamDisplayPanel.getParent());
				try {
					get();
					webcamDisplayPanel.update(webcam);
				} catch (ExecutionException | InterruptedException e) {
					webcamDisplayPanel.update((Webcam)null);
				}
			}
			
		};
		
		worker.execute();

	}

    public BufferedImage getImageFromWebcam() {
        return webcam.getImage();
    }
	
	/**
	 * <p>Connects to a IP network camera. After connection attempt, it updates webcamDisplayPanel</p>
	 * @param url
	 */
	
	public void connect(final String url) {	
		
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

			@Override
			protected Boolean doInBackground() throws Exception {
				// Not thread safe. Updating the driver is not thread safe. Driver field is volatile.
				Webcam.setDriver(new IpCamDriver());

				// IpCamMode.PUSH - stream Motion JPEG in real time and serve newest image on-demand.
				IpCamDeviceRegistry.register(IPWEBCAMDEVICENAME, url, IpCamMode.PUSH);
				
				webcam = Webcam.getDefault();
                webcam.setViewSize(WebcamResolution.VGA.getSize());
				webcam.open();
                return true;
			}
			
			@Override
			protected void done() {
				try {
					
					get();
					webcamDisplayPanel.update(webcam);
					
				} catch (ExecutionException | InterruptedException e) {
					webcamDisplayPanel.update((Webcam)null);
					// Reset driver.
					Webcam.resetDriver();
					IpCamDeviceRegistry.unregisterAll();
				}
			}
			
		};
        worker.execute();
	}
	
	/**
	 * <p>Disconnects webcam and updates webcamDisplayPanel</p>
	 */
	
	public void disconnect() {
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				
				webcam.close();

				IpCamDeviceRegistry.unregisterAll();
				
				return null;
			}

			@Override
			protected void done() {
				CardLayout layout = (CardLayout)webcamDisplayPanel.getParent().getLayout();
	    		layout.next(webcamDisplayPanel.getParent());
				// Update webcam display panel. Disconnect webcam.
				webcamDisplayPanel.update(webcam);
				// Not thread safe.
				Webcam.resetDriver();
			}
			
		};
		
		worker.execute();
		
	}
	
}
