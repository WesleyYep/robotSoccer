package ui;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import actions.Actions;

public class ActionParameterPanel extends JPanel {
	
	
	private JList actionList;
	
	public ActionParameterPanel () {
	
		actionList = new JList(Actions.getActions().toArray());
		this.setLayout(new MigLayout());
		
		this.add(new JLabel("Action List"), "wrap");
		this.add(actionList);
	}
	
	

}
