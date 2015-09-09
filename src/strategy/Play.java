package strategy;

import bot.Robot;
import org.opencv.core.Point;
import utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Play {
    private List<Pair<Role, RoleCriteria>> roles = new ArrayList<Pair<Role, RoleCriteria>>();
    //private Role[] roles = {null, null, null, null, null};
    private String playName;
    private int playParams[] = new int[]{0,0,0,0};
    private boolean isSetPlay = false;

//    public void addRole(int index, Role role) {
//        if (role == null) {
//            return;
//        }
//        roles[index] = role;
//    }

//    public void setPlayCriteria(int index, Point playCriteria) {
//        if (playCriteria == null) {
//            return;
//        }
//        criterias[index] = playCriteria;
//    }

    /**
     * <p>Assigns roles to each robot based on criteria</p>
     */
    public void assignRoles() {
        List<Robot> ignoreList = new ArrayList<Robot>();
        Iterator<Pair<Role, RoleCriteria>> iter = roles.iterator();

        Pair<Role, RoleCriteria> previous = null;
        Pair<Role, RoleCriteria> current = null;

        while(iter.hasNext()) {
            previous = current;
            current = iter.next();
            Role r = current.getFirst();
            RoleCriteria c = current.getSecond();

            // Remove already assigned robots from checklist
            if (previous != null) {
            	List<Robot> checkList = c.getCheckList();
                checkList.removeAll(ignoreList);
            }

            Robot assignedRobot = c.isMet();
            // assign robot to role
            r.addRobot(assignedRobot);
            ignoreList.add(assignedRobot);
        }
    }

    /**
     * Runs each role in the play
     */
    public void execute() {
        for (Pair<Role, RoleCriteria> role : roles) {
            Role r = role.getFirst();
            r.execute();
        }
    }

    public void setPlayName(String value) {
        playName = value;
    }

    @Override
    public String toString() {
        return playName;
    }

//    public Role[] getRoles() {
//        return roles;
//    }

//    public Point[] getPlayCriterias() {
//        return criterias;
//    }

    @Override
    public boolean equals (Object other) {
        return (toString().equals(((Play)other).toString()));
    }

    public void setPlayParameters(int param1, int param2, int param3, int param4) {
        playParams[0] = param1;
        playParams[1] = param2;
        playParams[2] = param3;
        playParams[3] = param4;
    }

    public boolean isSetPlay() {
        return isSetPlay;
    }

    public void setIsSetPlay(boolean isSetPlay) {
        this.isSetPlay = isSetPlay;
    }
}
