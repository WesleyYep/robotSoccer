package vision;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class KalmanFilter {
	
	private Mat A;
	private Mat B;
	private Mat H;
	private Mat R;
	private Mat Q;
	private Mat lastX;
	private Mat lastP;
	private Mat predX;
	private double dT;

	private long previousTime = 0;
	//https://www.cs.utexas.edu/~teammco/misc/kalman_filter/kalmanFilter.js
	//http://stackoverflow.com/questions/27212167/unable-to-multiply-matrix-in-opencv-java
	public KalmanFilter() {
		A = Mat.eye(4, 4, CvType.CV_32F);
		
		//System.out.println(A.dump());
		
		B = Mat.eye(4, 4,CvType.CV_32F);
		
		//System.out.println(B.dump());
		
		H = Mat.eye(4, 4,CvType.CV_32F);
		Q = Mat.zeros(4, 4, CvType.CV_32F);
		Q.put(0, 0, 0.001);
		Q.put(1, 1, 0.001);
		R = Mat.eye(4, 4,CvType.CV_32F);
		Core.multiply(R, new Scalar(0.1), R);
		//System.out.println(R.dump());
		lastX = Mat.zeros(4, 1, CvType.CV_32F);
		
		lastP = Mat.zeros(4, 4, CvType.CV_32F);
		
	}
	
	/** predict the ball current position then correct it with the given sensor input
	 * @param x position from the sensor
	 * @param y position from the sensor
	 */
	public void process(double x, double y) {
		
		double velX = x-lastX.get(0,0)[0];
		double velY = x-lastX.get(1,0)[0];
		
		//System.out.println(lastX.get(0, 0)[0] + " " + lastX.get(0, 0).length);
		Mat measurement = new Mat(4,1,CvType.CV_32F);
		measurement.put(0, 0, x);
		measurement.put(1, 0, y);
		measurement.put(2, 0, velX);
		measurement.put(3, 0, velY);
		Mat control = Mat.zeros(4, 1, CvType.CV_32F);
		//System.out.println(measurement.dump());
		//System.out.println(control.dump());
		//temp variables to store matrix for calculation
		Mat tempA = new Mat();
		Mat tempB = new Mat();
		Mat tempC = new Mat();
		
		
		if (previousTime != 0 ) {
			dT = System.currentTimeMillis()-previousTime;
			A.put(0, 2, dT/1000);
			A.put(1, 3, dT/1000);
		}
		previousTime = System.currentTimeMillis();
		
		//System.out.println("inside process: " +  measurement.dump());
		//prediction
		Core.gemm( A,lastX,1,new Mat(),0, tempA);
		
		Core.gemm(B,control,1,new Mat(),0, tempB);
		Mat xMat = new Mat();
		Core.add(tempA, tempB, xMat);
		//System.out.println(xMat.dump());
		
		Mat p = new Mat();
		Core.gemm(A,lastP,1,new Mat(),0, tempA);
		Core.transpose(A, tempB);
		Core.gemm(tempA,tempB,1,new Mat(),0, tempC);
		Core.add(tempC, Q, p);
		
		//correction
		Mat s = new Mat();
		Core.gemm(H,p,1,new Mat(),0, tempA);
		Core.transpose(H,tempB);
		Core.gemm(tempA,tempB,1,new Mat(),0, tempC);
		Core.add(tempC,R,s);
		
		Mat k = new Mat();
		Core.transpose(H,tempA);
		Core.gemm(p,tempA,1,new Mat(),0,tempB);
		Core.invert(s, tempC);
		Core.gemm(tempB,tempC,1,new Mat(),0, k);
		
		Mat yMat = new Mat();
		Core.gemm(H,xMat,1,new Mat(), 0, tempA);
		Core.subtract(measurement, tempA, yMat);
		
		Mat curX = new Mat();
		Core.gemm(k,yMat,1,new Mat(),0, tempA);
		Core.add(xMat,tempA,curX);
		
		Mat curP = new Mat();
		Core.gemm(k,H,1,new Mat(), 0, tempA);
		Core.subtract(Mat.eye(4, 4, CvType.CV_32F), tempA, tempB);
		Core.gemm(tempB,p,1,new Mat(),0, curP);
		
		lastX = curX;
		lastP = curP;		
	}
	
	
	/**
	 * predict the ball next n position
	 * @param n is the time in seconds
	 * 
	 */
	public void predict(double n) {
		predX = lastX;
		Mat tempA = new Mat();
		Mat tempB = new Mat();
		Mat control = Mat.zeros(4, 1, CvType.CV_32F);
		//dT is in ms
		int count = (int) Math.round(n/dT);
		for (int i =0; i<count; i++){
			Core.gemm( A,predX,1,new Mat(),0, tempA);
			Core.gemm(B,control,1,new Mat(),0, tempB);
			Core.add(tempA, tempB, predX);
		}
	}
	
	public double getEstimatedX() {
		return (double) lastX.get(0, 0)[0];
	}
	
	public double getEstimatedY() {
		return (double) lastX.get(1, 0)[0];
	}
}
