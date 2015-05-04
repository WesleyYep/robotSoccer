package communication;

import game.Tick;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to process audio editing commands in a background thread
 * @author wesley
 *
 */
public class Receiver extends SwingWorker<Void, String> {
	private Socket clientSocket;
	private List<ReceiverListener> listeners = new ArrayList<ReceiverListener>();
	private NetworkSocket serverSocket;
	private boolean isClientClosing = false;
    private Tick gameTick;

	public Receiver(Socket s, NetworkSocket nS) {
		clientSocket = s;
		serverSocket = nS;
	}
	
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {
    	System.out.println("listening");
    	
		try (
			InputStream inputStream = clientSocket.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			//Initialize progress property.
			while (!isCancelled()) {
				long start = System.currentTimeMillis();
				String message = bufferedReader.readLine();
				
				if (message.equals("closing")) {
					this.cancel(false);
				}
				
				if (message != null) {
					publish(message);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
    
    
    protected void process(List<String> chunks) {
    	for (ReceiverListener l : listeners) {
    		if (!this.isCancelled()) {
    			l.action(chunks);
                gameTick.run();
    		}
		}
    }

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
    	serverSocket.closeOutputStream();
    	serverSocket.closeServerSocket();
    	System.out.println("client has closed connection");
    }
    
    public void registerListener(ReceiverListener listener) {
    	listeners.add(listener);
    }


    public void setGameTick(Tick tick) {
        System.out.println("gametick set");
        if (tick == null){
            System.out.println("gametick is null!");
        }
        this.gameTick= tick;
    }
}
