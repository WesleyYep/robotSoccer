package actions;

import data.Coordinate;
import strategy.Action;
import ui.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Wesley on 18/07/2015.
 */
public class PIDMoveToSpot extends Action {

    private long lastTime = 0;
//    private LimitedQueue errorsList = new LimitedQueue(10);
    private boolean isPreviousDirectionForward = true;
    private double kp = 3;
//    private double ki = 0;
    private boolean presetToForward = false;  // if true, robot will definitely go forward
    private boolean presetToBackward = false; //if true, robot will definitely go backwards

    {
        parameters.put("spotX", 100);
        parameters.put("spotY", 70);
        parameters.put("turnSpotX", 110);
        parameters.put("turnSpotY", 90);
    }

    @Override
    public void execute() {

        double targetX = parameters.get("spotX");
        double targetY = parameters.get("spotY");
        double dist = getDistanceToTarget(bot, targetX, targetY);

        if (bot.isStuck(new Coordinate(bot.getXPosition(), bot.getYPosition()))) {
            if (!presetToBackward && !presetToForward && dist > 10) {
                System.out.println("bot is stuck :(");
                if (isPreviousDirectionForward) {
                    presetToBackward = true;
                } else {
                    presetToForward = true;
                }
            }
        } else {
            presetToBackward = false;
            presetToForward = false;
        }

        //check for obstacles
//        for (int i = 0; i < opponentRobots.getRobots().length; i++) {
//            Robot opp = opponentRobots.getRobot(i);
//            if ((isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) < 20)
//                    || (!isPreviousDirectionForward && Math.abs(getTargetTheta(bot, opp.getXPosition(), opp.getYPosition())) > 160)
//                    && getDistanceToTarget(bot, opp.getXPosition(), opp.getYPosition()) < 20) {
//                if (isPreviousDirectionForward) {
//                    presetToBackward = true;
//                } else {
//                    presetToForward = true;
//                }
//            }
//        }

        boolean isCurrentDirectionForward;
        double timePeriod;
        long currentTime = System.currentTimeMillis();

        timePeriod = lastTime == 0 ? 0 : currentTime - lastTime;
        lastTime = currentTime;

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        double actualAngleError;

        if ((!presetToForward && Math.abs(angleToTarget) > 90) || presetToBackward) {
            if (angleToTarget < 0) {
                actualAngleError = Math.toRadians(-180 - angleToTarget);
            } else {
                actualAngleError = Math.toRadians(180 - angleToTarget);
            }
            bot.angularVelocity = actualAngleError * kp * -1;
            bot.linearVelocity = 0.5 * -1;
            isCurrentDirectionForward = false;
        } else {
            actualAngleError =  Math.toRadians(angleToTarget);
            bot.angularVelocity = actualAngleError * kp;
            bot.linearVelocity = 0.5;
            isCurrentDirectionForward = true;
        }
//        if (isCurrentDirectionForward == isPreviousDirectionForward) {
//            errorsList.add(actualAngleError * timePeriod/1000.0);
//        } else {
//            errorsList.clear();
//        }
        isPreviousDirectionForward = isCurrentDirectionForward;
//        bot.angularVelocity += errorsList.getTotal() * ki;

        if (dist <= 3) {
            bot.linearVelocity = 0;
            turn();
        }else if (dist < 10) {
            bot.linearVelocity *= dist/20.0;
        }


    }

    private void turn() {
        double targetX = parameters.get("turnSpotX");
        double targetY = parameters.get("turnSpotY");

        //get angle to target
        double angleToTarget = getTargetTheta(bot, targetX, targetY);
        bot.angularVelocity = Math.toRadians(angleToTarget) * kp;
    }


    @Override
    public void draw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D)g.create();

        g2.setBackground(Color.RED);

        // Convert parameter values to correct values to be shown on field.
        int x1 = Field.fieldXValueToGUIValue(parameters.get("spotX"));
        int y1 = Field.fieldYValueToGUIValue(parameters.get("spotY"));

        int x2 = Field.fieldXValueToGUIValue(parameters.get("turnSpotX"));
        int y2 = Field.fieldYValueToGUIValue(parameters.get("turnSpotY"));

        // Draw string and how to change it
        g2.drawString("Left Click - Move", x1, y1);
        g2.drawString("Right Click - Turn", x2, y2);
        //    g2.drawLine(x1, y1, x2, y2);

        g2.dispose();
    }

    @Override
    public void react(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Transform to actual coordinates for the field
        x = Field.GUIXValueToFieldValue(x);
        y = Field.GUIYValueToFieldValue(y);

        if (SwingUtilities.isLeftMouseButton(e)) {
            parameters.put("spotX", x);
            parameters.put("spotY", y);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            parameters.put("turnSpotX", x);
            parameters.put("turnSpotY", y);
        }
    }




}
