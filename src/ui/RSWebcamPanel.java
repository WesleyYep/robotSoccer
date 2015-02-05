package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

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
	
	public class DetectionPainter extends WebcamPanel.DefaultPainter {

		private final Color DETECTDISPLAYCOLOR = Color.CYAN;
		
		private SamplingPanel samplingPanel;
		
		public DetectionPainter(SamplingPanel samplingPanel) {
			super();
			this.samplingPanel = samplingPanel;
		}
		
		public boolean isDetected(int y, int u, int v) {
			if (!((samplingPanel.getLowerBoundForY() <= y) && (y <= samplingPanel.getUpperBoundForY()))) {
				return false;
			}

			if (!((samplingPanel.getLowerBoundForU() <= u) && (u <= samplingPanel.getUpperBoundForU()))) {
				return false;
			}
			
			if (!((samplingPanel.getLowerBoundForV() <= v) && (v <= samplingPanel.getUpperBoundForV()))) {
				return false;
			}
			System.out.println("match");
			return true;
		}
		
		@Override
		public void paintImage(WebcamPanel owner, BufferedImage image, Graphics2D g2) {
			int r, g, b, y, u, v;
			Color color;
			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					color = new Color(image.getRGB(i, j));
					
					r = color.getRed();
					g = color.getGreen();
					b = color.getBlue();
					
			        y = (int)(0.299 * r + 0.587 * g + 0.114 * b);
			        u = (int)(-0.14713 * r + -0.28886 * g + 0.436 * b);
			        v = (int)(0.615 * r + -0.51499 * g + -0.10001 * b);
			        
//			        if (isDetected(y, u, v)) {
//			        	image.setRGB(i, y, DETECTDISPLAYCOLOR.getRGB());
//			        	System.out.println("yes");
//			      	}
			        
			        if (i % 2 == 0 && j % 2 == 0) {
			        	image.setRGB(i, y, DETECTDISPLAYCOLOR.getRGB());
			        }
			        
				}
			}
			
			super.paintImage(owner, image, g2);
		}
		
	}
	
}
