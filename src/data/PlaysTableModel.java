package data;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import strategy.Play;

public class PlaysTableModel extends AbstractTableModel {

	private List<Play> listOfPlays;
	private boolean isEditable = true;

	public PlaysTableModel(List<Play> plays) {
		listOfPlays = plays;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return listOfPlays.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return listOfPlays.get(row);
	}

	@Override
	public String getColumnName(int col) {
		return "";
	}

	public boolean isCellEditable(int row, int col) { 
		return isEditable;
	}

	public void setEditable(boolean value) {
		isEditable = value;
	}

	public void setValueAt(Object value, int row, int col) {
		Play p = listOfPlays.get(row);
		p.setPlayName((String) value);
		fireTableCellUpdated(row, col);
	}

	public void setListOfPlays(List<Play> plays) {
		listOfPlays = plays;
	}

}
