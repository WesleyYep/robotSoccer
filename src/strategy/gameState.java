package strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 21/05/2015.
 */
public class GameState {

    private static GameState instance = null;
    private List<String> whatsGoingOn = new ArrayList<String>();

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    /*
    This adds to the going on list - first checks if there's a duplicate
     */
    public void addToWhatsGoingOn(String action) {
        if (!whatsGoingOn.contains(action)) {
            whatsGoingOn.add(action);
        }
    }

    /*
    This removes from the going on list
     */
    public void removeFromWhatsGoingOn(String action){
        whatsGoingOn.remove(action);
    }

    /*
    This method checks if a particular action is going on
     */
    public boolean isGoingOn(String action) {
        return whatsGoingOn.contains(action);
    }

}
