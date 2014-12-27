package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import communication.Receiver;

public class RobotSoccerMain extends JPanel
                             implements ActionListener {

	public static final int DEFAULT_PORT_NUMBER = 31000;
    private JButton startButton;
    private JTextArea taskOutput;
    private Receiver task;
    private Field field;
    
    
    private JTextField portField;

    public RobotSoccerMain() {
        super(new BorderLayout());

        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
        
        portField = new JTextField(10);
        
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(portField);
        field = new Field();
        field.setBackground(Color.green);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        add(field, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
    	int portNumber;
    	try {
    		portNumber = Integer.parseInt(portField.getText());
    	}	catch (NumberFormatException e) {
    		portNumber = DEFAULT_PORT_NUMBER;
    		JOptionPane.showMessageDialog(RobotSoccerMain.this,"Incorrect character, will use default port: 31000");
    	}
    	
        task = new Receiver(taskOutput, portNumber);
        task.execute();
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