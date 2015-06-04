package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import jssc.SerialPortList;
import net.miginfocom.swing.MigLayout;
import bot.Robots;
import communication.Sender;
import communication.SenderListener;
import communication.SerialPortCommunicator;

public class TestComPanel extends JPanel implements SenderListener{


	//18.52 second from one side to another side actual
	//51.39 second from one side to another side in simulation
	private JComboBox<String> comboBox;

	private JButton testRotateBtn;
    private JButton testForwardBtn;
    private JButton testBackwardBtn;
	private JCheckBox simulationCheckBox;

	private Robots robots;
	private Sender sender = null;

	private boolean manualControl = false;
	private boolean testingForward = false;
	private boolean testingRotate = false;
	private SerialPortCommunicator serialCom;

	public TestComPanel (SerialPortCommunicator s, Robots bots) {
		serialCom = s;
		robots = bots;

		String[] portNames = SerialPortList.getPortNames();
		comboBox = new JComboBox<String>(portNames);
		simulationCheckBox = new JCheckBox("Simulation");

		testRotateBtn = new JButton("Rotate");
		testForwardBtn = new JButton("Forward");
        testBackwardBtn = new JButton("Backward");

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new MigLayout("ins 0"));
        buttonPanel.add(testRotateBtn, "w 30%");
        buttonPanel.add(testBackwardBtn, "w 30%");
		buttonPanel.add(testForwardBtn, "w 30%, wrap");
		buttonPanel.add(simulationCheckBox);

		this.setLayout(new MigLayout());
		this.add(comboBox, "pushx, growx, wrap");
		this.add(buttonPanel, "pushx, growx");

		//open the port and selecting COM3 port if available;
		for (int i =0; i<portNames.length; i++) {
			if (portNames[i].equals("COM3")) {
				comboBox.setSelectedIndex(i);
			}

			if (portNames[i].equals("COM4")) {
				comboBox.setSelectedIndex(i);
			}
		}
		serialCom.openPort((String) comboBox.getSelectedItem());
		
		

		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					serialCom.closePort();
					serialCom.openPort((String) comboBox.getSelectedItem());	
			}

		});

		
		testRotateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!manualControl || testingForward) {
					robots.testRotate();
					manualControl = true;
					testingForward = false;
					testingRotate = true;
				} else {
					manualControl = false;
					robots.stopAllMovement();
				}
			}

		});

		testForwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!manualControl || testingRotate) {
					robots.testForward();
					manualControl = true;
					testingRotate = false;
					testingForward = true;
				} else {
					manualControl = false;
					robots.stopAllMovement();
				}
			}

		});

        testBackwardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!manualControl || testingRotate) {
                    robots.testBackwards();
                    manualControl = true;
                    testingRotate = false;
                    testingForward = true;
                } else {
                    manualControl = false;
                    robots.stopAllMovement();
                }
            }

        });

		simulationCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
	}

	public boolean isSimulation() {
		return simulationCheckBox.isSelected();
	}


	@Override
	public void setSender(Sender s) {
		sender = s;
	}

	public boolean isManualControl() {
		return manualControl;
	}
}



