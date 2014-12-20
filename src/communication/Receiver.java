package communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements IReceiver {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream in;
	
	@Override
	public void accept(int port) {
		// Wait for client to connect
		try
		{
			serverSocket = new ServerSocket(port);
			clientSocket = serverSocket.accept();
			// Create a reader
			while (true){
				in = new DataInputStream(clientSocket.getInputStream());
				// Get the client message
				while(in.available() != 0){
					System.out.println(in.readInt()/256);
				}
			//	System.out.println("done");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
