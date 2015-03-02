package utils;

/**
 * <p>Contains static methods which have conversions from one colour space to another</p>
 * 
 * @see <a href="http://www.equasys.de/colorconversion.html">Conversion matrix</a>
 * @author Chang Kon, Wesley, John
 *
 */

public class ColorSpace {

	/**
	 * Clip values so that it returns values within 0-255 range.
	 * @param value
	 * @return pixel value
	 */
	
	public static double clip(double value) {
		
		if (value < 0) {
			return 0;
		}
		
		if (value > 255) {
			return 255;
		}
		
		return value;
	}
	
	/**
	 * <p>Convert RGB(0-255) values to YUV(0-255)</p>
	 * <p><strong>Note: </strong>array is in double so precision is not lost when doing extra conversions</p>
	 * @param r
	 * @param g
	 * @param b
	 * @return Array of size 3. [0] = y, [1] = u, [2] = v
	 */
	
	public static double[] RGBToYUV(double r, double g, double b) { //NOTE: DOESN'T DO ANYTHING ATM, change later when we switch to HSV
		double[] yuv = new double[3];
		
		yuv[0] = b;//clip((0.299 * r + 0.587 * g + 0.114 * b));
		yuv[1] = g;//clip(((-0.169 * r + -0.331 * g + 0.5 * b) + 128));
		yuv[2] = r;//clip(((0.5 * r + -0.419 * g + -0.081 * b) + 128));
		
		return yuv;
	}
	
	/**
	 * <p>Convert YUV(0-255) values to RGB(0-255)</p>
	 * <p><strong>Note: </strong>array is in double so precision is not lost when doing extra conversions</p>
	 * @param y
	 * @param u
	 * @param v
	 * @return Array of size 3. [0] = r, [1] = g, [2] = b
	 */
	
	public static double[] YUVToRGB(double y, double u, double v) { //NOTE: DOESN'T DO ANYTHING ATM, change later when we switch to HSV
		double[] rgb = new double[3];
		
		rgb[0] = y;//clip((y + 1.4 * (v - 128)));
		rgb[1] = u;//clip(((y + -0.343 * (u - 128) + -0.711 * (v - 128))));
		rgb[2] = v;//clip(((y + 1.765 * (u - 128))));
		
		return rgb;
	}
	
}
