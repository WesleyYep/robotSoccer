package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
	
	public NetworkSocket(int portNumber, JTextArea o) {
		try {
			
			output = o;
			serverSocket = new ServerSocket(portNumber);
			sender = null;
			receiver = null;
			this.portNumber = portNumber;
			clientSocket = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		// TODO Auto-generated method stub
		output.append("Started\n");
		clientSocket = serverSocket.accept();
		output.append("connected\n");
		publish(new Sender(clientSocket));
		receiver = new Receiver(clientSocket);
		for (ReceiverListener l : receiverListeners) {
			receiver.registerListener(l);
		}
		receiver.execute();
		
		return null;
	}
	
	@Override
    protected void done() {
       System.out.println("done");
    }
	 protected void process(List<Sender> chunks) {
		 for (Sender s : chunks) {
			 sender = s;
		 }
		 
		 for (SenderListener l : senderListeners) {
			 l.setSender(sender);
		 }
		 
	 }
	 
	 public void stop() {
		 
		 
		 
		 if (sender != null) {
			 sender.close();
		 }
		 
		 for (SenderListener l : senderListeners) {
			 l.setSender(null);
		 }

		 if (receiver != null) {
			 receiver.cancel(false);
		 }
		 
		try {
				if (clientSocket != null) clientSocket.close();
				if (serverSocket != null) serverSocket.close();

		} catch (IOException e) {
				e.printStackTrace();
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

}
