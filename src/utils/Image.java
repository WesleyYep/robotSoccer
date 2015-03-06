package utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.IplImage;

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
	 * <p>Converts IplImage to BufferedImage</p>
	 * @param IplImage input
	 * @return BufferedImage
	 */
	
	public static BufferedImage IplImageToBufferedImage(IplImage input) {
		// no worries with grayscale images
		if (input.nChannels()==1) {
			return input.getBufferedImage();
		}

		// otherwise: the order in IplImage is BGR, so create a BufferedImage accordingly
		BufferedImage result = new BufferedImage(input.width(), input.height(), BufferedImage.TYPE_3BYTE_BGR);
		input.copyTo(result);
		return result;
	}

	/**
	 * <p>Converts BufferedImage to IplImage</p>
	 * @param BufferedImage input
	 * @return IplImage
	 */
	
	public static IplImage BufferedImageToIplImage(BufferedImage input) {
		return IplImage.createFrom(input);
	}


    public static double euclideanDistance(org.opencv.core.Point p1, org.opencv.core.Point p2) {
        return Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
    }

    public static double angleBetweenTwoPoints(org.opencv.core.Point p1, org.opencv.core.Point p2) {
        return Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));
    }
}