package strategy;

import actions.Actions;
import config.ConfigFile;
import controllers.FieldController;
import criteria.Criterias;
import data.Situation;
import ui.SituationArea;

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
    private int[] robotMapping = new int[] {0, 1, 2, 3, 4};

    public CurrentStrategy (FieldController fieldController) {
        roles = new ArrayList<Role>();
        plays = new ArrayList<Play>();
        situations = new ArrayList<Situation>();
        this.fieldController = fieldController;;
    }

    public void changeMapping(int a, int b, int c, int d, int e) {
        robotMapping[0] = a-1;
        robotMapping[1] = b-1;
        robotMapping[2] = c-1;
        robotMapping[3] = d-1;
        robotMapping[4] = e-1;
    }

    public Role[] mapRoles(Role[] originalRoles) {
        Role[] mappedRoles = new Role[5];
        for (int i = 0; i < 5; i++) {
            mappedRoles[i] = originalRoles[robotMapping[i]];
        }
        return mappedRoles;
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

    public String saveToFile() {
    	JFileChooser fileChooser;
        String path;
        //read in the last save directory
        if ((path = ConfigFile.getInstance().getLastSaveDirectory()) == null) {
        	fileChooser = new JFileChooser();
        }
        else {
        	fileChooser = new JFileChooser(path);
        }
        fileChooser.showSaveDialog(null);
        
        if (fileChooser.getSelectedFile() == null) {
        	return null;
        }
        
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        String folderPath = fileName.substring(0, fileName.lastIndexOf("\\"));;
        ConfigFile.getInstance().setLastSaveDirectory(folderPath);
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
                }
                bufferedWriter.write("-----\n");
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
        return fileName;
    }

    public void readFromFile() {  	
    	
        JFileChooser fileChooser;
        String path;
        //read in the last open directory
        if ((path = ConfigFile.getInstance().getLastOpenDirectory()) == null) {
        	fileChooser = new JFileChooser();
        }
        else {
        	fileChooser = new JFileChooser(path);
        }
        fileChooser.showOpenDialog(null);
        
        if (fileChooser.getSelectedFile() == null) {
        	return;
        }
        
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        
        //creating the folder name and write into configuration
        String folderPath = fileName.substring(0, fileName.lastIndexOf("\\"));;
        ConfigFile.getInstance().setLastOpenDirectory(folderPath);
        String line = null;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            roles.clear();
            plays.clear();
            situations.clear();
            fieldController.removeAllSituationArea();
            while((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Role:")) {
                    int i = 0;
                    Criterias criterias = new Criterias();
                    Actions actions = new Actions();
                    Role role = new Role();
                    role.setRoleName(line.split(":")[1]);

                    while (!(line = bufferedReader.readLine()).equals("-----") && !line.startsWith("null")) {
                        role.setPair(criterias.findCriteria(line.split("-")[0]), (Action)Class.forName(line.split("-")[1]).newInstance(), i);
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
                    SituationArea area = new SituationArea(Integer.parseInt(splitLine[4]), Integer.parseInt(splitLine[5]));
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
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found!");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
