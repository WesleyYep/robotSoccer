package data;

import actions.Actions;
import criteria.Criterias;
import strategy.Action;
import strategy.Criteria;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CriteriaActionTableModel extends DefaultTableModel {
    private static final String[] COLNAMES = { "Criteria", "Action" };
    private static final int ROWCOUNT = 5;

    public CriteriaActionTableModel() {
        super(COLNAMES, ROWCOUNT);
    }

}