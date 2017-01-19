package Plugins;

public class PID 
{
	//These are the gain parameters for the PID system
	public double PGain;
	public double IGain;
	public double DGain;
	
	//These are the sensor and target values for the PID system
	public double sensorValue;
	public double targetValue;
	
	//These are caches to help calculate the integral and derivative parts of the PID system
	public double runningSum = 0;
	public double previousError = 0;
	
	//This value is the current output value calculated by the PID system
	public double outputValue;
	
	//These values are maximums that limit the output and integral values
	public double maxOutputValue;
	public double maxIntegralValue;
	
	//The following 3 constructors are called depending on how many parameters are given
	//These initialize the PID system
	public PID (double p, double i, double d)
	{
		PGain = p;
		IGain = i;
		DGain = d;
		
		runningSum = 0;
		previousError = 0;
		
		maxOutputValue = 1;
		maxIntegralValue = -1;
	}
	
	public PID (double p, double i, double d, double maxOutputPower)
	{
		PGain = p;
		IGain = i;
		DGain = d;
		
		runningSum = 0;
		previousError = 0;
		
		maxOutputValue = maxOutputPower;
		maxIntegralValue = -1;
	}
	
	public PID (double p, double i, double d, double maxOutputPower, double maxIntegral)
	{
		PGain = p;
		IGain = i;
		DGain = d;
		
		runningSum = 0;
		previousError = 0;
		
		maxOutputValue = maxOutputPower;
		maxIntegralValue = maxIntegral;
	}
	
	//This function sets the gains of the PID system
	public void setGains (double pGain, double iGain, double dGain)
	{
		PGain = pGain;
		IGain = iGain;
		DGain = dGain;
	}
	
	//This function sets the sensor and target values of the PID system
	public void setParameters (double sensor, double target)
	{
		sensorValue = sensor;
		targetValue = target;
	}
	
	//This function calculates an output value of the PID system
	public double calculateOutput ()
	{
		//Calculate the error
		double error = targetValue - sensorValue;
		
		//Calculate the proportional value of the PID system
		double pValue = error * PGain;
		
		//Calculate the integral value of the PID system
		double iValue = runningSum * IGain;
		//Add the error to the running sum (approximating integration)
		runningSum += error;
		
		//If there is a set maximum for the integral value
		if(maxIntegralValue >= 0)
		{
			//Clamp the integral value to be within a set range (to prevent the system from winding up)
			runningSum = Math.min(Math.max(runningSum, -maxIntegralValue), maxIntegralValue);
		}
		
		//Calculate the derivative value of the PID system
		double dValue = (error - previousError) * DGain;
		
		//Cache the current error to be used for the next call of this function
		previousError = error;
		
		//Sum the P, I, and D values, then clamp the output value to be within a set range
		outputValue = Math.min(Math.max(pValue + iValue + dValue, -maxOutputValue), maxOutputValue);
		
		return outputValue;
	}
}
