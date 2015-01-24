package strategy;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Role {
    private String roleName;
    private CriteriaActionPair[] pairs = {null, null, null, null, null};

    public void setRoleName(String value) {
        this.roleName = value;
    }

    public void setPair(Criteria criteria, Action action, int index) {
        if (pairs[index] == null) {
            pairs[index] = new CriteriaActionPair(criteria, action);
        } else {
            pairs[index].setAction(action);
            pairs[index].setCriteria(criteria);
        }
    }

    public Action[] getActions() {
        Action[] actions = {null, null, null, null, null};
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == null) {
                actions[i] = null;
            } else {
                actions[i] = pairs[i].getAction();
            }
        }
        return actions;
    }

    public Criteria[] getCriterias() {
        Criteria[] criterias = {null, null, null, null, null};
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == null) {
                criterias[i] = null;
            } else {
                criterias[i] = pairs[i].getCriteria();
            }
        }
        return criterias;
    }

    @Override
    public String toString() {
        return roleName;
    }

}
