package utils;

import java.util.LinkedList;

/**
 * Created by Wesley on 18/07/2015.
 */
public class LimitedQueue extends LinkedList<Double> {
    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    public boolean add(double o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }

    public double getTotal() {
        double total = 0;

        for (int i = 0; i < size(); i++) {
            total += get(i);
        }
        return total;
    }
}
