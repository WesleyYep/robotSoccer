package ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import controllers.WebcamController;

/**
 * Created by Wesley on 3/02/2015.
 */
public class SamplingPanel extends JPanel implements ActionListener {

    private WebcamController webcamController;
    private ColourSlider YSlider, USlider, VSlider;
    private JButton sampleButton, detectButton, setValueButton;
    private JLabel lowYLabel, highYLabel, lowULabel, highULabel, lowVLabel, highVLabel;
    
    public boolean isSampling = false;

    private static final String[] DETECTSTRING = {"Detect", "Stop"};
    
    public SamplingPanel (WebcamController wc) {
        this.setLayout(new MigLayout());
        this.webcamController = wc;

        lowYLabel = new JLabel();
        highYLabel = new JLabel();
        lowULabel = new JLabel();
        highULabel = new JLabel();
        lowVLabel = new JLabel();
        highVLabel = new JLabel();
        
        YSlider = new ColourSlider(0, 255);
        USlider = new ColourSlider(0, 255);
        VSlider = new ColourSlider(0, 255);
        
        // Add change listener. Update labels.
        YSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowYLabel.setText(YSlider.getLowValue() + "");
				highYLabel.setText(YSlider.getHighValue() + "");
			}
        	
        });
        
        USlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowULabel.setText(USlider.getLowValue() + "");
				highULabel.setText(USlider.getHighValue() + "");
			}
        	
        });
        
        VSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowVLabel.setText(VSlider.getLowValue() + "");
				highVLabel.setText(VSlider.getHighValue() + "");
			}
        	
        });
        
        sampleButton = new JButton("Start Sample");
        detectButton = new JButton(DETECTSTRING[0]);
        setValueButton = new JButton("Set Value");
        
        add(lowYLabel, "width 30:30:30, split 3");
        add(YSlider, "width 400:400:400");
        add(highYLabel, "width 30:30:30, wrap");
        add(lowULabel, "width 30:30:30, split 3");
        add(USlider, "width 400:400:400");
        add(highULabel, "width 30:30:30, wrap");
        add(lowVLabel, "width 30:30:30, split 3");
        add(VSlider, "width 400:400:400");
        add(highVLabel, "width 30:30:30, wrap 15");
        add(sampleButton, "span, split 3, align right");
        add(detectButton, "width 75:75:75");
        add(setValueButton, "wrap");
        
        sampleButton.addActionListener(this);
        detectButton.addActionListener(this);
        setValueButton.addActionListener(this);
    }

    public void takeSample(double xPos, double yPos) {
        BufferedImage image = webcamController.getImageFromWebcam();
        Color color = new Color(image.getRGB((int)xPos, (int)yPos));

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        // http://en.wikipedia.org/wiki/YUV#Full_swing_for_BT.601
        int y = ((76 * r + 150 * g +  29 * b + 128) >> 8);
        int u = ((-43 * r -  84 * g + 127 * b + 128) >> 8) + 128;
        int v = ((127 * r -  106 * g -  21 * b + 128) >> 8) + 128;

        YSlider.addToData(y);
        USlider.addToData(u);
        VSlider.addToData(v);

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
		} else if (e.getSource() == setValueButton) {

		}
	}
}
