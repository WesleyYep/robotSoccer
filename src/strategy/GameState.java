package strategy;

import java.util.ArrayList;
import java.util.List;

import config.ConfigFile;

public class GameState {

	private static GameState instance = null;
	private List<String> whatsGoingOn = new ArrayList<String>();
    private long lastStartedTime = 0;
	
	public static GameState getInstance() {
		if (instance == null) {
			instance = new GameState();
		}
		return instance;
	}
	
	public void addToWhatsGoingOn(String action) {
		if (!whatsGoingOn.contains(action)) {
			whatsGoingOn.add(action);
		}
	}
	
	public void removeFromWhatsGoingOn(String action) {
		whatsGoingOn.remove(action);
	}
	
	public boolean isGoingOn(String action) {
		return whatsGoingOn.contains(action);
	}

    public long getLastStartedTime() {
        return lastStartedTime;
    }

    public void setLastStartedTime() {
        lastStartedTime = System.currentTimeMillis();
    }

}
