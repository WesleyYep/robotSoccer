package criteria;

import strategy.Criteria;

public class Permanent extends Criteria {
    @Override
    public String getName() {
        return "permanent";
    }

	/*
	 * Incomplete?
	 */

    @Override
    public boolean isMet() {
        return true;
    }
}
