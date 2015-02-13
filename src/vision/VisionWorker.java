package vision;

import controllers.VisionController;
import controllers.WebcamController;
import data.Coordinate;
import data.VisionData;
import ui.ColourPanel;
import ui.SamplingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Wesley on 6/02/2015.
 */
public class VisionWorker extends SwingWorker<Void, VisionData> {
    private WebcamController webcamController;
    private VisionController visionController;
    private ColourPanel colourPanel;
    private int[] ballMin = new int[3];
    private int[] ballMax = new int[3];
    private int[] teamMin = new int[3];
    private int[] teamMax = new int[3];
    private int[] greenMin = new int[3];
    private int[] greenMax = new int[3];
    private List<VisionListener> listeners = new ArrayList<VisionListener>();
    private List<PixelGroup> groups = new ArrayList<PixelGroup>();
    private List<PixelGroup> greenGroups = new ArrayList<PixelGroup>();

    public VisionWorker(WebcamController wc, ColourPanel colourPanel, VisionController vc) {
        this.webcamController = wc;
        this.colourPanel = colourPanel;
        this.visionController = vc;
    }

    @Override
    protected Void doInBackground() throws Exception {
        SamplingPanel ballSP = colourPanel.ballSamplingPanel;
        SamplingPanel teamSp = colourPanel.teamSamplingPanel;
        SamplingPanel greenSp = colourPanel.greenSamplingPanel;

        ballMin = new int []{ ballSP.getLowerBoundForY(), ballSP.getLowerBoundForU(), ballSP.getLowerBoundForV() };
        ballMax = new int []{ ballSP.getUpperBoundForY(), ballSP.getUpperBoundForU(), ballSP.getUpperBoundForV() };
        teamMin = new int []{ teamSp.getLowerBoundForY(), teamSp.getLowerBoundForU(), teamSp.getLowerBoundForV() };
        teamMax = new int []{ teamSp.getUpperBoundForY(), teamSp.getUpperBoundForU(), teamSp.getUpperBoundForV() };
        greenMin = new int []{ greenSp.getLowerBoundForY(), greenSp.getLowerBoundForU(), greenSp.getLowerBoundForV() };
        greenMax = new int []{ greenSp.getUpperBoundForY(), greenSp.getUpperBoundForU(), greenSp.getUpperBoundForV() };


        while (!isCancelled()) try {

            long startTime = System.currentTimeMillis();
            BufferedImage image = webcamController.getImageFromWebcam();
            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();
            System.out.println(System.currentTimeMillis() - startTime);

            boolean previous = false;
            int rowWidth = 0;
            int highestRowWidth = 0;
            int ballX = 0;
            int ballY = 0;

            //loop through every row
            for (int i = 0; i < imageHeight; i += 1) {
                //loop through every 10th column from right to left
                for (int j = imageWidth - 1; j >= 0; j -= 1) {

                    Color color = new Color(image.getRGB(j, i));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();

                    int y = ((76 * r + 150 * g + 29 * b + 128) >> 8);
                    int u = ((-43 * r - 84 * g + 127 * b + 128) >> 8) + 128;
                    int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;

                    //ball detection
                    if (isBall(y, u, v)) {
                        previous = true;
                        rowWidth++;
                    } else if (previous) {
                        if (rowWidth > highestRowWidth) {
                            highestRowWidth = rowWidth;
                            ballX = j;
                            ballY = i;
                        }
                        rowWidth = 0;
                        previous = false;
                    }

                    //robot detection
                    if (isTeam(y, u, v)) {
                        if (groups.isEmpty()) {
                            groups.add(new PixelGroup(j, i));
                        } else {
                            boolean inAGroup = false;

                            for (PixelGroup pg : groups) {
                                boolean inThisGroup = true;
                                if (pg.mostRightCorner.x < j && Math.abs(pg.mostRightCorner.x - j) < 20 && Math.abs(pg.mostRightCorner.y - i) < 5) {
                                    pg.mostRightCorner.x = j;
                                    pg.mostRightCorner.y = i;
                                } else if (pg.mostLeftCorner.x > j && Math.abs(pg.mostLeftCorner.x - j) < 20 && Math.abs(pg.mostLeftCorner.y - i) < 5) {
                                    pg.mostLeftCorner.x = j;
                                    pg.mostLeftCorner.y = i;
                                } else if (pg.mostBottomCorner.y < i && Math.abs(pg.mostBottomCorner.y - i) < 20 && Math.abs(pg.mostBottomCorner.x - j) < 5) {
                                    pg.mostBottomCorner.x = j;
                                    pg.mostBottomCorner.y = i;
                                } else if (j < pg.mostLeftCorner.x || j > pg.mostRightCorner.x || i > pg.mostBottomCorner.y || i < pg.mostTopCorner.y) { //within pixel group
                                    inThisGroup = false;
                                }
                                if (inThisGroup) {
                                    inAGroup = true;
                                }
                            }
                            if (!inAGroup) {
                                //                          System.out.println("new group added!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                groups.add(new PixelGroup(j, i));
                            }
                        }
                    }

                    //green detection
                    if (isGreen(y, u, v)) {
                        if (greenGroups.isEmpty()) {
                            greenGroups.add(new PixelGroup(j, i));
                        } else {
                            boolean inAGroup = false;

                            for (PixelGroup pg : greenGroups) {
                                boolean inThisGroup = true;
                                if (pg.mostRightCorner.x < j && Math.abs(pg.mostRightCorner.x - j) < 20 && Math.abs(pg.mostRightCorner.y - i) < 5) {
                                    pg.mostRightCorner.x = j;
                                    pg.mostRightCorner.y = i;
                                } else if (pg.mostLeftCorner.x > j && Math.abs(pg.mostLeftCorner.x - j) < 20 && Math.abs(pg.mostLeftCorner.y - i) < 5) {
                                    pg.mostLeftCorner.x = j;
                                    pg.mostLeftCorner.y = i;
                                } else if (pg.mostBottomCorner.y < i && Math.abs(pg.mostBottomCorner.y - i) < 20 && Math.abs(pg.mostBottomCorner.x - j) < 5) {
                                    pg.mostBottomCorner.x = j;
                                    pg.mostBottomCorner.y = i;
                                } else if (j < pg.mostLeftCorner.x || j > pg.mostRightCorner.x || i > pg.mostBottomCorner.y || i < pg.mostTopCorner.y) { //within pixel group
                                    inThisGroup = false;
                                }
                                if (inThisGroup) {
                                    inAGroup = true;
                                }
                            }
                            if (!inAGroup) {
                                //                          System.out.println("new group added!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                greenGroups.add(new PixelGroup(j, i));
                            }
                        }
                    }

                }
            }

            Collections.sort(groups, new Comparator<PixelGroup>() {
                @Override
                public int compare(PixelGroup o1, PixelGroup o2) {
                    return o2.getSize() - o1.getSize();
                }
            });

            Collections.sort(greenGroups, new Comparator<PixelGroup>() {
                @Override
                public int compare(PixelGroup o1, PixelGroup o2) {
                    return o2.getSize() - o1.getSize();
                }
            });

            if (!groups.isEmpty()) {
                System.out.println(groups.size());
                System.out.println("top: " + groups.get(0).mostTopCorner.x + ", " + groups.get(0).mostTopCorner.y);
                System.out.println("left: " + groups.get(0).mostLeftCorner.x + ", " + groups.get(0).mostLeftCorner.y);
                System.out.println("right: " + groups.get(0).mostRightCorner.x + ", " + groups.get(0).mostRightCorner.y);
                System.out.println("bottom: " + groups.get(0).mostBottomCorner.x + ", " + groups.get(0).mostBottomCorner.y);
                System.out.println("centre: " + groups.get(0).getCentre().x + ", " + groups.get(0).getCentre().y);
                System.out.println("----------------------------------------------------");
                //make a for loop here
                int robotCount = 0;
                for (int i = 0; i < groups.size(); i++) {
                    PixelGroup pg = groups.get(i);
                    double theta = pg.getTheta();
                    int robotNum = identifyRobot(pg.getCentre(), theta, pg.getSize());

                    if (robotNum != 0) {
                        System.out.println("identified: " + robotNum);
                        if (robotNum < 0) {
                            theta = -theta;
                        }
                        Point2D robot = visionController.imagePosToActualPos(pg.getCentre().x, pg.getCentre().y); //add orientation here
                        publish(new VisionData(new Coordinate((int) robot.getX(), (int) robot.getY()), theta, "robot:" + robotNum));
                    }
                }
            }

            //send ball
            Point2D ball = visionController.imagePosToActualPos(ballX + highestRowWidth / 2 * 1, ballY);
            publish(new VisionData(new Coordinate((int) ball.getX(), (int) ball.getY()), 0, "ball"));

           //            System.out.println(System.currentTimeMillis() - startTime);

            groups.clear();
            greenGroups.clear();

        } catch (Exception e) {
            System.out.println("wtf");
            e.printStackTrace();
        }

        return null;
    }

    private int identifyRobot(Coordinate centre, double theta, int size) {
        boolean firstGreenQuadrant = false; // top left
        boolean secondGreenQuadrant = false; // top right
        boolean thirdGreenQuadrant = false; // bottom right
        boolean fourthGreenQuadrant = false; // bottom left
        System.out.println("num green groups: " + greenGroups.size());
        for (int i = 0; i < 5; i++) {
            if (i >= greenGroups.size()) { break; }
            PixelGroup pg = greenGroups.get(i);
            Coordinate centreOfGroup = pg.getCentre();
   //         System.out.println("green centre: " + centreOfGroup.x + ", " + centreOfGroup.y);
            if (Math.sqrt(squared(centreOfGroup.x - centre.x) + squared(centreOfGroup.y - centre.y)) < size/2){ //close to robot
                double angleOfGroup =  Math.atan2(centreOfGroup.y - centre.y, centre.x - centreOfGroup.x);
                if (angleOfGroup > 0) {
                    angleOfGroup -= Math.PI;
                } else if (angleOfGroup <= 0) {
                    angleOfGroup += Math.PI;
                }
                double difference = angleOfGroup - theta;
                System.out.println("green angle: " + Math.toDegrees(angleOfGroup));
                if (difference > Math.PI) {
                    difference -= (2 * Math.PI);
                } else if (difference < -Math.PI) {
                    difference += (2 * Math.PI);
                }
                difference = Math.toDegrees(difference);

                System.out.println("difference: " + difference);
                if (difference > 70 && difference < 110) { firstGreenQuadrant = true; fourthGreenQuadrant = true; }
                else if (difference < -70 && difference > -110) { secondGreenQuadrant = true; thirdGreenQuadrant = true; }
                else if (difference < 0 && difference > -90) { secondGreenQuadrant = true; }
                else if (difference > 0 && difference < 90) { firstGreenQuadrant = true; }
                else if (difference < -90 && difference > -180) { thirdGreenQuadrant = true; }
                else if (difference > 90 && difference < 180) { fourthGreenQuadrant = true; }

            }
        }
        if (!firstGreenQuadrant && !secondGreenQuadrant && !thirdGreenQuadrant && !fourthGreenQuadrant) {
            return 0;
        }

        if (firstGreenQuadrant) {
            if (secondGreenQuadrant) {
                if (thirdGreenQuadrant) {
                    return 5;
                } else if (fourthGreenQuadrant) {
                    return 4;
                } else {
                    return 3;
                }
            } else {
                return 1;
            }
        } else if (secondGreenQuadrant) {
            return 2;
        } else if (thirdGreenQuadrant) {  //negative sign for upside down robots
            if (fourthGreenQuadrant) {
                if (firstGreenQuadrant) {
                    return -5;
                } else if (secondGreenQuadrant) {
                    return -4;
                } else {
                    return -3;
                }
            } else {
                return -1;
            }
        } else if (fourthGreenQuadrant) {
            return -2;
        }
        return 0;
    }


    @Override
    public void process(List<VisionData> chunks) {
        for (VisionData v : chunks) {
            for (VisionListener l : listeners) {
                l.receive(v);
            }
        }
    }

    public void addListener(VisionListener listener) {
        listeners.add(listener);
    }

    protected int squared (int x) {
        return x * x;
    }


    private boolean isBall(int y, int u, int v) {
        return (y > ballMin[0] && y < ballMax[0] &&
                u > ballMin[1] && u < ballMax[1] &&
                v > ballMin[2] && v < ballMax[2]);
    }

    private boolean isTeam(int y, int u, int v) {
        return (y > teamMin[0] && y < teamMax[0] &&
                u > teamMin[1] && u < teamMax[1] &&
                v > teamMin[2] && v < teamMax[2]);
    }

    private boolean isGreen(int y, int u, int v) {
        return (y > greenMin[0] && y < greenMax[0] &&
                u > greenMin[1] && u < greenMax[1] &&
                v > greenMin[2] && v < greenMax[2]);
    }


}
