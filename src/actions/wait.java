package actions;

import bot.Robot;
import strategy.Action;

public class wait extends Action{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "do nothing";
	}

	@Override
	public void execute() {
		 Robot r = bots.getRobot(index);
		 r.linearVelocity = 0;
		 r.angularVelocity = 0;
	}

}
