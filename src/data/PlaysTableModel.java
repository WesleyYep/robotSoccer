package data;

import strategy.Play;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaysTableModel extends AbstractTableModel {

	private List<Play> listOfPlays;
	private boolean isEditable = true;

	public PlaysTableModel(List<Play> plays) {
		listOfPlays = plays;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return listOfPlays.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
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
