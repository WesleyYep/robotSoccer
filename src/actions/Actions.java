package actions;

import strategy.Action;

import java.util.List;

/**
 * Created by Wesley on 21/01/2015.
 */
public class Actions {
    private static Action[] actions = new Action[] { null, new KickAction() };

    public Action getAction(int index) {
        return actions[index];
    }

    public int getLength() {
        return actions.length;
    }

    public Action findAction(String name) {
        for (Action a : actions) {
            if (a == null) {
                continue;
            }
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }
}