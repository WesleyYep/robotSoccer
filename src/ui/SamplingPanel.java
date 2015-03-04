package ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import vision.ColourRangeListener;
import net.miginfocom.swing.MigLayout;
import utils.ColorSpace;
import controllers.WebcamController;

/**
 * <p>Users sample the webcam image and adds data to ColourSlider</p>
 * <p>{@link controllers.WebcamController}</p>
 * <p>{@link ui.ColourSlider}</p>
 * Created by Wesley on 3/02/2015.
 */
public class SamplingPanel extends JPanel implements ActionListener {

    private WebcamController webcamController;
    private ColourSlider YSlider, USlider, VSlider;
    private JButton sampleButton, detectButton, setValueButton, clearAllButton;
    private JLabel lowYLabel, highYLabel, lowULabel, highULabel, lowVLabel, highVLabel;
    private JPanel optionPanePanel;
    private JTextField lowValueTextField, highValueTextField;
    private JComboBox<String> YUVCombo;
    private List<ColourRangeListener> colourRangeListeners;
    
    public boolean isSampling = false;
    
    private static final String[] DETECTSTRING = {"Detect", "Stop"};
    
    public SamplingPanel (WebcamController wc) {
        this.setLayout(new MigLayout());
        this.webcamController = wc;

        colourRangeListeners = new ArrayList<ColourRangeListener>();
        lowYLabel = new JLabel();
        highYLabel = new JLabel();
        lowULabel = new JLabel();
        highULabel = new JLabel();
        lowVLabel = new JLabel();
        highVLabel = new JLabel();
        
        int minorTickSpacing = 5, majorTickSpacing = 25;
        
        YSlider = new ColourSlider(0, 260);
        YSlider.setMinorTickSpacing(minorTickSpacing);
        YSlider.setMajorTickSpacing(majorTickSpacing);
        YSlider.setLabelTable(YSlider.createStandardLabels(majorTickSpacing));
        YSlider.setPaintTicks(true);
        YSlider.setPaintLabels(true);
        
        USlider = new ColourSlider(0, 260);
        USlider.setMinorTickSpacing(minorTickSpacing);
        USlider.setMajorTickSpacing(majorTickSpacing);
        USlider.setLabelTable(USlider.createStandardLabels(majorTickSpacing));
        USlider.setPaintTicks(true);
        USlider.setPaintLabels(true);
        
        VSlider = new ColourSlider(0, 260);
        VSlider.setMinorTickSpacing(minorTickSpacing);
        VSlider.setMajorTickSpacing(majorTickSpacing);
        VSlider.setLabelTable(VSlider.createStandardLabels(majorTickSpacing));
        VSlider.setPaintTicks(true);
        VSlider.setPaintLabels(true);
        
        // Add change listener. Update labels.
        YSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowYLabel.setText(YSlider.getLowValue() + "");
				highYLabel.setText(YSlider.getHighValue() + "");
				
				for (ColourRangeListener c : colourRangeListeners) {
					c.yRangeChanged(YSlider.getHighValue(), YSlider.getLowValue(),SamplingPanel.this);
				}
			}
        	
        });
        
        USlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowULabel.setText(USlider.getLowValue() + "");
				highULabel.setText(USlider.getHighValue() + "");
				
				for (ColourRangeListener c : colourRangeListeners) {
					c.uRangeChanged(USlider.getHighValue(), USlider.getLowValue(),SamplingPanel.this);
				}
			}
        	
        });
        
        VSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				lowVLabel.setText(VSlider.getLowValue() + "");
				highVLabel.setText(VSlider.getHighValue() + "");
				
				for (ColourRangeListener c : colourRangeListeners) {
					c.vRangeChanged(VSlider.getHighValue(), VSlider.getLowValue(),SamplingPanel.this);
				}
			}
        	
        });
        
        sampleButton = new JButton("Start Sample");
        detectButton = new JButton(DETECTSTRING[0]);
        setValueButton = new JButton("Set Value");
        clearAllButton = new JButton("Clear All");
        
        optionPanePanel = new JPanel(new MigLayout());
        
        // Create textfield for optionPanePanel and set the documentfilter.
        lowValueTextField = new JTextField();
        ((AbstractDocument)lowValueTextField.getDocument()).setDocumentFilter(new YUVFilter());
        highValueTextField = new JTextField();
        ((AbstractDocument)highValueTextField.getDocument()).setDocumentFilter(new YUVFilter());
        
		YUVCombo = new JComboBox<String>();
		
		YUVCombo.addItem("Y");
		YUVCombo.addItem("U");
		YUVCombo.addItem("V");
        
		optionPanePanel.add(YUVCombo, "span, push, grow, wrap");
		optionPanePanel.add(new JLabel("Choose values 0-255"), "span, push, grow, wrap");
		optionPanePanel.add(new JLabel("Set low value"), "split 2, span");
        optionPanePanel.add(lowValueTextField, "push, grow, wrap");
        optionPanePanel.add(new JLabel("Set high value"), "split 2, span");
        optionPanePanel.add(highValueTextField, "push, grow");
        
        add(lowYLabel, "width 30:30:30, split 3");
        add(YSlider, "width 400:400:400");
        add(highYLabel, "width 30:30:30, wrap 15");
        add(lowULabel, "width 30:30:30, split 3");
        add(USlider, "width 400:400:400");
        add(highULabel, "width 30:30:30, wrap 15");
        add(lowVLabel, "width 30:30:30, split 3");
        add(VSlider, "width 400:400:400");
        add(highVLabel, "width 30:30:30, wrap 15");
        add(clearAllButton, "span, split 4, align right");
        add(sampleButton);
        add(detectButton, "width 75:75:75");
        add(setValueButton, "wrap");

        sampleButton.addActionListener(this);
        detectButton.addActionListener(this);
        setValueButton.addActionListener(this);
        clearAllButton.addActionListener(this);
    }

    public void takeSample(BufferedImage image, double xPos, double yPos) {
        Color color = new Color(image.getRGB((int)xPos, (int)yPos));
        
        double[] yuv = ColorSpace.RGBToYUV(color.getRed(), color.getGreen(), color.getBlue());

        YSlider.addToData((int)yuv[0]);
        USlider.addToData((int)yuv[1]);
        VSlider.addToData((int)yuv[2]);

        YSlider.repaint();
        USlider.repaint();
        VSlider.repaint();
    }
    
    public void addColourRangeListener(ColourRangeListener c) {
    	colourRangeListeners.add(c);
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
    
    public void setUpperBoundForY(int i) {
    	YSlider.setHighValue(i);
    }
    
    public void setLowerBoundForY(int i) {
    	YSlider.setLowValue(i);
    }
    
    public void setUpperBoundForU(int i) {
    	USlider.setHighValue(i);
    }
    
    public void setLowerBoundForU(int i) {
    	USlider.setLowValue(i);
    }
    
    public void setUpperBoundForV(int i) {
    	VSlider.setHighValue(i);
    }
    
    public void setLowerBoundForV(int i) {
    	VSlider.setLowValue(i);
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
            if (detectButton.getText().equals(DETECTSTRING[0])) {
                webcamController.getWebcamDisplayPanel().setSamplingPanel(this);
            //    webcamController.getWebcamDisplayPanel().setMaxandMinForFilter();
                webcamController.getWebcamDisplayPanel().setIsFiltering(true);
                detectButton.setText(DETECTSTRING[1]);
            } else {
                webcamController.getWebcamDisplayPanel().setIsFiltering(false);
                detectButton.setText(DETECTSTRING[0]);
            }
		} else if (e.getSource() == setValueButton) {
			int selection = JOptionPane.showConfirmDialog(
					null,
					optionPanePanel,
					"Set value for slider",
					JOptionPane.OK_CANCEL_OPTION
					);
			
			if (selection == JOptionPane.OK_OPTION) {
				try {
					String YUVSelection = (String)YUVCombo.getSelectedItem();
					int lowValue = Integer.parseInt(lowValueTextField.getText());
					int highValue = Integer.parseInt(highValueTextField.getText());
					
					if (lowValue > highValue) {
						JOptionPane.showMessageDialog(null, "Cannot have low value greater than high value");
					} else {
						
						switch(YUVSelection) {
						case "Y":
							
							YSlider.setLowValue(lowValue);
							YSlider.setHighValue(highValue);
							break;
						case "U":
							
							USlider.setLowValue(lowValue);
							USlider.setHighValue(highValue);
							break;
						case "V":
							
							VSlider.setLowValue(lowValue);
							VSlider.setHighValue(highValue);
							break;
						}
						
					}
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "You did not insert a number.");
				}
			}
			
		} else if (e.getSource() == clearAllButton) {
			YSlider.clearData();
			USlider.clearData();
			VSlider.clearData();
		}
	}
	
	
	public void deactiviate() {
		if (isSampling) {
			sampleButton.setText("Start Sample");
            isSampling = false;
		}
		
		if (!detectButton.getText().equals(DETECTSTRING[0])) {
			detectButton.setText(DETECTSTRING[0]);
			webcamController.getWebcamDisplayPanel().setIsFiltering(false);
			webcamController.getWebcamDisplayPanel().repaint();
        } 	
	}
	
	/**
	 * <p>Filters the text inserted into textfield for YUV values.</p>
	 * @author Chang Kon, Wesley, John
	 *
	 */
	
	private class YUVFilter extends DocumentFilter {

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			try {
				// Try to change text to int. Throw exception is not possible.
				Integer.parseInt(text);
				
				// If there are 3 digits, check the value before adding to textfield.
				if (offset == 2) {
					int value = Integer.parseInt(fb.getDocument().getText(0, offset) + text);
					
					if (value > 255) {
						return;
					}
				}
				
				// As the values are only from 0-255, anything greater than 3 digits will be above 255 and is invalid.
				if (offset > 2) {
					return;
				}
				
			} catch (NumberFormatException e) {
				return;
			}
			
			super.replace(fb, offset, length, text, attrs);
		}
		
	}
	
}
