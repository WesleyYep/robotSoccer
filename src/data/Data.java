package data;

public abstract class Data {
	
	/**
	 * <p>Return the given angle between -180 to 180 range.</p>
	 * @param angle
	 * @return clipped angle
	 */
	
	public static double clip(double angle) {
		if (angle > 180) {
			return angle -= 360;
		} else if (angle < -180) {
			return angle += 360;
		} else {
			return angle;
		}
	}
}
