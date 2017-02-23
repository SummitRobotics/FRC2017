package Plugins;

public class GeneralFunctions 
{
	/*This is a clamping function
	 * Parameters:
	 * 		- value: the value to clamp
	 * 		- min: the minimum value that "value" can be
	 * 		- max: the maximum value that "value" can be
	 */
	public static double clamp(double value, double min, double max)
	{
		//Clamp the value to not be lower than the minimum value
		if(value < min)
		{
			value = min;
		}
		
		//Clamp the value to not be greater than the maximum value
		if(value > max)
		{
			value = max;
		}
		
		return value;
	}
	
	/*This is a deadzone creation function
	 * Parameters:
	 * 		- joystickValue: the value of the joystick
	 * 		- deadzone: the range of the deadzone to create
	 */
	public static double deadzone(double joystickValue, double deadzone)
	{
		//if the joystickValue falls within the range of the deadzone...
		if (Math.abs(joystickValue) < deadzone)
		{
			//Set the joystick value to 0
			joystickValue = 0;
		}
		
		return joystickValue;
	}
	
	public static double toExponential(double value, double exponent)
	{
		value = Math.pow(Math.abs(value), exponent) * Math.signum(value);
		
		return value;
	}
}
