package ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
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

import net.miginfocom.swing.MigLayout;
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
    private JButton sampleButton, detectButton, setValueButton;
    private JLabel lowYLabel, highYLabel, lowULabel, highULabel, lowVLabel, highVLabel;
    private JPanel optionPanePanel;
    private JTextField lowValueTextField, highValueTextField;
    private JComboBox<String> YUVCombo;
    
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
