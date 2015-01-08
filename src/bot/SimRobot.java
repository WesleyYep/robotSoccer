package bot;

public class SimRobot extends Robot {

	public SimRobot(double x, double y, double theta) {
		super(x, y, theta);
	}
	
	@Override
	public void moveLinear() {
		System.out.println(linearVelocity);
		setX(getXPosition() + (linearVelocity * Math.cos(getTheta())));
		setY(getYPosition() + (angularVelocity * Math.sin(getTheta())));
	}

	@Override
	public void moveAngular() {
		// TODO Auto-generated method stub
		
	}
	
}
