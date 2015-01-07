package game;

import java.util.TimerTask;

import ui.Field;
import bot.Robot;

//public class Tick extends TimerTask {
//	private Robot[] bots;
//	
//	public Tick(Robot[] bots) {
//		this.bots = bots;
//	}
//	
//	public void run() {
//		System.out.println("tick");
//		for (int i = 0; i < 5; i++) {
//			bots[i].repaint();
//		}
//	}
//	
//}


public class Tick extends TimerTask {
	private Field field;
	private Robot[] bots;
	
	public Tick(Field field, Robot[] bots) {
		this.bots = bots;
		this.field = field;
	}
	
	public void run() {
		System.out.println("tick");
		for (int i = 0; i < 5; i++) {
			bots[i].moveLinear();
		}
		field.repaint();
	}
	
}
