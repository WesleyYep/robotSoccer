package ui;

import communication.*;

public class RobotSoccerMain {

	public static void main(String[] args) {
		IReceiver receiver = new Receiver();
		//probably need to make a swingworker for this?
		receiver.accept(80);
	}

}
