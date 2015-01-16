package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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

import data.SituationTableModel;

public class SituationPanel extends JPanel {
	
	private ArrayList<SituationArea> listOfSituations;
	
	private JButton addButton;
	private JButton removeButton;
	
	private JTable tableOfSituations;
	
	private SituationTableModel situationModel;
	
	private Field field;;
	
	public SituationPanel(Field field) {
		this.field = field;
		this.setLayout(new BorderLayout());
		
		listOfSituations = new ArrayList<SituationArea>();
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
                        System.out.println("No rows are selected.");
                    } else {
                    	for (SituationArea a : listOfSituations) {
        					a.setActive(false);
        					a.setBorderColor(Color.GRAY);
        				}
                        int selectedRow = lsm.getMinSelectionIndex();
                        
                        ((SituationArea)situationModel.getValueAt(selectedRow, 0)).setActive(true);
                        ((SituationArea)situationModel.getValueAt(selectedRow, 0)).setBorderColor(Color.red);
                        SituationPanel.this.field.setSelectedArea((SituationArea)situationModel.getValueAt(selectedRow, 0));
                        System.out.println("Row " + selectedRow
                                + " is now selected.");
                    }
                }
       });
		
		removeButton = new JButton("Remove");
		addButton = new JButton("Add");
		
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(new JScrollPane(tableOfSituations), BorderLayout.CENTER);
		
		
		
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//making the other 
				
				
				SituationArea newArea = new SituationArea(100,100);
				
				listOfSituations.add(newArea);
				
				SituationPanel.this.field.add(newArea);
				newArea.addAreaListener(SituationPanel.this.field);
				
				SituationPanel.this.field.repaint();	
				SituationPanel.this.field.setSelectedArea(newArea);
				SituationPanel.this.field.setComponentZOrder(newArea, 0);

				situationModel.fireTableDataChanged();
				tableOfSituations.setRowSelectionInterval(listOfSituations.size()-1, listOfSituations.size()-1);
			}
			
		});
		
		
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = tableOfSituations.getSelectedRow();
				
				if (listOfSituations.size() > 0 && selectedRow >= 0  && selectedRow < listOfSituations.size()) {
					
					SituationArea removeArea= ((SituationArea)situationModel.getValueAt(selectedRow, 0));
					listOfSituations.remove(removeArea);
					SituationPanel.this.field.remove(removeArea);
					
					SituationPanel.this.field.repaint();
					situationModel.fireTableDataChanged();
				}
			}
			
		});
	}

}
