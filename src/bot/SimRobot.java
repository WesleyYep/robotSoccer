package bot;

public class SimRobot extends Robot {

	public SimRobot(double x, double y, double theta) {
		super(x, y, theta);
	}
	
	@Override
	public void moveLinear() {
		setX(getXPosition() + (linearVelocity * Math.cos(Math.toRadians(getTheta()))));
		setY(getYPosition() + (linearVelocity * -Math.sin(Math.toRadians(getTheta()))));
	}

	@Override
	public void moveAngular() {
		// TODO Auto-generated method stub
		
	}

	
}
