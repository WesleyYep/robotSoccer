package ui;

import data.PlaysTableModel;
import net.miginfocom.swing.MigLayout;
import strategy.CurrentStrategy;
import strategy.Play;
import strategy.Role;
import strategy.StrategyListener;

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
public class PlaysPanel extends JPanel implements StrategyListener{

    private PlaysTableModel playsTableModel;
    private JTable allPlaysTable;
    private JScrollPane playsScrollPane;
    private JButton addButton = new JButton("New");
    private JButton removeButton = new JButton("Remove");
    private JButton saveButton = new JButton("Save");
    private JComboBox role1 = new JComboBox();
    private JComboBox role2 = new JComboBox();
    private JComboBox role3 = new JComboBox();
    private JComboBox role4 = new JComboBox();
    private JComboBox role5 = new JComboBox();
    private List<Play> playsList = new ArrayList<Play>();
    private CurrentStrategy currentStrategy;
    private Play lastSelectedPlay;

    public PlaysPanel(final CurrentStrategy currentStrategy) {
        this.setLayout(new MigLayout());

        this.currentStrategy = currentStrategy;
        currentStrategy.addListener(this);

        playsTableModel = new PlaysTableModel(playsList);
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
        add(addButton, "split 2");
        add(removeButton, "wrap");
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

        allPlaysTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Play play = new Play();
                play.setPlayName("test");
                playsList.add(play);
                lastSelectedPlay = play;
                currentStrategy.setPlays(playsList);
                playsTableModel.fireTableDataChanged();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playsList.remove(lastSelectedPlay);
                currentStrategy.setPlays(playsList);
                playsTableModel.fireTableDataChanged();
            }
        });


        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Play play = lastSelectedPlay;
                if (role1.getSelectedItem() != null) {
                    play.addRole(0, (Role)role1.getSelectedItem());
                }
                if (role1.getSelectedItem() != null) {
                    play.addRole(1, (Role)role2.getSelectedItem());
                }
                if (role1.getSelectedItem() != null) {
                    play.addRole(2, (Role)role3.getSelectedItem());
                }
                if (role1.getSelectedItem() != null) {
                    play.addRole(3, (Role)role4.getSelectedItem());
                }
                if (role1.getSelectedItem() != null) {
                    play.addRole(4, (Role)role5.getSelectedItem());
                }

            }
        });

        ListSelectionModel playsRows = allPlaysTable.getSelectionModel();
        playsRows.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                //ignore extra messages
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {

                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    Play play = (Play)playsTableModel.getValueAt(selectedRow, 0);
                    lastSelectedPlay = play;
                    for (int i = 0; i < play.getRoles().length; i++){
                        role1.setSelectedItem(play.getRoles()[0]);
                        role2.setSelectedItem(play.getRoles()[1]);
                        role3.setSelectedItem(play.getRoles()[2]);
                        role4.setSelectedItem(play.getRoles()[3]);
                        role5.setSelectedItem(play.getRoles()[4]);
                    }
                }

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
        if (lastSelectedPlay == null) {
            return;
        }
        for (int i = 0; i < lastSelectedPlay.getRoles().length; i++){
            role1.setSelectedItem(lastSelectedPlay.getRoles()[0]);
            role2.setSelectedItem(lastSelectedPlay.getRoles()[1]);
            role3.setSelectedItem(lastSelectedPlay.getRoles()[2]);
            role4.setSelectedItem(lastSelectedPlay.getRoles()[3]);
            role5.setSelectedItem(lastSelectedPlay.getRoles()[4]);
        }
    }

    @Override
    public void strategyChanged() {
        playsList = currentStrategy.getPlays();
        playsTableModel.setListOfPlays(playsList);
        playsTableModel.fireTableDataChanged();
    }
}
