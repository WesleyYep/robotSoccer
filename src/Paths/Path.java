package Paths;

import bot.Robot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 30/01/2015.
 */
public abstract class Path {
	protected List<Integer> pointsXAlongPath = new ArrayList<Integer>();
	protected List<Integer> pointsYAlongPath = new ArrayList<Integer>();
	protected int index = 0;
	protected int startPointX;
	protected int startPointY;
	protected int endPointX;
	protected int endPointY;
	protected Robot robot;

	public abstract boolean hasReachedTarget();

	public abstract void setPoints();

}
