package vision;

import controllers.WebcamController;
import data.Coordinate;
import ui.ColourPanel;
import ui.SamplingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 6/02/2015.
 */
public class VisionWorker extends SwingWorker<Void, Coordinate> {
    private WebcamController webcamController;
    private ColourPanel colourPanel;
    private int[] ballMin = new int[3];
    private int[] ballMax = new int[3];
    private int[] teamMin = new int[3];
    private int[] teamMax = new int[3];
    private List<Coordinate> pixelsInARobot = new ArrayList<Coordinate>();
    private List<VisionListener> listeners = new ArrayList<VisionListener>();

    public VisionWorker(WebcamController wc, ColourPanel colourPanel) {
        this.webcamController = wc;
        this.colourPanel = colourPanel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        SamplingPanel ballSP = colourPanel.ballSamplingPanel;
        SamplingPanel teamSp = colourPanel.teamSamplingPanel;

        ballMin = new int []{ ballSP.getLowerBoundForY(), ballSP.getLowerBoundForU(), ballSP.getLowerBoundForV() };
        ballMax = new int []{ ballSP.getUpperBoundForY(), ballSP.getUpperBoundForU(), ballSP.getUpperBoundForV() };
        teamMin = new int []{ teamSp.getLowerBoundForY(), teamSp.getLowerBoundForU(), teamSp.getLowerBoundForV() };
        teamMax = new int []{ teamSp.getUpperBoundForY(), teamSp.getUpperBoundForU(), teamSp.getUpperBoundForV() };

       // System.out.println("team min: " + ballMin[0] + ", " + ballMin[1] + ", " + ballMin[2]);

        while (!isCancelled()) {
            BufferedImage image = webcamController.getImageFromWebcam();
            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();
            boolean previous = false;
            int rowWidth = 0;
            int highestRowWidth = 0;
            int ballX = 0;
            int ballY = 0;

            //   long startTime = System.currentTimeMillis();

            //loop through every 10th row
            for (int i = 0; i < imageHeight; i+=10) {
                //loop through every 10th column from right to left
                for (int j = imageWidth - 1; j >= 0; j-=10) {

                    Color color = new Color(image.getRGB(j, i));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();

                    int y = ((76 * r + 150 * g +  29 * b + 128) >> 8);
                    int u = ((-43 * r -  84 * g + 127 * b + 128) >> 8) + 128;
                    int v = ((127 * r -  106 * g -  21 * b + 128) >> 8) + 128;

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
                    if (!partOfRobot(j,i) && isTeam(y, u, v)) {

                    }


                }
            }
     //       System.out.println("X: " + ballX + "  Y: " + ballY);
            publish(new Coordinate(ballX+highestRowWidth/2*10, ballY));
//            System.out.println(System.currentTimeMillis() - startTime);

        }

        return null;
    }

    @Override
    public void process(List<Coordinate> chunks) {
        for (Coordinate c : chunks) {
            for (VisionListener l : listeners) {
                l.receive(c);
            }
        }
    }

    public void addListener(VisionListener listener) {
        listeners.add(listener);
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

    private boolean partOfRobot(int x, int y) {
        return (pixelsInARobot.contains(new Coordinate(x, y)));
    }



}
