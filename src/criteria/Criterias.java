package criteria;

import strategy.Criteria;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Criterias {
    private Criteria[] criterias = new Criteria[] { null, new NearBallCriterion() };

    public Criteria getAction(int index) {
        return criterias[index];
    }

    public int getLength() {
        return criterias.length;
    }

}
