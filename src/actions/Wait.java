package actions;

import bot.Robot;
import strategy.Action;

public class Wait extends Action {

	@Override
	public void execute() {
		 Robot r = bot;
		 r.linearVelocity = 0;
		 r.angularVelocity = 0;
	}

}
