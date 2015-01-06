package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import communication.RobotController;
import communication.SerialPortCommunicator;
import jssc.SerialPortList;

public class TestComPanel extends JPanel {
	
	private JComboBox<String> comboBox;
	
	private JButton testRotateBtn;
	private JButton testForwardBtn;
	
	private double[] linearVelocity;
	private double[] angularVelocity;
	
	private TestWorker currentWorker;
	
	private SerialPortCommunicator serialCom;
	
	public TestComPanel (SerialPortCommunicator s) {
		linearVelocity = new double[11];
		angularVelocity = new double[11];
		serialCom = s;
		
		String[] portNames = SerialPortList.getPortNames();
		comboBox = new JComboBox<String>(portNames);
		
		testRotateBtn = new JButton("Rotate");
		
		testForwardBtn = new JButton("Forward");
		
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(testRotateBtn);
		buttonPanel.add(testForwardBtn);
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(comboBox);
		this.add(buttonPanel);
		
		//open the port;
		serialCom.openPort((String)comboBox.getSelectedItem());
		
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				serialCom.closePort();
				serialCom.openPort((String)comboBox.getSelectedItem());
			}
			
		});
		
		testRotateBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (currentWorker != null ) {
					currentWorker.cancel(true);
				}
				
				for (int i = 0; i< 11; i++) {
					linearVelocity[i] = 0;
					angularVelocity[i] = (3.14159265358979323846) / 2;;
				}
				currentWorker = new TestWorker();
				currentWorker.execute();
				
			}
			
		});
		
		testForwardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentWorker != null ) {
					currentWorker.cancel(true);
				}
				
				for (int i = 0; i< 11; i++) {				
					linearVelocity[i] = 0.1;
					angularVelocity[i] = 0;
				}
				currentWorker = new TestWorker();
				currentWorker.execute();
			}
			
		});
		
		
	}
	
	class TestWorker extends SwingWorker<Integer,Integer> {

		private RobotController controller;
		
		public TestWorker () {
			controller = new RobotController(serialCom);
		}	
		@Override
		protected Integer doInBackground() throws Exception {
			long startTime = System.currentTimeMillis();
			
			long endTime = startTime +5000;
			while (!isCancelled() && System.currentTimeMillis() < endTime) {
				controller.sendVelocity(linearVelocity, angularVelocity);
			}
			return null;
		}

	}
}



