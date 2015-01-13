package bot;

public class SimRobot extends Robot {

	public SimRobot(double x, double y, double theta, int id) {
		super(x, y, theta, id);
	}
	
	@Override
	public void moveLinear() {
		setX(getXPosition() + (linearVelocity * Math.cos(Math.toRadians(getTheta()))));
		setY(getYPosition() + (linearVelocity * -Math.sin(Math.toRadians(getTheta()))));
	}

	@Override
	public void moveAngular() {
		setTheta(getTheta() + angularVelocity);
	}

	
}
