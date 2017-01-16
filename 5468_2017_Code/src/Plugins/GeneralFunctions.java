package Plugins;

public class GeneralFunctions 
{
	//This function clamps a given value to within a range from min to max
	public static double clamp(double value, double min, double max)
	{
		//If the value is less than the minimum, set the value to the minimum
		if(value < min)
		{
			value = min;
		}
		
		//If the value is greater than the maximum, set the value to the maximum
		if(value > max)
		{
			value = max;
		}
		
		//Return the (potentially) modified value
		return value;
	}
	
	//This function creates a deadzone for a joystick value
	public static double deadzone(double joystickValue, double deadzone)
	{
		//If the joystick value lies within the deadzone...
		if (Math.abs(joystickValue) < deadzone)
		{
			//Set the joystick value
			joystickValue = 0;
		}
		
		//Return the (potentially) modified joystick value
		return joystickValue;
	}
}
