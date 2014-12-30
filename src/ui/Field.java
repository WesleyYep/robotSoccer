package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import communication.ReceiverListener;
import bot.Robot;

public class Field extends JPanel implements ReceiverListener {
	
	//actual measurement of miroSot Middle leagure playground (in cm);
	final public static int OUTER_BOUNDARY_WIDTH = 220;
	final public static int OUTER_BOUNDARY_HEIGHT = 180;
	
	final public static int FREE_BALL_FROM_THE_CLOSEST_SIDE = 30;
	final public static int FREE_BALL_FROM_GOAL_LINE = 55;
	
	final public static int PENALTY_AREA_WIDTH = 35;
	final public static int PENALTY_AREA_HEIGHT = 80;
	
	final public static int GOAL_AREA_WIDTH = 15;
	final public static  int GOAL_AREA_HEIGHT = 50;
	
	final public static int CENTER_CIRCLE_DIAMETER = 50;
	
	final public static int FREE_BALL_DOTS_SPACE = 25;
	
	final public static int INNER_GOAL_AREA_WIDTH = 15;
	final public static int INNER_GOAL_AREA_HEIGHT = 40;
	
	final public static int SCALE_FACTOR = 2;
	
	final public static int CORNER_LENGTH = 7;
	
	final public static int ORIGIN_X = 5+INNER_GOAL_AREA_WIDTH*SCALE_FACTOR;
	final public static int ORIGIN_Y = 5;
    private Robot[] bots = new Robot[5];
    
    public Field() {
		//draw robots
    	for (int i = 0; i < 5; i++) {
    		bots[i] = new Robot();
    	}  
    }
    
    @Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g); 
		
		g.setColor(Color.black);

		// Draw outer boundary
		//g.drawRect(5,5,390,190);
		
		g.drawRect(ORIGIN_X,ORIGIN_Y,SCALE_FACTOR*OUTER_BOUNDARY_WIDTH, SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT);
	
		// Draw center line and center circle
		//g.drawLine(200,5,200,195);
		g.drawLine(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH/2+ORIGIN_X,
				   ORIGIN_Y,
				   SCALE_FACTOR*OUTER_BOUNDARY_WIDTH/2+ORIGIN_X,
				   SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);
		
		//g.drawOval(200-50/2,100-50/2,50,50);
		g.drawOval((OUTER_BOUNDARY_WIDTH/2-CENTER_CIRCLE_DIAMETER/2)*SCALE_FACTOR+ORIGIN_X,
 				   (OUTER_BOUNDARY_HEIGHT/2-CENTER_CIRCLE_DIAMETER/2)*SCALE_FACTOR+ORIGIN_Y,
 				   CENTER_CIRCLE_DIAMETER*SCALE_FACTOR,
 				   CENTER_CIRCLE_DIAMETER*SCALE_FACTOR);
		
		// Draw penalty areas
		g.drawRect(ORIGIN_X, 
				   ((OUTER_BOUNDARY_HEIGHT-PENALTY_AREA_HEIGHT)/2)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR*PENALTY_AREA_WIDTH,
				   SCALE_FACTOR*PENALTY_AREA_HEIGHT);
			
		g.drawRect(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*PENALTY_AREA_WIDTH,
				   ((OUTER_BOUNDARY_HEIGHT-PENALTY_AREA_HEIGHT)/2)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR*PENALTY_AREA_WIDTH,
				   SCALE_FACTOR*PENALTY_AREA_HEIGHT);


		// Draw corners
		// lower left
		g.drawLine(ORIGIN_X,
				   ORIGIN_Y+SCALE_FACTOR*CORNER_LENGTH,
				   ORIGIN_X+SCALE_FACTOR*CORNER_LENGTH,
				   ORIGIN_Y);
		// lower right
		g.drawLine(SCALE_FACTOR*(OUTER_BOUNDARY_WIDTH-CORNER_LENGTH)+ORIGIN_X,
				   ORIGIN_Y,
				   SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X,
				   ORIGIN_Y+SCALE_FACTOR*CORNER_LENGTH);
		// upper right
		g.drawLine(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X,
				   SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y-SCALE_FACTOR*CORNER_LENGTH,
				   SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*CORNER_LENGTH,
				   SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);
		// upper left
		//g.drawLine(385,5,395,15);
		g.drawLine(ORIGIN_X,
				   SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y-SCALE_FACTOR*CORNER_LENGTH,
				   ORIGIN_X+SCALE_FACTOR*CORNER_LENGTH,
				   SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);

		// Draw goals
		g.drawRect(ORIGIN_X,
				   (OUTER_BOUNDARY_HEIGHT/2-GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR*GOAL_AREA_WIDTH,
				   SCALE_FACTOR*GOAL_AREA_HEIGHT);
		//g.drawRect(395-15, 75, 15, 50);
		g.drawRect(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*GOAL_AREA_WIDTH,
					(OUTER_BOUNDARY_HEIGHT/2-GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR*GOAL_AREA_WIDTH,
				   SCALE_FACTOR*GOAL_AREA_HEIGHT);
		
		//freeball dots
		//-------------------------------------------
		//|						|					|
		//|			1st		    |			2nd		|
		//|						|					|
		//|						|					|
		//-------------------------------------------
		//|						|					|
		//|		3rd				|		4th			|
		//|						|					|
		//|						|					|
		//-------------------------------------------
		
		//1st quarter
		//center
		g.fillRect(FREE_BALL_FROM_GOAL_LINE*SCALE_FACTOR+ORIGIN_X,
				   FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		
		//right dot
		g.fillRect((FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				   FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		//left dot
		g.fillRect((FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				   FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		
		//2nd quarter
		//center dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE)*SCALE_FACTOR+ORIGIN_X,
				   FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		
		//right dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				   FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		//left dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				   FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		
		//3rd quarter
		g.fillRect(FREE_BALL_FROM_GOAL_LINE*SCALE_FACTOR+ORIGIN_X,
				   (OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		
		//right dot
		g.fillRect((FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		//left dot
		g.fillRect((FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR,
				   SCALE_FACTOR);
		
		//4th quarter
		//center dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE)*SCALE_FACTOR+ORIGIN_X,
					(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				    SCALE_FACTOR,
					SCALE_FACTOR);
				
		//right dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
					(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
					SCALE_FACTOR,
					SCALE_FACTOR);
		//left dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
					(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
					SCALE_FACTOR,
					SCALE_FACTOR);
		
		//inner goal area
		g.drawRect(ORIGIN_X-(INNER_GOAL_AREA_WIDTH*SCALE_FACTOR),
				   (OUTER_BOUNDARY_HEIGHT/2-INNER_GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR*INNER_GOAL_AREA_WIDTH,
				   SCALE_FACTOR*INNER_GOAL_AREA_HEIGHT);
		
		g.drawRect(ORIGIN_X+(OUTER_BOUNDARY_WIDTH*SCALE_FACTOR),
				   (OUTER_BOUNDARY_HEIGHT/2-INNER_GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				   SCALE_FACTOR*INNER_GOAL_AREA_WIDTH,
				   SCALE_FACTOR*INNER_GOAL_AREA_HEIGHT);
		
		
		//draw robots
    	for (Robot r : bots) {
    		r.draw(g);
    	}    
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SCALE_FACTOR*(OUTER_BOUNDARY_WIDTH+INNER_GOAL_AREA_WIDTH*2)+10, 
        					 SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+10); // appropriate constants
    }
    
   
    @Override
    public void action(List<Integer> chunks) {
    	for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i) > 9000) {
				bots[4].setY(OUTER_BOUNDARY_HEIGHT-(chunks.get(i) - 9001));
			} else if (chunks.get(i) > 8000) {
				bots[3].setY(OUTER_BOUNDARY_HEIGHT-(chunks.get(i) - 8001) );
			} else if (chunks.get(i) > 7000) {
				bots[2].setY(OUTER_BOUNDARY_HEIGHT-(chunks.get(i) - 7001));
			} else if (chunks.get(i) > 6000) {
				bots[1].setY(OUTER_BOUNDARY_HEIGHT-(chunks.get(i) - 6001));
			} else if (chunks.get(i) > 5000) {
				bots[0].setY(OUTER_BOUNDARY_HEIGHT-(chunks.get(i) - 5001));
			} else if (chunks.get(i) > 4000) {
				bots[4].setX((chunks.get(i) - 4001));
			} else if (chunks.get(i) > 3000) {
				bots[3].setX((chunks.get(i) - 3001));
			} else if (chunks.get(i) > 2000) {
				bots[2].setX((chunks.get(i) - 2001));
			} else if (chunks.get(i) > 1000) {
				bots[1].setX((chunks.get(i) - 1001));
			} else {
				bots[0].setX((chunks.get(i) - 0001));
			}
			
			
			//for testing purposes
			/*for (int j=0; j<5; j++) {
		        	System.out.println("robot "  + (j+1) + "x=" + bots[j].getX() +  " y=" + bots[j].getY());
		    }*/   
		    //System.out.println(chunks.get(i));
		    
		}
    	repaint();
    }
    
}