package AutonomousPrograms;
import org.usfirst.frc.team5468.robot.Robot;

import Templates.*;
import Plugins.*;

//extends our abstract class
public class straightForward extends AutonomousProgram 
{
	//the thread class enables stopping of bot mid-function.
	//this makes the robot safer and obey rules
	AutonomousUtilities autoUtilities;
	SF autoThread;
	
	//constructor
	public straightForward(Robot robot, String name)
	{
		super(robot, name);
	}

	//overriding the abstract class
	@Override
	public void autonomousInit() 
	{
		autoUtilities = new AutonomousUtilities(this);
		
		forwardPIDP = mainRobot.programPreferences.getDouble("Auto Forward P", 0.01);
		forwardPIDI = mainRobot.programPreferences.getDouble("Auto Forward I", 0);
		forwardPIDD = mainRobot.programPreferences.getDouble("Auto Forward D", 0);
		turnPIDP = mainRobot.programPreferences.getDouble("Auto Turn P", 0.01);
		turnPIDI = mainRobot.programPreferences.getDouble("Auto Turn I", 0);
		turnPIDD = mainRobot.programPreferences.getDouble("Auto Turn D", 0);

		//Reset the gyro's position
		mainRobot.hardwareMap.gyro.reset();
				
		//Create a new auto program thread
		autoThread = new SF(this);
		
		mainRobot.hardwareMap.lfDrive.enable();
		mainRobot.hardwareMap.lrDrive.enable();
		mainRobot.hardwareMap.rfDrive.enable();
		mainRobot.hardwareMap.rrDrive.enable();
			
		//Start executing the auto thread
		autoThread.start();
		
	}

	@Override
	public void autonomousPeriodic() 
	{
		// TODO Auto-generated method stub
		
	}

	//do this when the bot is disabled
	//this should kill the Gossamer thread
	@Override
	public void autonomousDisabledInit() 
	{
		//Stop the thread
		if(autoThread != null)
		{
			autoThread.interrupt();
		}
		
		mainRobot.hardwareMap.rfDrive.disable();
		mainRobot.hardwareMap.rrDrive.disable();
		mainRobot.hardwareMap.lfDrive.disable();
		mainRobot.hardwareMap.lrDrive.disable();
	}

	@Override
	public void autonomousDisabledPeriodic() 
	{
		// TODO Auto-generated method stub
		
	}
}

//This thread will enable functions to run in a safe format
class SF extends Thread
{
	//Reference the auto class
	straightForward autoProgram;

	//Constructor saves an instance of auto class for reference
	public SF (straightForward program)
	{
		autoProgram = program;
		
		autoProgram.currentHeading = 0;
	}
	
	//insert commands here
	//will follow linear format
	public void run ()
	{
		//Straight
		autoProgram.autoUtilities.forwardWithGyro(0.75, 2);
	}
}