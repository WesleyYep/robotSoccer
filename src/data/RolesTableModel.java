package data;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import strategy.Role;
import ui.SituationArea;

public class RolesTableModel extends AbstractTableModel {

    private ArrayList<Role> listOfRoles;

    public RolesTableModel(ArrayList<Role> list) {
        listOfRoles = list;
    }

    @Override
    public int getColumnCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public int getRowCount() {
        // TODO Auto-generated method stub
        return listOfRoles.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        // TODO Auto-generated method stub
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

}
