package data;

/**
 * Created by Wesley on 6/02/2015.
 */
public class Coordinate {
	public int x;
	public int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * <p>Checks if the coordinates (x and y) are equal.</p>
	 * @param other
	 * @return Co ordinates are equal
	 */

	public boolean equals(Coordinate other) {
		return (x == other.x && y == other.y);
	}
}
