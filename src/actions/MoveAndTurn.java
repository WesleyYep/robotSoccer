package actions;

import bot.Robot;
import data.Coordinate;
import strategy.Action;
import ui.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;


/**
 * Created by Wesley on 27/02/2015.
 */
public class MoveAndTurn extends Action {
//    private double oldDistanceToTarget = 0;
//    private int countTimesThatSeemStuck = 0;
    private static boolean madeItToPrespot = false;

    //non-static initialiser block
    {
        if(!(parameters.containsKey("spotX") && parameters.containsKey("spotY"))) {
            //don't bother if these already exist
            parameters.put("spotX", 100);
            parameters.put("spotY", 70);
            parameters.put("turnSpotX", 110);
            parameters.put("turnSpotY", 90);
        }
    }

    @Override
    public void execute() {

//        //check if robot is stuck
//        double newTargetDistance = getDistanceToTarget(r);
//        //  System.out.println(Math.abs(oldDistanceToTarget - newTargetDistance));
//        if (Math.abs(oldDistanceToTarget - newTargetDistance) < 0.4) {
//            countTimesThatSeemStuck++;
//        } else if (r.linearVelocity >= 0){
//            countTimesThatSeemStuck = 0;
//        }
//        if (countTimesThatSeemStuck > 20) {
//            countTimesThatSeemStuck = 0;
//            return;
//        } else if (countTimesThatSeemStuck > 10) {
//            r.linearVelocity = -0.5;
//            r.angularVelocity = 10;
//            countTimesThatSeemStuck++;
//            return;
//        }

        int targetX = parameters.get("spotX");
        int targetY = parameters.get("spotY");
        int targetTurnX = parameters.get("turnSpotX");
        int targetTurnY = parameters.get("turnSpotY");


        moveAndTurn(bot, targetX, targetY, targetTurnX, targetTurnY);

    }

    public static void moveAndTurn(Robot r, int targetX, int targetY, int targetTurnX, int targetTurnY) {
        //get pre-spot
        Coordinate prespot = new Coordinate(0,0);
        double endAngle = Math.atan2(targetY - targetTurnY, targetTurnX - targetX);
        double oppositeAngle = endAngle + Math.PI;
        if (oppositeAngle > Math.PI) {
            oppositeAngle -= (2 * Math.PI);
        } else if (oppositeAngle < -Math.PI) {
            oppositeAngle += (2 * Math.PI);
        }
        prespot.x = targetX + 10 * Math.cos(oppositeAngle);
        prespot.y = targetY - 10 * Math.sin(oppositeAngle);
  //      System.out.println("angle: " + endAngle + " opp: " + oppositeAngle + " pre-x: " + prespot.x + " pre-y: " + prespot.y);

        if (!madeItToPrespot) {
            //move and turn
            if (Math.abs(r.getXPosition() - prespot.x) < 5 && Math.abs(r.getYPosition() - prespot.y) < 5) { //already at spot, now turn to target
                TurnTo.turn(r, new Coordinate(targetTurnX, targetTurnY), 1);
                double targetTheta = getTargetTheta(r, targetTurnX, targetTurnY);
                r.linearVelocity = 0;
                if (Math.abs(targetTheta) < 5) {
                    r.angularVelocity = 0;
                    madeItToPrespot = true;
                }
            } else {
                MoveToSpot.move(r, new Coordinate(prespot.x, prespot.y), 0.5, true);
    //            oldDistanceToTarget = getDistanceToTarget(r);
            }
        } else {
            //already went to prespot
            //move and turn
            if (Math.abs(r.getXPosition() - targetX) < 5 && Math.abs(r.getYPosition() - targetY) < 5) { //already at spot, now turn to target
                TurnTo.turn(r, new Coordinate(targetTurnX, targetTurnY), 1);
                double targetTheta = getTargetTheta(r, targetTurnX, targetTurnY);
                r.linearVelocity = 0;
                if (Math.abs(targetTheta) < 5) {
                    r.angularVelocity = 0;
                    madeItToPrespot = true;
                }
      //          countTimesThatSeemStuck = 0;
            } else {
                MoveToSpot.move(r, new Coordinate(targetX, targetY), 0.5, false);
     //           oldDistanceToTarget = getDistanceToTarget(r);
            }
            if (getDistanceToTarget(r, targetX, targetY) > 12) {
                madeItToPrespot = false;
            }
        }
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


    public static double getDistanceToTarget(Robot r, int targetX, int targetY) {
        return Math.sqrt(squared(targetX - r.getXPosition()) + squared(targetY - r.getYPosition()));
    }

    protected static double squared (double x) {
        return x * x;
    }

}
