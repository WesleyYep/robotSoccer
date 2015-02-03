package ui;

import com.github.sarxos.webcam.Webcam;
import com.jidesoft.swing.RangeSlider;
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
    private ColourSlider YSlider = new ColourSlider();
    private ColourSlider USlider = new ColourSlider();
    private ColourSlider VSlider = new ColourSlider();

    public ColourPanel(WebcamController wc) {
        this.setLayout(new MigLayout());
        this.webcamController = wc;

        USlider.setMin(-111);
        USlider.setMax(111);
        VSlider.setMin(-157);
        VSlider.setMax(157);

        add(sampleButton, "wrap");
        add(YSlider, "wrap, width 400:400:400");
        add(USlider, "wrap, width 400:400:400");
        add(VSlider, "wrap, width 400:400:400");

        sampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSampling) {
                    sampleButton.setText("Stop Sample");
                    isSampling = true;
                } else {
                    sampleButton.setText("Start Sample");
                }
            }
        });
    }

    public void takeSample(double xPos, double yPos) {
//        System.out.println("X: " + x);
//        System.out.println(("Y: " + y));
        BufferedImage image = webcamController.getImageFromWebcam();
        System.out.println(image.getWidth());
        System.out.println(image.getHeight());
        Color color = new Color(image.getRGB((int)xPos, (int)yPos));

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int y = (int)(0.299 * r + 0.587 * g + 0.114 * b);
        int u = (int)(-0.14713 * r + -0.28886 * g + 0.436 * b);
        int v = (int)(0.615 * r + -0.51499 * g + -0.10001 * b);

        YSlider.addToData(y);
        USlider.addToData(u);
        VSlider.addToData(v);

        System.out.println("Y: " + y);
        System.out.println("U: " + u);
        System.out.println("V: " + v);
    }

    public boolean getIsSampling() {
        return isSampling;
    }

    @Override
    public void viewStateChanged() {
        //do nothing
    }
}
