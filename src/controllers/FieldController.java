package controllers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;

import ui.*;
import bot.Robot;
import bot.Robots;
import communication.ReceiverListener;

public class FieldController implements ReceiverListener, AreaListener {

    private Field field;

    private Ball ball;
    private Robots bots;

    private Point startPoint;
    private Point endPoint;

    private boolean isMouseDrag;
    
    private SituationArea selectedArea;

    public FieldController(Field field, Robots bots, Ball ball) {
        this.bots = bots;
        this.ball = ball;
        this.field = field;
        field.setBackground(Color.green);
    }

    @Override
    public void action(List<String> chunks) {
        for (String s : chunks) {

            if (s.indexOf("Robot") != -1) {
                int idIndex = s.indexOf("id=");
                int xIndex = s.indexOf("x=");
                int yIndex = s.indexOf("y=");
                int thetaIndex = s.indexOf("theta=");
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
	
	public void bringComponentToTheTop(Component comp) {
		if (comp != null  && field.getComponentZOrder(comp) != -1) {
			for (Component c : field.getComponents()) {
				field.setComponentZOrder(c, field.getComponentCount()-1);
			}
			field.setComponentZOrder(comp, 0);
		}
	}
	
	public void showArea(boolean show) {
			for (Component c : field.getComponents()) {
				if (c.getClass().equals(SituationArea.class)){
					c.setVisible(show);
				}
			}
	}

}