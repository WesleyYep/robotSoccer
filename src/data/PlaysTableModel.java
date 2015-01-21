package data;

import strategy.Play;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class PlaysTableModel extends AbstractTableModel {

	//for now the model we use the list of area, later on we could create
	//situation object which contain the area and other information relate to the situations

	private ArrayList<Play> listOfPlays;

	public PlaysTableModel(ArrayList<Play> list) {
		listOfPlays = list;
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
	
	public String getColumnName(int col) {
			return "";
    }
	
	
	public boolean isCellEditable(int row, int col) { 
	    return true; 
	}
	
	public void setValueAt(Object value, int row, int col) {
	    Play p = listOfPlays.get(row);
	    p.setSituationName((String)value);
	    fireTableCellUpdated(row, col);
	    
	 }

}
