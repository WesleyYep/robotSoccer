package bot;

import java.awt.Graphics;
import java.awt.Graphics2D;

import ui.Field;

public class Robots {
    private Robot[] bots;
    
    public Robots() {
        bots = new Robot[5];

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
    		bots[i].linearVelocity = 1;
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
		}
	}
}
