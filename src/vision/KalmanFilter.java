package vision;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class KalmanFilter {
	
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
	
	public Mat pred;
	
	
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
		
	    tempC.release();
		tempB.release();
		tempA.release();
		
		return statePost;
	}
	
	public Mat predictNextPosition(double n) {
		Mat tempA = new Mat();
		statePost.copyTo(tempA);
		
		double dT = transitionMatrix.get(0, 2)[0];
		if (dT == 0) return null;
		
		int count = (int)Math.round(n/dT);
		//System.out.println("before " + tempA.dump());
		for (int i = 0; i<count; i++) {
			Core.gemm(transitionMatrix,tempA,1,new Mat(), 0, tempA);
		}
		//System.out.println("after " + tempA.dump());
		
		
		return tempA;
	}
	
}
