package ui;

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
        add(tabPane, "wrap");
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
                sp.takeSample(xPos, yPos);
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
