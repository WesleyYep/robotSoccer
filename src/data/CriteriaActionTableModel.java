package data;

import actions.Actions;
import criteria.Criterias;
import strategy.Action;
import strategy.Criteria;

import javax.swing.table.DefaultTableModel;

public class CriteriaActionTableModel extends DefaultTableModel {
    private Action[] actions = Actions.getActions();
    private Criteria[] criterias = Criterias.getCriterias();
    private String[] colNames = { "Criteria", "Action" };
    private Object[][] data = { { criterias[0], actions[0] },
            { criterias[0], actions[0]  },
            { criterias[0], actions[0]  },
            { criterias[0], actions[0]  },
            { criterias[0], actions[0]  } };

    public CriteriaActionTableModel() {
        setDataVector( data, colNames );
    }


}