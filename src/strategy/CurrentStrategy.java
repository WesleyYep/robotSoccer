package strategy;

import actions.Actions;
import controllers.FieldController;
import criteria.Criterias;
import data.Situation;
import ui.RobotSoccerMain;
import ui.SituationArea;
import ui.SituationPanel;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 23/01/2015.
 */
public class CurrentStrategy {
    private List<Role> roles;
    private List<Play> plays;
    private List<Situation> situations;
    private List<StrategyListener> listeners = new ArrayList<StrategyListener>();
    private FieldController fieldController;

    public CurrentStrategy (FieldController fieldController) {
        roles = new ArrayList<Role>();
        plays = new ArrayList<Play>();
        situations = new ArrayList<Situation>();
        this.fieldController = fieldController;
    }

    public void addListener(StrategyListener listener) {
        listeners.add(listener);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Play> getPlays() {
        return plays;
    }

    public void setPlays(List<Play> plays) {
        this.plays = plays;
    }

    public List<Situation> getSituations() {
        return situations;
    }

    public void setSituations(List<Situation> situations) {
        this.situations = situations;
    }

    public Role getRoleByName(String name) {
        for (Role r : roles) {
            if (r.toString().equals(name)) {
                return r;
            }
        }
        return null;
    }


    public Play getPlayByName(String name) {
        for (Play p : plays) {
            if (p.toString().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(null);
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Role r : getRoles()) {
                bufferedWriter.write("Role:" + r.toString() + "\n");
                for (int i = 0; i < r.getCriterias().length; i++) {
                    if (r.getActions()[i] == null || r.getCriterias()[i] == null) {
                        bufferedWriter.write("null-null" + "\n");
                        continue;
                    }
                    bufferedWriter.write(r.getCriterias()[i].toString() + "-" + r.getActions()[i].toString() + "\n");
                }
                bufferedWriter.write("-----\n");
            }

            for (Play p : getPlays()) {
                bufferedWriter.write("Play:" + p.toString() + "\n");
                for (Role r : p.getRoles()) {
                    if (r == null) {
                        bufferedWriter.write(null + "\n");
                        continue;
                    }
                    bufferedWriter.write(r.toString() + "\n");
                    bufferedWriter.write("-----\n");
                }
            }

            for (Situation s : situations) {
                bufferedWriter.write("Situation:" + s.toString() + ":" + s.getArea().getX() + ":" + s.getArea().getY()
                                        + ":" + s.getArea().getWidth() + ":" + s.getArea().getHeight() +  "\n");
                for (Play p : s.getPlays()) {
                    bufferedWriter.write(p.toString() + "\n");
                }
                bufferedWriter.write("-----\n");
            }

            bufferedWriter.close();
        }
        catch (IOException ex) {
            System.out.println("Unable to open file: " + fileName);
        }
    }

    public void readFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        String line = null;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            roles.clear();
            plays.clear();
            situations.clear();

            while((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Role:")) {
                    int i = 0;
                    Criterias criterias = new Criterias();
                    Actions actions = new Actions();
                    Role role = new Role();
                    role.setRoleName(line.split(":")[1]);

                    while (!(line = bufferedReader.readLine()).equals("-----")) {
                        role.setPair(criterias.findCriteria(line.split("-")[0]), actions.findAction(line.split("-")[1]), i);
                        i++;
                    }
                    roles.add(role);
                } else if (line.startsWith("Play:")) {
                    Play play = new Play();
                    play.setPlayName(line.split(":")[1]);
                    int i = 0;

                    while (!(line = bufferedReader.readLine()).equals("-----")) {
                        play.addRole(i, getRoleByName(line));
                        i++;
                    }
                    plays.add(play);
                } else if (line.startsWith("Situation:")) {
                    String[] splitLine = line.split(":");
                    SituationArea area = new SituationArea(0,0);
                    area.addAreaListener(fieldController);
                    area.setBounds(Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]),
                                                Integer.parseInt(splitLine[4]), Integer.parseInt(splitLine[5]));
                    fieldController.addArea(area); 
                    fieldController.setSelectedArea(area);
                    
                    for (Situation s : situations) {
                    	s.setAreaActive(false);
                    }
                    Situation situation = new Situation(area, splitLine[1]);
                    //why does area not show up when row selected?
                    while (!(line = bufferedReader.readLine()).equals("-----")) {
                        situation.addPlay(getPlayByName(line));
                    }
                    situations.add(situation);
                    situation.setAreaActive(true);
                }
            }
            for (StrategyListener listener : listeners) {
                listener.strategyChanged(); //this informs situationpanel, playspanel, and rolepanel that they need to update
            }

            bufferedReader.close();
        } catch (IOException ex) {
            System.out.println("Unable to open file: " + fileName);
        }
    }
}
