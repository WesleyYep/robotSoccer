package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.jidesoft.swing.RangeSlider;

import controllers.WebcamController;

/**
 * Created by Wesley on 2/02/2015.
 */
public class ColourPanel extends JPanel implements WebcamDisplayPanelListener {
    public SamplingPanel ballSamplingPanel;
    public SamplingPanel teamSamplingPanel;
    public SamplingPanel groundSamplingPanel;
    public SamplingPanel opponentSamplingPanel;
    public SamplingPanel greenSamplingPanel;
    private SamplingPanel[] samplingPanels;
    private RangeSlider robotSizeSlider = new RangeSlider(0, 500);
    private RangeSlider ballSizeSlider = new RangeSlider(0, 500);
    private JLabel robotSizeLabel = new JLabel("0 : 500");
    private JLabel ballSizeLabel = new JLabel("0 : 500");
    private JTabbedPane tabPane = new JTabbedPane();
    private JLabel zoomLabel;
    
    public ColourPanel(WebcamController wc) {
        this.setLayout(new MigLayout());
        ballSamplingPanel = new SamplingPanel(wc);
        teamSamplingPanel = new SamplingPanel(wc);
        greenSamplingPanel = new SamplingPanel(wc);
        groundSamplingPanel = new SamplingPanel(wc);
        opponentSamplingPanel = new SamplingPanel(wc);
        samplingPanels = new SamplingPanel[] { ballSamplingPanel, teamSamplingPanel, greenSamplingPanel, groundSamplingPanel, opponentSamplingPanel };
        tabPane.addTab("Ball", ballSamplingPanel);
        tabPane.addTab("Team", teamSamplingPanel);
        tabPane.addTab("Green", greenSamplingPanel);
        tabPane.addTab("Ground", groundSamplingPanel);
        tabPane.addTab("Opponent", opponentSamplingPanel);
        
        zoomLabel = new JLabel();
        zoomLabel.setSize(new Dimension(150, 150));
        zoomLabel.setPreferredSize(new Dimension(150, 150));
        zoomLabel.setOpaque(true);
        zoomLabel.setBackground(Color.BLACK);
        
        zoomLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (getIsSampling()) {
					if (zoomLabel.getIcon() != null) {
						takeSample(e.getX(), e.getY());
					} else {
						System.err.println("No image to sample");
					}
				}
			}
        	
        });
        
        add(zoomLabel, "span, align center, wrap");
        add(tabPane, "span, pushx, growx, wrap");
        add(new JLabel("Robot Pixel Range"), "wrap");
        add(robotSizeSlider, "wrap");
        add(robotSizeLabel, "wrap");
        add(new JLabel("Ball Pixel Range"), "wrap");
        add(ballSizeSlider, "wrap");
        add(ballSizeLabel, "wrap");

        robotSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                robotSizeLabel.setText(robotSizeSlider.getLowValue() + " : " + robotSizeSlider.getHighValue());
            }
        });

        ballSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ballSizeLabel.setText(ballSizeSlider.getLowValue() + " : " + ballSizeSlider.getHighValue());
            }
        });

    }

    public void takeSample(double xPos, double yPos) {
        for (SamplingPanel sp : samplingPanels) {
            if (sp.isSampling) {
            	ImageIcon icon = (ImageIcon)zoomLabel.getIcon();
            	BufferedImage image = (BufferedImage)icon.getImage();
                sp.takeSample(image, xPos, yPos);
            }
        }
    }

    public boolean getIsSampling() {
        for (SamplingPanel sp : samplingPanels) {
            if (sp.isSampling) {
                return true;
            }
        }
        return false;
    }
    
    public void setZoomLabelIcon(BufferedImage image) {
    	BufferedImage scaled = utils.Image.resize(image, zoomLabel.getWidth(), zoomLabel.getHeight());
    	zoomLabel.setIcon(new ImageIcon(scaled));
    }
    
//    public void saveColourData(String fileName) {
//        try {
//            FileWriter fileWriter = new FileWriter(fileName, true);
//            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//
//            bufferedWriter.write();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void viewStateChanged() {
        //do nothing
    }
}
