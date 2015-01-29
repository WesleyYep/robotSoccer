package game;

import java.util.TimerTask;

import communication.Receiver;
import communication.Sender;
import communication.SenderListener;
import ui.Field;
import bot.Robot;
import bot.Robots;
import ui.TestComPanel;

public class Tick extends TimerTask implements SenderListener{
	private Field field;
	private Robots bots;
	private TestComPanel comPanel;
	
	private Sender sender;
//	private Physics physics;
	
	public Tick(Field field, Robots bots, TestComPanel comPanel) {
		this.bots = bots;
		this.field = field;
		this.comPanel = comPanel;
//		physics = new Physics();
	}
	
	public void run() {
		//link to actions class somewhere here, set linearVelocity and angularVelocity of robots.
		field.executeStrategy();
		if (comPanel.isSimulation()) {
			field.repaint();
			setBotCoordinates();
			if (sender != null) {
				sender.sendStuff();
			}
		} else {
			bots.send();
		}
	}

	private void setBotCoordinates() {
		Robot[] botArray = bots.getRobots();
		for (int i = 0; i < 5; i++) {
			Sender.botXs[i] ="lin bot" + i + ": " + botArray[i].linearVelocity;
			Sender.botYs[i] ="ang bot" + i + ": " + botArray[i].angularVelocity;
		}
	}

	
	public void setSender(Sender sender) {
		this.sender = sender;
	}

}
