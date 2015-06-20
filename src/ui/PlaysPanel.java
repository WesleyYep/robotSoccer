package ui;

import data.PlaysTableModel;
import net.miginfocom.swing.MigLayout;
import org.opencv.core.*;
import org.opencv.core.Point;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private JButton saveMappingButton = new JButton("Save Mapping");
    private JComboBox roleA = new JComboBox();
    private JComboBox roleB = new JComboBox();
    private JComboBox roleC = new JComboBox();
    private JComboBox roleD = new JComboBox();
    private JComboBox roleE = new JComboBox();
//    private JComboBox playComboA = new JComboBox(new String[] {"Permanent", "Closest to Ball", "Closest to Spot", "Rest"});
//    private JComboBox playComboB = new JComboBox(new String[] {"Permanent", "Closest to Ball", "Closest to Spot", "Rest"});
//    private JComboBox playComboC = new JComboBox(new String[] {"Permanent", "Closest to Ball", "Closest to Spot", "Rest"});
//    private JComboBox playComboD = new JComboBox(new String[] {"Permanent", "Closest to Ball", "Closest to Spot", "Rest"});
//    private JComboBox playComboE = new JComboBox(new String[] {"Permanent", "Closest to Ball", "Closest to Spot", "Rest"});
    JTextField robotAField = new JTextField("1");
    JTextField robotBField = new JTextField("2");
    JTextField robotCField = new JTextField("3");
    JTextField robotDField = new JTextField("4");
    JTextField robotEField = new JTextField("5");
    private List<Play> playsList = new ArrayList<Play>();
    private CurrentStrategy currentStrategy;
    private Play lastSelectedPlay;
    private boolean updating = false;

    public PlaysPanel(final CurrentStrategy currentStrategy) {
        this.setLayout(new MigLayout());

        this.currentStrategy = currentStrategy;
        currentStrategy.addListener(this);

        playsTableModel = new PlaysTableModel(playsList);
        allPlaysTable = new JTable(playsTableModel);
        playsScrollPane = new JScrollPane(allPlaysTable);
        playsScrollPane.setPreferredSize(new Dimension(300, 100));
        roleA.setPreferredSize(new Dimension(200, 10));
        roleB.setPreferredSize(new Dimension(200, 10));
        roleC.setPreferredSize(new Dimension(200, 10));
        roleD.setPreferredSize(new Dimension(200, 10));
        roleE.setPreferredSize(new Dimension(200, 10));
//        playComboA.setPreferredSize(new Dimension(200, 10));
//        playComboB.setPreferredSize(new Dimension(200, 10));
//        playComboC.setPreferredSize(new Dimension(200, 10));
//        playComboD.setPreferredSize(new Dimension(200, 10));
//        playComboE.setPreferredSize(new Dimension(200, 10));

        add(new JLabel("Plays"), "wrap");
        add(playsScrollPane, "wrap");
        add(addButton, "split 2");
        add(removeButton, "wrap");
        add(new JLabel("Robot A:"), "split 2");
       // add(playComboA, "split 2");
        add(roleA, "wrap, span");
        add(new JLabel("Robot B:"), "split 2");
        //add(playComboB, "split 2");
        add(roleB, "wrap, span");
        add(new JLabel("Robot C:"), "split 2");
        //add(playComboC, "split 2");
        add(roleC, "wrap, span");
        add(new JLabel("Robot D:"), "split 2");
        //add(playComboD, "split 2");
        add(roleD, "wrap, span");
        add(new JLabel("Robot E:"), "split 2");
        //add(playComboE, "split 2");
        add(roleE, "wrap, span");
        add(saveButton, "wrap");
        add(new JLabel("Robot Mapping"), "wrap");
        add(new JLabel("A: "), "split 4");
        add(robotAField, "width 100");
        add(new JLabel("B: "));
        add(robotBField, "width 100, wrap");
        add(new JLabel("C: "), "split 4");
        add(robotCField, "width 100");
        add(new JLabel("D: "));
        add(robotDField, "width 100, wrap");
        add(new JLabel("E: "), "split 4");
        add(robotEField, "width 100");
        add(saveMappingButton, "span 2");

        allPlaysTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Play play = new Play();
                play.setPlayName("new play");
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
                if (roleA.getSelectedItem() != null) {
                    play.addRole(0, (Role) roleA.getSelectedItem());
                }
                if (roleB.getSelectedItem() != null) {
                    play.addRole(1, (Role) roleB.getSelectedItem());
                }
                if (roleC.getSelectedItem() != null) {
                    play.addRole(2, (Role) roleC.getSelectedItem());
                }
                if (roleD.getSelectedItem() != null) {
                    play.addRole(3, (Role) roleD.getSelectedItem());
                }
                if (roleE.getSelectedItem() != null) {
                    play.addRole(4, (Role) roleE.getSelectedItem());
                }
            }
        });

        saveMappingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ex) {
                int a = Integer.parseInt(robotAField.getText());
                int b = Integer.parseInt(robotBField.getText());
                int c = Integer.parseInt(robotCField.getText());
                int d = Integer.parseInt(robotDField.getText());
                int e = Integer.parseInt(robotEField.getText());

                currentStrategy.changeMapping(a, b, c, d, e);
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
                    Role[] roles = play.getRoles();
                    roleA.setSelectedItem(roles[0]);
                    roleB.setSelectedItem(roles[1]);
                    roleC.setSelectedItem(roles[2]);
                    roleD.setSelectedItem(roles[3]);
                    roleE.setSelectedItem(roles[4]);
//                    Point[] criteriaPoints = play.getPlayCriterias();
//                    setComboBox(playComboA, criteriaPoints, 0);
//                    setComboBox(playComboB, criteriaPoints, 1);
//                    setComboBox(playComboC, criteriaPoints, 2);
//                    setComboBox(playComboD, criteriaPoints, 3);
//                    setComboBox(playComboE, criteriaPoints, 4);
                }

            }
        });

    }

    @Override
    public void repaint() {
        if (currentStrategy == null) {
            return;
        }
        roleA.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            roleA.addItem(currentStrategy.getRoles().get(i));
        }
        roleB.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            roleB.addItem(currentStrategy.getRoles().get(i));
        }
        roleC.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            roleC.addItem(currentStrategy.getRoles().get(i));
        }
        roleD.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            roleD.addItem(currentStrategy.getRoles().get(i));
        }
        roleE.removeAllItems();
        for (int i = 0; i < currentStrategy.getRoles().size(); i++) {
            roleE.addItem(currentStrategy.getRoles().get(i));
        }
        if (lastSelectedPlay == null) {
            return;
        }
        for (int i = 0; i < lastSelectedPlay.getRoles().length; i++){
            roleA.setSelectedItem(lastSelectedPlay.getRoles()[0]);
            roleB.setSelectedItem(lastSelectedPlay.getRoles()[1]);
            roleC.setSelectedItem(lastSelectedPlay.getRoles()[2]);
            roleD.setSelectedItem(lastSelectedPlay.getRoles()[3]);
            roleE.setSelectedItem(lastSelectedPlay.getRoles()[4]);
        }
    }

    @Override
    public void strategyChanged() {
        playsList = currentStrategy.getPlays();
        playsTableModel.setListOfPlays(playsList);
        playsTableModel.fireTableDataChanged();
    }
}
