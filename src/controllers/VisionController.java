package controllers;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.media.jai.PerspectiveTransform;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.bridj.SizeT;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import org.bytedeco.javacv.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import org.bytedeco.javacpp.Pointer;
import ui.Field;

public class VisionController {
	
	private Point2D topRight;
	private Point2D topLeft;
	
	private Point2D bottomRight;
	private Point2D bottomLeft;
	
	private double mapLeft = 121;
	private double mapRight = 517;
	private double mapTop = 48;
	private double mapBot = 372;
	

	private PerspectiveTransform t;
	private PerspectiveTransform tInverse;
	
	public VisionController() {
		topRight = new Point2D.Double(200,100);
		topLeft = new Point2D.Double(100,100);
		bottomLeft = new Point2D.Double(100,200);
		bottomRight = new Point2D.Double(200,200);
		this.createTransformMatrix();
		
	}
	
	
	public void createTransformMatrix() {
		//x y: point that u want to map to
		//xp yp: orginal points
		tInverse = PerspectiveTransform.getQuadToQuad(mapLeft, mapTop, mapLeft, mapBot, mapRight, mapBot, mapRight, mapTop,
				topLeft.getX(), topLeft.getY(), bottomLeft.getX(),bottomLeft.getY()
				, bottomRight.getX(),bottomRight.getY(), topRight.getX(),topRight.getY());

		/*
		System.out.println("mapping: " + mapLeft + " " + mapRight + " " + mapTop + " " + mapBot);
		System.out.println(topLeft);
		System.out.println(topRight);
		System.out.println(bottomRight);
		System.out.println(bottomLeft);
		*/
		try {
			t = tInverse.createInverse();
			//System.out.println(t.toString());
	//		System.out.println(tInverse.toString());
		} catch (NoninvertibleTransformException e) {

			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public Point2D imagePosToActualPos (double x, double y) {
		if (t != null ) {
			Point2D selectedPoint = new Point2D.Double();
			t.transform(new Point2D.Double(x,y), selectedPoint);
			double actualX = (selectedPoint.getX() - mapLeft) / ((mapRight-mapLeft)/(double)Field.OUTER_BOUNDARY_WIDTH);
			double actualY = (selectedPoint.getY() - mapTop) / ((mapBot-mapTop)/(double)Field.OUTER_BOUNDARY_HEIGHT);
			
			return new Point2D.Double(actualX,actualY);
		}
		else {
			return null;
		}
		
	}

	public Point2D getTopRight() {
		return topRight;
	}


	public void setTopRight(Point2D topRight) {
		this.topRight = topRight;
		this.createTransformMatrix();
	}


	public Point2D getTopLeft() {
		return topLeft;
	}


	public void setTopLeft(Point2D topLeft) {
		this.topLeft = topLeft;
		this.createTransformMatrix();
	}


	public Point2D getBottomRight() {
		return bottomRight;
	}


	public void setBottomRight(Point2D bottomRight) {
		this.bottomRight = bottomRight;
		this.createTransformMatrix();
	}


	public Point2D getBottomLeft() {
		return bottomLeft;
	}


	public void setBottomLeft(Point2D bottomLeft) {
		this.bottomLeft = bottomLeft;
		this.createTransformMatrix();
	}


	public void showBlurImage(BufferedImage webcamImage) {
		gaussianBlur(webcamImage);
	}

	public void testBlur(IplImage image) {
		IplImage dst = null;
		//GaussianBlur(dst,dst,s,11.0);
		//cvSmooth(image, image, CV_GAUSSIAN, 11, 0, 0, 0);
		
		CvMemStorage storage = cvCreateMemStorage(0);
		CvSeq lines = new CvSeq();
		dst = cvCreateImage(cvGetSize(image), image.depth(), 1);
        IplImage colorDst = cvCreateImage(cvGetSize(image), image.depth(), 3);
        cvCanny(image, dst, 50, 200, 3);
        cvCvtColor(dst, colorDst, CV_GRAY2BGR);
		 lines = cvHoughLines2(dst, storage, CV_HOUGH_PROBABILISTIC, 1, Math.PI / 180, 40, 50, 10);
		
		 for (int i = 0; i < lines.total(); i++) {
             // Based on JavaCPP, the equivalent of the C code:
             // CvPoint* line = (CvPoint*)cvGetSeqElem(lines,i);
             // CvPoint first=line[0], second=line[1]
             // is:
             Pointer line = cvGetSeqElem(lines, i);
             CvPoint pt1  = new CvPoint(line).position(0);
             CvPoint pt2  = new CvPoint(line).position(1);

             	cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0); // draw the segment on the image
         	
		}
		 
		ImageIcon ii = new ImageIcon(colorDst.getBufferedImage());
		JOptionPane.showMessageDialog(null, ii);
	}
	
	
	
	private void gaussianBlur(BufferedImage image) {
		//http://blog.ivank.net/fastest-gaussian-blur.html
		
		/*
		int w = image.getWidth();
		int h = image.getHeight();
		
		int[] targetR = new int[w*h];
		int[] targetG = new int[w*h];
		int[] targetB = new int[w*h];
		
		
		int[] srcR = new int[w*h];
		int[] srcG = new int[w*h];
		int[] srcB = new int[w*h];
		
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		
		int pixelLength = 3;
		int index = 0;
		for (int pixel = 0; pixel<pixels.length; pixel+= pixelLength) {	
			srcR[index] =(((int) pixels[pixel + 2] & 0xff));
		    srcG[index] =(((int) pixels[pixel + 1] & 0xff));
		    srcB[index] =((int) pixels[pixel] & 0xff);
		    index++;
		}
		
		int radius = 10;
		
		gaussBlur_4 (srcR, targetR,  w,  h,  radius);
		gaussBlur_4 (srcG, targetG,  w,  h,  radius);
		gaussBlur_4 (srcB, targetB,  w,  h,  radius);
		
		int row = 0;
		int col = 0;
		
		for (int i = 0; i<targetR.length; i++) {
			int rgb = 0;
			
			rgb += srcR[i] ;
			rgb += srcG[i] << 8;
			rgb += srcB[i] << 16;
			
			image.setRGB(col, row, rgb);
			col++;
			
			if (col == w) {
				col =0;
				row++;
			}	
		}
		*/
		
		
		ImageIcon ii = new ImageIcon(image);
		JOptionPane.showMessageDialog(null, ii);
	}
	
	
	private double[] boxesForGauss(double sigma, int n) {
		
		double wIdeal = Math.sqrt((12*sigma*sigma/n)+1);
		double wl = Math.floor(wIdeal);
		double wu = wl+2;
		if (wl % 2 == 0) wl--;
		
		double mIdeal = (12*sigma*sigma - n*wl*wl - 4*n*wl - 3*n)/(-4*wl - 4);
		double m = Math.round(mIdeal);
		double[] size = new double[3];
		
		for (int i = 0; i <n; i++) {
			if (i<m) {
				size[i] = wl;
			}
			else {
				size[i] = wu;
			}
		}
		
		return size;
	}
	
	private void gaussBlur_4 (int[] scl, int[]tcl, int w, int h, int r) {
		double[] bxs = boxesForGauss(r, 3);
	    boxBlur_4 (scl, tcl, w, h, (bxs[0]-1)/2);
		boxBlur_4 (tcl, scl, w, h, (bxs[1]-1)/2);
		boxBlur_4 (scl, tcl, w, h, (bxs[2]-1)/2);
	}

	private void boxBlur_4(int[] scl, int[] tcl, int w, int h, double r) {
		for(int i=0; i<scl.length; i++) {
			tcl[i] = scl[i];
		}
		
		
		boxBlurH_4(tcl, scl, w, h, r);
		boxBlurT_4(scl, tcl, w, h, r);
	}

	private void boxBlurT_4(int[] scl, int[] tcl, int w, int h, double r) {
		double iarr = 1 / (r+r+1);
		    for(int i=0; i<h; i++) {
		        int ti = i*w;
		        int li = ti; 
		        int ri = (int) (ti+Math.round(r));
		        int fv = scl[ ti];
		        int lv = scl[ti+w-1];
		        int val = (int) ((Math.round(r)+1)*fv);
		        
		        for(int j=0; j<r; j++) {
		        	val += scl[ti+j];
		        }
		        for(int j=0  ; j<=r ; j++) { val += scl[ri++] - fv       ;   tcl[ti++] = (int) Math.round(val*iarr); }
		        for(int j=(int)(Math.round(r)+1); j<w-r; j++) { val += scl[ri++] - scl[li++];   tcl[ti++] = (int) Math.round(val*iarr); }
		        for(int j=(int)(w-Math.round(r)); j<w  ; j++) { val += lv        - scl[li++];   tcl[ti++] = (int) Math.round(val*iarr); }
		    }
	}

	private void boxBlurH_4(int[] tcl, int[] scl, int w, int h, double r) {
		double iarr = 1 / (r+r+1);
		    for(int i=0; i<w; i++) {
		        int ti = i, li = ti, ri = (int) (ti+Math.round(r)*w);
		        int fv = scl[ti], lv = scl[ti+w*(h-1)], val = (int) ((Math.round(r)+1)*fv);
		        for(int j=0; j<r; j++) val += scl[ti+j*w];
		        for(int j=0  ; j<=r ; j++) { val += scl[ri] - fv     ;  tcl[ti] = (int) Math.round(val*iarr);  ri+=w; ti+=w; }
		        for(int j=(int) (Math.round(r)+1); j<h-r; j++) { val += scl[ri] - scl[li];  tcl[ti] = (int) Math.round(val*iarr);  li+=w; ri+=w; ti+=w; }
		        for(int j=(int) (h-Math.round(r)); j<h  ; j++) { val += lv      - scl[li];  tcl[ti] = (int) Math.round(val*iarr);  li+=w; ti+=w; }
		}
	}
	
}
