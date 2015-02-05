package ui;

import controllers.WebcamController;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Wesley on 3/02/2015.
 */
public class SamplingPanel extends JPanel implements ActionListener {

    private WebcamController webcamController;
    private ColourSlider YSlider = new ColourSlider();
    private ColourSlider USlider = new ColourSlider();
    private ColourSlider VSlider = new ColourSlider();
    private JButton sampleButton, detectButton;
    public boolean isSampling = false;

    private static final String[] DETECTSTRING = {"Detect", "Stop"};
    
    public SamplingPanel (WebcamController wc) {
        this.setLayout(new MigLayout());
        this.webcamController = wc;

        USlider.setMin(-111);
        USlider.setMax(111);
        VSlider.setMin(-157);
        VSlider.setMax(157);

        sampleButton = new JButton("Start Sample");
        detectButton = new JButton(DETECTSTRING[0]);
        
        add(sampleButton, "split 2");
        add(detectButton, "width 75:75:75, wrap");
        add(YSlider, "wrap, width 400:400:400");
        add(USlider, "wrap, width 400:400:400");
        add(VSlider, "wrap, width 400:400:400");
        
        sampleButton.addActionListener(this);
        detectButton.addActionListener(this);
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
        YSlider.repaint();
        USlider.repaint();
        VSlider.repaint();
    }

    public int getLowerBoundForY() {
        return YSlider.getLowValue();
    }

    public int getUpperBoundForY() {
        return YSlider.getHighValue();
    }

    public int getLowerBoundForU() {
        return USlider.getLowValue();
    }

    public int getUpperBoundForU() {
        return USlider.getHighValue();
    }
    public int getLowerBoundForV() {
        return YSlider.getLowValue();
    }

    public int getUpperBoundForV() {
        return YSlider.getHighValue();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sampleButton) {
            if (!isSampling) {
                sampleButton.setText("Stop Sample");
                isSampling = true;
            } else {
                sampleButton.setText("Start Sample");
                isSampling = false;
            }
		} else if (e.getSource() == detectButton) {
			RSWebcamPanel webcamPanel = webcamController.getWebcamDisplayPanel().getRSWebcamPanel();
			if (detectButton.getText().equals(DETECTSTRING[0])) {
				webcamController.setPainter(webcamPanel.new DetectionPainter(this));
				detectButton.setText(DETECTSTRING[1]);
			} else {
				webcamController.setPainter(webcamPanel.getDefaultPainter());
				detectButton.setText(DETECTSTRING[0]);
			}
		}
	}
}
