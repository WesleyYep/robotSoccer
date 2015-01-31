package controllers;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import ui.WebcamDisplayPanel;

import com.github.sarxos.webcam.Webcam;
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
	
	public void connect() {
		
		SwingWorker<Boolean, Webcam> worker = new SwingWorker<Boolean, Webcam>() {
			
			@Override
			protected Boolean doInBackground() throws Exception {
				webcam = Webcam.getDefault();

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
					get();
					webcamDisplayPanel.update(webcam);
				} catch (ExecutionException | InterruptedException e) {
					webcamDisplayPanel.update((Webcam)null);
				}
			}
			
		};
		
		worker.execute();

	}
	
	public void connect(final String url) {	
		
		SwingWorker<Boolean, Webcam> worker = new SwingWorker<Boolean, Webcam>() {

			@Override
			protected Boolean doInBackground() throws Exception {
				// Not thread safe. Updating the driver is not thread safe. Driver field is volatile.
				Webcam.setDriver(new IpCamDriver());

				// IpCamMode.PUSH - stream Motion JPEG in real time and serve newest image on-demand.
				IpCamDeviceRegistry.register(IPWEBCAMDEVICENAME, url, IpCamMode.PUSH);
				
				webcam = Webcam.getDefault();
				webcam.open();
				return (webcam == null) ? false : true;
			}
			
			@Override
			protected void process(List<Webcam> chunks) {
				for (Webcam webcam : chunks) {
					webcamDisplayPanel.update(webcam);
				}
			}
			
			@Override
			protected void done() {
				try {
					boolean connected = get();
					webcamDisplayPanel.update(webcam);
					
					if (!connected) {
						// If an exception occurred or webcam is null, reset driver.
						Webcam.resetDriver();
					}
					
				} catch (ExecutionException | InterruptedException e) {
					webcamDisplayPanel.update((Webcam)null);
					
					// If an exception occurred or webcam is null, reset driver.
					Webcam.resetDriver();
				}
			}
			
		};
		
		worker.execute();
	}
	
	public void disconnect() {
		
		SwingWorker<Void, Webcam> worker = new SwingWorker<Void, Webcam>() {

			@Override
			protected Void doInBackground() throws Exception {
				
				webcam.close();

				IpCamDeviceRegistry.unregisterAll();
				
				return null;
			}

			@Override
			protected void done() {
				// Update webcam display panel. Disconnect webcam.
				webcamDisplayPanel.update(webcam);
				
				// Not thread safe.
				Webcam.resetDriver();
			}
			
		};
		
		worker.execute();
		
	}
	
}
