package data;

import java.awt.Color;

import javax.swing.JPanel;

import ui.SituationArea;

public class Situation {
	
	private String situationName;
	private SituationArea area;
	
	
	public Situation(SituationArea a, String name) {
		situationName = name;
		area = a;
		area.setName(situationName);
	}
	
	public void setSituationName(String name) {
		situationName = name;
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

}
