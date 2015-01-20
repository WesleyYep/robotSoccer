package game;

import java.util.TimerTask;

import communication.Receiver;
import communication.Sender;
import ui.Field;
import bot.Robot;
import bot.Robots;
import ui.TestComPanel;

public class Tick extends TimerTask {
	private Field field;
	private Robots bots;
	private TestComPanel comPanel;
//	private Physics physics;
	
	public Tick(Field field, Robots bots, TestComPanel comPanel) {
		this.bots = bots;
		this.field = field;
		this.comPanel = comPanel;
//		physics = new Physics();
	}
	
	public void run() {
		//link to actions class somewhere here, set linearVelocity and angularVelocity of robots.
		if (comPanel.isSimulation()) {
			bots.moveBots();
//			physics.calculatePhysics(field, bots);
			field.moveBall(); //do we need this?
			field.repaint();

			setBallCoordinates();
			setBotCoordinates();
			Sender.sendStuff();
		} else {
			bots.send();
		}
	}

	private void setBotCoordinates() {
		Robot[] botArray = bots.getRobots();

		for (int i = 0; i < 5; i++) {
			double botX = botArray[i].getXPosition();
			double botY = Field.OUTER_BOUNDARY_HEIGHT - botArray[i].getYPosition();

			if (botX < 10) {
				Sender.botXs[i] = "botX" + i + ":00" + botX;
			} else if (botX < 100) {
				Sender.botXs[i] = "botX" + i + ":0" + botX;
			} else {
				Sender.botXs[i] = "botX" + i + ":" + botX;
			}
			if (botY < 10) {
				Sender.botYs[i] = "botY" + i + ":00" + botY;
			} else if (botY < 100) {
				Sender.botYs[i] = "botY" + i + ":0" + botY;
			} else {
				Sender.botYs[i] = "botY" + i + ":" + botY;
			}
		}
	}

	private void setBallCoordinates() {
		double ballX = field.getBallX();
		double ballY = Field.OUTER_BOUNDARY_HEIGHT - field.getBallY();

		if (ballX < 10) {
			Sender.ballX = "ballX:00" + ballX;
		} else if (ballX < 100) {
			Sender.ballX = "ballX:0" + ballX;
		} else {
			Sender.ballX = "ballX:" + ballX;
		}
		if (ballY < 10) {
			Sender.ballY = "ballY:00" + ballY;
		} else if (ballY < 100) {
			Sender.ballY = "ballY:0" + ballY;
		} else {
			Sender.ballY = "ballY:" + ballY;
		}
	}

}
