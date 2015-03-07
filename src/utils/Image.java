package utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * <p>Consists of static utility classes when working with images</p>
 * @author Chang Kon, Wesley, John
 *
 */

public class Image {

	/**
	 * <p>Takes a source image and resizes it to the given width and height value</p>
	 * @param image
	 * @param width
	 * @param height
	 * @return resized bufferedimage
	 */

	public static BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resize = new BufferedImage(width, height, image.getType());
		Graphics2D g2d = resize.createGraphics();
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return resize;
	}

	/**
	 * <p>Finds the euclidean distance between two points and returns it</p>
	 * @param p1
	 * @param p2
	 * @return euclidean distance between two points
	 */
	
    public static double euclideanDistance(org.opencv.core.Point p1, org.opencv.core.Point p2) {
        return Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
    }

    /**
     * <p>Calculates the angle between the two points, respect to the x axis in degrees</p>
     * @param p1
     * @param p2
     * @return angle between two points in degrees, respect to x axis. <strong>Note:</strong> range -180 to 180.
     */
    
    public static double angleBetweenTwoPoints(org.opencv.core.Point p1, org.opencv.core.Point p2) {
        return Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));
    }
}
