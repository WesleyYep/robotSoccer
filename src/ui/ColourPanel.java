package ui;

import com.github.sarxos.webcam.Webcam;
import controllers.WebcamController;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by Wesley on 2/02/2015.
 */
public class ColourPanel extends JPanel implements WebcamDisplayPanelListener {

    private WebcamController webcamController;
    private JButton sampleButton = new JButton("Start Sample");
    private boolean isSampling = false;

    public ColourPanel(WebcamController wc) {
        this.setLayout(new MigLayout());
        this.webcamController = wc;

        add(sampleButton);

        sampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSampling) {
                    sampleButton.setText("Stop Sample");
                    isSampling = true;
                } else {
                    sampleButton.setText("Start Sample");
                    isSampling = false;
                }
            }
        });
    }

    public void takeSample(double x, double y) {
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        BufferedImage image = webcamController.getImageFromWebcam();
        System.out.println("Width: " + image.getWidth());
        System.out.println("Height: " + image.getHeight());
        Color color = new Color(image.getRGB((int)x, (int)y));
        System.out.println("Red: " + color.getRed());
        System.out.println("Green: " + color.getGreen());
        System.out.println("Blue: " + color.getBlue());
    }

    public boolean getIsSampling() {
        return isSampling;
    }

    @Override
    public void viewStateChanged() {
        //do nothing
    }
}
