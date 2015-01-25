package strategy;

/**
 * Created by Wesley on 21/01/2015.
 */
public abstract class Action {
    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }

//    @Override
//    public boolean equals(Object action) {
//        return (((Action)action).toString().equals(toString()));
//    }
}
