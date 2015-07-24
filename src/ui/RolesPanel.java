package ui;

import actions.ActionFactory;
import actions.Actions;
import controllers.FieldController;
import criteria.CriteriaFactory;
import criteria.Criterias;
import data.CriteriaActionTableModel;
import data.RolesTableModel;
import net.miginfocom.swing.MigLayout;
import strategy.Action;
import strategy.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.FocusListener;
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

    private JComboBox<String> criteriaCombo;
    private JComboBox<String> actionCombo;
//    private JLabel paramLabel1 = new JLabel("Parameter 1:");
//    private JLabel paramLabel2 = new JLabel("Parameter 2:");
//    private JLabel paramLabel3 = new JLabel("Parameter 3:");
//    private JLabel paramLabel4 = new JLabel("Parameter 4:");
//    private JLabel[] parameterLabels = new JLabel[] { paramLabel1, paramLabel2, paramLabel3, paramLabel4 };
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
    private int criteriaActionLastSelectedIndex;

    private FieldController fieldController;

    public RolesPanel(final CurrentStrategy currentStrategy, FieldController fieldController) {

        this.currentStrategy = currentStrategy;
        currentStrategy.addListener(this);

        this.fieldController = fieldController;
        this.setLayout(new MigLayout());

        rolesTableModel = new RolesTableModel(rolesList);
        rolesTable = new JTable(rolesTableModel);
        scrollRoles = new JScrollPane(rolesTable);
        scrollRoles.setPreferredSize(new Dimension(300, 300));

        criteriaActionTableModel = new CriteriaActionTableModel();
        criteriaActionTable = new JTable(criteriaActionTableModel);

        // set up jcombobox
        // criteria
        List<String> criteriasList = Criterias.getCriterias();
        String[] critierias = criteriasList.toArray(new String[criteriasList.size()]);
        criteriaCombo = new JComboBox<>(critierias);
        DefaultCellEditor criteriaEditor = new DefaultCellEditor(criteriaCombo);
        criteriaEditor.setClickCountToStart(2);
        criteriaActionTable.getColumnModel().getColumn(0).setCellEditor(criteriaEditor);

        // actions
        List<String> actionsList = Actions.getActions();
        String[] actions = actionsList.toArray(new String[actionsList.size()]);
        actionCombo = new JComboBox<>(actions);
        DefaultCellEditor actionEditor = new DefaultCellEditor(actionCombo);
        actionEditor.setClickCountToStart(2);
        criteriaActionTable.getColumnModel().getColumn(1).setCellEditor(actionEditor);

        scrollCriteriaActions = new JScrollPane(criteriaActionTable);
        criteriaActionTable.setPreferredScrollableViewportSize(criteriaActionTable.getPreferredSize());
        criteriaActionTable.setFillsViewportHeight(true);

        add(new JLabel("Roles"), "wrap");
        add(scrollRoles, "wrap");
        add(addButton, "split 2");
        add(removeButton, "wrap");

        add(scrollCriteriaActions, "w 250:300:max, wrap");

        //add(paramLabel1, "split 2");
        add(input1, "wrap");
        //add(paramLabel2, "split 2");
        add(input2, "wrap");
        //add(paramLabel3, "split 2");
        add(input3, "wrap");
        //add(paramLabel4, "split 2");
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
//                for (int i = 0; i < 4; i++) {
//                    if (!inputs[i].getText().equals("")) {
//                        try {
//                            //lastSelectedAction.updateParameters(parameterLabels[i].getText(), Integer.parseInt(inputs[i].getText()));
//                        } catch (NumberFormatException ex) {
//                            //System.out.println("Invalid input for parameter " + parameterLabels[i].getText());
//                        }
//                    }
//                }

                Role role = lastSelectedRole;

                if (role == null) {
                    return;
                }

                for (int i = 0; i < criteriaActionTableModel.getRowCount(); i++) {
                    String criteriaSimpleName = (String) criteriaActionTableModel.getValueAt(i, 0);
                    String actionSimpleName = (String) criteriaActionTableModel.getValueAt(i, 1);

                    if (criteriaSimpleName == null || actionSimpleName == null) {
                        continue;
                    }

                    Criteria criteria = CriteriaFactory.getCriteria(criteriaSimpleName);
                    Action action = ActionFactory.getAction(role, actionSimpleName);
                    role.setPair(criteria, action, i);
                }

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
                    Action[] actions = role.getActions();
                    Criteria[] criterias = role.getCriterias();
                    lastSelectedRole = role;

                    // update criteriaactiontable model
                    for (int i = 0; i < CriteriaActionTableModel.ROWCOUNT; i++) {
                        try {
                            criteriaActionTableModel.setValueAt(criterias[i].getClass().getSimpleName(), i, 0);
                            criteriaActionTableModel.setValueAt(actions[i].getClass().getSimpleName(), i, 1);
                        } catch (NullPointerException ex) {
                            criteriaActionTableModel.setValueAt(null, i, 0);
                            criteriaActionTableModel.setValueAt(null, i, 1);
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
                    try {
                        criteriaActionLastSelectedIndex = lsm.getMinSelectionIndex();
                        // get action string
                        String actionString = (String)criteriaActionTableModel.getValueAt(criteriaActionLastSelectedIndex, 1);

                        if (actionString == null) {
                            setDrawAction(false);
                            return;
                        }

                        Action action = ActionFactory.getAction(lastSelectedRole, actionString);

                        lastSelectedAction = action;

                        updateAction(lastSelectedAction);
                    } catch (NullPointerException ex) {
                        //do nothing - there is no minimum number of criteria action pairs
                    }
                }
            }
        });

        criteriaActionTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (criteriaActionTable.getSelectedRow() == -1) {
                    return;
                }

                // get action string
                String actionString = (String)criteriaActionTableModel.getValueAt(criteriaActionLastSelectedIndex, 1);

                if (actionString == null) {
                    setDrawAction(false);
                    return;
                }

                Action action = ActionFactory.getAction(lastSelectedRole, actionString);

                lastSelectedAction = action;

                updateAction(lastSelectedAction);
            }
        });

        criteriaActionTable.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
//                if (lastSelectedAction == null) {
//                    setDrawAction(false);
//                } else {
//                    setDrawAction(true);
//                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                criteriaActionTable.clearSelection();
                setDrawAction(false);
            }
        });
    }

    @Override
    public void strategyChanged() {
        rolesList = currentStrategy.getRoles();
        rolesTableModel.setListOfRoles(rolesList);
        rolesTableModel.fireTableDataChanged();
        for (int i = 0; i < 4; i++) {
            //parameterLabels[i].setText("Parameter " + (i + 1) + ":");
            inputs[i].setText("");
        }
    }

    @Override
    public void setPlayChanged(Play setPlay) {
        //nothing
    }

    public void updateAction(Action action) {
        setDrawAction(true);
        fieldController.setAction(action);
    }

    public void setDrawAction(boolean draw) {
        fieldController.setDrawAction(draw);
    }
}
