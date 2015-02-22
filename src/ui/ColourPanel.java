package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import controllers.WebcamController;
import data.Coordinate;

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
    private JSlider robotSizeSlider = new JSlider(0, 1000, 100);
    private JSlider ballSizeSlider = new JSlider(0, 1000, 100);
    private JLabel robotSizeLabel = new JLabel("100");
    private JLabel ballSizeLabel = new JLabel("100");
    private JTabbedPane tabPane = new JTabbedPane();
    private JLabel zoomLabel;
    private JButton setRobotDimensionButton = new JButton("Click to set robot dimension");
    private JTextField robotDimensionField = new JTextField("8");
    private boolean isGettingRobotDimension = false;
    private Coordinate middleOfRobot;
    private int clickNumber = 1;

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
        add(tabPane, "wrap");
        add(new JLabel("Robot Pixel Minimum"), "wrap");
        add(robotSizeSlider, "wrap");
        add(robotSizeLabel, "wrap");
        add(new JLabel("Ball Pixel Minimum"), "wrap");
        add(ballSizeSlider, "wrap");
        add(ballSizeLabel, "wrap");
        add(setRobotDimensionButton, "wrap");
        add(robotDimensionField, "wrap, w 50");

        robotSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                robotSizeLabel.setText(robotSizeSlider.getValue() + "");
            }
        });

        ballSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ballSizeLabel.setText(ballSizeSlider.getValue() + "");
            }
        });


        setRobotDimensionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Please click centre of robot, then click center of a green quadrant.");
                isGettingRobotDimension = true;
            }
        });
    }

    public void setRobotDimension(int xPos, int yPos) {
        if (clickNumber == 1) { //first click is middle of robot
            middleOfRobot = new Coordinate(xPos, yPos);
            clickNumber = 2;
        } else if (clickNumber == 2) {
            clickNumber = 1;
            int distance = (int) Math.sqrt(squared(xPos - middleOfRobot.x) + squared(yPos - middleOfRobot.y));
            System.out.println("dimension: " + distance + "px");
            robotDimensionField.setText(distance + "");
            isGettingRobotDimension = false;
        }
    }

    public int getRobotDimension() {
        return Integer.parseInt(robotDimensionField.getText());
    }

    public int getRobotSizeMinimum() {
        return robotSizeSlider.getValue();
    }

    public int getBallSizeMinimum() {
        return ballSizeSlider.getValue();
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
    
    public boolean getIsGettingRobotDimension() {
        return isGettingRobotDimension;
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

    protected int squared (int x) {
        return x * x;
    }

}
