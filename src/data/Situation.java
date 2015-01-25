package data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import strategy.Play;
import strategy.Role;
import ui.SituationArea;

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
	
	public void setAreaActive(boolean active) {
		if (active) {
			area.setActive(true);
			area.setBorderColor(Color.RED);
		}
		else {
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

	public List<Play> getPlays() {
		return plays;
	}


}
