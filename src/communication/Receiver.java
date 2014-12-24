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
			if (chunks.get(i) > 4000) {
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

//public class Receiver implements IReceiver{
//	private ServerSocket serverSocket;
//	private Socket clientSocket;
//	private DataInputStream in;
//	
//	public void accept(int port) {
//		//		serverSocket = new ServerSocket(port);
////		clientSocket = serverSocket.accept();
//		worker.execute();
//		// Create a reader
////			while (true){
////				in = new DataInputStream(clientSocket.getInputStream());
////				// Get the client message
////				while(in.available() != 0){
////					publish(in.readInt()/256);
////				}
////			}
//	}
//
//
//	private SwingWorker <Integer, String> worker = new SwingWorker<Integer, String>(){
//		
//		@Override
//		  protected Integer doInBackground() throws Exception {
//		    // Start
//		    publish("Start");
//		    setProgress(1);
//		    
//		    // More work was done
//		    publish("More work was done");
//		    setProgress(10);
//
//		    // Complete
//		    publish("Complete");
//		    setProgress(100);
//		    return 1;
//		  }
//		  
//		  @Override
//		  protected void process(List<String> chunks) {
//		    System.out.println(chunks.get(0));
//		  }
////		@Override
////		protected Void doInBackground() throws Exception {
////			System.out.println("connected");
////		    for (int i = 0; i < 1000; i++) {
////		    	publish (i);
////		    }
////		    return null;
////		}
////		
////		@Override
////		protected void process(List<Integer> chunks) {
////			for (int i = 0; i < chunks.size(); i++) {
////				if (i > 4000) {
////					System.out.println("Robot 5: x = " + (chunks.get(i) - 4000));
////				} else if (i > 3000) {
////					System.out.println("Robot 4: x = " + (chunks.get(i) - 3000));
////				} else if (i > 2000) {
////					System.out.println("Robot 3: x = " + (chunks.get(i) - 2000));
////				} else if (i > 1000) {
////					System.out.println("Robot 2: x = " + (chunks.get(i) - 1000));
////				} else {
////					System.out.println("Robot 1: x = " + chunks.get(i));
////				}
////			}
////		}
////		
////		@Override
////		protected void done() {
////			System.out.println("done");
////		}
//	};
//	
//}
