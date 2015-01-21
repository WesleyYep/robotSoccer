package ui;

import actions.Actions;
import criteria.Criterias;
import data.RolesTableModel;
import net.miginfocom.swing.MigLayout;
import strategy.*;
import strategy.Action;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Wesley on 21/01/2015.
 */
public class RolesPanel extends JPanel {

    private RolesTableModel rolesTableModel;
    private JTable rolesTable;
    private JScrollPane scrollRoles;
    private JButton addButton = new JButton("Add");
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

    public RolesPanel() {

        this.setLayout(new MigLayout());

        rolesTableModel = new RolesTableModel(new ArrayList<Role>());
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

        add(new JLabel("Roles"), "wrap");
        add(scrollRoles, "wrap");
        add(addButton, "wrap");

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


    }

}
