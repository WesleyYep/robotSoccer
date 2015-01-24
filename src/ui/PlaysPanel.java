package ui;

import data.PlaysTableModel;
import net.miginfocom.swing.MigLayout;
import strategy.CurrentStrategy;
import strategy.Play;
import strategy.Role;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class PlaysPanel extends JPanel{

    private PlaysTableModel playsTableModel;
    private JTable allPlaysTable;
    private JScrollPane playsScrollPane;
    private JButton newButton = new JButton("New");
    private JButton saveButton = new JButton("Save");
    private JComboBox role1 = new JComboBox();
    private JComboBox role2 = new JComboBox();
    private JComboBox role3 = new JComboBox();
    private JComboBox role4 = new JComboBox();
    private JComboBox role5 = new JComboBox();
    private List<Play> plays = new ArrayList<Play>();
    private CurrentStrategy currentStrategy;

    public PlaysPanel(final CurrentStrategy currentStrategy) {
        this.setLayout(new MigLayout());

        this.currentStrategy = currentStrategy;

        playsTableModel = new PlaysTableModel(plays);
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
        add(saveButton, "wrap");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Play play = new Play();
                play.addRole(0, (Role)role1.getSelectedItem());
                play.addRole(1, (Role)role2.getSelectedItem());
                play.addRole(2, (Role)role3.getSelectedItem());
                play.addRole(3, (Role)role4.getSelectedItem());
                play.addRole(4, (Role)role5.getSelectedItem());
                plays.add(play);

                currentStrategy.setPlays(plays);
            }
        });
    }


    @Override
    public void repaint() {
        if (currentStrategy == null) {
            return;
        }
        role1.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            role1.addItem(currentStrategy.getRoles().get(i));
        }
        role2.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            role2.addItem(currentStrategy.getRoles().get(i));
        }
        role3.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            role3.addItem(currentStrategy.getRoles().get(i));
        }
        role4.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            role4.addItem(currentStrategy.getRoles().get(i));
        }
        role5.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            role5.addItem(currentStrategy.getRoles().get(i));
        }
    }

}
