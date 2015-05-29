package controllers;

import bot.Robots;
import communication.ReceiverListener;
import data.VisionData;
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

	private TestComPanel comPanel;

	private SituationArea selectedArea;
	
	public FieldController(Field field, Robots bots, Ball ball) {
		this.bots = bots;
		this.ball = ball;
		this.field = field;
		//kFilter = new KalmanFilter();
		field.setBackground(Color.green);
	}

	/*
	 * We don't do anything with comPanel.
	 */
	
	public void setComPanel(TestComPanel p) {
		comPanel = p;
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
				
				ball.setX((int) Math.round(x * 100));
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

	public void bounceBall() {
		ball.bounce();
	}

	public void moveBall() {
		ball.move();
	}

	@Override
	public void moveArea(int x, int y) {
		int newX = selectedArea.getX()-x;
		int newY = selectedArea.getY()-y;


		selectedArea.setBounds(newX, newY, selectedArea.getWidth(),selectedArea.getHeight());
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
			org.opencv.core.Point p = VisionController.imagePosToActualPos(data.getCoordinate());
			//Point2D p = VisionController.imagePosToActualPos(ballCoord.x, ballCoord.y);
			ball.setX((int) p.x); //hardcoded for now
			ball.setY((int)p.y);
			
		} else if (data.getType().startsWith("robot")) {
			org.opencv.core.Point p = VisionController.imagePosToActualPos(data.getCoordinate());
			//Point2D p = VisionController.imagePosToActualPos(robotCoord.x, robotCoord.y);
			int index = Math.abs(Integer.parseInt(data.getType().split(":")[1])) - 1;

			bots.getRobot(index).setX(p.x);
			bots.getRobot(index).setY(p.y);
			bots.getRobot(index).setTheta(Math.toDegrees(data.getTheta()));

		}
	}
}