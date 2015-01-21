package data;

import strategy.Play;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PlaysTableModel extends AbstractTableModel {

	private static List<Play> listOfPlays;
	private List<Play> listOfSomePlays;
	private List<Play> listToUse;

	public PlaysTableModel(List<Play> plays) {
		listOfSomePlays = plays;
		listToUse = listOfSomePlays;
	}

	public PlaysTableModel() {
		if (listOfPlays == null) {
			listOfPlays = new ArrayList<Play>();
		}
		listToUse = listOfPlays;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return listToUse.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		return listToUse.get(row);
	}

	@Override
	public String getColumnName(int col) {
			return "";
    }

	
	public boolean isCellEditable(int row, int col) { 
	    return true; 
	}
	
	public void setValueAt(Object value, int row, int col) {
	    Play p = listToUse.get(row);
	    p.setPlayName((String) value);
	    fireTableCellUpdated(row, col);
	    
	 }

}
