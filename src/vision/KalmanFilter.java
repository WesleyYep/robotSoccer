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
	//https://www.cs.utexas.edu/~teammco/misc/kalman_filter/kalmanFilter.js
	//http://stackoverflow.com/questions/27212167/unable-to-multiply-matrix-in-opencv-java
	public KalmanFilter() {
		A = Mat.eye(4, 4, CvType.CV_32F);
		A.put(0, 2, 0.2);
		A.put(1, 3, 0.2);
		
		//System.out.println(A.dump());
		
		B = Mat.eye(4, 4,CvType.CV_32F);
		
		//System.out.println(B.dump());
		
		H = Mat.eye(4, 4,CvType.CV_32F);
		Q = Mat.zeros(4, 4, CvType.CV_32F);
		R = Mat.eye(4, 4,CvType.CV_32F);
		Core.multiply(R, new Scalar(0.1), R);
		//System.out.println(R.dump());
		lastX = Mat.zeros(1, 4, CvType.CV_32F);
		
		lastP = Mat.zeros(4, 4, CvType.CV_32F);
		
	}
	
	public void process(double x, double y) {
		
		double velX = x-lastX.get(0, 0)[0];
		double velY = y-lastX.get(0,1)[0];
		
		Mat measurement = new Mat(1,4,CvType.CV_32F);
		measurement.put(0, 0, x);
		measurement.put(0, 1, y);
		measurement.put(0, 2, velX);
		measurement.put(0, 3, velY);
		Mat control = Mat.zeros(1, 4, CvType.CV_32F);
		//System.out.println(measurement.dump());
		//System.out.println(control.dump());
		//temp variables to store matrix for calculation
		Mat tempA = new Mat();
		Mat tempB = new Mat();
		Mat tempC = new Mat();
		
		
		//prediction
		Core.gemm( lastX,A,1,new Mat(),0, tempA);
		
		Core.gemm(control,B,1,new Mat(),0, tempB);
		Mat xMat = new Mat();
		Core.add(tempA, tempB, xMat);

		
		Mat p = new Mat();
		Core.gemm(lastP,A,1,new Mat(),0, tempA);
		Core.transpose(A, tempB);
		Core.gemm(tempB,tempA,1,new Mat(),0, tempC);
		Core.add(tempC, Q, p);
		
		//correction
		Mat s = new Mat();
		Core.gemm(p,H,1,new Mat(),0, tempA);
		Core.transpose(H,tempB);
		Core.gemm(tempB,tempA,1,new Mat(),0, tempC);
		Core.add(tempC,R,s);
		
		Mat k = new Mat();
		Core.transpose(H,tempA);
		Core.gemm(tempA,p,1,new Mat(),0,tempB);
		Core.invert(s, tempC);
		Core.gemm(tempC,tempB,1,new Mat(),0, k);
		
		Mat yMat = new Mat();
		Core.gemm(xMat,H,1,new Mat(), 0, tempA);
		Core.subtract(measurement, tempA, yMat);
		
		Mat curX = new Mat();
		Core.gemm(yMat,k,1,new Mat(),0, tempA);
		Core.add(xMat,tempA,curX);
		
		Mat curP = new Mat();
		Core.gemm(H,k,1,new Mat(), 0, tempA);
		Core.subtract(Mat.eye(4, 4, CvType.CV_32F), tempA, tempB);
		Core.gemm(p,tempB,1,new Mat(),0, curP);
		
		lastX = curX;
		lastP = curP;		
		
	}
	
	public int getEstimatedX() {
		return (int) lastX.get(0, 0)[0];
	}
	
	public int getEstimatedY() {
		return (int) lastX.get(0, 1)[0];
	}
}
