package data;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SituationTableModel extends AbstractTableModel {

	//for now the model we use the list of area, later on we could create
	//situation object which contain the area and other information relate to the situations

	private List<Situation> listOfSituations;

	public SituationTableModel(List<Situation> list) {
		listOfSituations = list;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return listOfSituations.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return listOfSituations.get(row);
	}

	public String getColumnName(int col) {
		if (col == 0) {
			return "Situation Name";
		}
		else {
			return "";
		}
	}

	public boolean isCellEditable(int row, int col) { 
		return true; 
	}

	public void setValueAt(Object value, int row, int col) {
		Situation s = listOfSituations.get(row);   
		s.setSituationName((String)value);
		fireTableCellUpdated(row, col);
	}

	public void setListOfSituations(List<Situation> situations) {
		listOfSituations = situations;
	}

}
