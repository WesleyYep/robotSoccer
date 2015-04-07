package ui;

import bot.Robots;
import communication.Sender;
import communication.SenderListener;
import communication.SerialPortCommunicator;
import jssc.SerialPortList;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestComPanel extends JPanel implements SenderListener{


	//18.52 second from one side to another side actual
	//51.39 second from one side to another side in simulation
	private JComboBox<String> comboBox;

	private JButton testRotateBtn;
	private JButton testForwardBtn;
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

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new MigLayout("ins 0"));
		buttonPanel.add(testRotateBtn, "w 50%");
		buttonPanel.add(testForwardBtn, "w 50%, wrap");
		buttonPanel.add(simulationCheckBox);

		this.setLayout(new MigLayout());
		this.add(comboBox, "pushx, growx, wrap");
		this.add(buttonPanel, "pushx, growx");

		//open the port;
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
				//		if (simulationCheckBox.isSelected()) {
				if (!manualControl || testingForward) {
					robots.testRotate();
					manualControl = true;
					testingForward = false;
					testingRotate = true;
				} else {
					manualControl = false;
					robots.stopAllMovement();
				}
				//		} else {
				//					if (currentWorker != null) {
				//						currentWorker.cancel(true);
				//					}
				//
				//					for (int i = 0; i < 11; i++) {
				//						linearVelocity[i] = 0;
				//						angularVelocity[i] = (3.14159265358979323846) / 2;
				//						;
				//					}
				//					currentWorker = new TestWorker();
				//					currentWorker.execute();
				//				}
			}

		});

		testForwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//				if (simulationCheckBox.isSelected()) {
				if (!manualControl || testingRotate) {
					robots.testForward();
					manualControl = true;
					testingRotate = false;
					testingForward = true;
				} else {
					manualControl = false;
					robots.stopAllMovement();
				}
				//				} else {
				//					if (currentWorker != null) {
				//						currentWorker.cancel(true);
				//					}
				//
				//					for (int i = 0; i < 11; i++) {
				//						linearVelocity[i] = 0.1;
				//						angularVelocity[i] = 0;
				//					}
				//					currentWorker = new TestWorker();
				//					currentWorker.execute();
				//				}
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



