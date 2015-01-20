package communication;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is used to process audio editing commands in a background thread
 * @author wesley
 *
 */
public class Sender {
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static int portNumber;
	private String errorMessage = "";
	public static String ballX = "ballX:070";
	public static String ballY = "ballY:070";
	public static String[] botXs = {"botX0:050", "botX1:060", "botX2:080","botX3:090","botX4:100"};
	public static String[] botYs = {"botY0:050", "botY1:060", "botY2:080","botY3:090","botY4:100"};

	public static void connect() {
		portNumber = 32000;
		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Started!\n");
			clientSocket = serverSocket.accept();
			System.out.println("Connected!");

		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendStuff() {
		try {
			clientSocket.getOutputStream().write(ballY.getBytes());
			clientSocket.getOutputStream().write(ballX.getBytes());
			for (int i = 0; i < 5; i++) {
				clientSocket.getOutputStream().write(botXs[i].getBytes());
				clientSocket.getOutputStream().write(botYs[i].getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
}
