package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import bot.Robots;
import communication.Sender;
import communication.SenderListener;
import communication.SerialPortCommunicator;
import jssc.SerialPortList;

public class TestComPanel extends JPanel implements SenderListener{
	
	
	//18.52 second from one side to another side actual
	//51.39 second from one side to another side in simulation
	private JComboBox<String> comboBox;
	
	private JButton testRotateBtn;
	private JButton testForwardBtn;
    private JCheckBox simulationCheckBox;

	private double[] linearVelocity;
	private double[] angularVelocity;

//	private TestWorker currentWorker;
	private SimulationWorker currentSimWorker;
	private Robots robots;
	private Sender sender = null;
	
	private SerialPortCommunicator serialCom;
	
	public TestComPanel (SerialPortCommunicator s, Robots bots) {
		linearVelocity = new double[11];
		angularVelocity = new double[11];
		serialCom = s;
		robots = bots;

		String[] portNames = SerialPortList.getPortNames();
		comboBox = new JComboBox<String>(portNames);
		simulationCheckBox = new JCheckBox("Simulation");

		testRotateBtn = new JButton("Rotate");

		testForwardBtn = new JButton("Forward");

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(testRotateBtn);
		buttonPanel.add(testForwardBtn);
		buttonPanel.add(simulationCheckBox);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(comboBox);
		this.add(buttonPanel);

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
					if (currentSimWorker != null) {
						currentSimWorker.cancel(true);
						currentSimWorker = null;
						robots.stopAllMovement();
					} else {
						currentSimWorker = new SimulationWorker("rotate");
						currentSimWorker.execute();
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
					if (currentSimWorker != null) {
						currentSimWorker.cancel(true);
						currentSimWorker = null;
						robots.stopAllMovement();
					} else {
						currentSimWorker = new SimulationWorker("forward");
						currentSimWorker.execute();
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


	//may possibly not even need this worker
	class SimulationWorker extends SwingWorker<Integer,Integer> {
		private String command;
		
		public SimulationWorker(String command) {
			this.command = command;
		}
		@Override
		protected Integer doInBackground() throws Exception {
		//	while (!isCancelled()) {
				if (command.equals("forward")) {
					robots.testForward();
				} else {
					robots.testRotate();
				}
		//	}
		//	robots.stopAllMovement();
			return null;
		}

	}


	@Override
	public void setSender(Sender s) {
		sender = s;
	}
}



