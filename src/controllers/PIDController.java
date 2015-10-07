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

    private boolean clip;
    private double maximumOutput;
    private double minimumOutput;
	
	public PIDController(double Kp, double Ki, double Kd) {
		this.Kp = Kp;
		this.Ki = Ki;
		this.Kd = Kd;
        totalError = 0;
	}
	
	private void calculate() {
		double pTerm = 0, iTerm = 0, dTerm = 0;
		
		error = setPoint - input;
		
		// Calculate proportional term
		pTerm = Kp * error;
		
		totalError = totalError + error;
		
		// calculate integral term
		iTerm = Ki * totalError;
		
		// calculate differential
		dTerm = Kd * (error - previousError);
		
		// update previous error
		previousError = error;

        System.out.println("Total error: " + totalError);
        System.out.println("Error: " + error);
        System.out.println("pTerm: " + pTerm);
        System.out.println("iTerm: " + iTerm);
        System.out.println("dTerm: " + dTerm);

		result = pTerm + iTerm + dTerm;

        // check if clipping needed
        if (clip) {
            if (result > maximumOutput) {
                result = maximumOutput;
            } else if (result < minimumOutput) {
                result = minimumOutput;
            }
        }
	}

	public double performPID() {
		calculate();
		return result;
	}

    public boolean isClip() {
        return clip;
    }

    public void setClip(boolean clip) {
        this.clip = clip;
    }

    public double getMaximumOutput() {
        return maximumOutput;
    }

    public void setMaximumOutput(double maximumOutput) {
        this.maximumOutput = maximumOutput;
    }

    public double getMinimumOutput() {
        return minimumOutput;
    }

    public void setMinimumOutput(double minimumOutput) {
        this.minimumOutput = minimumOutput;
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
