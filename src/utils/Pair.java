package utils;

/**
 * Generic pair class which groups two classes together.
 */
public class Pair<F, S> {
    private F f;
    private S s;

    /**
     * Creates pair class which contains first and second class
     * @param f
     * @param s
     */
    public Pair(F f, S s) {
        this.f = f;
        this.s = s;
    }

    /**
     * Sets first class instance
     * @param f
     */
    public void setFirst(F f) {
        this.f = f;
    }

    /**
     * Sets second class instance
     * @param s
     */
    public void setSecond(S s) {
        this.s = s;
    }

    /**
     * Returns first class instance
     * @return
     */
    public F getFirst() {
        return f;
    }

    /**
     * Returns second class instance
     * @return
     */
    public S getSecond() {
        return s;
    }
}
