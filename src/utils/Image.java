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
	
}
