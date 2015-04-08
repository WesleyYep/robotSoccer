package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

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
	private JCheckBox simulationCheckBox;

	private Robots robots;
	private Sender sender = null;

	private boolean manualControl = false;
	private boolean testingForward = false;
	private boolean testingRotate = false;
	private boolean isManualComList = true;
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
				
				
			//	if (isManualComList) {
					serialCom.closePort();
					serialCom.openPort((String) comboBox.getSelectedItem());	
				//}
			//	else {
				//	isManualComList = true;
				//}
			}

		});
		/*
		comboBox.addMouseListener( new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				String[] names = SerialPortList.getPortNames();
				if (Arrays.equals(comboBox.getSelectedObjects(), names)|| comboBox.getSelectedObjects().length != names.length) {
					isManualComList = false;
					comboBox.removeAllItems();
					
					for(int i=0; i<names.length; i++) {
						isManualComList = false;
						comboBox.addItem(names[i]);
					}
					comboBox.repaint();
				}	 
			}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		});*/
		
		
		comboBox.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				
				
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



