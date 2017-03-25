package Plugins;
import Templates.AutonomousProgram;

public class AutonomousUtilities 
{
	AutonomousProgram autoProgram;
	
	public AutonomousUtilities(AutonomousProgram autoReference)
	{
		autoProgram = autoReference;
	}
	
	public void visionAlignX(double power, double desiredTargetAngle)
	{
		while(Math.abs(autoProgram.mainRobot.hardwareMap.gyro.getAngle() - autoProgram.currentHeading) > autoProgram.TURN_ERROR)
		{
			turnWithGyro(power, autoProgram.vision.getTargetAngle()-desiredTargetAngle);
		}
	}
	
	public void visionAlignY(double power, double desiredTargetHeight)
	{
		while(Math.abs(autoProgram.vision.getTargetScreenY() - desiredTargetHeight) > autoProgram.HEIGHT_ERROR)
		{
			forwardWithGyro(power*Math.signum(autoProgram.vision.getTargetScreenY() - desiredTargetHeight), autoProgram.MAX_FORWARD_ALIGN_PERIOD);
		}
	}
	
	//Move forward at "power" for "time" (seconds)
	public void forward(double power, double time)
	{
		//Save the time started
		long startTime = System.currentTimeMillis();

		//Try catch statement prevents errors if the thread is interrupted
		try
		{
			//For the duration of the travel (as given in paramater)
			//And the threat is stable
			//Give power scotty
			while(System.currentTimeMillis() - startTime < time*1000 && !Thread.interrupted())
			{
				autoProgram.assignPower(power,power);
				//A cullen thing <-- this ensures the roborio doesn't get mad and crash, James
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {}

		//Disable all motors
		autoProgram.assignPower(0,0);
	}

	//Drive forward while monitoring and maintaining a heading
	public void forwardWithGyro(double power, double time)
	{
		//Save the time we started this command
		long startTime = System.currentTimeMillis();

		//Set the PID system so the output will be between -1 and 1
		autoProgram.gyroPID.setMaxOutput(1.0);
		autoProgram.gyroPID.setGains(autoProgram.forwardPIDP, autoProgram.forwardPIDI, autoProgram.forwardPIDD);
		try
		{
			//Loop until enough time has passed or this thread has been interrupted
			while(System.currentTimeMillis() - startTime < time*1000 && !Thread.interrupted())
			{
				//Set the target and sensor values for the PID controller
				autoProgram.gyroPID.setParameters(autoProgram.mainRobot.hardwareMap.gyro.getAngle(), autoProgram.currentHeading);

				//PID magic occurs here (OOOOH, AHHHH)
				double pidOutput = autoProgram.gyroPID.calculateOutput();

				//for debugging purposes
				/*SmartDashboard.putNumber("Right Power", power - pidOutput);
					SmartDashboard.putNumber("Left Power", power + pidOutput);
					SmartDashboard.putNumber("Gyro Angle", autoProgram.mainRobot.hardwareMap.gyro.getAngle());*/

				//Adjust the power accordingly
				autoProgram.assignPower(power + pidOutput, power - pidOutput);

				//cullen thing <- Prevent crashing (the program, not the robot, unfortunately)
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {}

		//stop motors
		autoProgram.assignPower(0,0);
	}

	//Turn and angle using the gyro, positive theta is for right turns
	public void turnWithGyro(double power, double theta)
	{
		//Adjust our heading
		autoProgram.currentHeading += theta;

		//Save the current time
		long start = System.currentTimeMillis();

		//Flag for stopping turning when we've reached our wanted angle
		boolean finished = false;

		//Counter for the number of ms the robot is within the turning error
		int errorCounts = 0;

		//Constrain the PID output to be the maximum power we want while turning
		autoProgram.gyroPID.setMaxOutput(power);
		autoProgram.gyroPID.setGains(autoProgram.turnPIDP, autoProgram.turnPIDI, autoProgram.turnPIDD);

		try
		{
			//Loop while the robot hasn't reached our goal and while we haven't given up
			while(!Thread.interrupted() & !finished & (System.currentTimeMillis() - start < autoProgram.MAX_TURN_TIME))
			{
				//Set the PID parameters so our target is our new heading
				autoProgram.gyroPID.setParameters(autoProgram.mainRobot.hardwareMap.gyro.getAngle(), autoProgram.currentHeading);

				//PID magic
				double pidOutput = autoProgram.gyroPID.calculateOutput();

				//if the angle is good enough...
				if(Math.abs(autoProgram.currentHeading - autoProgram.mainRobot.hardwareMap.gyro.getAngle()) < autoProgram.TURN_ERROR)
				{
					//Increase the number of ms we're in our error
					errorCounts++;
				}
				else
				{
					//We're not there yet, so reset the counter
					errorCounts = 0;
				}

				//If we've been within the error for long enough...
				if(errorCounts >= autoProgram.MAX_TURN_ERROR_WAIT)
				{
					//We're done!
					finished = true;
				}

				//Set motor powers
				autoProgram.assignPower(pidOutput, -pidOutput);

				//Prevent the program from exploding (you're WELCOME, James)
				Thread.sleep(1);
			}
		} catch(InterruptedException e) {}

		//Stop!!!
		autoProgram.assignPower(0,0);
	}

	//Make the robot wait for some time
	public void waitForTime(double time)
	{
		//Save the current time
		long startTime = System.currentTimeMillis();

		//Ensure we aren't moving
		autoProgram.assignPower(0,0);

		try
		{
			//Loop until enough time has passed
			while (System.currentTimeMillis() - startTime < time * 1000)
			{
				//Don't crash pls
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {}
	}
}
