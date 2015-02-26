package game;

import java.util.TimerTask;

import communication.Receiver;
import communication.Sender;
import communication.SenderListener;
import ui.Field;
import bot.Robot;
import bot.Robots;
import ui.TestComPanel;

public class Tick extends TimerTask implements SenderListener {
	private Field field;
	private Robots bots;
	private TestComPanel comPanel;

	private boolean runStrat = false;
	private Sender sender;
	//private Physics physics;

	public Tick(Field field, Robots bots, TestComPanel comPanel) {
		this.bots = bots;
		this.field = field;
		this.comPanel = comPanel;
		//physics = new Physics();
	}

	public void run() {
		//link to actions class somewhere here, set linearVelocity and angularVelocity of robots.
		if (!comPanel.isManualControl()) {
			if (runStrat) {
				field.executeStrategy();
			}
			else {
				bots.stopAllMovement();
			}
		}
		field.repaint();

		if (comPanel.isSimulation()) {
			if (sender != null) {
				sender.sendStuff(createBotCoordinatesMessage());
			}
		} else {
			bots.send();
		}
	}

	private String createBotCoordinatesMessage() {
		StringBuilder outputMessage = new StringBuilder();
		Robot[] botArray = bots.getRobots();
		for (int i = 0; i < Robots.BOTTEAMMEMBERCOUNT; i++) {
			outputMessage.append("lin bot" + i + ": " + botArray[i].linearVelocity + System.lineSeparator());
			outputMessage.append("ang bot" + i + ": " + botArray[i].angularVelocity + System.lineSeparator());
		}
		return outputMessage.toString();
	}


	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public void startGame(boolean start) {
		runStrat = start;
	}

	public void runStrategy(boolean run) {
		runStrat = run;
	}


}
