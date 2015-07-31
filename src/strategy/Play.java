package strategy;

import org.opencv.core.Point;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Play {
    private Role[] roles = {null, null, null, null, null};
//    private Point[] criterias = {new Point(-1,-1), new Point(-1,-1), new Point(-1,-1), new Point(-1,-1), new Point(-1,-1)}; //-1 is permanent, -2 is closest to ball
    private String playName;
    private int playParams[] = new int[]{0,0,0,0};
    private boolean isSetPlay = false;

    public void addRole(int index, Role role) {
        if (role == null) {
            return;
        }
        roles[index] = role;
    }

//    public void setPlayCriteria(int index, Point playCriteria) {
//        if (playCriteria == null) {
//            return;
//        }
//        criterias[index] = playCriteria;
//    }



    public void setPlayName(String value) {
        playName = value;
    }

    @Override
    public String toString() {
        return playName;
    }

    public Role[] getRoles() {
        return roles;
    }

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
