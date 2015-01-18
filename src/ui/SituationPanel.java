package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import controllers.FieldController;
import data.Situation;
import data.SituationTableModel;

public class SituationPanel extends JPanel {
	
	private ArrayList<Situation> listOfSituations;
	
	private JButton addButton;
	private JButton removeButton;
	
	private JTable tableOfSituations;
	
	private SituationTableModel situationModel;
	
	private JScrollPane scrollTable;
	
	private FieldController fieldController;
	
	private DrawAreaGlassPanel glassPanel;

	
	public SituationPanel(FieldController fieldController) {
		this.fieldController = fieldController;
		this.setLayout(new BorderLayout());
		
		listOfSituations = new ArrayList<Situation>();
		situationModel = new SituationTableModel(listOfSituations);
		tableOfSituations = new JTable(situationModel);
		
		tableOfSituations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = tableOfSituations.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    //Ignore extra messages.
                    if (e.getValueIsAdjusting()) return;
 
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (lsm.isSelectionEmpty()) {
                       
                    } else {
                    	for (Situation s : listOfSituations) {
        					s.setAreaActive(false);
        				}
                        int selectedRow = lsm.getMinSelectionIndex();
                        
                        ((Situation)situationModel.getValueAt(selectedRow, 0)).setAreaActive(true);
                        
                        SituationPanel.this.fieldController.setSelectedArea(((Situation)situationModel.getValueAt(selectedRow, 0)).getArea());
                    }
                }
       });
		
		removeButton = new JButton("Remove");
		addButton = new JButton("Add");
		
		scrollTable = new JScrollPane(tableOfSituations);
		scrollTable.setPreferredSize(new Dimension(300, scrollTable.getPreferredSize().height));
		
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(scrollTable, BorderLayout.CENTER);
		
		
		
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				SituationPanel.this.fieldController.bringComponentToTheTop(glassPanel);
				glassPanel.setVisible(true);
			}
			
		});
		
		
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = tableOfSituations.getSelectedRow();
				
				if (listOfSituations.size() > 0 && selectedRow >= 0  && selectedRow < listOfSituations.size()) {
					
					Situation removeSituation= ((Situation)situationModel.getValueAt(selectedRow, 0));
					
					listOfSituations.remove(removeSituation);
					
					SituationPanel.this.fieldController.removeArea(removeSituation.getArea());
					
					SituationPanel.this.fieldController.repaintField();
					situationModel.fireTableDataChanged();
				}
			}
			
		});
	}
	
	
	public void addSituations(Rectangle r) {
				
		SituationArea newArea = new SituationArea((int)r.getWidth(),(int)r.getHeight());
		newArea.addAreaListener(SituationPanel.this.fieldController);
		Situation newSituation = new Situation(newArea, "new situation " + (listOfSituations.size()+1));
		
		listOfSituations.add(newSituation);
		SituationPanel.this.fieldController.addArea(newArea);
		
		situationModel.fireTableDataChanged();
		tableOfSituations.setRowSelectionInterval(listOfSituations.size()-1, listOfSituations.size()-1);
		
		newArea.setBounds((int)r.getX(), (int)r.getY(),newArea.getWidth(), newArea.getHeight());
		
		fieldController.setSelectedArea(newArea);
		
	}
	
	public void setGlassPanel(DrawAreaGlassPanel panel) {
		glassPanel = panel;
	}
	
	public void updateSituationTable () {
		situationModel.fireTableDataChanged();
		tableOfSituations.setRowSelectionInterval(listOfSituations.size()-1, listOfSituations.size()-1);
	}

}
