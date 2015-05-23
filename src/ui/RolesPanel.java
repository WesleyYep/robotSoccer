package ui;

import actions.Actions;
import criteria.Criterias;
import data.CriteriaActionTableModel;
import data.RolesTableModel;
import net.miginfocom.swing.MigLayout;
import strategy.Action;
import strategy.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Wesley on 21/01/2015.
 */
public class RolesPanel extends JPanel implements StrategyListener {

    private RolesTableModel rolesTableModel;
    private JTable rolesTable;
    private JScrollPane scrollRoles;

    private CriteriaActionTableModel criteriaActionTableModel;
    private JTable criteriaActionTable;
    private JScrollPane scrollCriteriaActions;

    private JButton addButton = new JButton("Add");
    private JButton removeButton = new JButton("Remove");

    private JComboBox<Criteria> criteria1;
    private JComboBox<Action> action1;
    private JLabel paramLabel1 = new JLabel("Parameter 1:");
    private JLabel paramLabel2 = new JLabel("Parameter 2:");
    private JLabel paramLabel3 = new JLabel("Parameter 3:");
    private JLabel paramLabel4 = new JLabel("Parameter 4:");
    private JLabel[] parameterLabels = new JLabel[] { paramLabel1, paramLabel2, paramLabel3, paramLabel4 };
    private JTextField input1 = new JTextField(100);
    private JTextField input2 = new JTextField(100);
    private JTextField input3 = new JTextField(100);
    private JTextField input4 = new JTextField(100);
    private JTextField[] inputs = new JTextField[]{ input1, input2, input3, input4 };


    private JButton saveButton = new JButton("Save");
    private List<Role> rolesList = new ArrayList<Role>();
    private CurrentStrategy currentStrategy;

    private Role lastSelectedRole;
    private Action lastSelectedAction;

    public RolesPanel(final CurrentStrategy currentStrategy) {

        this.currentStrategy = currentStrategy;
        currentStrategy.addListener(this);

        this.setLayout(new MigLayout());

        rolesTableModel = new RolesTableModel(rolesList);
        rolesTable = new JTable(rolesTableModel);
        scrollRoles = new JScrollPane(rolesTable);
        scrollRoles.setPreferredSize(new Dimension(300, 300));

        criteriaActionTableModel = new CriteriaActionTableModel();
        criteriaActionTable = new JTable(criteriaActionTableModel);
        scrollCriteriaActions = new JScrollPane(criteriaActionTable);
        scrollCriteriaActions.setPreferredSize(new Dimension(500, 120));

        (criteria1 = new JComboBox<Criteria>()).setPreferredSize(new Dimension(140, 10));
        (action1 = new JComboBox<Action>()).setPreferredSize(new Dimension(140, 10));

        setComboBoxes();

        add(new JLabel("Roles"), "wrap");
        add(scrollRoles, "wrap");
        add(addButton, "split 2");
        add(removeButton, "wrap");

        TableColumn col = criteriaActionTable.getColumnModel().getColumn(0);
        col.setCellEditor( new DefaultCellEditor(criteria1));

        TableColumn col1 = criteriaActionTable.getColumnModel().getColumn(1);
        col1.setCellEditor( new DefaultCellEditor(action1));

        add(scrollCriteriaActions, "wrap");

        add(paramLabel1, "split 2");
        add(input1, "wrap");
        add(paramLabel2, "split 2");
        add(input2, "wrap");
        add(paramLabel3, "split 2");
        add(input3, "wrap");
        add(paramLabel4, "split 2");
        add(input4, "wrap");

        add(saveButton);

        rolesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Role role = new Role();
                role.setRoleName("new role");
                rolesList.add(role);
                lastSelectedRole = role;
                currentStrategy.setRoles(rolesList);
                rolesTableModel.fireTableDataChanged();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rolesList.remove(lastSelectedRole);
                currentStrategy.setRoles(rolesList);
                rolesTableModel.fireTableDataChanged();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 4; i++) {
                    if (!inputs[i].getText().equals("")) {
                        try {
                            lastSelectedAction.updateParameters(parameterLabels[i].getText(), Integer.parseInt(inputs[i].getText()));
                        }catch (NumberFormatException ex) {
                            System.out.println("Invalid input for parameter " + parameterLabels[i].getText());
                        }
                    }
                }
                Role role = lastSelectedRole;
                criteriaActionTableModel.getValueAt(0, 0);
                role.setPair((Criteria) criteriaActionTableModel.getValueAt(0, 0), (Action) criteriaActionTableModel.getValueAt(0, 1), 0);
                role.setPair((Criteria) criteriaActionTableModel.getValueAt(1, 0), (Action)criteriaActionTableModel.getValueAt(1, 1), 1);
                role.setPair((Criteria) criteriaActionTableModel.getValueAt(2, 0), (Action)criteriaActionTableModel.getValueAt(2, 1), 2);
                role.setPair((Criteria) criteriaActionTableModel.getValueAt(3, 0), (Action)criteriaActionTableModel.getValueAt(3, 1), 3);
                role.setPair((Criteria) criteriaActionTableModel.getValueAt(4, 0), (Action)criteriaActionTableModel.getValueAt(4, 1), 4);
            }
        });

        ListSelectionModel rolesRows = rolesTable.getSelectionModel();
        rolesRows.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //ignore extra messages
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {

                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    Role role = (Role) rolesTableModel.getValueAt(selectedRow, 0);
                    lastSelectedRole = role;
                    for (int i = 0; i < role.getActions().length; i++) {
                        for (int j = 0; j < 5; j++) {
                            criteriaActionTableModel.setValueAt(role.getCriterias()[j], j, 0);
                            criteriaActionTableModel.setValueAt(role.getActions()[j], j, 1);
                        }

                    }
                }

            }
        });

        ListSelectionModel actionsRows = criteriaActionTable.getSelectionModel();
        actionsRows.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //ignore extra messages
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {

                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    Action action = (Action) criteriaActionTable.getValueAt(selectedRow, 1);
                    lastSelectedAction = action;
                    List<String> keys = new ArrayList<String>(action.getParameters());
                    List<Integer> values = new ArrayList<Integer>(action.getValues());
                    for (int i = 0; i < keys.size(); i++) {
                        parameterLabels[i].setText(keys.get(i) + "");
                        inputs[i].setText(values.get(i) + "");
                    }
                    //now fill the rest with defaults
                    for (int i = keys.size(); i < 4; i++) {
                        parameterLabels[i].setText("Parameter " + (i + 1) + ":");
                        inputs[i].setText("");
                    }
                }
            }
        });
    }

    private void setComboBoxes() {
        Criterias criterias = new Criterias();
        for (int i = 0; i < criterias.getLength(); i++) {
            criteria1.addItem(criterias.getAction(i));
        }
        List<Action> actions = Actions.getActions();
        for (Action a: actions) {
            action1.addItem(a);
        }
    }

    @Override
    public void strategyChanged() {
        rolesList = currentStrategy.getRoles();
        rolesTableModel.setListOfRoles(rolesList);
        rolesTableModel.fireTableDataChanged();
    }

}
