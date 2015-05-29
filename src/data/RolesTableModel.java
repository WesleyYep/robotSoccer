package data;

import strategy.Role;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class RolesTableModel extends AbstractTableModel {

	private List<Role> listOfRoles;

	public RolesTableModel(List<Role> list) {
		listOfRoles = list;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return listOfRoles.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return listOfRoles.get(row);
	}

	@Override
	public String getColumnName(int col) {
		return "";
	}

	public boolean isCellEditable(int row, int col) {
		return true;
	}

	public void setValueAt(Object value, int row, int col) {
		Role r = listOfRoles.get(row);
		r.setRoleName((String)value);
		fireTableCellUpdated(row, col);
	}

	public void setListOfRoles(List<Role> roles) {
		listOfRoles = roles;
	}

}
