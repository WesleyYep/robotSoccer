package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

/**
 * <p>This class has an additional painter which detects the colour and overwrites pixel colour values</p>
 * <p>{@link WebcamDisplayPanel}</p>
 * @author Chang Kon, Wesley, John
 *
 */

public class RSWebcamPanel extends WebcamPanel {
	
	public RSWebcamPanel(Webcam webcam) {
		super(webcam);
	}

	public RSWebcamPanel(Webcam webcam, boolean start) {
		super(webcam, start);
	}
	
	public RSWebcamPanel(Webcam webcam, Dimension size, boolean start) {
		super(webcam, size, start);
	}
	
	/**
	 * <p>Detects for pixels which are sampled by the sampling panel. Colours the pixels into a different colour</p>
	 * <p>{@link ui.SamplingPanel}</p>
	 * @author Chang Kon, Wesley, John
	 *
	 */
	
	public class DetectionPainter extends WebcamPanel.DefaultPainter {

		// The color to show.
		private final Color DETECTDISPLAYCOLOR = Color.CYAN;
		
		private SamplingPanel samplingPanel;
		
		public DetectionPainter(SamplingPanel samplingPanel) {
			super();
			this.samplingPanel = samplingPanel;
		}
		
		/**
		 * <p>Retrieves the YUV values for the pixel and checks if it is in the acceptable range set by the SamplingPanel</p>
		 * @param y
		 * @param u
		 * @param v
		 * @return boolean
		 */
		
		public boolean isDetected(int y, int u, int v) {
			if (!((y >= samplingPanel.getLowerBoundForY()) && (y <= samplingPanel.getUpperBoundForY()))) {
				return false;
			}

			if (!((u >= samplingPanel.getLowerBoundForU()) && (u <= samplingPanel.getUpperBoundForU()))) {
				return false;
			}
			
			if (!((v >= samplingPanel.getLowerBoundForV()) && (v <= samplingPanel.getUpperBoundForV()))) {
				return false;
			}

			return true;
		}
		
		@Override
		public void paintImage(WebcamPanel owner, BufferedImage image, Graphics2D g2) {
			int r, g, b, y, u, v;
			
			Color color;

			for (int j = 0; j < image.getHeight(); j++) {
				for (int i = 0; i < image.getWidth(); i++) {
					color = new Color(image.getRGB(i, j));
					
					r = color.getRed();
					g = color.getGreen();
					b = color.getBlue();
					
			        // http://en.wikipedia.org/wiki/YUV#Full_swing_for_BT.601
			        y = ((76 * r + 150 * g +  29 * b + 128) >> 8);
			        u = ((-43 * r -  84 * g + 127 * b + 128) >> 8) + 128;
			        v = ((127 * r -  106 * g -  21 * b + 128) >> 8) + 128;
			        
			        if (isDetected(y, u, v)) {
			        	image.setRGB(i, j, DETECTDISPLAYCOLOR.getRGB());
			      	}
				}
			}
			
			super.paintImage(owner, image, g2);
		}
		
	}
	
}
