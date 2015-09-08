package bot;

import communication.SerialPortCommunicator;
import controllers.RobotController;
import ui.Field;

import java.awt.*;

/**
 * Robots class. Singleton pattern
 */
public class Robots {
	
	public final static int BOTTOTALCOUNT = 10;
	public final static int BOTTEAMMEMBERCOUNT = 5;
	
    private Robot[] bots;
    private SerialPortCommunicator serialCom;

    private static Robots instance = null;

    public Robots getInstance() {
        if (instance == null) {
            instance = new Robots();
            instance.makeTeamRobots();
            instance.makeOpponentRobots();
        }

        return instance;
    }

    private Robots() {
		bots = new Robot[BOTTEAMMEMBERCOUNT];
    }

    public void addSerialPortCom(SerialPortCommunicator s) {
        serialCom = s;
    }

    public void makeTeamRobots() {
    	for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
    		if (bots[i] == null) {
        		bots[i] = new RealRobot(-10, 10 + 10*i, 0, i);
    		} else {
        		bots[i] = new RealRobot(bots[i].getXPosition(), bots[i].getYPosition(), bots[i].getTheta(), bots[i].getId());
    		}
    	} 
    }

	public void makeOpponentRobots() {
		for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
			if (bots[i] == null) {
				bots[i] = new RealRobot(230, 10 + 10*i, 0, i);
			} else {
				bots[i] = new RealRobot(bots[i].getXPosition(), bots[i].getYPosition(), bots[i].getTheta(), bots[i].getId());
			}
		}
	}
    
    public void makeSimRobots() {
    	for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
    		bots[i] = new SimRobot(bots[i].getXPosition(), bots[i].getYPosition(), bots[i].getTheta(), i);
    	}
    }
    
    public void testForward() {
    	for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
    		bots[i].linearVelocity = 0.5;
    		bots[i].angularVelocity = 0;
    	} 
    }

    public void testBackwards() {
        for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
            bots[i].linearVelocity = -0.5;
            bots[i].angularVelocity = 0;
        }
    }
    
	public void testRotate() {
		for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
    		bots[i].angularVelocity = Math.PI/2;
    		bots[i].linearVelocity = 0;
    	} 
	}
	
	public void stopAllMovement() {
		for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
			bots[i].linearVelocity = 0;
			bots[i].angularVelocity = 0;
		}
	}
    
    public Robot getRobot(int id) {
    	return bots[id];
    }
    
    public Robot[] getRobots() {
    	return bots;
    }
    
    public void draw(Graphics2D g) {
    	for (Robot r : bots) {
    		r.draw(g);
    	}
    }

    /**
     * <p>Set the individual position of the robot on the field</p>
     * @param id
     * @param x
     * @param y
     * @param theta
     */
    
	public void setIndividualBotPosition(int id, double x, double y, double theta) {
		bots[id].setX(x*100);
		bots[id].setY(Field.OUTER_BOUNDARY_HEIGHT-Math.round(y*100));
		bots[id].setTheta(theta);
	}

	/**
	 * Instruct each robot to move.
	 */
	
	public void moveBots() {
		for (int i = 0; i < BOTTEAMMEMBERCOUNT; i++) {
			bots[i].moveLinear();
			bots[i].moveAngular();
		}
	}

	//will try using this after we check if the other way works first
	public void send() {
		double[] linearVelocity = new double[11];
		double[] angularVelocity = new double[11];

		//System.out.println(bots[0].linearVelocity + " " + bots[0].getXPosition() + " " + bots[0].getYPosition() + " " + System.currentTimeMillis());

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
