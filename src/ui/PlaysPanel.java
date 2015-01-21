package ui;

import data.PlaysTableModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Wesley on 21/01/2015.
 */
public class PlaysPanel extends JPanel{

    private PlaysTableModel playsTableModel;
    private JTable allPlaysTable;
    private JScrollPane playsScrollPane;
    private JButton newButton = new JButton("New");
//    private JButton saveButton = new JButton("Save");
    private JComboBox role1 = new JComboBox();
    private JComboBox role2 = new JComboBox();
    private JComboBox role3 = new JComboBox();
    private JComboBox role4 = new JComboBox();
    private JComboBox role5 = new JComboBox();

    public PlaysPanel() {
        this.setLayout(new MigLayout());

        playsTableModel = new PlaysTableModel();
        allPlaysTable = new JTable(playsTableModel);
        playsScrollPane = new JScrollPane(allPlaysTable);
        playsScrollPane.setPreferredSize(new Dimension(300, 100));
        role1.setPreferredSize(new Dimension(200, 10));
        role2.setPreferredSize(new Dimension(200, 10));
        role3.setPreferredSize(new Dimension(200, 10));
        role4.setPreferredSize(new Dimension(200, 10));
        role5.setPreferredSize(new Dimension(200, 10));

        add(new JLabel("Plays"), "wrap");
        add(playsScrollPane, "wrap");
        add(newButton, "wrap");
        add(new JLabel("Role 1:"), "split 2");
        add(role1, "wrap, span");
        add(new JLabel("Role 2:"), "split 2");
        add(role2, "wrap, span");
        add(new JLabel("Role 3:"), "split 2");
        add(role3, "wrap, span");
        add(new JLabel("Role 4:"), "split 2");
        add(role4, "wrap, span");
        add(new JLabel("Role 5:"), "split 2");
        add(role5, "wrap, span");
    }

}
