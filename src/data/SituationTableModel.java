package data;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import ui.SituationArea;

public class SituationTableModel extends AbstractTableModel {
	
	//for now the model we use the list of area, later on we could create
	//situation object which contain the area and other information relate to the situations
	
	private ArrayList<SituationArea> listOfSituations;
	
	public SituationTableModel(ArrayList<SituationArea> list) {
		listOfSituations = list;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return listOfSituations.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		return listOfSituations.get(row);
	}

}
