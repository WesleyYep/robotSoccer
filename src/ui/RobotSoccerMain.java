package ui;

import bot.Robots;

import com.alee.laf.WebLookAndFeel;

import com.jidesoft.plaf.xerto.VerticalLabelUI;
import communication.NetworkSocket;
import communication.NetworkSocketListener;
import communication.SerialPortCommunicator;
import config.ConfigFile;
import config.ConfigPreviousFile;
import controllers.*;
import game.Tick;
import jssc.SerialPortList;
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
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

public class RobotSoccerMain extends JFrame implements ActionListener, WebcamDisplayPanelListener, NetworkSocketListener {

	public static final int DEFAULT_PORT_NUMBER = 31000;
	public static final int TICK_TIME_MS = 5;

	private JButton startButton, connectionButton;

	private NetworkSocket serverSocket;
	private FieldController fieldController;
	private JTextField portField, webcamURLField;
	private RobotInfoPanel[] robotInfoPanels;
	private SerialPortCommunicator serialCom;
	private JComboBox<String> webcamTypeComboBox;

	private Tick gameTick;

	private JTabbedPane tabPane;

	private WebcamController webcamController;

	private VisionPanel visionPanel;
	private VisionSettingFile visionSetting;

	private VisionWorker visionWorker;
	private VisionController visionController;
	private WindowController windowController;

	private boolean manualControl = false;
	private boolean simulation = false;

	// Constant string so that you can switch between cards.
	private final static String FIELDSTRING = "Card with Field";
	private final static String CAMSTRING = "Card with Cam";

	private final static String[] CONNECTION = {"Connect", "Disconnect"};

	private final static String[] WEBCAMCONNECTIONTYPE = {"Default", "IP"};


	public RobotSoccerMain() throws MalformedURLException {
		// Auto wrap after 12 columns.
		// https://www.youtube.com/watch?v=U6xJfP7-HCc
		// Layout constraint, column constraint
		super("BLAZE Robot Soccer");
		setLayout(new BorderLayout());
		// Set default close operation.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel(new BorderLayout());
		// Toolbar
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setLayout(new MigLayout("ins 0, top"));

		//Create the demo's UI.
		//create start button and text field for port number
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		portField = new JTextField(7);
		JLabel networkLabel = new JLabel("Network");
		networkLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		JPanel networkPanel = new JPanel(new MigLayout());
		networkPanel.add(networkLabel, "span, wrap");
		networkPanel.add(new JLabel("Port Number"));
		networkPanel.add(portField, "wrap");
		networkPanel.add(startButton, "span, align right");
		networkPanel.setOpaque(false);

		//create serial port communicator;
		serialCom = new SerialPortCommunicator();
		Robots bots = new Robots(serialCom);
		bots.makeRealRobots();

		Ball ball = new Ball();
		Field field = new Field(bots, ball);
		fieldController = new FieldController(field);

		// connectionPanel
		JPanel connectionPanel = new JPanel(new MigLayout());
		JLabel connectionLabel = new JLabel("Connection");
		connectionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		// get the port names.
		String[] portNames = SerialPortList.getPortNames();
		JComboBox<String> portNamesComboBox = new JComboBox<String>(portNames);
		JToggleButton testRotateButton = new JToggleButton("Rotate");
		JToggleButton testBackwardButton = new JToggleButton("Backward");
		JToggleButton testForwardButton = new JToggleButton("Forward");

		List<JToggleButton> toggleButtonList = new ArrayList<JToggleButton>();
		toggleButtonList.add(testForwardButton);
		toggleButtonList.add(testRotateButton);
		toggleButtonList.add(testBackwardButton);

		//open the port and selecting COM3 port if available;
		for (int i =0; i<portNames.length; i++) {
			if (portNames[i].equals("COM3")) {
				portNamesComboBox.setSelectedIndex(i);
			}

			if (portNames[i].equals("COM4")) {
				portNamesComboBox.setSelectedIndex(i);
			}
		}

		serialCom.openPort((String) portNamesComboBox.getSelectedItem());

		portNamesComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serialCom.closePort();
				serialCom.openPort((String) portNamesComboBox.getSelectedItem());
			}

		});

		testForwardButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					for (JToggleButton b : toggleButtonList) {
						if (!b.equals(testForwardButton)) {
							b.setSelected(false);
						}
					}
					bots.testForward();
					manualControl = true;
				} else {
					manualControl = false;
					bots.stopAllMovement();
				}
			}
		});

		testBackwardButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					for (JToggleButton b : toggleButtonList) {
						if (!b.equals(testBackwardButton)) {
							b.setSelected(false);
						}
					}
					bots.testBackwards();
					manualControl = true;
				} else {
					manualControl = false;
					bots.stopAllMovement();
				}
			}
		});

		testRotateButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					for (JToggleButton b : toggleButtonList) {
						if (!b.equals(testRotateButton)) {
							b.setSelected(false);
						}
					}
					bots.testRotate();
					manualControl = true;
				} else {
					manualControl = false;
					bots.stopAllMovement();
				}
			}
		});

		connectionPanel.setOpaque(false);
		connectionPanel.add(connectionLabel, "wrap");
		connectionPanel.add(new JLabel("COM"));
		connectionPanel.add(portNamesComboBox, "w 80, wrap");
		connectionPanel.add(testForwardButton, "w 80");
		connectionPanel.add(testBackwardButton, "w 80, wrap");
		connectionPanel.add(testRotateButton, "w 80");

		JButton runStratButton = new JButton("Run Strat");
		JButton stopStratButton = new JButton("Stop");
		JButton runSetPlayButton = new JButton("Set play");
		JLabel stratStatusLbl = new JLabel("Stopped");
		JLabel stratLabel = new JLabel("Strategy");
		stratLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		JCheckBox simCheckBox = new JCheckBox("Simulation");

		simCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					simulation = true;
				} else {
					simulation = false;
				}
			}
		});

		JPanel stratControlPanel = new JPanel(new MigLayout());
		stratControlPanel.setOpaque(false);
		stratControlPanel.add(stratLabel, "wrap");
		stratControlPanel.add(stratStatusLbl);
		stratControlPanel.add(simCheckBox, "wrap");
		stratControlPanel.add(runStratButton, "w 80");
		stratControlPanel.add(stopStratButton, "w 80, wrap");
		stratControlPanel.add(runSetPlayButton, "w 80");

		//creating panel holding robot informations
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout());
		robotInfoPanels = new RobotInfoPanel[5];

		for (int i = 0; i<5; i++) {
			robotInfoPanels[i] = new RobotInfoPanel(bots.getRobot(i), i);
			infoPanel.add(robotInfoPanels[i]);
		}

		CurrentStrategy currentStrategy = new CurrentStrategy(fieldController);
		field.setCurrentStrategy(currentStrategy);
		SituationPanel situationPanel = new SituationPanel(fieldController, currentStrategy);
		PlaysPanel playsPanel = new PlaysPanel(currentStrategy);
		RolesPanel rolesPanel = new RolesPanel(currentStrategy);
		ActionParameterPanel actionPanel = new ActionParameterPanel();

		DrawAreaGlassPanel glassPanel = new DrawAreaGlassPanel(field, situationPanel);
		glassPanel.setVisible(false);
		field.add(glassPanel);
		field.addComponentListener(glassPanel);
		situationPanel.setGlassPanel(glassPanel);

		//create tab pane
		tabPane = new JTabbedPane();
		tabPane.addTab("Situation", situationPanel);
		tabPane.addTab("Plays", playsPanel);
		tabPane.addTab("Roles", rolesPanel);
	//	tabPane.addTab("Actions" ,actionPanel);

		// Create webcam component panel.
		JPanel webcamComponentPanel = new JPanel(new MigLayout());
		JLabel webcamLabel = new JLabel("Webcam");
		webcamLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		// Create the components.
		webcamURLField = new JTextField(15);
		webcamTypeComboBox = new JComboBox<String>(WEBCAMCONNECTIONTYPE);
		webcamTypeComboBox.addActionListener(this);
		webcamTypeComboBox.setSelectedIndex(0);
		connectionButton = new JButton(CONNECTION[0]);

		// Add listeners
		connectionButton.addActionListener(this);

		// Add components into panel.
		webcamComponentPanel.add(webcamLabel, "span, wrap");
		webcamComponentPanel.add(new JLabel("Type"));
		webcamComponentPanel.add(webcamTypeComboBox, "align right, wrap");
		webcamComponentPanel.add(new JLabel("URL"), "gapright 10");
		webcamComponentPanel.add(webcamURLField, "wrap");
		webcamComponentPanel.add(connectionButton, "span, align right");
		webcamComponentPanel.setOpaque(false);

        //create the gameTick
        gameTick = new Tick(this);

        WebcamDisplayPanel webcamDisplayPanel = new WebcamDisplayPanel();
		webcamController = new WebcamController(webcamDisplayPanel, gameTick);
		ColourPanel colourPanel = new ColourPanel(webcamController);
		
		visionWorker = new VisionWorker(colourPanel);
		visionWorker.addListener(fieldController);
		
		// Add listener
		webcamDisplayPanel.addWebcamDisplayPanelListener(this);
		webcamDisplayPanel.addWebcamDisplayPanelListener(visionWorker);
		webcamDisplayPanel.addWebcamDisplayPanelListener(colourPanel);

		visionController = new VisionController();
		
		//cards.add(field, FIELDSTRING);
		//cards.add(webcamDisplayPanel, CAMSTRING);

		visionSetting = new VisionSettingFile(webcamController,colourPanel,visionController);
		tabPane.addTab("Colour", colourPanel);

		visionPanel = new VisionPanel(webcamController, visionController);
		webcamDisplayPanel.addWebcamDisplayPanelListener(visionPanel);
		tabPane.addTab("Vision", visionPanel);

		//window listener
		windowController = new WindowController(webcamController,currentStrategy,visionSetting);
		this.addWindowListener(windowController);
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(windowController);
		
		//contentPane.add(cards, "span 6, width 640:640:640, height 480:480:480");
		//contentPane.add(tabPane, "span 6 5, width 600:600:600, pushy, growy, wrap");
		//contentPane.add(infoPanel, "span 6, width 600:600:600, wrap");
		//contentPane.add(portPanel, "span 3, width 300:300:300");
		//contentPane.add(webcamComponentPanel, "span 3, width 300:300:300");
		//contentPane.add(testComContainerPanel, "span 3, width 300:300:300");
		//contentPane.add(stratControlPanel, "span 3, width 300:300:300");

		//contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		//contentPane.setPreferredSize(new Dimension(1290, 900));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

		tabbedPane.addTab(null, situationPanel);
		JLabel situationLabel = new JLabel("Situation");
		situationLabel.setUI(new VerticalLabelUI(false));
		situationLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		tabbedPane.setTabComponentAt(0, situationLabel);

		tabbedPane.addTab(null, playsPanel);
		JLabel playsLabel = new JLabel("Plays");
		playsLabel.setUI(new VerticalLabelUI(false));
		playsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		tabbedPane.setTabComponentAt(1, playsLabel);

		tabbedPane.addTab(null, rolesPanel);
		JLabel rolesLabel = new JLabel("Roles");
		rolesLabel.setUI(new VerticalLabelUI(false));
		rolesLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		tabbedPane.setTabComponentAt(2, rolesLabel);

		JPanel robotViewPanel = new JPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, robotViewPanel) {
			@Override
			public void setDividerLocation(int location) {
				int minimumWidth = 200;
				int maximumWidth = (getSize().width == 0) ? 500 : (int)(getSize().width * 0.3);

				if (location < minimumWidth) {
					super.setDividerLocation(minimumWidth);
				} else if (location > maximumWidth) {
					super.setDividerLocation(maximumWidth);
				} else {
					super.setDividerLocation(location);
				}
			}
		};
		splitPane.setResizeWeight(0.3);

		contentPane.add(splitPane, BorderLayout.CENTER);

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
					//changeCard(CAMSTRING);
				} else {
					//changeCard(FIELDSTRING);
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
                gameTick.runSetPlay(false);
                gameTick.runStrategy(true);
				stratStatusLbl.setText("Running");
			}

		});

		stopStratButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                gameTick.runSetPlay(false);
                gameTick.runStrategy(false);
				stratStatusLbl.setText("Stopped");
			}
             });

        runSetPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameTick.runSetPlay(true);
            }
        });


        //setting up configuration for the program
        ConfigFile configFile = ConfigFile.getInstance();
        configFile.createConfigFile();
        ConfigPreviousFile configPreviousFile = ConfigPreviousFile.getInstance();
        configPreviousFile.createConfigFile();

		// Create the menu
		JMenuBar menuBar = new JMenuBar();

		JMenu visionMenu = new JMenu("Vision");
		JMenu stratMenu = new JMenu("Strategy");
		JMenu openPreviousFilesMenu = new JMenu("Open Previous Files");

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

		openPreviousFilesMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentStrategy.read(ConfigPreviousFile.getInstance().getPreviousStratFile());
				visionSetting.open(ConfigPreviousFile.getInstance().getPreviousVisionFile());
			}
		});

		visionMenu.add(openVisionMenuItem);
		visionMenu.add(saveVisionMenuItem);

		stratMenu.add(openStrategyMenuItem);
		stratMenu.add(saveStrategyMenuItem);

		menuBar.add(visionMenu);
		menuBar.add(stratMenu);
		menuBar.add(openPreviousFilesMenu);

		setJMenuBar(menuBar);

		// set toolbar
		toolbar.add(networkPanel);
		toolbar.add(webcamComponentPanel);
		toolbar.add(connectionPanel);
		toolbar.add(stratControlPanel);

		add(toolbar, BorderLayout.PAGE_START);
		add(contentPane, BorderLayout.CENTER);
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
    	    		System.out.println("Incorrect character, will use default port: 31000");
    	    	}


    	    	serverSocket = new NetworkSocket(portNumber);
    	    	System.out.println("created new socket");
    	    	serverSocket.execute();
    	    	serverSocket.addReceiverListener(fieldController);
    	    	serverSocket.addSenderListener(gameTick);
                serverSocket.setGameTick(gameTick);
    		} else {
        		//tell the serverSocket to begin the closing procedure;
        		serverSocket.close();
    		}
    	} else if (evt.getSource() == connectionButton) {

    		if (connectionButton.getText().equals(CONNECTION[0])) {

				String selectedType = (String)webcamTypeComboBox.getSelectedItem();
				if (selectedType.equals(WEBCAMCONNECTIONTYPE[0])) {
					webcamController.connect();
				} else {
					webcamController.connect(webcamURLField.getText());
				}

            } else {
    			webcamController.disconnect();
    		}

    	} else if (evt.getSource() == webcamTypeComboBox) {
			String selectedType = (String)webcamTypeComboBox.getSelectedItem();
			if (selectedType.equals(WEBCAMCONNECTIONTYPE[0])) {
				webcamURLField.setEditable(false);
			} else {
				webcamURLField.setEditable(true);
			}
		}
    }

//    public void changeCard(String cardName) {
//    	CardLayout layout = (CardLayout)cards.getLayout();
//    	layout.show(cards, cardName);
//    }

//	public void setUpGame() {
//		java.util.Timer timer = new java.util.Timer();
//		timer.schedule(gameTick, 0, TICK_TIME_MS);
//	}


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

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		String path = System.getProperty("user.dir");
		System.load( path + "\\native\\opencv_java2411.dll" );
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

	// TODO Check if EDT thread
	@Override
	public void connectionOpen() {
		startButton.setText("Stop");
	}

	@Override
	public void connectionClose() {
		startButton.setText("Start");
	}

	public boolean isManualControl() {
		return manualControl;
	}

	public boolean isSimulation() {
		return simulation;
	}

	public FieldController getFieldController() {
		return fieldController;
	}

	public VisionController getVisionController() {
		return visionController;
	}

	public WebcamController getWebcamController() {
		return webcamController;
	}

	public WindowController getWindowController() {
		return windowController;
	}
}