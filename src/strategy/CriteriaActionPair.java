package strategy;

/**
 * Created by Wesley on 23/01/2015.
 */
public class CriteriaActionPair {
    private Criteria criteria;
    private Action action;

    public CriteriaActionPair(Criteria criteria, Action action) {
        this.criteria = criteria;
        this.action = action;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }



}
