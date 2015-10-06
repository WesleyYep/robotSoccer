package controllers;

public class PIDController {

	// PID gain values
	private double Kp;
	private double Ki;
	private double Kd;
	
	// input value
	private double input;
	
	// error values
	private double error;
	private double previousError;
	// integrator state
	private double totalError;
	
	// goal
	private double setPoint;

	// result
	private double result;
	
	public PIDController(double Kp, double Ki, double Kd) {
		this.Kp = Kp;
		this.Ki = Ki;
		this.Kd = Kd;
	}
	
	private void calculate() {
		double pTerm = 0, iTerm = 0, dTerm = 0;
		
		error = setPoint - input;
		
		// Calculate proportional term
		pTerm = Kp * error;
		
		totalError += error;
		
		// calculate integral term
		iTerm = Ki * totalError;
		
		// calculate differential
		dTerm = Kd * (error - previousError);
		
		// update previous error
		previousError = error;
		
		result = pTerm + iTerm + dTerm;
	}

	public double performPID() {
		calculate();
		return result;
	}
	
	public double getKp() {
		return Kp;
	}

	public void setKp(double kp) {
		Kp = kp;
	}

	public double getKi() {
		return Ki;
	}

	public void setKi(double ki) {
		Ki = ki;
	}

	public double getKd() {
		return Kd;
	}

	public void setKd(double kd) {
		Kd = kd;
	}

	public double getInput() {
		return input;
	}

	public void setInput(double input) {
		this.input = input;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getPreviousError() {
		return previousError;
	}

	public void setPreviousError(double previousError) {
		this.previousError = previousError;
	}

	public double getTotalError() {
		return totalError;
	}

	public void setTotalError(double totalError) {
		this.totalError = totalError;
	}

	public double getSetPoint() {
		return setPoint;
	}

	public void setSetPoint(double setPoint) {
		this.setPoint = setPoint;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}
	
}
