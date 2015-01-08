package game;

import java.util.TimerTask;

import ui.Field;
import bot.Robot;
import bot.Robots;

public class Tick extends TimerTask {
	private Field field;
	private Robots bots;
	
	public Tick(Field field, Robots bots) {
		this.bots = bots;
		this.field = field;
	}
	
	public void run() {
		bots.moveBots();
		field.repaint();
	}
	
}
