package ui;

import actions.Actions;
import criteria.Criterias;
import data.RolesTableModel;
import data.Situation;
import net.miginfocom.swing.MigLayout;
import strategy.*;
import strategy.Action;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class RolesPanel extends JPanel implements StrategyListener {

    private RolesTableModel rolesTableModel;
    private JTable rolesTable;
    private JScrollPane scrollRoles;
    private JButton addButton = new JButton("Add");
    private JButton removeButton = new JButton("Remove");
    private JComboBox<Criteria> criteria1;
    private JComboBox<Criteria> criteria2;
    private JComboBox<Criteria> criteria3;
    private JComboBox<Criteria> criteria4;
    private JComboBox<Criteria> criteria5;
    private JComboBox<Action> action1;
    private JComboBox<Action> action2;
    private JComboBox<Action> action3;
    private JComboBox<Action> action4;
    private JComboBox<Action> action5;
    private JButton saveButton = new JButton("Save");
    private List<Role> rolesList = new ArrayList<Role>();
    private CurrentStrategy currentStrategy;

    private Role lastSelectedRole;

    public RolesPanel(final CurrentStrategy currentStrategy) {

        this.currentStrategy = currentStrategy;
        currentStrategy.addListener(this);

        this.setLayout(new MigLayout());

        rolesTableModel = new RolesTableModel(rolesList);
        rolesTable = new JTable(rolesTableModel);
        scrollRoles = new JScrollPane(rolesTable);
        scrollRoles.setPreferredSize(new Dimension(300, 100));

        (criteria1 = new JComboBox<Criteria>()).setPreferredSize(new Dimension(140, 10));
        (criteria2 = new JComboBox<Criteria>()).setPreferredSize(new Dimension(140,10));
        (criteria3 = new JComboBox<Criteria>()).setPreferredSize(new Dimension(140,10));
        (criteria4 = new JComboBox<Criteria>()).setPreferredSize(new Dimension(140,10));
        (criteria5 = new JComboBox<Criteria>()).setPreferredSize(new Dimension(140,10));

        (action1 = new JComboBox<Action>()).setPreferredSize(new Dimension(140, 10));
        (action2 = new JComboBox<Action>()).setPreferredSize(new Dimension(140, 10));
        (action3 = new JComboBox<Action>()).setPreferredSize(new Dimension(140, 10));
        (action4 = new JComboBox<Action>()).setPreferredSize(new Dimension(140, 10));
        (action5 = new JComboBox<Action>()).setPreferredSize(new Dimension(140, 10));

        setComboBoxes();

        add(new JLabel("Roles"), "wrap");
        add(scrollRoles, "wrap");
        add(addButton, "split 2");
        add(removeButton, "wrap");

        add(new JLabel("Criteria"), "split 2");
        add(new JLabel("Action"), "gapleft 100, wrap");

        add(criteria1, "split 2");
        add(action1, "wrap");
        add(criteria2, "split 2");
        add(action2, "wrap");
        add(criteria3, "split 2");
        add(action3, "wrap");
        add(criteria4, "split 2");
        add(action4, "wrap");
        add(criteria5, "split 2");
        add(action5, "wrap");
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
                Role role = lastSelectedRole;
                if (criteria1.getSelectedItem() != null && action1.getSelectedItem() != null) {
                    role.setPair((Criteria) criteria1.getSelectedItem(), (Action) action1.getSelectedItem(), 0);
                }
                if (criteria2.getSelectedItem() != null && action2.getSelectedItem() != null) {
                    role.setPair((Criteria) criteria2.getSelectedItem(), (Action) action2.getSelectedItem(), 1);
                }
                if (criteria3.getSelectedItem() != null && action3.getSelectedItem() != null) {
                    role.setPair((Criteria) criteria3.getSelectedItem(), (Action) action3.getSelectedItem(), 2);
                }
                if (criteria4.getSelectedItem() != null && action4.getSelectedItem() != null) {
                    role.setPair((Criteria) criteria4.getSelectedItem(), (Action) action4.getSelectedItem(), 3);
                }
                if (criteria5.getSelectedItem() != null && action5.getSelectedItem() != null) {
                    role.setPair((Criteria) criteria5.getSelectedItem(), (Action) action5.getSelectedItem(), 4);
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
                    lastSelectedRole = role;
                    for (int i = 0; i < role.getActions().length; i++) {
                        action1.setSelectedItem(role.getActions()[0]);
                        action2.setSelectedItem(role.getActions()[1]);
                        action3.setSelectedItem(role.getActions()[2]);
                        action4.setSelectedItem(role.getActions()[3]);
                        action5.setSelectedItem(role.getActions()[4]);
                        criteria1.setSelectedItem(role.getCriterias()[0]);
                        criteria2.setSelectedItem(role.getCriterias()[1]);
                        criteria3.setSelectedItem(role.getCriterias()[2]);
                        criteria4.setSelectedItem(role.getCriterias()[3]);
                        criteria5.setSelectedItem(role.getCriterias()[4]);
                    }
                }

            }
        });
    }

    private void setComboBoxes() {
        Criterias criterias = new Criterias();
        for (int i = 0; i < criterias.getLength(); i++) {
            criteria1.addItem(criterias.getAction(i));
            criteria2.addItem(criterias.getAction(i));
            criteria3.addItem(criterias.getAction(i));
            criteria4.addItem(criterias.getAction(i));
            criteria5.addItem(criterias.getAction(i));
        }
        Actions actions = new Actions();
        for (int i = 0; i < actions.getLength(); i++) {
            action1.addItem(actions.getAction(i));
            action2.addItem(actions.getAction(i));
            action3.addItem(actions.getAction(i));
            action4.addItem(actions.getAction(i));
            action5.addItem(actions.getAction(i));
        }
    }

    @Override
    public void strategyChanged() {
        rolesList = currentStrategy.getRoles();
        rolesTableModel.setListOfRoles(rolesList);
        rolesTableModel.fireTableDataChanged();
    }

}
