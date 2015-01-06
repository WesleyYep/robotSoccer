package communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
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
	private JTextArea output;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream in;
	private int portNumber;
	private String errorMessage = "";
	private List<ReceiverListener> listeners = new ArrayList<ReceiverListener>();
	
	public Receiver(JTextArea output, int portNumber) {
		this.output =output;
		this.portNumber = portNumber;
	}
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {
        Random random = new Random();
		try {
			serverSocket = new ServerSocket(portNumber);
			output.append("Started!\n");
			clientSocket = serverSocket.accept();
			output.append("Connected!");
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        //Initialize progress property.
			while (true){
				String message = reader.readLine();
				
				if (message != null) {
					publish(message);
				}
				
			}
		}catch (IOException e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
			return null;
		} finally {
			// Could do this in try-with resources but w/e. Close the resources when finished reading. Close in reverse
			// order that they were created.
			try {
				in.close();
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    protected void process(List<String> chunks) {
    	for (ReceiverListener l : listeners) {
			l.action(chunks);
		}
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
    	if (errorMessage.equals("")) {
    		output.append("Done!\n");
    	}
    	else if ( errorMessage.equals("Address already in use: JVM_Bind")) {
    		output.append("Please choose another port number as port " + portNumber + " is unavailable");
    	}
    	else {
    		output.append("Unknown error\n");
    	}
    }
    
    public void registerListener(ReceiverListener listener) {
    	listeners.add(listener);
    }
}
