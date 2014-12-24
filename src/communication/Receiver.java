package communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * This class is used to process audio editing commands in a background thread
 * @author wesley
 *
 */
public class Receiver extends SwingWorker<Void, Integer> {
	private JTextArea output;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream in;
	
	public Receiver(JTextArea output) {
		this.output = output;
	}
    /*
     * Main task. Executed in background thread.
     */
    @Override
    public Void doInBackground() {
        Random random = new Random();
		try {
			output.append("Started!\n");
			serverSocket = new ServerSocket(31000);
			clientSocket = serverSocket.accept();
			output.append("Connected!");
	        //Initialize progress property.
			while (true){
				in = new DataInputStream(clientSocket.getInputStream());
				// Get the client message
				while(in.available() != 0){
					publish(in.readInt()/256);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }
    
	@Override
	protected void process(List<Integer> chunks) {
		for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i) > 9000) {
				output.append("\nRobot 5: y = " + (chunks.get(i) - 9000));
			} else if (chunks.get(i) > 8000) {
				output.append("\nRobot 4: y = " + (chunks.get(i) - 8000));
			} else if (chunks.get(i) > 7000) {
				output.append("\nRobot 3: y = " + (chunks.get(i) - 7000));
			} else if (chunks.get(i) > 6000) {
				output.append("\nRobot 2: y = " + (chunks.get(i) - 6000));
			} else if (chunks.get(i) > 5000) {
				output.append("\nRobot 1: y = " + (chunks.get(i) - 5000));
			} else if (chunks.get(i) > 4000) {
				output.append("\nRobot 5: x = " + (chunks.get(i) - 4000));
			} else if (chunks.get(i) > 3000) {
				output.append("\nRobot 4: x = " + (chunks.get(i) - 3000));
			} else if (chunks.get(i) > 2000) {
				output.append("\nRobot 3: x = " + (chunks.get(i) - 2000));
			} else if (chunks.get(i) > 1000) {
				output.append("\nRobot 2: x = " + (chunks.get(i) - 1000));
			} else {
				output.append("\nRobot 1: x = " + chunks.get(i));
			}
		}
	}

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
        output.append("Done!\n");
    }
}
