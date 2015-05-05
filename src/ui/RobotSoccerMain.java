package ui;

import bot.Robots;
import com.alee.laf.WebLookAndFeel;
import communication.NetworkSocket;
import communication.SerialPortCommunicator;
import config.ConfigFile;
import controllers.*;
import game.Tick;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.Core;
import strategy.CurrentStrategy;
import ui.WebcamDisplayPanel.ViewState;
import vision.VisionSettingFile;
import vision.VisionWorker;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

public class RobotSoccerMain extends JFrame implements ActionListener, WebcamDisplayPanelListener {

	public static final int DEFAULT_PORT_NUMBER = 31000;
	public static final int TICK_TIME_MS = 5;

	private JButton startButton, connectionButton, recordButton;
	private JTextArea taskOutput;

	private NetworkSocket serverSocket;
	private FieldController fieldController;
	private BallController ballController;
	private Field field;
	private Ball ball;
	private JTextField portField, webcamURLField;
	private RobotInfoPanel[] robotInfoPanels;
	private TestComPanel testComPanel;
	private SerialPortCommunicator serialCom;
	private Robots bots;
	private JRadioButton defaultWebcamRadioButton, IPWebcamRadioButton;

	private Tick gameTick;

	private SituationPanel situationPanel;
	private PlaysPanel playsPanel;
	private RolesPanel rolesPanel;
	private ColourPanel colourPanel;
	private CurrentStrategy currentStrategy;

	private JTabbedPane tabPane;
	private DrawAreaGlassPanel glassPanel;

	private WebcamController webcamController;

	private JPanel cards;
	private VisionPanel visionPanel;
	private VisionSettingFile visionSetting;

	private VisionWorker visionWorker;
	private VisionController visionController;
	private WindowController windowController;
    private WebcamDisplayPanel webcamDisplayPanel;

	private JButton runStratButton;
	private JButton stopStratButton;
	private JLabel	stratStatusLbl;
	private ActionParameterPanel actionPanel;

	// Constant string so that you can switch between cards.
	private final static String FIELDSTRING = "Card with Field";
	private final static String CAMSTRING = "Card with Cam";

	private final static String[] CONNECTION = {"Connect", "Disconnect"};
	private final static String[] VIDEOCAPTURE = {"Record", "Stop"};

	private final static String[] WEBCAMCONNECTIONTYPE = {"Default", "IP"};


	public RobotSoccerMain() throws MalformedURLException {
		// Auto wrap after 12 columns.
		// https://www.youtube.com/watch?v=U6xJfP7-HCc
		// Layout constraint, column constraint
		super("BLAZE Robot Soccer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JPanel contentPane = new JPanel(new MigLayout("wrap 12"));
		//Create the demo's UI.
		//create start button and text field for port number
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		portField = new JTextField();
		runStratButton = new JButton("Run Strat");
		stopStratButton = new JButton("Stop Strat");
		stratStatusLbl = new JLabel("Stopped");

		JPanel stratControlPanel = new JPanel(new MigLayout());
		stratControlPanel.add(runStratButton);
		stratControlPanel.add(stopStratButton);
		stratControlPanel.add(stratStatusLbl);

		JPanel portPanel = new JPanel(new MigLayout());
		portPanel.add(startButton);
		portPanel.add(portField, "pushx, growx");

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

		JPanel testComContainerPanel = new JPanel(new MigLayout("ins 0"));
		testComPanel = new TestComPanel(serialCom, bots);
		testComContainerPanel.add(testComPanel, "pushx, growx");

		//creating panel holding robot informations
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout());
		robotInfoPanels = new RobotInfoPanel[5];

		fieldController.setComPanel(testComPanel);

		for (int i = 0; i<5; i++) {
			robotInfoPanels[i] = new RobotInfoPanel(bots.getRobot(i), i);
			infoPanel.add(robotInfoPanels[i]);
		}

		currentStrategy = new CurrentStrategy(fieldController);
		field.setCurrentStrategy(currentStrategy);
		situationPanel = new SituationPanel(fieldController, currentStrategy);
		playsPanel = new PlaysPanel(currentStrategy);
		rolesPanel = new RolesPanel(currentStrategy);
		actionPanel = new ActionParameterPanel();

		glassPanel = new DrawAreaGlassPanel(field, situationPanel);
		glassPanel.setVisible(false);
		field.add(glassPanel);
		field.addComponentListener(glassPanel);
		situationPanel.setGlassPanel(glassPanel);


		//create tab pane
		tabPane = new JTabbedPane();
		tabPane.addTab("Output", new JScrollPane(taskOutput));
		tabPane.addTab("Situation", situationPanel);
		tabPane.addTab("Plays", playsPanel);
		tabPane.addTab("Roles", rolesPanel);
		tabPane.addTab("Actions" ,actionPanel);

		// Create webcam component panel.
		JPanel webcamComponentPanel = new JPanel(new MigLayout());
		JLabel webcamURLLabel = new JLabel("URL");
		// Create the components.
		webcamURLField = new JTextField();

		defaultWebcamRadioButton = new JRadioButton(WEBCAMCONNECTIONTYPE[0]);
		IPWebcamRadioButton = new JRadioButton(WEBCAMCONNECTIONTYPE[1]);

		ButtonGroup webcamSelectionGroup = new ButtonGroup();
		webcamSelectionGroup.add(defaultWebcamRadioButton);
		webcamSelectionGroup.add(IPWebcamRadioButton);

		// Add Listener for radio buttons.
		defaultWebcamRadioButton.addActionListener(this);
		IPWebcamRadioButton.addActionListener(this);

		// Initially set defaultWebcamRadioButton
		defaultWebcamRadioButton.doClick();

		connectionButton = new JButton(CONNECTION[0]);
		recordButton = new JButton(VIDEOCAPTURE[0]);

		// Add listeners
		connectionButton.addActionListener(this);
		recordButton.addActionListener(this);

		// Add components into panel.
		webcamComponentPanel.add(webcamURLLabel, "wrap");
		webcamComponentPanel.add(webcamURLField, "span 2, pushx, growx, wrap");
		webcamComponentPanel.add(defaultWebcamRadioButton, "split 2");
		webcamComponentPanel.add(IPWebcamRadioButton, "wrap");
		webcamComponentPanel.add(connectionButton, "w 50%");
		webcamComponentPanel.add(recordButton, "w 50%, wrap");

		// Create the cards.
		cards = new JPanel(new CardLayout());

        //create the gameTick
        gameTick = new Tick(field, bots, testComPanel);

        webcamDisplayPanel = new WebcamDisplayPanel();
		webcamController = new WebcamController(webcamDisplayPanel, gameTick);
		colourPanel = new ColourPanel(webcamController);
		
		visionWorker = new VisionWorker(colourPanel);
		visionWorker.addListener(fieldController);
		
		// Add listener
		webcamDisplayPanel.addWebcamDisplayPanelListener(this);
		webcamDisplayPanel.addWebcamDisplayPanelListener(visionWorker);
		webcamDisplayPanel.addWebcamDisplayPanelListener(colourPanel);
		
		
		visionController = new VisionController();


		
		cards.add(field, FIELDSTRING);
		cards.add(webcamDisplayPanel, CAMSTRING);

		visionSetting = new VisionSettingFile(webcamController,colourPanel,visionController);
		tabPane.addTab("Colour", colourPanel);

		visionPanel = new VisionPanel(webcamController, visionController);
		webcamDisplayPanel.addWebcamDisplayPanelListener(visionPanel);
		tabPane.addTab("Vision", visionPanel);

		//window listener
		windowController = new WindowController(webcamController);
		contentPane.add(cards, "span 6, width 640:640:640, height 480:480:480");
		contentPane.add(tabPane, "span 6 5, width 600:600:600, pushy, growy, wrap");
		contentPane.add(infoPanel, "span 6, width 600:600:600, wrap");
		contentPane.add(portPanel, "span 3, width 300:300:300");
		contentPane.add(webcamComponentPanel, "span 3, width 300:300:300");
		contentPane.add(testComContainerPanel, "span 3, width 300:300:300");
		contentPane.add(stratControlPanel, "span 3, width 300:300:300");

		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		contentPane.setPreferredSize(new Dimension(1290, 900));

		tabPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int selectedIndex = tabPane.getSelectedIndex();
				String tabTitle = tabPane.getTitleAt(selectedIndex);

				if (tabTitle.equals("Situation")) {
					fieldController.showArea(true);
				} else {
					fieldController.showArea(false);
				}

				if (tabTitle.equals("Colour") || tabTitle.equals("Vision")) {
					changeCard(CAMSTRING);
				} else {
					changeCard(FIELDSTRING);
				}

				if (tabTitle.equals("Colour")) {
					webcamController.getWebcamDisplayPanel().setZoomCursor();
				} else {
					webcamController.getWebcamDisplayPanel().setDefaultCursor();
				}

				fieldController.repaintField();
			}

		});

	//	setUpGame();

		runStratButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
                gameTick.runStrategy(true);
				stratStatusLbl.setText("Running");
			}

		});

		stopStratButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                gameTick.runStrategy(false);
				stratStatusLbl.setText("Stopped");
			}
        });

        //setting up configuration for the program
        ConfigFile configFile = ConfigFile.getInstance();
        configFile.createConfigFile();

		// Create the menu
		createMenu();
		add(contentPane);
    }
    
    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
    	if (evt.getSource() == startButton) {
    		if (startButton.getText() == "Start") {
    	    	
    	    	int portNumber;
    	    	try {
    	    		portNumber = Integer.parseInt(portField.getText());
    	    	}	catch (NumberFormatException e) {
    	    		portNumber = DEFAULT_PORT_NUMBER;
    	    		taskOutput.append("\nIncorrect character, will use default port: 31000\n");
    	    	}
    	    	

    	    	serverSocket = new NetworkSocket(portNumber, taskOutput, startButton);
    	    	System.out.println("created new socket");
    	    	serverSocket.execute();
    	    	serverSocket.addReceiverListener(fieldController);
    	    	serverSocket.addSenderListener(gameTick);
    	    	serverSocket.addSenderListener(testComPanel);
                serverSocket.setGameTick(gameTick);
    		} else {
        		//tell the serverSocket to begin the closing procedure;
        		serverSocket.close();
    		}
    	} else if (evt.getSource() == defaultWebcamRadioButton) {
    		webcamURLField.setEditable(false);
    	} else if (evt.getSource() == IPWebcamRadioButton) {
    		webcamURLField.setEditable(true);
    	} else if (evt.getSource() == connectionButton) {
    		
    		if (connectionButton.getText().equals(CONNECTION[0])) {

    			// Only one of the radio buttons can be selected at a time
    			if (defaultWebcamRadioButton.isSelected()) {
    				webcamController.connect();
    			} else {
    				webcamController.connect(webcamURLField.getText());
    			}
                
            } else {
    			webcamController.disconnect();
    		}
    		
    	} else if (evt.getSource() == recordButton) {
    		
    		if (recordButton.getText().equals(VIDEOCAPTURE[0])) {
    			recordButton.setText(VIDEOCAPTURE[1]);
    		} else {
    			recordButton.setText(VIDEOCAPTURE[0]);
    		}
    	}
    }

    public void changeCard(String cardName) {
    	CardLayout layout = (CardLayout)cards.getLayout();
    	layout.show(cards, cardName);
    }

//	public void setUpGame() {
//		java.util.Timer timer = new java.util.Timer();
//		timer.schedule(gameTick, 0, TICK_TIME_MS);
//	}

	public WindowController getWindowController() {
		return windowController;
	}

	@Override
	public void viewStateChanged(ViewState currentViewState) {

		switch(currentViewState) {
		case CONNECTED:
			connectionButton.setText(CONNECTION[1]);
			break;
		default:
			connectionButton.setText(CONNECTION[0]);
			break;
		}
	}

	@Override
	public void imageUpdated(BufferedImage image) {
	}
	
	/**
	 * Create the GUI and show it. As with all GUI code, this must run
	 * on the event-dispatching thread.
	 * @throws MalformedURLException 
	 */
	private static void createAndShowGUI() throws MalformedURLException {


		//Create and set up the content pane.
		JFrame frame = new RobotSoccerMain();
		frame.setMinimumSize(new Dimension(1290, 1000));

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * <p>Creates Menu</p>
	 * @param
	 */

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu visionMenu = new JMenu("Vision");
		JMenu stratMenu = new JMenu("Strategy");

		JMenuItem openVisionMenuItem = new JMenuItem("Open Vision/Colour");
		JMenuItem saveVisionMenuItem = new JMenuItem("Save Vision/Colour");

		JMenuItem openStrategyMenuItem = new JMenuItem("Open Strat");
		JMenuItem saveStrategyMenuItem = new JMenuItem("Save Strat");

		// Listeners
		openVisionMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visionSetting.openVisionSetting();
			}
		});

		saveVisionMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visionSetting.saveVisionSetting();
			}
		});

		openStrategyMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentStrategy.readFromFile();
			}
		});

		saveStrategyMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String file = currentStrategy.saveToFile();
			}
		});

		visionMenu.add(openVisionMenuItem);
		visionMenu.add(saveVisionMenuItem);

		stratMenu.add(openStrategyMenuItem);
		stratMenu.add(saveStrategyMenuItem);

		menuBar.add(visionMenu);
		menuBar.add(stratMenu);

		setJMenuBar(menuBar);
	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel( WebLookAndFeel.class.getCanonicalName () );
					createAndShowGUI();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		});
	}
}