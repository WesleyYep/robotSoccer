package vision;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class KalmanFilter {
	/*
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
		
		B = Mat.zeros(4, 4,CvType.CV_32F);
		
		//System.out.println(B.dump());
		
		H = Mat.eye(4, 4,CvType.CV_32F);
		//H.put(0, 0, 1);
		//H.put(0,2, 1);
		//H.put(1, 1, 1);
		//H.put(1, 3, 1);
		System.out.println(H.dump());
		Q = Mat.zeros(4, 4, CvType.CV_32F);
	//	Q.put(0, 0, 0);
	//	Q.put(1, 1, 0);
	//	Q.put(2, 2, 0);
	//	Q.put(3, 3, 0);
		R = Mat.eye(4, 4,CvType.CV_32F);
		Core.multiply(R, new Scalar(0.2), R);
		R.put(2, 2, 0.2);
		R.put(3, 3, 0.2);
		//System.out.println(R.dump());
		lastX = Mat.zeros(4, 1, CvType.CV_32F);
		
		lastP = Mat.eye(4, 4, CvType.CV_32F);
		//lastP = Mat.eye(4, 4, CvType.CV_32F);
		
	}
	

	public void process(double x, double y) {
		
		double velX = x-lastX.get(0,0)[0];
		double velY = x-lastX.get(1,0)[0];
		
		System.out.println("Vel: " + velX + " " + velY);
		//System.out.println(lastX.get(0, 0)[0] + " " + lastX.get(0, 0).length);
		Mat measurement = new Mat(4,1,CvType.CV_32F);
		measurement.put(0, 0, x);
		measurement.put(1, 0, velX);
		measurement.put(2, 0, y);
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
			System.out.println("DT: " + dT);
			A.put(0, 1, dT/1000);
			A.put(2, 3, dT/1000);
		}
		previousTime = System.currentTimeMillis(); 
		
		//System.out.println("inside process: " +  measurement.dump());
		//prediction
		//state prediction
		Core.gemm( A,lastX,1,new Mat(),0, tempA);
		Core.gemm(B,control,1,new Mat(),0, tempB);
		Mat xMat = new Mat();
		Core.add(tempA, tempB, xMat);
		
		//covariance prediction
		Mat p = new Mat();
		Core.gemm(A,lastP,1,new Mat(),0, tempA);
		Core.transpose(A, tempB);
		Core.gemm(tempA,tempB,1,new Mat(),0, tempC);
		Core.add(tempC, Q, p);
		
		//correction
		//innovation covariance
		Mat s = new Mat();
		Core.gemm(H,p,1,new Mat(),0, tempA);
		Core.transpose(H,tempB);
		Core.gemm(tempA,tempB,1,new Mat(),0, tempC);
		Core.add(tempC,R,s);
		
		//kalman gain
		Mat k = new Mat();
		Core.transpose(H,tempA);
		Core.gemm(p,tempA,1,new Mat(),0,tempB);
		Core.invert(s, tempC);
		Core.gemm(tempB,tempC,1,new Mat(),0, k);
		
		//innovation
		Mat yMat = new Mat();
		Core.gemm(H,xMat,1,new Mat(), 0, tempA);
		Core.subtract(measurement, tempA, yMat);
		
		//state update
		Mat curX = new Mat();
		Core.gemm(k,yMat,1,new Mat(),0, tempA);
		Core.add(xMat,tempA,curX);
		
		//covaraince update
		Mat curP = new Mat();
		Core.gemm(k,H,1,new Mat(), 0, tempA);
		Core.subtract(Mat.eye(4, 4, CvType.CV_32F), tempA, tempB);
		Core.gemm(tempB,p,1,new Mat(),0, curP);
		
		lastX = curX;
		lastP = curP;		
	}
	
	/*
	
	public void predict(double n) {
		predX = lastX.clone();
		Mat tempA = new Mat();
		Mat tempB = new Mat();
		Mat control = Mat.zeros(4, 1, CvType.CV_32F);
		//dT is in ms
		int count = (int) Math.round(n/(dT/1000));
		//int count = 200;
		//System.out.println(dT + " " + count);
		//System.out.println(Q.dump());
		//A.put(0, 2, n);
		//A.put(1, 3, n);
		//System.out.println("before: " + predX.dump());
		for (int i =0; i<(2*count); i++){
			Core.gemm( A,predX,1,new Mat(),0, tempA);
			Core.gemm(B,control,1,new Mat(),0, tempB);
			Core.add(tempA, tempB, predX);
			
			//A.put(0, 2, dT/1000);
			//A.put(1, 3, dT/1000);
		}
		
		
		//System.out.println("after: " + predX.dump());
	} 
	
	/*
	public double getEstimatedX() {
		return (double) lastX.get(0, 0)[0];
	}
	
	public double getEstimatedY() {
		return (double) lastX.get(1, 0)[0];
	}
	
	public double getPredX() {
		return (double) predX.get(0, 0)[0];
	}
	
	public double getPredY() {
		return (double) predX.get(1, 0)[0];
	}
	
	public double getPredXVelocity() {
		return (double) predX.get(2, 0)[0];
	}
	
	public double getLinearVelocity() {
		return (double) lastX.get(2, 0)[0];
	}
	*/
	
	public Mat statePre;
	public Mat statePost;
	public Mat transitionMatrix;
	
	public Mat processNoiseCov;
	public Mat measurementMatrix;
	public Mat measurementNoiseCov;
	
	public Mat errorCovPre;
	public Mat errorCovPost;
	
	public Mat gain;
	
	public Mat controlMatrix;
	
	public Mat temp1;
	public Mat temp2;
	public Mat temp3;
	public Mat temp4;
	public Mat temp5;
	
	
	public KalmanFilter(int dp, int mp, int cp, int type) {
		
		statePre = Mat.zeros(dp, 1,type);
		statePost = Mat.zeros(dp, 1,type);
		transitionMatrix = Mat.eye(dp, dp,type);
		
		processNoiseCov = Mat.eye(dp, dp,type);
		measurementMatrix = Mat.zeros(mp, dp,type);
		measurementNoiseCov = Mat.eye(mp, mp,type);
		
		errorCovPre = Mat.zeros(dp, dp,type);
		errorCovPost = Mat.zeros(dp, dp,type);
		gain = Mat.zeros(dp,mp, type);
		
		controlMatrix = Mat.zeros(dp, cp,type);
		
		temp1 = new Mat(dp,dp,type);
		temp2 = new Mat(mp,dp,type);
		temp3 = new Mat(mp,mp,type);
		temp4 = new Mat(mp,dp,type);
		temp5 = new Mat(mp,1,type);
	}
	
	
	public Mat predict(Mat control) {
		
		Core.gemm(transitionMatrix,statePost,1,new Mat(), 0, statePre);
		
		if (!control.empty()) {
			Mat tempA = new Mat();
			Core.gemm(controlMatrix, control, 1, new Mat(),0, tempA);
			Core.add(statePre,tempA,statePre);
		}
		
		Core.gemm(transitionMatrix,errorCovPost,1,new Mat(),0,temp1);
		Core.gemm(temp1, transitionMatrix, 1, processNoiseCov, 1, errorCovPre, Core.GEMM_2_T);
		
		statePre.copyTo(statePost);
		errorCovPre.copyTo(errorCovPost);
		
		return statePre;
	}
	
	public Mat correct(Mat measurement) {
		//Core.gemm(tempA,tempB,1,new Mat(),0, tempC);
		// temp2 = H*P'(k)
		//System.out.println(measurement.dump());
	    Core.gemm(measurementMatrix,errorCovPre,1,new Mat(),0,temp2);
	    
	    // temp3 = temp2*Ht + R
	    Core.gemm(temp2, measurementMatrix, 1, measurementNoiseCov, 1, temp3, Core.GEMM_2_T);

	    // temp4 = inv(temp3)*temp2 = Kt(k)
	    Core.gemm(temp3.inv(),temp2,1,new Mat(),0,temp4);

	    // K(k)
	    Core.transpose(temp4, gain);

	    // temp5 = z(k) - H*x'(k)
	    Mat tempA = new Mat();
	    
	    Core.gemm(measurementMatrix,statePre,1,new Mat(), 0, tempA);
	    Core.subtract(measurement, tempA, temp5);

	    // x(k) = x'(k) + K(k)*temp5
	    Mat tempB = new Mat();
	    Core.gemm(gain,temp5,1, new Mat(), 0 ,tempB);
	    Core.add(statePre, tempB, statePost);

	    // P(k) = P'(k) - K(k)*temp2
	    Mat tempC = new Mat();
	    Core.gemm(gain,temp2,1, new Mat(), 0 ,tempC);
	    Core.subtract(errorCovPre, tempC, errorCovPost);
		
		
		return statePost;
	}
	
	public Mat predictNextPosition(double n) {
		Mat tempA = new Mat();
		statePost.copyTo(tempA);
		
		
		//System.out.println("before " + tempA.dump());
		for (int i = 0; i<200; i++) {
			
			Core.gemm(transitionMatrix,tempA,1,new Mat(), 0, tempA);
			
			
		}
		
		//System.out.println("after " + tempA.dump());
		
		
		return tempA;
	}
	
}
