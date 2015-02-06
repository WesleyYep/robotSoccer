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
    private ColourSlider YSlider, USlider, VSlider;
    private JButton sampleButton, detectButton;
    public boolean isSampling = false;

    private static final String[] DETECTSTRING = {"Detect", "Stop"};
    
    public SamplingPanel (WebcamController wc) {
        this.setLayout(new MigLayout());
        this.webcamController = wc;

        YSlider = new ColourSlider(0, 255);
        USlider = new ColourSlider(0, 255);
        VSlider = new ColourSlider(0, 255);

        sampleButton = new JButton("Start Sample");
        detectButton = new JButton(DETECTSTRING[0]);
        
        add(sampleButton, "split 2");
        add(detectButton, "width 75:75:75, wrap");
        add(YSlider, "width 400:400:400, wrap");
        add(USlider, "width 400:400:400, wrap");
        add(VSlider, "width 400:400:400, wrap");
        
        sampleButton.addActionListener(this);
        detectButton.addActionListener(this);
    }

    public void takeSample(double xPos, double yPos) {
        BufferedImage image = webcamController.getImageFromWebcam();
        Color color = new Color(image.getRGB((int)xPos, (int)yPos));

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        System.out.println("R: " + r);
        System.out.println("G: " + g);
        System.out.println("B: " + b);
        
        // http://en.wikipedia.org/wiki/YUV#Full_swing_for_BT.601
        int y = ((76 * r + 150 * g +  29 * b + 128) >> 8);
        int u = ((-43 * r -  84 * g + 127 * b + 128) >> 8) + 128;
        int v = ((127 * r -  106 * g -  21 * b + 128) >> 8) + 128;

        YSlider.addToData(y);
        USlider.addToData(u);
        VSlider.addToData(v);

        System.out.println("Y: " + y);
        System.out.println("U: " + u);
        System.out.println("V: " + v);
        YSlider.repaint();
        USlider.repaint();
        VSlider.repaint();
        System.out.println(getLowerBoundForY());
        System.out.println(getUpperBoundForY());
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
        return VSlider.getLowValue();
    }

    public int getUpperBoundForV() {
        return VSlider.getHighValue();
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
