package ui;

import bot.Robot;
import bot.Robots;
import data.Situation;
import strategy.CurrentStrategy;
import strategy.Play;
import strategy.Role;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

public class Field extends JPanel implements MouseListener, MouseMotionListener {


	//actual measurement of miroSot Middle league playground (in cm);
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

	private Ball ball;
	private Robots bots;

	private Point startPoint;
	private Point endPoint;

	private boolean isMouseDrag;

	private CurrentStrategy currentStrategy;
	private double predX = 0;
	private double predY = 0;

    public Field(Robots bots, Ball ball) {
		this.bots = bots;
		this.ball = ball;
		isMouseDrag = false;

		// Add mouse listeners
		addMouseListener(this);
		addMouseMotionListener(this);

		setLayout(null);
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
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g.fillRect((FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g.fillRect((FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//2nd quarter
		//center dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
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

		//draw ball
		ball.draw(g);

		//draw robots
		bots.draw(g);
		
		//predict ball
		g.setColor(Color.red);
		g.fillOval(
				(int)predX *Field.SCALE_FACTOR+Field.ORIGIN_X-(4*Field.SCALE_FACTOR/2),
				(int)predY *Field.SCALE_FACTOR+Field.ORIGIN_Y-(4*Field.SCALE_FACTOR/2),
				4*Field.SCALE_FACTOR,
				4*Field.SCALE_FACTOR
				);  

		if (isMouseDrag) {
			// drawRect does not take negative values hence values need to be calculated so it doesn't fill the rectangle.
			// Rectangle co-ordinates.
			int x, y, w, h;

			x = Math.min(startPoint.x, endPoint.x);
			y = Math.min(startPoint.y, endPoint.y);

			w = Math.abs(endPoint.x - startPoint.x);
			h = Math.abs(endPoint.y - startPoint.y);

			g.setColor(Color.BLUE);

			g.drawRect(x, y, w, h);
		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(SCALE_FACTOR*(OUTER_BOUNDARY_WIDTH+INNER_GOAL_AREA_WIDTH*2)+10, 
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+10); // appropriate constants
	}

	public double getBallX() {
		return ball.getXPosition();
	}

	public double getBallY() {
		return ball.getYPosition();
	}

	public void kickBall(double linearVelocity, double theta) {
		ball.setLinearVelocity(linearVelocity);
		ball.setTheta(theta);
	}

	public void bounceBall() {
		ball.bounce();
	}

	public void moveBall() {
		ball.move();
	}

	/**
	 * Updates the select boolean variable in Robot. GUI updates in subsequent paint calls. <br />
	 * {@link bot.Robot}
	 * @param Rectangle r
	 */

	private void isRobotFocused(Rectangle r) {
		Rectangle botRect;

		// Casts so it may not be super accurate.
		for (Robot element : bots.getRobots()) {
			botRect = new Rectangle(
					(int)(element.getXPosition()*SCALE_FACTOR+ORIGIN_X-(Robot.ROBOT_WIDTH*SCALE_FACTOR/2)),
					(int)(element.getYPosition()*SCALE_FACTOR+ORIGIN_Y-(Robot.ROBOT_WIDTH*SCALE_FACTOR/2)),
					Robot.ROBOT_WIDTH*SCALE_FACTOR,
					Robot.ROBOT_HEIGHT*SCALE_FACTOR
					);

			if (botRect.intersects(r) || botRect.contains(new Point(r.x, r.y))) {
				element.setFocus(true);
			} else {
				element.setFocus(false);
			}

		}
	}

	private void isBallFocused(Rectangle r) {
		Rectangle ballRect = new Rectangle(
				(int)ball.getXPosition()*SCALE_FACTOR+ORIGIN_X-(Ball.BALL_DIAMETER*SCALE_FACTOR/2),
				(int)ball.getYPosition()*SCALE_FACTOR+ORIGIN_Y-(Ball.BALL_DIAMETER*SCALE_FACTOR/2),
				Ball.BALL_DIAMETER*SCALE_FACTOR,
				Ball.BALL_DIAMETER*SCALE_FACTOR
				);

		if (ballRect.intersects(r) || ballRect.contains(new Point(r.x, r.y))) {
			ball.setFocus(true);
		} else {
			ball.setFocus(false);
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		isMouseDrag = true;
		endPoint = e.getPoint();

		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		endPoint = e.getPoint();
		isMouseDrag = false;

		Rectangle r = new Rectangle(startPoint);
		r.add(endPoint);

		isRobotFocused(r);
		isBallFocused(r);

		repaint();
	}

	public void setCurrentStrategy(CurrentStrategy c) {
		this.currentStrategy = c;
	}

	public void executeStrategy() {
		List<Situation> situations = currentStrategy.getSituations();
		for (int i = 0; i < situations.size(); i++) {
			if (situations.get(i).getArea().containsPoint(getBallX(), getBallY())) {
				if (situations.get(i).getPlays().size() == 0) { break; }
                Play p = situations.get(i).getPlays().get(0); //get the first play
                if (p == null) { break; }
				for (int j = 0; j < 5; j++) {
					Role role = currentStrategy.mapRoles(p.getRoles())[j];
					if (role == null) { continue; }
					role.addRobot(bots, j);
					//role.setBallPosition(ball.getXPosition(), ball.getYPosition());
					role.setBallPosition(ball.getXPosition(), ball.getYPosition());
					role.setPredictedPosition(predX, predY);
					role.execute();
				}
			}
		}
	}

    public void executeSetPlay() {
        List<Situation> situations = currentStrategy.getSituations();
        for (int i = 0; i < situations.size(); i++) {
            if (situations.get(i).getArea().containsPoint(getBallX(), getBallY())) {
                if (situations.get(i).getPlays().size() == 0) { break; }
                Play p = currentStrategy.getSetPlay();

                if (p == null) { break; }
                for (int j = 0; j < 5; j++) {
                    Role role = currentStrategy.mapRoles(p.getRoles())[j];
                    if (role == null) { continue; }
                    role.addRobot(bots, j);
                    //role.setBallPosition(ball.getXPosition(), ball.getYPosition());
                    role.setBallPosition(ball.getXPosition(), ball.getYPosition());
                    role.setPredictedPosition(predX, predY);
                    role.execute();
                }
            }
        }
    }


	public void setPredPoint(double predX2, double predY2) {
		predX = predX2;
		predY = predY2;
	}

}