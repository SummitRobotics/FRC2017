package Plugins;

public class GeneralFunctions 
{
	public static double clamp(double value, double min, double max)
	{
		if(value < min)
		{
			value = min;
		}
		
		if(value > max)
		{
			value = max;
		}
		
		return value;
	}
	
	public static double deadzone(double joystickValue, double deadzone)
	{
		if (Math.abs(joystickValue) < deadzone)
		{
			joystickValue = 0;
		}
		
		return joystickValue;
	}
}
