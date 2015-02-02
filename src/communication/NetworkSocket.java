package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class NetworkSocket extends SwingWorker<Integer, Sender>{

	
	private int portNumber;
	private Receiver receiver;
	private Sender sender;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private JTextArea output;
	private List<ReceiverListener> receiverListeners = new ArrayList<ReceiverListener>();
	private List<SenderListener> senderListeners = new ArrayList<SenderListener>();
	private JButton toggleButton;
	private boolean isClientConnected = false;
	
	public NetworkSocket(int portNumber, JTextArea o, JButton button) {
		try {
			toggleButton = button;
	        toggleButton.setText("Stop");
			output = o;
			serverSocket = new ServerSocket(portNumber);
			sender = null;
			receiver = null;
			this.portNumber = portNumber;
			clientSocket = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error");
		}
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		// TODO Auto-generated method stub
		output.append("Started\n");
		clientSocket = serverSocket.accept();
		output.append("connected\n");
		publish(new Sender(clientSocket));
		receiver = new Receiver(clientSocket, this);
		for (ReceiverListener l : receiverListeners) {
			receiver.registerListener(l);
		}
		receiver.execute();
		isClientConnected = true;
		return null;
	}
	
	@Override
    protected void done() {
		if (isClientConnected) {
			System.out.println("connected to client");
		}
    }
	 protected void process(List<Sender> chunks) {
		 for (Sender s : chunks) {
			 sender = s;
		 }
		 
		 for (SenderListener l : senderListeners) {
			 l.setSender(sender);
		 }
		 
	 }
	 
	 public void closeOutputStream() {	
		 if (sender != null) {
			 sender.close();
		 } 
	 }
	 
	 public Sender getSender() {
		 return sender;
	 }
	
	 public void addReceiverListener(ReceiverListener listener) {
		 receiverListeners.add(listener);
	 }
	 
	 public void addSenderListener(SenderListener listener) {
		 senderListeners.add(listener);
	 }

	public void closeServerSocket() {
		try {
			this.clientSocket.close();
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		toggleButton.setText("Start");
		
	}

	public void close() {
		/**
		 * if a client is connected already, set sender to null for all the object using it
		 * then send a message to C++ program requesting a closing message back. Once the receiver 
		 * read the closing message from the C++ program. The receiver will stop listening and close 
		 * all the input readers and stream. Then in the receiver done method, it will close the outputstream
		 * first after that it will close the sockets and toggle the button.
		 */
		if (isClientConnected) {
			for (SenderListener l : senderListeners) {
				 l.setSender(null);
			}
			
			if (sender != null) {
				sender.sendStuff("close connection" + System.lineSeparator());
			}
		}
		/**
		 * if no client is connected, the program can cancel the current worker and
		 * toggle the button back to start
		 */
		else {
			this.cancel(true);
			System.out.println("no client connected, stopping the server socket");
			toggleButton.setText("Start");
		}
	}

}
