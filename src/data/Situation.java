package data;

import strategy.Play;
import strategy.PlayCriteria;
import ui.SituationArea;
import utils.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Situation {

	private String situationName;
	private SituationArea area;
	private List<Pair<Play, PlayCriteria>> plays;

	public Situation(SituationArea a, String name) {
		plays = new ArrayList<Pair<Play, PlayCriteria>>();
		situationName = name;
		area = a;
		area.setName(situationName);
		area.repaint();
	}

	public void setSituationName(String name) {
		situationName = name;
		area.setName(name);
	}

	@Override
	public String toString() {
		return situationName;
	}

	/**
	 * <p>Sets the active state of the area and colours the border</p>
	 * @param active boolean variable
	 */
	
	public void setAreaActive(boolean active) {
		if (active) {
			area.setActive(true);
			area.setBorderColor(Color.RED);
		} else {
			area.setActive(false);
			area.setBorderColor(Color.GRAY);
		}
	}

	public SituationArea getArea() {
		return area;
	}

	public void addPlay(Pair<Play, PlayCriteria> play) {
		plays.add(play);
	}

	public void removePlay(Pair<Play, PlayCriteria> play) {
		plays.remove(play);
	}

	public List<Pair<Play, PlayCriteria>> getPlays() {
		return plays;
	}

    /**
     * <p>Finds the play to execute in situation</p>
     * <strong>FIRST COME FIRST SERVE basis. First play criteria which is met is returned</strong>
     * @return
     */
    public Play getCurrentPlay() {
        Play currentPlay = null;
        for(Pair<Play, PlayCriteria> play : plays) {
            PlayCriteria criteria = play.getSecond();
            if (criteria.isMet()) {
                currentPlay = play.getFirst();
                break;
            }
        }

        return currentPlay;
    }

}
