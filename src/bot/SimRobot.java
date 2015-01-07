package bot;

public class SimRobot extends Robot {

	@Override
	public void moveLinear() {
		setX(getXPosition() + (int)(linearVelocity * Math.cos(getTheta())));
		setY(getYPosition() + (int)(angularVelocity * Math.sin(getTheta())));
	}

	@Override
	public void moveAngular() {
		// TODO Auto-generated method stub
		
	}
	
}
