package ui;

import actions.MoveAndTurn;
import actions.Wait;
import bot.Robot;
import bot.Robots;
import controllers.WindowController;
import controllers.WindowControllerListener;
import criteria.Permanent;
import data.Coordinate;
import data.Situation;
import strategy.CurrentStrategy;
import strategy.Play;
import strategy.Role;
import utils.Geometry;

import javax.swing.*;

import java.awt.*;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Field extends JPanel implements MouseListener, MouseMotionListener, WindowControllerListener {


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
	private Robots opponentBots;

	private Point startPoint;
	private Point endPoint;

	private boolean isMouseDrag;
    private boolean isManualMovement;
    private int movingOpponentRobot = -1; //-1 means no robot being moved

	private CurrentStrategy currentStrategy;
    private Situation currentSituation;
    private List<Robot> currentOrder;
	private double predX = 0;
	private double predY = 0;
    private RobotSoccerMain main;

	public strategy.Action action;
	public boolean drawAction;

    private Action left = new AbstractAction("Left") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Left!");
        }
    };

    public Field(Robots bots, Robots opponents, Ball ball, RobotSoccerMain main) {
		this.bots = bots;
		this.ball = ball;
		this.opponentBots = opponents;
        this.main = main;
		isMouseDrag = false;
        WindowController.getWindowController().addListener(this);

		// Add mouse listeners
		addMouseListener(this);
		addMouseMotionListener(this);

		setLayout(null);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left");
        this.getActionMap().put("Left", left);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); 

		Graphics2D g2 = (Graphics2D)g.create();

		g2.setColor(Color.black);

		// Draw outer boundary
		//g.drawRect(5,5,390,190);

		g2.drawRect(ORIGIN_X,ORIGIN_Y,SCALE_FACTOR*OUTER_BOUNDARY_WIDTH, SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT);

		// Draw center line and center circle
		//g.drawLine(200,5,200,195);
		g2.drawLine(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH/2+ORIGIN_X,
				ORIGIN_Y,
				SCALE_FACTOR*OUTER_BOUNDARY_WIDTH/2+ORIGIN_X,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);

		//g.drawOval(200-50/2,100-50/2,50,50);
		g2.drawOval((OUTER_BOUNDARY_WIDTH/2-CENTER_CIRCLE_DIAMETER/2)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT/2-CENTER_CIRCLE_DIAMETER/2)*SCALE_FACTOR+ORIGIN_Y,
				CENTER_CIRCLE_DIAMETER*SCALE_FACTOR,
				CENTER_CIRCLE_DIAMETER*SCALE_FACTOR);

		// Draw penalty areas
		g2.drawRect(ORIGIN_X,
				((OUTER_BOUNDARY_HEIGHT-PENALTY_AREA_HEIGHT)/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*PENALTY_AREA_WIDTH,
				SCALE_FACTOR*PENALTY_AREA_HEIGHT);

		g2.drawRect(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*PENALTY_AREA_WIDTH,
				((OUTER_BOUNDARY_HEIGHT-PENALTY_AREA_HEIGHT)/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*PENALTY_AREA_WIDTH,
				SCALE_FACTOR*PENALTY_AREA_HEIGHT);


		// Draw corners
		// lower left
		g2.drawLine(ORIGIN_X,
				ORIGIN_Y+SCALE_FACTOR*CORNER_LENGTH,
				ORIGIN_X+SCALE_FACTOR*CORNER_LENGTH,
				ORIGIN_Y);
		// lower right
		g2.drawLine(SCALE_FACTOR*(OUTER_BOUNDARY_WIDTH-CORNER_LENGTH)+ORIGIN_X,
				ORIGIN_Y,
				SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X,
				ORIGIN_Y+SCALE_FACTOR*CORNER_LENGTH);
		// upper right
		g2.drawLine(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y-SCALE_FACTOR*CORNER_LENGTH,
				SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*CORNER_LENGTH,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);
		// upper left
		//g.drawLine(385,5,395,15);
		g2.drawLine(ORIGIN_X,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y-SCALE_FACTOR*CORNER_LENGTH,
				ORIGIN_X+SCALE_FACTOR*CORNER_LENGTH,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);

		// Draw goals
		g2.drawRect(ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT/2-GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*GOAL_AREA_WIDTH,
				SCALE_FACTOR*GOAL_AREA_HEIGHT);
		//g.drawRect(395-15, 75, 15, 50);
		g2.drawRect(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*GOAL_AREA_WIDTH,
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
		g2.fillRect(FREE_BALL_FROM_GOAL_LINE*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//2nd quarter
		//center dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//3rd quarter
		g2.fillRect(FREE_BALL_FROM_GOAL_LINE*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//4th quarter
		//center dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//inner goal area
		g2.drawRect(ORIGIN_X-(INNER_GOAL_AREA_WIDTH*SCALE_FACTOR),
				(OUTER_BOUNDARY_HEIGHT/2-INNER_GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*INNER_GOAL_AREA_WIDTH,
				SCALE_FACTOR*INNER_GOAL_AREA_HEIGHT);

		g2.drawRect(ORIGIN_X+(OUTER_BOUNDARY_WIDTH*SCALE_FACTOR),
				(OUTER_BOUNDARY_HEIGHT/2-INNER_GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*INNER_GOAL_AREA_WIDTH,
				SCALE_FACTOR*INNER_GOAL_AREA_HEIGHT);

		//draw ball
		ball.draw(g2);

		//draw robots
		bots.draw(g2);

		//draw opponents

		for (int i=0; i<5; i++) {
			g2.setColor(Color.blue);
			g2.fillOval(
					(int)opponentBots.getRobot(i).getXPosition() *Field.SCALE_FACTOR+Field.ORIGIN_X-(6*Field.SCALE_FACTOR/2),
					(int)opponentBots.getRobot(i).getYPosition() *Field.SCALE_FACTOR+Field.ORIGIN_Y-(6*Field.SCALE_FACTOR/2),
					6*Field.SCALE_FACTOR,
					6*Field.SCALE_FACTOR
			);
		}

		
		//predict ball
		g2.setColor(Color.red);
		g2.fillOval(
				(int) predX * Field.SCALE_FACTOR + Field.ORIGIN_X - (4 * Field.SCALE_FACTOR / 2),
				(int) predY * Field.SCALE_FACTOR + Field.ORIGIN_Y - (4 * Field.SCALE_FACTOR / 2),
				4 * Field.SCALE_FACTOR,
				4 * Field.SCALE_FACTOR
		);

		// draw action
		if (drawAction) {
			action.draw(g2);
		} else if (isMouseDrag) {
			// drawRect does not take negative values hence values need to be calculated so it doesn't fill the rectangle.
			// Rectangle co-ordinates.
			int x, y, w, h;

			x = Math.min(startPoint.x, endPoint.x);
			y = Math.min(startPoint.y, endPoint.y);

			w = Math.abs(endPoint.x - startPoint.x);
			h = Math.abs(endPoint.y - startPoint.y);

			g2.setColor(Color.BLUE);

			g2.drawRect(x, y, w, h);
		}

		g2.dispose();
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
        // Casts so it may not be super accurate.
        for (Robot element : bots.getRobots()) {
            checkFocus(element, r);
        }
	}

    private void isOpponentFocused(Rectangle r) {
        // Casts so it may not be super accurate.
        for (Robot element : opponentBots.getRobots()) {
            checkFocus(element, r);
        }
    }

    private void checkFocus(Robot element, Rectangle r) {
        Rectangle botRect = new Rectangle(
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
        if (movingOpponentRobot >= 0) {
            Robot or = opponentBots.getRobot(movingOpponentRobot);
            or.setX((e.getX()-Field.ORIGIN_X)/Field.SCALE_FACTOR);
            or.setY((e.getY()-Field.ORIGIN_Y)/Field.SCALE_FACTOR);
        }

		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		if (drawAction) {
			action.react(e);
			isMouseDrag = false;
			repaint();
		}
    }

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (drawAction) {
			return;
		}

        startPoint = e.getPoint();
        main.toggleMouseControl(true);
        for (Robot r : bots.getRobots()) {
            if (r.isFocused()) {
                Coordinate c = new Coordinate(e.getX(), e.getY());
                if (SwingUtilities.isRightMouseButton(e)) {
                    r.setManualMoveSpot(new Coordinate((int) ((c.x - Field.ORIGIN_X) / Field.SCALE_FACTOR), (int) ((c.y - Field.ORIGIN_Y) / Field.SCALE_FACTOR)));
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    r.setManualTurnSpot(new Coordinate((int) ((c.x - Field.ORIGIN_X) / Field.SCALE_FACTOR), (int) ((c.y - Field.ORIGIN_Y) / Field.SCALE_FACTOR)));
                }
            }
        }
        Rectangle r = new Rectangle(startPoint);
        r.add(startPoint);
        isOpponentFocused(r);
        movingOpponentRobot = -1;
        for (Robot or : opponentBots.getRobots()) {
            if (or.isFocused()) {
                movingOpponentRobot = or.getId();
            }
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (drawAction) {
			return;
		}

        movingOpponentRobot = -1;
        if (SwingUtilities.isLeftMouseButton(e)) {
            endPoint = e.getPoint();
            isMouseDrag = false;

            Rectangle r = new Rectangle(startPoint);
            r.add(endPoint);

            isRobotFocused(r);
            isBallFocused(r);

            repaint();
        }
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
                List<Robot> order;

//                if (currentOrder != null && situations.get(i) == currentSituation) {
//                    order = currentOrder;
//                } else {
                    order = Arrays.asList(bots.getRobots());
//                    currentOrder = order;
//                    currentSituation = situations.get(i);
//                }

				for (int j = 0; j < 5; j++) {
					Role role = currentStrategy.mapRoles(p.getRoles())[j];
					if (role == null) { continue; }
					role.addRobot(order.get(j));
					role.addTeamRobots(bots);
					role.addOpponentRobots(opponentBots);
					//role.setBallPosition(ball.getXPosition(), ball.getYPosition());
					role.setBallPosition(ball.getXPosition(), ball.getYPosition());
					role.setPredictedPosition(predX, predY);
					role.execute();
				}
			}
		}
	}

    //MAKE SURE PERMANENT ROBOTS ARE AT THE TOP OF PLAYS and ALL REST ROBOTS ARE AT THE BOTTOM OF PLAYS! @@IMPORTANT
//    private List<Robot> getOrder(org.opencv.core.Point[] playCriterias) {
//        List<Robot> botList = new ArrayList<>(Arrays.asList(bots.getRobots()));
//        List<Robot> order = new ArrayList<Robot>();
//
//        for (org.opencv.core.Point p : playCriterias) {
//            double minDist = 1000;
//            int rIndex = 0;
//            org.opencv.core.Point point = new org.opencv.core.Point(p.x, p.y);
//
//            if (p.x == -2 && p.y == -2) {
//                point.x = getBallX();
//                point.y = getBallY();
//            }
//
//            for (int i = 0; i < botList.size(); i++) {
//                Robot r = botList.get(i);
//                if (point.x == -1 && point.y == -1 || point.x == -3 && point.y == -3) {
//                    rIndex = i;
//                    break;
//                }
//                double d = Geometry.euclideanDistance(point, new org.opencv.core.Point(r.getXPosition(), r.getYPosition()));
//
//                if (d < minDist) {
//                    minDist = d;
//                    rIndex = i;
//                }
//            }
//            order.add(botList.get(rIndex));
//            botList.remove(rIndex);
//        }
//        return order;
//    }

    public void executeSetPlay() {
        Play p = currentStrategy.getSetPlay();

        for (int j = 0; j < 5; j++) {
            Role role = currentStrategy.mapRoles(p.getRoles())[j];
            if (role == null) { continue; }
            role.addRobot(bots.getRobot(j));
            role.addTeamRobots(bots);
            role.addOpponentRobots(opponentBots);
            role.setBallPosition(ball.getXPosition(), ball.getYPosition());
            role.setPredictedPosition(predX, predY);
            role.execute();
        }
    }

    public void executeManualControl() {
        for (int i = 0; i < 5; i++) {
            Robot bot = bots.getRobot(i);
            Role role = getRoleToMoveRobotToSpot(bot);

            role.addRobot(bot);
            role.addTeamRobots(bots);
            role.addOpponentRobots(opponentBots);
            role.setBallPosition(ball.getXPosition(), ball.getYPosition());
            role.setPredictedPosition(predX, predY);
            role.execute();
        }
    }

    private Role getRoleToMoveRobotToSpot(Robot robot) {
        Role role = new Role();
        Coordinate c = robot.getManualMoveSpot();
        Coordinate cTurn = robot.getManualTurnSpot();

        if(c.x == 0 && c.y ==0) {
            role.setPair(new Permanent(), new Wait(), 0);
            return role;
        }

        MoveAndTurn moveAndTurn = new MoveAndTurn();

        moveAndTurn.updateParameters("spotX", (int)c.x);
        moveAndTurn.updateParameters("spotY", (int)c.y);
        moveAndTurn.updateParameters("turnSpotX", (int)cTurn.x);
        moveAndTurn.updateParameters("turnSpotY", (int)cTurn.y);

        role.setPair(new Permanent(), moveAndTurn, 0);
        return role;
    }


    public void setPredPoint(double predX2, double predY2) {
		predX = predX2;
		predY = predY2;
	}

	public Robots getRobots() {
		return bots;
	}

	public Robots getOpponentRobots() {
		return opponentBots;
	}

	public Ball getBall() {
		return ball;
	}

    public boolean isManualMovement() {
        return isManualMovement;
    }

    public void setManualMovement(boolean isManualMovement) {
        this.isManualMovement = isManualMovement;
        if (!isManualMovement) {
            for (Robot r : bots.getRobots()) {
                r.setManualMoveSpot(new Coordinate(0,0));
            }
        }
    }

    @Override
    public void windowKeyPressed(String key) {
        if (!isManualMovement()){
            return;
        }
        for (Robot r : bots.getRobots()) {
            if (r.isFocused()) {
                main.toggleMouseControl(false);
                r.setManualMoveSpot(new Coordinate(0,0));
                if (key.equals("up")) {
                    r.linearVelocity = 0.5;
                } else if (key.equals("down")) {
                    r.linearVelocity = -0.5;
                } else if (key.equals("left")) {
                    r.angularVelocity = 3;
                } else if (key.equals("right")) {
                    r.angularVelocity = -3;
                } else if (key.equals("release")) {
                    r.linearVelocity = 0;
                    r.angularVelocity = 0;
                }
            }
        }
    }

	public void setDrawAction(boolean draw) {
		drawAction = draw;
	}

	public void setAction(strategy.Action action) {
		this.action = action;
	}

	/**
	 * <p>Retrieves x value from field and returns x value to show on GUI field</p>
	 * @param x
	 * @return
	 */
	public static int fieldXValueToGUIValue(int x) {
		return x * SCALE_FACTOR + ORIGIN_X;
	}

	/**
	 * <p>Retrieves y value from field and returns y value to show on GUI field</p>
	 * @param y
	 * @return
	 */
	public static int fieldYValueToGUIValue(int y) {
		return y * SCALE_FACTOR + ORIGIN_X;
	}

	/**
	 * <p>Retrieves x value from gui and returns x value for field</p>
	 * @param x
	 * @return
	 */
	public static int GUIXValueToFieldValue(int x) {
		return (x - ORIGIN_X) / SCALE_FACTOR;
	}

	/**
	 * <p>Retrieves y value from gui and returns y value for field</p>
	 * @param y
	 * @return
	 */
	public static int GUIYValueToFieldValue(int y) {
		return (y - ORIGIN_X) / SCALE_FACTOR;
	}
}