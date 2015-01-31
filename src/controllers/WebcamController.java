package controllers;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingWorker;

import ui.WebcamDisplayPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

public class WebcamController {

	private Webcam webcam;
	private WebcamDisplayPanel webcamDisplayPanel;
	
	private final static String IPWEBCAMDEVICENAME = "BLAZE";
	
	public WebcamController(WebcamDisplayPanel webcamDisplayPanel) {
		webcam = null;
		this.webcamDisplayPanel = webcamDisplayPanel;
	}
	
	public boolean connect() {
		
		boolean connected = false;
		
		SwingWorker<Boolean, Webcam> worker = new SwingWorker<Boolean, Webcam>() {
			
			@Override
			protected Boolean doInBackground() throws Exception {
				// TODO Auto-generated method stub
				webcam = Webcam.getDefault();

//				publish(webcam);

				webcam.open();
				
				return (webcam == null) ? false : true;
			}

			
			
			@Override
			protected void process(List<Webcam> chunks) {
				for (Webcam w : chunks) {
					webcamDisplayPanel.update(w);
				}
			}

			@Override
			protected void done() {
				try {
					boolean connected = get();
					webcamDisplayPanel.update(webcam);
				} catch (ExecutionException | InterruptedException e) {
					webcamDisplayPanel.update((Webcam)null);
				}
			}
			
		};
		
		worker.execute();
		
//		try {
//			webcam = Webcam.getDefault();
//			webcam.open();
//			webcamDisplayPanel.update(webcam);
//
//			return (webcam == null) ? false : true;
//		} catch (IllegalArgumentException | WebcamException e) {
//			webcamDisplayPanel.update((Webcam)null);
//		}
		
		return true;

	}
	
	public boolean connect(String url) {	
		
		try {
			// Not thread safe. Updating the driver is not thread safe. Driver field is volatile.
			Webcam.setDriver(new IpCamDriver());

			// IpCamMode.PUSH - stream Motion JPEG in real time and serve newest image on-demand.
			IpCamDeviceRegistry.register(IPWEBCAMDEVICENAME, url, IpCamMode.PUSH);
			webcam = Webcam.getDefault();
			webcam.open();
			webcamDisplayPanel.update(webcam);
			
			if (webcam != null) {
				return true;
			}
			
		} catch (MalformedURLException | IllegalArgumentException | WebcamException e) {
			// An error occurred in processing url.
			webcamDisplayPanel.update((Webcam)null);
		}
		
		// If an exception occurred or webcam is null, reset driver.
		Webcam.resetDriver();
		return false;
	}
	
	public void disconnect() {
		
		webcam.close();

		IpCamDeviceRegistry.unregisterAll();
		
		// Update webcam display panel. Disconnect webcam.
		webcamDisplayPanel.update(webcam);
		
		// Not thread safe.
		Webcam.resetDriver();
		
	}
	
}
