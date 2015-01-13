package game;

import java.util.TimerTask;

import ui.Field;
import bot.Robot;
import bot.Robots;
import ui.TestComPanel;

public class Tick extends TimerTask {
	private Field field;
	private Robots bots;
	private TestComPanel comPanel;
	
	public Tick(Field field, Robots bots, TestComPanel comPanel) {
		this.bots = bots;
		this.field = field;
		this.comPanel = comPanel;
	}
	
	public void run() {
		//link to actions class somewhere here, set linearVelocity and angularVelocity of robots.

		if (comPanel.isSimulation()) {
			bots.moveBots();
			field.repaint();
		} else {
			bots.send();
		}
	}
	
}
