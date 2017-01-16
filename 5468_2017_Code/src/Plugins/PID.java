package Plugins;

public class PID 
{
	public double PGain;
	public double IGain;
	public double DGain;
	
	public double sensorValue;
	public double targetValue;
	
	public double runningSum = 0;
	public double previousError = 0;
	
	public double outputValue;
	
	public double maxOutputValue;
	public double maxIntegralValue;
	
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
	
	public void setGains (double pGain, double iGain, double dGain)
	{
		PGain = pGain;
		IGain = iGain;
		DGain = dGain;
	}
	
	public void setParameters (double sensor, double target)
	{
		sensorValue = sensor;
		targetValue = target;
	}
	
	public double calculateOutput ()
	{
		double error = targetValue - sensorValue;
		
		double pValue = error * PGain;
		
		double iValue = runningSum * IGain;
		runningSum += error;
		if(maxIntegralValue >= 0)
		{
			runningSum = Math.min(Math.max(runningSum, -maxIntegralValue), maxIntegralValue);
		}
		
		double dValue = (error - previousError) * DGain;
		previousError = error;
		
		outputValue = Math.min(Math.max(pValue + iValue + dValue, -maxOutputValue), maxOutputValue);
		
		return outputValue;
	}
}
