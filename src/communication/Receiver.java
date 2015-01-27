package communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * This class is used to process audio editing commands in a background thread
 * @author wesley
 *
 */
public class Receiver extends SwingWorker<Void, String> {
	private Socket clientSocket;
	private BufferedReader bReader;
	private InputStreamReader iReader;
	private InputStream in;
	private String errorMessage = "";
	private List<ReceiverListener> listeners = new ArrayList<ReceiverListener>();
	private ServerSocket serverSocket;

	public Receiver(Socket s) {
		clientSocket = s;
	}
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {
		try {
			in = clientSocket.getInputStream();
			iReader = new InputStreamReader(in);
			bReader = new BufferedReader(iReader);
			//Initialize progress property.
			while (!isCancelled()) {
				
				String message = bReader.readLine();

				if (message != null) {
					publish(message);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
			return null;
		} finally {
			
		}
		return null;
	}
    
    
    protected void process(List<String> chunks) {
    	for (ReceiverListener l : listeners) {
    		if (!this.isCancelled())
    			l.action(chunks);
		}
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
    	try {
			if (bReader != null) bReader.close();
			if (iReader != null) iReader.close();
			if (in != null)  in.close();

			bReader = null;
			iReader = null;
			in = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void registerListener(ReceiverListener listener) {
    	listeners.add(listener);
    }
    
}
