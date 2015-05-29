package communication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This class is used to process audio editing commands in a background thread
 * @author wesley
 *
 */
public class Sender {
	private Socket clientSocket;

	public BufferedWriter os = null;

	public Sender(Socket s) {
		clientSocket = s;
		try {
			os = new BufferedWriter(new OutputStreamWriter( clientSocket.getOutputStream()) );
		} catch (IOException e) {
			os = null;
			e.printStackTrace();
		}
	}


	public void sendStuff(String outputMessage) {
		try {

			StringBuilder outputBuffer = new StringBuilder();
			outputBuffer.append("Ignore First Line" + System.lineSeparator());
			outputBuffer.append(outputMessage);

			while (outputBuffer.length() <= 512) {
				outputBuffer.append(" ");
			}

			os.write(outputBuffer.toString(), 0 , outputBuffer.length());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}


	public void close() {
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}