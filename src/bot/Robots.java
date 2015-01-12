package bot;

import java.awt.Graphics;
import java.awt.Graphics2D;

import communication.RobotController;
import communication.SerialPortCommunicator;
import ui.Field;

public class Robots {
    private Robot[] bots;
    private SerialPortCommunicator serialCom;

    public Robots(SerialPortCommunicator s) {
		bots = new Robot[5];
		serialCom = s;
    }

    public void makeRealRobots() {
    	for (int i = 0; i < 5; i++) {
    		if (bots[i] == null) {
        		bots[i] = new RealRobot(50, 50, 0);
    		} else {
        		bots[i] = new RealRobot(bots[i].getXPosition(), bots[i].getYPosition(), bots[i].getTheta());
    		}
    	} 
    }
    
    public void makeSimRobots() {
    	for (int i = 0; i < 5; i++) {
    		bots[i] = new SimRobot(bots[i].getXPosition(), bots[i].getYPosition(), bots[i].getTheta());
    	}
    }
    
    public void testForward() {
    	for (int i = 0; i < 5; i++) {
    		bots[i].linearVelocity = 0.1;
    	} 
    }
    
	public void testRotate() {
		for (int i = 0; i < 5; i++) {
    		bots[i].angularVelocity = Math.PI/2;
    	} 
	}
	
	public void stopAllMovement() {
		for (int i = 0; i < 5; i++) {
			bots[i].linearVelocity = 0;
			bots[i].angularVelocity = 0;
		}
	}
    
    public Robot getRobot(int id) {
    	return bots[id];
    }
    
    public void draw(Graphics g) {
    	for (Robot r : bots) {
    		r.draw((Graphics2D) g);
    	} 
    }

	public void setIndividualBotPosition(int id, double x, double y, double theta) {
		bots[id].setX(x*100);
		bots[id].setY(Field.OUTER_BOUNDARY_HEIGHT-Math.round(y*100));
		bots[id].setTheta(theta);
	}

	public void moveBots() {
		for (int i = 0; i < 5; i++) {
			bots[i].moveLinear();
			bots[i].moveAngular();
		}
	}


	//will try using this after we check if the other way works first
	public void send() {
		double[] linearVelocity = new double[11];
		double[] angularVelocity = new double[11];

		for (int i = 0; i < 11; i++) {
			if (i < 5) {
				linearVelocity[i] = bots[i].linearVelocity;
				angularVelocity[i] = bots[i].angularVelocity;
			} else {
				linearVelocity[i] = 0;
				angularVelocity[i] = 0;
			}
		}
		RobotController controller = new RobotController(serialCom);
		controller.sendVelocity(linearVelocity, angularVelocity);
	}

}
