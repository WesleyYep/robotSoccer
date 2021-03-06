package controllers;

import bot.Robots;
import communication.ReceiverListener;
import data.VisionData;
import strategy.Action;
import ui.*;
import utils.Geometry;
import vision.KalmanFilter;
import vision.VisionListener;

import java.awt.*;
import java.util.List;

public class FieldController implements ReceiverListener, AreaListener, VisionListener {

	private Field field;

	private Ball ball;
	private Robots bots;
	private Robots opponentBots;

	private SituationArea selectedArea;

	public FieldController(Field field) {
		this.field = field;
		bots = field.getRobots();
		ball = field.getBall();
		opponentBots = field.getOpponentRobots();
		//kFilter = new KalmanFilter();
		field.setBackground(Color.green);
	}

	/**
	 * Removes all components added to field which contains a situation area.
	 */

	public void removeAllSituationArea() {
		for (Component c : field.getComponents()) {
			if (c instanceof SituationArea) {
				field.remove(c);
			}
		}
	}

    public void toggleVisibilityForGlassPanels(boolean visible) {
        for (Component c : field.getComponents()) {
            if (c instanceof SituationArea) {
                c.setVisible(visible);
            }
        }
    }

	@Override
	public void action(List<String> chunks) {
		for (String s : chunks) {
			// -1 doesn't exist.
			if (s.indexOf("Robot") != -1) {
				int idIndex = s.indexOf("id=");
				int xIndex = s.indexOf("x=");
				int yIndex = s.indexOf("y=");
				int thetaIndex = s.indexOf("theta=");

				// id must be 0 - 9. so length must be 1.
				int id = Integer.parseInt(s.substring(idIndex + 3, idIndex + 4));
				double x = Double.parseDouble(s.substring(xIndex + 2, yIndex - 1));
				double y = Double.parseDouble(s.substring(yIndex + 2, thetaIndex - 1));
				double theta = Double.parseDouble(s.substring(thetaIndex + 6, s.length()));
				
				bots.setIndividualBotPosition(id, x, y, theta);

			} else if (s.indexOf("Ball") != -1) {
			
				int xIndex = s.indexOf("x=");
				int yIndex = s.indexOf("y=");

				double x = Double.parseDouble(s.substring(xIndex + 2, yIndex - 1));
				double y = Double.parseDouble(s.substring(yIndex + 2, s.length()));
				
				ball.setX(Math.round(x * 100));
				ball.setY(Field.OUTER_BOUNDARY_HEIGHT - (int) Math.round(y * 100));
			}
		}
		field.repaint();
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

	@Override
	public void moveArea(int x, int y) {
		int newX = selectedArea.getX()-x;
		int newY = selectedArea.getY()-y;


		selectedArea.setBounds(newX, newY, selectedArea.getWidth(), selectedArea.getHeight());
		field.repaint();
	}

	@Override
	public void resizeArea(int w, int h, int x, int y) {

		//positive to the left
		//negative to the right
		int newWidth = selectedArea.getWidth() + w;
		int newHeight = selectedArea.getHeight() + h;
		selectedArea.setBounds(x, y, newWidth, newHeight);

		field.repaint();
	}

	@Override
	public void redrawArea() {
		field.revalidate();
		field.repaint();
	}

	public void setSelectedArea(SituationArea a) {
		bringComponentToTheTop(a);
		selectedArea = a;
	}

	public void addArea(SituationArea area) {
		field.add(area);
	}

	public void removeArea(SituationArea area) {
		field.remove(area);
	}

	public void repaintField() {
		field.repaint();
	}

	/**
	 * <p>Brings the component to top level</p>
	 * @param comp
	 */
	
	public void bringComponentToTheTop(Component comp) {
		if (comp != null  && field.getComponentZOrder(comp) != -1) {
			for (Component c : field.getComponents()) {
				field.setComponentZOrder(c, field.getComponentCount()-1);
			}
			
			field.setComponentZOrder(comp, 0);
		}

	}

	/**
	 * <p>Visibility of the situation area on the field</p>
	 * @param show boolean variable
	 */
	
	public void showArea(boolean show) {
		for (Component c : field.getComponents()) {
			if (c.getClass().equals(SituationArea.class)){
				c.setVisible(show);
			}
		}
	}

	/**
	 * <p>Updates the ball or robots x, y, theta positions from the vision data</p>
	 * <p>{@link data.VisionData}</p>
	 * @param VisionData data
	 */
	
	@Override
	public void receive(VisionData data) {
		if (data.getType().equals("ball")) {
			//org.opencv.core.Point p = VisionController.imagePosToActualPos(data.getCoordinate());
			//Point2D p = VisionController.imagePosToActualPos(ballCoord.x, ballCoord.y);
			ball.setX(data.getCoordinate().x); //hardcoded for now
			ball.setY(data.getCoordinate().y);

		} else if (data.getType().startsWith("robot")) {
			/*
			org.opencv.core.Point p = VisionController.imagePosToActualPos(data.getCoordinate());
			//Point2D p = VisionController.imagePosToActualPos(robotCoord.x, robotCoord.y);
			int index = Math.abs(Integer.parseInt(data.getType().split(":")[1])) - 1;
			double correctTheta = VisionController.imageThetaToActualTheta(data.getTheta());
			bots.getRobot(index).setX(p.x);
			bots.getRobot(index).setY(p.y);
			bots.getRobot(index).setTheta(Math.toDegrees(correctTheta)); */

			//Point2D p = VisionController.imagePosToActualPos(robotCoord.x, robotCoord.y);
			int index = Math.abs(Integer.parseInt(data.getType().split(":")[1])) - 1;
			double correctTheta = VisionController.imageThetaToActualTheta(data.getTheta());
			bots.getRobot(index).setX(data.getCoordinate().x);
			bots.getRobot(index).setY(data.getCoordinate().y);
			bots.getRobot(index).setTheta(Math.toDegrees(correctTheta));


		} else if (data.getType().startsWith("opponent")) {

			org.opencv.core.Point p = VisionController.imagePosToActualPos(data.getCoordinate());

			int index = Math.abs(Integer.parseInt(data.getType().split(":")[1])) - 1;

			opponentBots.getRobot(index).setX(p.x);
			opponentBots.getRobot(index).setY(p.y);

			opponentBots.getRobot(index).setTheta(0);
		}
	}

	public void executeStrategy() {
		field.executeStrategy();
	}

	public void executeSetPlay() {
		field.executeSetPlay();
	}

    public void executeManualControl() {
        field.executeManualControl();
    }

	public Ball getBall() {
		return ball;
	}

	public Robots getRobots() {
		return bots;
	}

	public void setPredPoint(double x, double y) {
		field.setPredPoint(x, y);
	}

	public void setDrawAction(boolean draw) {
		field.setDrawAction(draw);
		field.repaint();
	}

	public void setAction(Action action) {
		field.setAction(action);
		field.repaint();
	}

	public void resetActiveSituation() {
		field.resetActiveSituation();
	}
}