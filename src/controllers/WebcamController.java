package controllers;

import java.net.MalformedURLException;

import ui.WebcamDisplayPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
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
		
		try {
			webcam = Webcam.getDefault();
			webcam.open();
			webcamDisplayPanel.update(webcam);

			return (webcam == null) ? false : true;
		} catch (IllegalArgumentException | WebcamException e) {
			webcamDisplayPanel.update((Webcam)null);
		}
		
		return false;

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
