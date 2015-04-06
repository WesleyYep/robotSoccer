package criteria;

import strategy.Criteria;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Criterias {
	private static Criteria[] criterias = new Criteria[] {
            null,
            new ClosestToBall(),
            new Permanent(),
            new PointingAtBall(),
            new PositiveSituation(),
            new IsSpinningFast()

    };

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

	/*
	 * Same suggestion as Actions.
	 * 
	 * private static List<Criteria> criterias = new ArrayList<Criteria>() {{
	 * 		add(new ClosestToBall());
	 * 		add(new Permanent());
	 * }};
	 * 
	 * public Criteria findCriteria(Class<? extends Criteria> className) {
	 * 		for (Criteria c : criterias) {
	 * 			if (c.getClass() == className) {
	 * 				return c;
	 * 			}
	 * 		}
	 * 		return null;
	 * }
	 * 
	 */

}
