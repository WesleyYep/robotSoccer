package communication;

import game.Tick;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkSocket extends SwingWorker<Integer, Sender> {

	private int portNumber;
	private Receiver receiver;
	private Sender sender;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private List<ReceiverListener> receiverListeners = new ArrayList<ReceiverListener>();
	private List<SenderListener> senderListeners = new ArrayList<SenderListener>();
	private List<NetworkSocketListener> networkListeners = new ArrayList<NetworkSocketListener>();
	private boolean isClientConnected = false;
    private Tick gameTick;

    public NetworkSocket(int portNumber) {
		try {
			this.portNumber = portNumber;
			serverSocket = new ServerSocket(portNumber);
			sender = null;
			receiver = null;
			clientSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error");
		}
	}

	@Override
	protected Integer doInBackground() throws Exception {
		System.out.println("Started");;
		clientSocket = serverSocket.accept();
		notifyNetworkSocketListenerConnectionOpen();
		System.out.println("connected");
		publish(new Sender(clientSocket));
		receiver = new Receiver(clientSocket, this);
        receiver.setGameTick(gameTick);
		for (ReceiverListener l : receiverListeners) {
			receiver.registerListener(l);
		}
		receiver.execute();
		isClientConnected = true;
		return null;
	}

	public int getPortNumber() {
		return portNumber;
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

	public void addNetworkSocketListener(NetworkSocketListener listener) {
		networkListeners.add(listener);
	}

	public void removeNetworkSocketListener(NetworkSocketListener listener) {
		networkListeners.remove(listener);
	}

	private void notifyNetworkSocketListenerConnectionOpen() {
		for (NetworkSocketListener l : networkListeners) {
			l.connectionOpen();
		}
	}

	private void notifyNetworkSocketListenerConnectionClose() {
		for (NetworkSocketListener l : networkListeners) {
			l.connectionClose();
		}
	}

	public void closeServerSocket() {
		try {
			this.clientSocket.close();
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		notifyNetworkSocketListenerConnectionClose();

	}

	public void close() {
		/*
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
		/*
		 * if no client is connected, the program can cancel the current worker and
		 * toggle the button back to start
		 */
		else {
			this.cancel(true);
			System.out.println("no client connected, stopping the server socket");
			notifyNetworkSocketListenerConnectionClose();
		}
	}

    public void setGameTick(Tick tick){
        this.gameTick = tick;
    }

}
