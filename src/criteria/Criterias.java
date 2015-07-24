package criteria;

import strategy.Criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Criterias {
	private static List<String> criterias = new ArrayList<String>() {{
          //  new ClosestToBall(),
            add(Permanent.class.getSimpleName());
            add(PointingAtBall.class.getSimpleName());
            add(PositiveSituation.class.getSimpleName());
            add(NegativeSituation.class.getSimpleName());
            add(IsSpinningFast.class.getSimpleName());
            add(IsFirst2Seconds.class.getSimpleName());
    }};

//	public Criteria getAction(int index) {
//		return criterias[index];
//	}

//	public int getLength() {
//		return criterias.length;
//	}

//	public Criteria findCriteria(String name) {
//		for (Criteria c : criterias) {
//			if (c == null) {
//				continue;
//			}
//			if (c.getName().equals(name)) {
//				return c;
//			}
//		}
//		return null;
//	}

    public static List<String> getCriterias() {
        return criterias;
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
