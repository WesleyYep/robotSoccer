package ui;

import controllers.FieldController;
import data.PlaysTableModel;
import data.Situation;
import data.SituationTableModel;
import net.miginfocom.swing.MigLayout;
import strategy.CurrentStrategy;
import strategy.Play;
import strategy.StrategyListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SituationPanel extends JPanel implements StrategyListener{

	private List<Situation> listOfSituations;

	private JButton addButton;
	private JButton removeButton;
	private JButton addPlayButton = new JButton("Add play");
	private JButton removePlayButton = new JButton("Remove play");
	private JButton upButton = new JButton("Up");
	private JButton downButton = new JButton("Down");
	private JTable tableOfSituations;
	private JTable tableOfPlays;
	private JTable tableOfAllPlays;

	private SituationTableModel situationModel;
	private PlaysTableModel playsModel;
	private PlaysTableModel allPlaysModel;

	private JScrollPane scrollTable;
	private JScrollPane scrollTablePlays;
	private JScrollPane scrollTableAllPlays;

	private FieldController fieldController;

	private DrawAreaGlassPanel glassPanel;

	private CurrentStrategy currentStrategy;

	private Situation lastSelectedSituation;
	private Play lastSelectedPlay;
	private Play lastSelectedAddedPlay;


	public SituationPanel(FieldController fieldController, final CurrentStrategy currentStrategy) {
		this.fieldController = fieldController;
		this.setLayout(new MigLayout());
		this.currentStrategy = currentStrategy;
		currentStrategy.addListener(this);

		listOfSituations = new ArrayList<Situation>();
		situationModel = new SituationTableModel(listOfSituations);
		tableOfSituations = new JTable(situationModel);

		tableOfSituations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = tableOfSituations.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;

				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {

				} else {
					for (Situation s : listOfSituations) {
						s.setAreaActive(false);
					}
					int selectedRow = lsm.getMinSelectionIndex();
					Situation sit = ((Situation) situationModel.getValueAt(selectedRow, 0));
					sit.setAreaActive(true);
					playsModel.setListOfPlays(sit.getPlays());
					playsModel.fireTableDataChanged();
					lastSelectedSituation = sit;
					SituationPanel.this.fieldController.setSelectedArea(sit.getArea());
				}
			}
		});

		removeButton = new JButton("Remove");
		addButton = new JButton("Add");

		scrollTable = new JScrollPane(tableOfSituations);
		scrollTable.setPreferredSize(new Dimension(300, 100));

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);

		JPanel playsPanel = new JPanel(new MigLayout());
		playsModel = new PlaysTableModel(new ArrayList<Play>());
		playsModel.setEditable(false);
		allPlaysModel = new PlaysTableModel(currentStrategy.getPlays());
		tableOfPlays = new JTable(playsModel);
		tableOfAllPlays = new JTable(allPlaysModel);
		scrollTablePlays = new JScrollPane((tableOfPlays));
		scrollTablePlays.setPreferredSize(new Dimension(300, 100));
		scrollTableAllPlays = new JScrollPane((tableOfAllPlays));
		scrollTableAllPlays.setPreferredSize(new Dimension(300, 300));
		playsPanel.add(new JLabel("Plays in situation"), "wrap");
		playsPanel.add(scrollTablePlays, "pushx, growx, wrap, span");
		playsPanel.add(upButton, "split 2");
		playsPanel.add(downButton, "wrap");
		playsPanel.add(new JLabel("All Plays"), "wrap");
		playsPanel.add(scrollTableAllPlays, "pushx, growx, wrap, span");
		playsPanel.add(addPlayButton, "split 2");
		playsPanel.add(removePlayButton, "wrap");

		tableOfAllPlays.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM2 = tableOfAllPlays.getSelectionModel();
		rowSM2.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;

				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {}
				else {
					int selectedRow = lsm.getMinSelectionIndex();
					Play p = ((Play) allPlaysModel.getValueAt(selectedRow, 0));
					lastSelectedPlay = p;
                    currentStrategy.setSetPlay(lastSelectedPlay);
                    System.out.println("Set play: " + lastSelectedPlay.toString());
				}
			}
		});

		this.add(scrollTable, "pushx, alignx 50%, w 70%, wmax 500, wrap");
		this.add(buttonPanel, "alignx 50%, wrap");
		this.add(playsPanel, "alignx 50%, w 70%, wmax 500, wrap");

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SituationPanel.this.fieldController.bringComponentToTheTop(glassPanel);
				glassPanel.setVisible(true);
			}

		});


		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = tableOfSituations.getSelectedRow();

				if (listOfSituations.size() > 0 && selectedRow >= 0  && selectedRow < listOfSituations.size()) {

					Situation removeSituation= ((Situation)situationModel.getValueAt(selectedRow, 0));

					listOfSituations.remove(removeSituation);

					SituationPanel.this.fieldController.removeArea(removeSituation.getArea());

					SituationPanel.this.fieldController.repaintField();
					situationModel.fireTableDataChanged();
					currentStrategy.setSituations(listOfSituations);
				}
			}

		});

		addPlayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lastSelectedSituation == null) {
					return;
				}
				lastSelectedSituation.addPlay(lastSelectedPlay);
				playsModel.setListOfPlays(lastSelectedSituation.getPlays());
				playsModel.fireTableDataChanged();
			}
		});

		removePlayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lastSelectedSituation == null) {
					return;
				}
				lastSelectedSituation.removePlay(lastSelectedAddedPlay);
				playsModel.setListOfPlays(lastSelectedSituation.getPlays());
				playsModel.fireTableDataChanged();
			}
		});

		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = lastSelectedSituation.getPlays().indexOf(lastSelectedAddedPlay);
				if (index != 0) {
					Collections.swap(lastSelectedSituation.getPlays(), index, index -1);
				}
				playsModel.setListOfPlays(lastSelectedSituation.getPlays());
				playsModel.fireTableDataChanged();
			}
		});

		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = lastSelectedSituation.getPlays().indexOf(lastSelectedAddedPlay);
				if (index != lastSelectedSituation.getPlays().size() - 1) {
					Collections.swap(lastSelectedSituation.getPlays(), index, index + 1);
				}
				playsModel.setListOfPlays(lastSelectedSituation.getPlays());
				playsModel.fireTableDataChanged();
			}
		});

	}

	public void addSituations(Rectangle r) {

		SituationArea newArea = new SituationArea((int)r.getWidth(),(int)r.getHeight());
		newArea.addAreaListener(SituationPanel.this.fieldController);
		Situation newSituation = new Situation(newArea, "new situation " + (listOfSituations.size()+1));
		lastSelectedSituation = newSituation;

		listOfSituations.add(newSituation);
		SituationPanel.this.fieldController.addArea(newArea);

		situationModel.fireTableDataChanged();
		tableOfSituations.setRowSelectionInterval(listOfSituations.size()-1, listOfSituations.size()-1);

		newArea.setBounds((int)r.getX(), (int)r.getY(),newArea.getWidth(), newArea.getHeight());

		fieldController.setSelectedArea(newArea);
		currentStrategy.setSituations(listOfSituations);
	}

	public void setGlassPanel(DrawAreaGlassPanel panel) {
		glassPanel = panel;
	}

	public void updateSituationTable () {
		situationModel.fireTableDataChanged();
		tableOfSituations.setRowSelectionInterval(listOfSituations.size()-1, listOfSituations.size()-1);
	}

	@Override
	public void repaint() {
		if (currentStrategy == null) {
			return;
		}

		allPlaysModel.setListOfPlays(currentStrategy.getPlays());
		allPlaysModel.fireTableDataChanged();
	}


	@Override
	public void strategyChanged() {

		allPlaysModel.setListOfPlays(currentStrategy.getPlays());
		allPlaysModel.fireTableDataChanged();
		listOfSituations = currentStrategy.getSituations();
		situationModel.setListOfSituations(listOfSituations);

		situationModel.fireTableDataChanged();
        if (listOfSituations.size() > 0) {
            System.out.println(listOfSituations.get(0).getPlays().size());
            playsModel.setListOfPlays(listOfSituations.get(0).getPlays());
            //add anything else here?

            tableOfSituations.setRowSelectionInterval(listOfSituations.size() - 1, listOfSituations.size() - 1);
            //((JTabbedPane) this.getParent()).setSelectedComponent(this);
        }
	}
}
