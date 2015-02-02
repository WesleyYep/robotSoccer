package criteria;

import strategy.Criteria;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Criterias {
    private static Criteria[] criterias = new Criteria[] { null, new closestToBall(), new permanent() };

    public Criteria getAction(int index) {
        return criterias[index];
    }

    public int getLength() {
        return criterias.length;
    }

    public Criteria findCriteria(String name) {
        for (Criteria c : criterias) {
            if (c == null) {
                continue;
            }
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

}
