package strategy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import vision.KalmanFilter;
import Paths.Path;
import bot.Robots;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Wesley on 21/01/2015.
 */
public abstract class Action {
	protected Robots bots;
	protected double ballX;
	protected double ballY;
	protected double predX;
	protected double predY;
	protected int index;
	protected Path path;
    protected HashMap<String, Integer> parameters = new HashMap<String, Integer>();
//	protected static KalmanFilter kFilter = new KalmanFilter();

	public String getName() {
        return getClass().getSimpleName();
    }

	public void addRobot (Robots bots, int index) {
		this.bots = bots;
		this.index = index;
	}

	public void setBallPosition(double x, double y) {
		this.ballX = x;
		this.ballY = y;
	}

    public Set<String> getParameters() {
        return parameters.keySet();
    }

    public Collection<Integer> getValues() {
        return parameters.values();
    }

    public void updateParameters(String key, int value) {
        parameters.put(key, value);
    }

	@Override
	public String toString() {
		return getName();
	}

	public abstract void execute();

	public void setPredBallPosition(double predictedBallX, double predictedBallY) {
		this.predX = predictedBallX;
		this.predY = predictedBallY;
	}

	/**
	 * <p>Loads fuzzy file and returns FunctionBlock object.</p>
	 * <p>When FunctionBlock is returned</p>
	 * @param filename
	 * @return FunctionBlock object
	 */
	protected FunctionBlock loadFuzzy(String filename) {
		FIS fis = FIS.load(filename);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		return fb;
	}
}
