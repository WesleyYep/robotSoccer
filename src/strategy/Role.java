package strategy;

import bot.Robots;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Role {
    private String roleName;
    private CriteriaActionPair[] pairs = {null, null, null, null, null};
    private Robots bots;
    private int index;
    private double ballX;
    private double ballY;

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

    public void execute() {
        for (CriteriaActionPair cap : pairs) {
            if (cap == null) { continue; }
            Criteria c = cap.getCriteria();
            if (c == null) { continue; }
            c.addRobot(bots, index);
            c.setBallPosition(ballX, ballY);
            if (cap.getCriteria().isMet()) {
                Action a = cap.getAction();
                a.addRobot(bots, index);
                a.setBallPosition(ballX, ballY);
                a.execute();
                break; //only have one action at a time!
            }
        }
    }

    public void addRobot (Robots bots, int index) {
        this.bots = bots;
        this.index = index;
    }

    public void setBallPosition(double x, double y) {
        this.ballX = x;
        this.ballY = y;
    }

    @Override
    public String toString() {
        return roleName;
    }


}
