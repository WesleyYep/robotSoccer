package communication;

import javax.swing.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * This class is used to process audio editing commands in a background thread
 * @author wesley
 *
 */
public class Sender {
	private Socket clientSocket;
	public static String[] botXs = {"", "", "","",""};
	public static String[] botYs = {"", "", "","",""};

	public static BufferedWriter os = null;
	
	public Sender(Socket s) {
		clientSocket = s;
		try {
			os = new BufferedWriter(new OutputStreamWriter( clientSocket.getOutputStream()) );
		} catch (IOException e) {
			os = null;
			e.printStackTrace();
		}
	}

	public void sendStuff() {
		try {
			
			StringBuilder outputBuffer = new StringBuilder();
			
			outputBuffer.append("Ignore first line" + System.lineSeparator());
			for (int i = 0; i<5; i++) {
				outputBuffer.append(botXs[i]+ System.lineSeparator());
				outputBuffer.append(botYs[i]+ System.lineSeparator());
			}
			
			while (outputBuffer.length() <=512 ) {
				outputBuffer.append(" ");
			}
			os.write(outputBuffer.toString(),0, outputBuffer.length());
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