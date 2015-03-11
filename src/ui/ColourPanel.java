package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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











import com.jidesoft.swing.RangeSlider;

import vision.ColourRangeListener;
import vision.LookupTable;
import data.Coordinate;
import net.miginfocom.swing.MigLayout;
import controllers.WebcamController;
import data.Coordinate;

/**
 * Created by Wesley on 2/02/2015.
 */
public class ColourPanel extends JPanel implements WebcamDisplayPanelListener, ColourRangeListener {
    public SamplingPanel ballSamplingPanel;
    public SamplingPanel teamSamplingPanel;
    public SamplingPanel groundSamplingPanel;
    public SamplingPanel opponentSamplingPanel;
    public SamplingPanel greenSamplingPanel;
    private SamplingPanel[] samplingPanels;
  //  private JSlider robotSizeSlider = new JSlider(0, 1000, 100);
  //  private JSlider ballSizeSlider = new JSlider(0, 1000, 10);
  //  private JSlider greenSizeSlider = new JSlider(0, 1000, 50);
    private JLabel robotMinSizeLabel = new JLabel("10");
    private JLabel ballMinSizeLabel = new JLabel("10");
    private JLabel greenMinSizeLabel = new JLabel("10");
    
    private JLabel robotMaxSizeLabel = new JLabel("100");
    private JLabel ballMaxSizeLabel = new JLabel("100");
    private JLabel greenMaxSizeLabel = new JLabel("100");
    
    private RangeSlider robotSizeSlider = new RangeSlider(0,500);
    private RangeSlider greenSizeSlider = new RangeSlider(0,500);
    private RangeSlider ballSizeSlider = new RangeSlider(0,500);

    private JTabbedPane tabPane = new JTabbedPane();

    private JLabel zoomLabel;
    private JButton setRobotDimensionButton = new JButton("Click to set robot dimension");
    private JTextField robotDimensionField = new JTextField("8");
    private boolean isGettingRobotDimension = false;
    private Coordinate middleOfRobot;
    private int clickNumber = 1;
    
    private BufferedImage originalImage = null;
    private int selectRadius = 5;

    public ColourPanel(WebcamController wc) {
        this.setLayout(new MigLayout());
        
        
        robotSizeSlider.setLowValue(10);
        ballSizeSlider.setLowValue(10);
        greenSizeSlider.setLowValue(10);
        
        robotSizeSlider.setHighValue(100);
        ballSizeSlider.setHighValue(100);
        greenSizeSlider.setHighValue(100);
        
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
        
        tabPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println("here");
				SamplingPanel panel = (SamplingPanel) tabPane.getSelectedComponent();
				panel.deactiviate();
				panel.repaint();
			}
        	
        });
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
        
        zoomLabel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (zoomLabel.getIcon() != null) {			
					displayCircleOnIcon(e);
				}
			}
        	
        });
        
        zoomLabel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				int notches = arg0.getWheelRotation();
			    if (notches < 0) {
			          selectRadius++;
			          if (selectRadius > 20) {
			        	  selectRadius = 20;
			          }
 			          displayCircleOnIcon(arg0);
			     } else {
			          selectRadius--;
			          if (selectRadius < 1) {
			        	  selectRadius = 1;
			          }
			          displayCircleOnIcon(arg0);
			     }
			}
        
        });
        
        add(zoomLabel, "span, align center, wrap");
        add(tabPane, "span, pushx, growx, wrap");
        add(new JLabel("Robot Pixel Range"), "wrap");
        add(tabPane, "wrap");
        add(new JLabel("Robot Pixel Minimum"), "wrap");
        add(robotSizeSlider, "grow, wrap");
        add(robotMinSizeLabel);
        add(robotMaxSizeLabel, "wrap");
        add(new JLabel("Green Pixel Minimum"), "wrap");
        add(greenSizeSlider, "grow, wrap");
        add(greenMinSizeLabel);
        add(greenMaxSizeLabel, "wrap");
        add(new JLabel("Ball Pixel Minimum"), "wrap");
        add(ballSizeSlider, "grow,wrap");
        add(ballMinSizeLabel);
        add(ballMaxSizeLabel,"wrap");
        
        ballSamplingPanel.addColourRangeListener(this);
        teamSamplingPanel.addColourRangeListener(this);
        greenSamplingPanel.addColourRangeListener(this);
        
        add(setRobotDimensionButton, "wrap");
        add(robotDimensionField, "wrap, w 50");

        robotSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                robotMinSizeLabel.setText(robotSizeSlider.getLowValue() + "");
                robotMaxSizeLabel.setText(robotSizeSlider.getHighValue() + "");
            }
            
        });

        ballSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ballMinSizeLabel.setText(ballSizeSlider.getLowValue() + "");
                ballMaxSizeLabel.setText(ballSizeSlider.getHighValue() + "");
            }
        });

        greenSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                greenMinSizeLabel.setText(greenSizeSlider.getLowValue() + "");
                greenMaxSizeLabel.setText(greenSizeSlider.getHighValue() + "");
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

    protected void displayCircleOnIcon(MouseEvent e) {
    	BufferedImage copy = new BufferedImage(originalImage.getWidth(),
				originalImage.getHeight(),
				originalImage.getType());
		
		Graphics g = copy.getGraphics();
	    g.drawImage(originalImage, 0, 0, null);
	    g.drawOval(e.getX()-selectRadius, e.getY()-selectRadius, 2*selectRadius, 2*selectRadius);
	    g.dispose();
		
		zoomLabel.setIcon(new ImageIcon(copy));
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

    //getter
    public int getRobotSizeMinimum() {
        return robotSizeSlider.getLowValue();
    }

    public int getBallSizeMinimum() {
        return ballSizeSlider.getLowValue();
    }

    public int getGreenSizeMinimum() {
        return greenSizeSlider.getLowValue();
    }
    
    public int getRobotSizeMaximum() {
        return robotSizeSlider.getHighValue();
    }

    public int getBallSizeMaximum() {
        return ballSizeSlider.getHighValue();
    }

    public int getGreenSizeMaximum() {
        return greenSizeSlider.getHighValue();
    }

    //setter
    public void setRobotSizeMinimum(int value) {
        robotSizeSlider.setLowValue(value);
    }

    public void setGreenSizeMinimum(int value) {
        greenSizeSlider.setLowValue(value);
    }

    public void setBallSizeMinimum(int value) {
        ballSizeSlider.setLowValue(value);
    }
    
    public void setRobotSizeMaximum(int value) {
        robotSizeSlider.setHighValue(value);
    }

    public void setGreenSizeMaximum(int value) {
        greenSizeSlider.setHighValue(value);
    }

    public void setBallSizeMaximum(int value) {
        ballSizeSlider.setHighValue(value);
    }

    public void takeSample(double xPos, double yPos) {
        for (SamplingPanel sp : samplingPanels) {
            if (sp.isSampling) {
            	/*
            	ImageIcon icon = (ImageIcon)zoomLabel.getIcon();
            	BufferedImage image = (BufferedImage)icon.getImage();
                sp.takeSample(image, xPos, yPos);
                */
            	
            	for (int x=(int) (xPos-selectRadius); x<xPos+selectRadius; x++) {
            		
            		for (int y=(int) (yPos-selectRadius); y<yPos+selectRadius; y++) {
            			if ( Math.pow((x-xPos),2) + Math.pow((y-yPos),2) <= Math.pow(selectRadius-1, 2)){
            				ImageIcon icon = (ImageIcon)zoomLabel.getIcon();
                        	BufferedImage image = (BufferedImage)icon.getImage();
                            sp.takeSample(image,  x, y);
            			}
                	}
            	}
            	//sp.setRange();
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
    	originalImage = scaled;
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

	@Override
	public void yRangeChanged(int max, int min, SamplingPanel panel) {
		byte temp = 0;
		if (panel.equals(this.teamSamplingPanel)) {
			temp = (1 << LookupTable.TEAM_BIT_POS);
		}
		else if (panel.equals(this.greenSamplingPanel)) {
			temp = (1 << LookupTable.GREEN_BIT_POS);
		}
		else if (panel.equals(this.ballSamplingPanel)) {
			temp = (1 << LookupTable.BALL_BIT_POS);
		}
		LookupTable.setYTable(max, min, temp);
	}

	@Override
	public void uRangeChanged(int max, int min, SamplingPanel panel) {
		byte temp = 0;
		if (panel.equals(this.teamSamplingPanel)) {
			temp = (1 << LookupTable.TEAM_BIT_POS);
		}
		else if (panel.equals(this.greenSamplingPanel)) {
			temp = (1 << LookupTable.GREEN_BIT_POS);
		}
		else if (panel.equals(this.ballSamplingPanel)) {
			temp = (1 << LookupTable.BALL_BIT_POS);
		}
		LookupTable.setUTable(max, min, temp);
	}

	@Override
	public void vRangeChanged(int max, int min, SamplingPanel panel) {
		byte temp = 0;
		if (panel.equals(this.teamSamplingPanel)) {
			temp = (1 << LookupTable.TEAM_BIT_POS);
		}
		else if (panel.equals(this.greenSamplingPanel)) {
			temp = (1 << LookupTable.GREEN_BIT_POS);
		}
		else if (panel.equals(this.ballSamplingPanel)) {
			temp = (1 << LookupTable.BALL_BIT_POS);
		}
		LookupTable.setVTable(max, min, temp);
	}
    protected int squared (int x) {
        return x * x;
    }

}