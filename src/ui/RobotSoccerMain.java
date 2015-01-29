package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import communication.NetworkSocket;
import communication.Receiver;
import communication.Sender;
import communication.SerialPortCommunicator;
import bot.Robots;
import controllers.BallController;
import controllers.FieldController;
import game.Tick;

public class RobotSoccerMain extends JPanel
                             implements ActionListener {

	public static final int DEFAULT_PORT_NUMBER = 31000;
    private JButton startButton;
    private JTextArea taskOutput;

    private NetworkSocket serverSocket;
    private FieldController fieldController;
    private BallController ballController;
    private Field field;
    private Ball ball;
    private JTextField portField;
    private RobotInfoPanel[] robotInfoPanels;
    private TestComPanel testComPanel;
    private SerialPortCommunicator serialCom;
    private Robots bots;
    
    private Tick gameTick;
    
    private SituationPanel situationPanel;
    
    private JTabbedPane tabPane;
	private DrawAreaGlassPanel glassPanel;

    public RobotSoccerMain() {
        super(new BorderLayout());
        
        //Create the demo's UI.
        //create start button and text field for port number
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        portField = new JTextField(10);
        
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(portField);
        
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false); 
       
        
        //create serial port communicator;
        serialCom = new SerialPortCommunicator();
        bots = new Robots(serialCom);
        bots.makeRealRobots();

        ball = new Ball();
        field = new Field(bots, ball);
        ballController = new BallController(ball);
        fieldController = new FieldController(field, bots, ball);
        
        

        //creating panel holding robot informations
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        robotInfoPanels = new RobotInfoPanel[5];
        
        testComPanel = new TestComPanel(serialCom, bots);
        fieldController.setComPanel(testComPanel);
        infoPanel.add(testComPanel);
        
        for (int i = 0; i<5; i++) {
        	robotInfoPanels[i] = new RobotInfoPanel(bots.getRobot(i), i);
        	infoPanel.add(robotInfoPanels[i]);
        }
        
        
        situationPanel = new SituationPanel(fieldController);

        glassPanel = new DrawAreaGlassPanel(field, situationPanel);
		glassPanel.setVisible(false);
		field.add(glassPanel);
		situationPanel.setGlassPanel(glassPanel);
		
        //create tab pane
        tabPane = new JTabbedPane();
        
        tabPane.addTab("Output", new JScrollPane(taskOutput));
        tabPane.addTab("Situation", situationPanel);
        
        tabPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (tabPane.getTitleAt(tabPane.getSelectedIndex()).equals("Situation")){
					fieldController.showArea(true);
				}
				else {
					fieldController.showArea(false);
				}
				fieldController.repaintField();
			}
			
        	
        });
         
        add(panel, BorderLayout.PAGE_START);
        
        add(tabPane, BorderLayout.LINE_END);
        
        add(field, BorderLayout.CENTER);
        
        add(infoPanel,BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        gameTick = new Tick(field, bots, testComPanel);
        setUpGame();
    }

    public void setUpGame() {
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(gameTick, 0, 50);
    }
    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
    	if (startButton.getText() == "Start") {
	    	int portNumber;
	    	try {
	    		portNumber = Integer.parseInt(portField.getText());
	    	}	catch (NumberFormatException e) {
	    		portNumber = DEFAULT_PORT_NUMBER;
	    		JOptionPane.showMessageDialog(RobotSoccerMain.this,"Incorrect character, will use default port: 31000");
	    	}
	    	serverSocket = new NetworkSocket(portNumber, taskOutput, startButton);
	    	serverSocket.execute();
	    	serverSocket.addReceiverListener(fieldController);
	    	serverSocket.addSenderListener(gameTick);
    	} else {
    		serverSocket.cancel(true);
    		serverSocket.stop();
    		
    	}
    }

    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Robot Soccer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new RobotSoccerMain();
        frame.add(newContentPane);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}