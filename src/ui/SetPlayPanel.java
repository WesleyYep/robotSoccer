package ui;

import controllers.FieldController;
import data.PlaysTableModel;
import net.miginfocom.swing.MigLayout;
import strategy.CurrentStrategy;
import strategy.Play;
import strategy.StrategyListener;

import javax.swing.*;
import java.awt.*;

/**
 * Created by chan743 on 10/09/2015.
 */
public class SetPlayPanel extends JPanel implements StrategyListener {

    CurrentStrategy currentStrategy;
    FieldController fc;

    public SetPlayPanel(CurrentStrategy currentStrategy, FieldController fieldController) {
        this.currentStrategy = currentStrategy;
        this.fc = fieldController;
        this.setLayout(new MigLayout());

        JList<Play> moveSetPlayList = new JList<>();
        JList<Play> kickOffSetPlayList = new JList<>();
        JList<Play> penaltySetPlayList = new JList<>();
        JList<Play> freeBallSetPlayList = new JList<>();
        JList<Play> goalKickSetPlayList = new JList<>();

        JList[] playLists = new JList[] {moveSetPlayList, kickOffSetPlayList, penaltySetPlayList, freeBallSetPlayList, goalKickSetPlayList };
        String[] playListNames = new String[] {"Move to", "Kick Off", "Penalty", "Free Ball", "Goal Kick" };

        add(new JLabel("Set Plays"), "wrap 20");

        for (int i = 0; i < playLists.length; i++) {
            JList<Play> playList = playLists[i];
            add(new JLabel(playListNames[i]), "wrap");
            JScrollPane setPlaysScrollPane = new JScrollPane(playList);
            setPlaysScrollPane.setPreferredSize(new Dimension(300, 100));
            add(setPlaysScrollPane, "wrap 20");
        }

    }

    @Override
    public void strategyChanged() {

    }

    @Override
    public void setPlayChanged(Play setPlay) {

    }
}
