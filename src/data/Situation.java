package data;

import strategy.Play;
import ui.SituationArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Situation {

	private String situationName;
	private SituationArea area;
	private List<Play> plays;

	public Situation(SituationArea a, String name) {
		plays = new ArrayList<Play>();
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

	public void addPlay(Play play) {
		plays.add(play);
	}

	public void removePlay (Play play) {
		plays.remove(play);
	}

	public List<Play> getPlays() {
		return plays;
	}

}
