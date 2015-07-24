package criteria;

import strategy.Criteria;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chang Kon on 24/07/2015.
 */
public class CriteriaFactory {
    // Hashmap containing actions
    private static final Map<String, Criteria> criteriaMap = new HashMap();

    public static Criteria getCriteria(String criteriaSimpleName) {
        Criteria criteria = criteriaMap.get(criteriaSimpleName);

        if (criteria == null) {
            if (criteriaSimpleName.equals(ClosestToBall.class.getSimpleName())) {
                ClosestToBall closestToBall = new ClosestToBall();
                criteriaMap.put(criteriaSimpleName, closestToBall);
                return closestToBall;
            } else if (criteriaSimpleName.equals(IsFirst2Seconds.class.getSimpleName())) {
                IsFirst2Seconds isFirst2Seconds = new IsFirst2Seconds();
                criteriaMap.put(criteriaSimpleName, isFirst2Seconds);
                return isFirst2Seconds;
            } else if (criteriaSimpleName.equals(IsSpinningFast.class.getSimpleName())) {
                IsSpinningFast isSpinningFast = new IsSpinningFast();
                criteriaMap.put(criteriaSimpleName, isSpinningFast);
                return isSpinningFast;
            } else if (criteriaSimpleName.equals(NegativeSituation.class.getSimpleName())) {
                NegativeSituation negativeSituation = new NegativeSituation();
                criteriaMap.put(criteriaSimpleName, negativeSituation);
                return negativeSituation;
            } else if (criteriaSimpleName.equals(Permanent.class.getSimpleName())) {
                Permanent permanent = new Permanent();
                criteriaMap.put(criteriaSimpleName, permanent);
                return permanent;
            } else if (criteriaSimpleName.equals(PointingAtBall.class.getSimpleName())) {
                PointingAtBall pointingAtBall = new PointingAtBall();
                criteriaMap.put(criteriaSimpleName, pointingAtBall);
                return pointingAtBall;
            } else if (criteriaSimpleName.equals(PositiveSituation.class.getSimpleName())) {
                PositiveSituation positiveSituation = new PositiveSituation();
                criteriaMap.put(criteriaSimpleName, positiveSituation);
                return positiveSituation;
            }
        }

        return criteria;
    }
}
