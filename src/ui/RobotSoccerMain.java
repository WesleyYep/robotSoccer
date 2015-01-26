package ui;

import game.Tick;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import strategy.CurrentStrategy;
import bot.Robots;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.ds.ipcam.IpCamAuth;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import communication.Receiver;
import communication.SerialPortCommunicator;

import controllers.BallController;
import controllers.FieldController;


public class RobotSoccerMain extends JPanel implements ActionListener {

	static {
	    Webcam.setDriver(new IpCamDriver());
	}
	
	public static final int DEFAULT_PORT_NUMBER = 31000;
    private JButton startButton;
    private JTextArea taskOutput;
    private Receiver task;
    private FieldController fieldController;
    private BallController ballController;
    private Field field;
    private Ball ball;
    private JTextField portField;
    private RobotInfoPanel[] robotInfoPanels;
    private TestComPanel testComPanel;
    private SerialPortCommunicator serialCom;
    private Robots bots;
    
    private SituationPanel situationPanel;
    private PlaysPanel playsPanel;
    private RolesPanel rolesPanel;
    private CurrentStrategy currentStrategy;
    
    private JTabbedPane tabPane;
	private DrawAreaGlassPanel glassPanel;
	
	// Constant string so that you can switch between cards.
	final static String FIELDSTRING = "Card with Field";
	final static String CAMSTRING = "Card with Cam";
	
    public RobotSoccerMain() throws MalformedURLException {
    	// Auto wrap after 12 columns.
    	// https://www.youtube.com/watch?v=U6xJfP7-HCc
    	// Layout constraint, column constraint
        super(new MigLayout("wrap 12"));
        
        //Create the demo's UI.
        //create start button and text field for port number
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        portField = new JTextField();
        
        JPanel portPanel = new JPanel(new MigLayout());
        portPanel.add(startButton);
        portPanel.add(portField, "push, grow");
        
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
        
        JPanel testComContainerPanel = new JPanel();
        testComPanel = new TestComPanel(serialCom, bots);
        testComContainerPanel.add(testComPanel);
        
        //creating panel holding robot informations
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        robotInfoPanels = new RobotInfoPanel[5];
        
        for (int i = 0; i<5; i++) {
        	robotInfoPanels[i] = new RobotInfoPanel(bots.getRobot(i), i);
        	infoPanel.add(robotInfoPanels[i]);
        }

        currentStrategy = new CurrentStrategy();
        situationPanel = new SituationPanel(fieldController, currentStrategy);
        playsPanel = new PlaysPanel(currentStrategy);
        rolesPanel = new RolesPanel(currentStrategy);

        glassPanel = new DrawAreaGlassPanel(field, situationPanel);
		glassPanel.setVisible(false);
		field.add(glassPanel);
		situationPanel.setGlassPanel(glassPanel);
		
        //create tab pane
        tabPane = new JTabbedPane();
        tabPane.addTab("Situation", situationPanel);
        tabPane.addTab("Plays", playsPanel);
        tabPane.addTab("Roles", rolesPanel);

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
//        WebcamLogConfigurator.configure("logback.xml");
        // Create the cards.
        JPanel cards = new JPanel(new CardLayout());
//        cards.add(field, FIELDSTRING);
        // Set up webcam.
        IpCamDevice camDevice = new IpCamDevice("BLAZE", "http://192.168.1.78:9000/video", IpCamMode.PUSH);
        IpCamDeviceRegistry.register(camDevice);
        Webcam webcam = Webcam.getDefault();

        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        
        cards.add(webcamPanel, CAMSTRING);
        setUpGame();
        
        add(cards, "span 6, width 600:600:600");
        add(tabPane, "span 6 4, width 600:600:600, pushy, growy, wrap");
        add(infoPanel, "span 6, width 600:600:600, wrap");
        add(portPanel, "span 2, width 200:200:200");
        add(new JScrollPane(taskOutput), "span 4 2, width 400:400:400, pushy, growy, wrap");
        add(testComContainerPanel, "span 2, width 200:200:200");
        
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void setUpGame() {
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new Tick(field, bots, testComPanel), 0, 50);
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
	    	
	        task = new Receiver(taskOutput, portNumber);
	        task.registerListener(fieldController);
	        task.execute();
	        startButton.setText("Stop");
    	} else {
    		task.stop();
    		startButton.setText("Start");
    	}
    }

    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     * @throws MalformedURLException 
     */
    private static void createAndShowGUI() throws MalformedURLException {
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
                try {
					createAndShowGUI();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
            }
        });
    }
}