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
    private List<VisionListener> listeners = new ArrayList<VisionListener>();

    public VisionWorker(WebcamController wc, ColourPanel colourPanel) {
        this.webcamController = wc;
        this.colourPanel = colourPanel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        SamplingPanel ballSP = colourPanel.ballSamplingPanel;
        ballMin = new int []{ ballSP.getLowerBoundForY(), ballSP.getLowerBoundForU(), ballSP.getLowerBoundForV() };
        ballMax = new int []{ ballSP.getUpperBoundForY(), ballSP.getUpperBoundForU(), ballSP.getUpperBoundForV() };
        System.out.println("ball min: " + ballMin[0] + ", " + ballMin[1] + ", " + ballMin[2]);

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

                    if (isBall(image, j, i)) {
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
                }
            }
            System.out.println("X: " + ballX + "  Y: " + ballY);
            publish(new Coordinate(ballX+highestRowWidth/2, ballY));
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

    private boolean isBall(BufferedImage image, int xPos, int yPos) {
        Color color = new Color(image.getRGB(xPos, yPos));

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int y = (int)(0.299 * r + 0.587 * g + 0.114 * b);
        int u = (int)(-0.14713 * r + -0.28886 * g + 0.436 * b);
        int v = (int)(0.615 * r + -0.51499 * g + -0.10001 * b);

        return (y > ballMin[0] && y < ballMax[0] &&
                u > ballMin[1] && u < ballMax[1] &&
                v > ballMin[2] && v < ballMax[2]);
    }



}
