package actions;

import strategy.Action;

public class FreeBallKicker extends Action {

	 {
	        parameters.put("lin", 30);
	        parameters.put("ang", 0);
	 }
	@Override
	public void execute() {
		int lin = parameters.get("lin");
		int ang = parameters.get("ang");
		
		bot.linearVelocity = (lin/10.00);
		bot.angularVelocity = (ang/10.00);
	}

}
