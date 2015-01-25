package strategy;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Play {
    private Role[] roles = {null, null, null, null, null};
    private String playName;

    public void addRole(int index, Role role) {
        if (role == null) {
            return;
        }
        roles[index] = role;
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
}
