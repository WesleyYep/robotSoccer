package actions;

import bot.Robot;
import bot.Robots;
import data.Coordinate;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import strategy.Action;
import ui.Field;

/**
 * Created by Wesley on 21/01/2015.
 */
public class MoveToSpot extends Action{
    public static final double ERROR_MARGIN = 5;
    private static Robots teamBots;
    private static Robots opponentBots;
    //non-static initialiser block
    {
        if(!(parameters.containsKey("startingX"))) {
            //don't bother if these already exist
            parameters.put("startingX", 20);
        }
    }

    public static void addTeamRobotsToMoveToSpot(Robots team) {
        teamBots = team;
    }

    public static void addOpponentRobotsToMoveToSpot(Robots opponents) {
        opponentBots = opponents;
    }

    @Override
    public void execute() {
        Robot r = bot;
        Coordinate spot = new Coordinate(parameters.get("startingX"), (int)Math.random()* Field.OUTER_BOUNDARY_HEIGHT );  //This method is used only for going back for the chaseBall methods. Use MoveAndTurn action for other uses.
        //change to left/right side depending on where ball is
        move(r, spot, 1, true);
    }

    public static void move(Robot r, Coordinate spot, double speed, boolean avoidObstacles) {
        double targetTheta = Math.atan2(r.getYPosition() - spot.y, spot.x - r.getXPosition());
        double difference = targetTheta - Math.toRadians(r.getTheta());

        //some hack to make the difference -Pi < theta < Pi
        if (difference > Math.PI) {
            difference -= (2 * Math.PI);
        } else if (difference < -Math.PI) {
            difference += (2 * Math.PI);
        }
        targetTheta = Math.toDegrees(difference);
        double targetDist = Math.sqrt(Math.pow((spot.x-r.getXPosition()),2) + Math.pow((spot.y-r.getYPosition()),2));

        //if directly behind, just hard code
        if (Math.abs(targetTheta) > 175) {
            r.angularVelocity = 0;
            r.linearVelocity = -0.3;
            return;
        }

        FunctionBlock fb;

        if (avoidObstacles) {
            double obstacleTheta = 180, obstacleDist = 220, obstacleX = 0, obstacleY = 0;


            for (int i = 0; i < 5; i++) {
                if (!teamBots.getRobot(i).equals(r)) {
                    Robot obs = teamBots.getRobot(i);
                    double tempTheta = Math.atan2(r.getYPosition() - obs.getYPosition(), obs.getXPosition() - r.getXPosition());
                    double tempDifference = tempTheta - Math.toRadians(r.getTheta());
                    if (tempDifference > Math.PI) {
                        tempDifference -= (2 * Math.PI);
                    } else if (tempDifference < -Math.PI) {
                        tempDifference += (2 * Math.PI);
                    }
                    tempDifference = Math.toDegrees(tempDifference);
                    tempTheta = tempDifference;

                    if (Math.abs(tempTheta) < 50) {
                        double tempDist = Math.sqrt(Math.pow((obs.getXPosition() - r.getXPosition()), 2) + Math.pow((obs.getYPosition() - r.getYPosition()), 2));
                        if (tempDist < obstacleDist) {
                            obstacleDist = tempDist;
                            obstacleTheta = tempTheta;
                            obstacleY = obs.getYPosition();
                            obstacleX = obs.getXPosition();
                        }
                    }
                }
            }

            for (int i = 0; i < 5; i++) {
                Robot obs = opponentBots.getRobot(i);
                double tempTheta = Math.atan2(r.getYPosition() - obs.getYPosition(), obs.getXPosition() - r.getXPosition());
                double tempDifference = tempTheta - Math.toRadians(r.getTheta());
                if (tempDifference > Math.PI) {
                    tempDifference -= (2 * Math.PI);
                } else if (tempDifference < -Math.PI) {
                    tempDifference += (2 * Math.PI);
                }
                tempDifference = Math.toDegrees(tempDifference);
                tempTheta = tempDifference;

                if (Math.abs(tempTheta) < 50) {
                    double tempDist = Math.sqrt(Math.pow((obs.getXPosition() - r.getXPosition()), 2) + Math.pow((obs.getYPosition() - r.getYPosition()), 2));
                    if (tempDist < obstacleDist) {
                        obstacleDist = tempDist;
                        obstacleTheta = tempTheta;
                        obstacleY = obs.getYPosition();
                        obstacleX = obs.getXPosition();
                    }
                }
            }

            String filename = "fuzzy/selfMadeObstacle.fcl";
            FIS fis = FIS.load(filename, true);

            if (fis == null) {
                System.err.println("Can't load file: '" + filename + "'");
                System.exit(1);
            }

            double distBetweenObsAndTarget = Math.sqrt(Math.pow((spot.x-obstacleX),2) + Math.pow((spot.y-obstacleY),2));

            // Get default function block
            fb = fis.getFunctionBlock(null);
            fb.setVariable("distBetweenObsAndTarget", distBetweenObsAndTarget);
            fb.setVariable("obstacleDist", obstacleDist);
            fb.setVariable("obstacleTheta", obstacleTheta);
            fb.setVariable("targetTheta", targetTheta);
            fb.setVariable("targetDist", Math.abs(targetDist));
            fb.setVariable("direction", r.getTheta());
            fb.setVariable("xPos", r.getXPosition());
            fb.setVariable("yPos", r.getYPosition());
        } else {
            String filename = "fuzzy/selfMade.fcl";
            FIS fis = FIS.load(filename, true);

            if (fis == null) {
                System.err.println("Can't load file: '" + filename + "'");
                System.exit(1);
            }

            // Get default function block
            fb = fis.getFunctionBlock(null);

            fb.setVariable("targetTheta", targetTheta);
            fb.setVariable("targetDist", Math.abs(targetDist));
            fb.setVariable("direction", r.getTheta());
            fb.setVariable("xPos", r.getXPosition());
            fb.setVariable("yPos", r.getYPosition());
        }

        // Evaluate
        fb.evaluate();

        double linear  = fb.getVariable("linearVelocity").getValue();
        double angular = fb.getVariable("angularVelocity").getValue();
        r.linearVelocity = linear*speed;
        r.angularVelocity = angular*-1;

        return;

    }
}
