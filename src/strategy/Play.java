package strategy;

import org.opencv.core.Point;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Play {
    private Role[] roles = {null, null, null, null, null};
    private Point[] criterias = {new Point(-1,-1), new Point(-1,-1), new Point(-1,-1), new Point(-1,-1), new Point(-1,-1)}; //-1 is permanent, -2 is closest to ball
    private String playName;

    public void addRole(int index, Role role) {
        if (role == null) {
            return;
        }
        roles[index] = role;
    }

    public void setPlayCriteria(int index, Point playCriteria) {
        if (playCriteria == null) {
            return;
        }
        criterias[index] = playCriteria;
    }



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

    public Point[] getPlayCriterias() {
        return criterias;
    }

    @Override
    public boolean equals (Object other) {
        return (toString().equals(((Play)other).toString()));
    }
}
