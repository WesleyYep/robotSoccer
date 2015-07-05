package strategy;

import config.ConfigFile;
import config.ConfigPreviousFile;
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
    private Play setPlay = null;

    public boolean openedStratFile = false;

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
        save(fileName);
        return fileName;
    }

    public void save(String name) {
        String fileName = name;
        String folderPath = fileName.substring(0, fileName.lastIndexOf("\\"));;
        ConfigFile.getInstance().setLastSaveDirectory(folderPath);
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Role r : getRoles()) {
                if (r.isSetPlayRole()) {
                    continue;
                }
                bufferedWriter.write("Role:" + r.toString() + "\n");
                for (int i = 0; i < r.getCriterias().length; i++) {
                    if (r.getActions()[i] == null || r.getCriterias()[i] == null) {
                        bufferedWriter.write("null-null" + "\n");
                        continue;
                    }
                    Action action = r.getActions()[i];
                    bufferedWriter.write(r.getCriterias()[i].toString() + "-actions." + action.toString() + "-" + action.getParameters() + "-" + action.getValues() + "\n");
                }
                bufferedWriter.write("-----\n");
            }

            for (Play p : getPlays()) {
                if (p.isSetPlay()) {
                    continue;
                }
                bufferedWriter.write("Play:" + p.toString() + "\n");
                for (Role r : p.getRoles()) {
                    if (r == null) {
                        bufferedWriter.write(null + "\n");
                        continue;
                    }
                    bufferedWriter.write(r.toString() + "\n");
                }
                bufferedWriter.write("-----\n");
//                for (Point point : p.getPlayCriterias()) {
//                    bufferedWriter.write(point.x + ":" + point.y + "\n");
//                }
//                bufferedWriter.write("-----\n");
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
            //save last read file
            ConfigPreviousFile.getInstance().setPreviousStratFile(fileName);
        }
        catch (IOException ex) {
            System.out.println("Unable to open file: " + fileName);
        }
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
        read(fileName);
        //save last read file
        ConfigPreviousFile.getInstance().setPreviousStratFile(fileName);
    }

    public void read(String fileName) {
        String line = null;
        openedStratFile = true;
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
                    Role role = new Role();
                    role.setRoleName(line.split(":")[1]);

                    while (!(line = bufferedReader.readLine()).equals("-----") && !line.startsWith("null")) {
                        String[] lineArray = line.split("-");
                        Action action = (Action)Class.forName(lineArray[1]).newInstance();
                        if (lineArray.length < 3) {
                            //do nothing
                        } else {
                            for (int j = 0; j < fromString(lineArray[2]).length; j++) {
                                if (lineArray[2].equals("[]") || lineArray[3].equals("[]")) {
                                    continue;
                                }
                                action.updateParameters(fromString(lineArray[2])[j], fromStringInt(lineArray[3])[j]);
                            }
                        }
                        role.setPair(criterias.findCriteria(line.split("-")[0]), action, i);
                        i++;
                    }
                    roles.add(role);
                } else if (line.startsWith("Play:")) {
                    Play play = new Play();
                    play.setPlayName(line.split(":")[1]);
                    int i = 0;

                    while (!(line = bufferedReader.readLine()).equals("-----")) {
                        String[] lineArray = line.split(":");
                        Role roleToAdd = cloneRole(getRoleByName(lineArray[0]));
                        Action firstAction = roleToAdd.getActions()[0];
                        Object[] params = firstAction.getParameters().toArray();

                        for (int j = 0; j < params.length; j++) {
                            if (lineArray.length == 5) {
                                firstAction.updateParameters((String)params[j], Integer.parseInt(lineArray[j+1]));
                            } else {
                                firstAction.parameters = getRoleByName(lineArray[0]).getActions()[0].parameters;
                            }
                        }
                        play.addRole(i, roleToAdd);

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

            bufferedReader.close();
            if (!fileName.contains("setPlay.xml")) { //don't open set play file if we deliberately are opening it!
                readSetPlay(new File(fileName).getParentFile().getAbsolutePath());
            }
            for (StrategyListener listener : listeners) {
                listener.strategyChanged(); //this informs situationpanel, playspanel, and rolepanel that they need to update
            }
        } catch (IOException ex) {
            System.out.println("Unable to open file: " + fileName);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void readSetPlay(String directory) {
        String line = null;
        openedStratFile = true;
        try {
            FileReader fileReader = new FileReader(directory + "\\setPlay.xml");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Role:")) {
                    int i = 0;
                    Criterias criterias = new Criterias();
                    Role role = new Role();
                    role.setRoleName(line.split(":")[1]);
                    if (containsRole(role)) {
                        continue;
                    }
                    role.setIsSetPlayRole(true);

                    while (!(line = bufferedReader.readLine()).equals("-----") && !line.startsWith("null")) {
                        String[] lineArray = line.split("-");
                        Action action = (Action)Class.forName(lineArray[1]).newInstance();
                        if (lineArray.length < 3) {
                            //do nothing
                        } else {
                            for (int j = 0; j < fromString(lineArray[2]).length; j++) {
                                if (lineArray[2].equals("[]") || lineArray[3].equals("[]")) {
                                    continue;
                                }
                                action.updateParameters(fromString(lineArray[2])[j], fromStringInt(lineArray[3])[j]);
                            }
                        }
                        role.setPair(criterias.findCriteria(line.split("-")[0]), action, i);
                        i++;
                    }
                    roles.add(role);
                } else if (line.startsWith("Play:")) {
                    Play play = new Play();
                    play.setPlayName(line.split(":")[1]);
                    play.setIsSetPlay(true);
                    int i = 0;

                    while (!(line = bufferedReader.readLine()).equals("-----")) {
                        String[] lineArray = line.split(":");
                        Role roleToAdd = cloneRole(getRoleByName(lineArray[0]));

                            Action firstAction = roleToAdd.getActions()[0];
                            Object[] params = firstAction.getParameters().toArray();
                            for (int j = 0; j < params.length; j++) {
                                if (lineArray.length >= params.length) {
                                    firstAction.updateParameters((String)params[j], Integer.parseInt(lineArray[j+1]));
                                } else {
                                    firstAction.parameters = getRoleByName(lineArray[0]).getActions()[0].parameters;
                                    break;
                                }
                            }
                        play.addRole(i, roleToAdd);

                        i++;
                    }
                    plays.add(play);
                }
            }

        } catch (IOException ex) {
            System.out.println("Unable to open file: " + directory + "/setPlay.xml");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private int[] fromStringInt(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        int result[] = new int[strings.length];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = Integer.parseInt(strings[i]);
            }catch (NumberFormatException ex) {
                System.out.println("Action parameter is not a number");
            }
        }
        return result;
    }

    private String[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        return strings;
    }

    public boolean containsRole(Role role) {
        for (Role r : roles) {
            if ( r.toString().equals(role.toString())) {
                return true;
            }
        }
        return false;
    }

    public void setSetPlay(Play play) {
        setPlay = play;
    }

    public Play getSetPlay() {
        return setPlay;
    }

    private static Role cloneRole(Role role){
        try{
            Role clone = role.getClass().newInstance();
            clone.setRoleName(role.toString());
            Action[] actions = role.getActions();
            Criteria[] crits = role.getCriterias();

            for (int i = 0; i < actions.length; i++) {
                if (actions[i] == null || crits[i] == null) {
                    break;
                }
                clone.setPair(crits[i], (Action)Class.forName("actions." + actions[i].toString()).newInstance(), i);
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }
}
